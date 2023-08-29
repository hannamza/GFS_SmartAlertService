package com.gfs.gfs_smartalert;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.widget.AbsListView;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.TimerTask;

import static com.gfs.gfs_smartalert.R.drawable.fire_cnt_bg1;


public class MainActivity extends Activity {
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    public static Context context_main;
    int nCurrentVersion = 2;
    //String sServerIP = "192.168.1.84";
    String sServerIP = "160.202.162.3";//"192.168.1.222"
    gfsDBHelper dbHelper;
    SQLiteDatabase sqlDB;
    String mToken = "";

    ListView m_ListView;
    CustomAdapter m_Adapter;
    ImageButton setup;
    String fromAlertTitle, fromAlertAddress, fromAlertDateTime, fromAlertContext, fromAlertAction;
    ToggleButton toggle_cate1, toggle_cate2, toggle_cate3, toggle_cate4;
    boolean toggleState, toggleState1, toggleState2, toggleState3, toggleState4;
    boolean bFinishCreate = false;
    boolean mLockListView = false;
    boolean mLastListView = false;
    TextView text1, text2, text3, text4;
    boolean mAlert1 = true, mAlert2 = true, mAlert3 = true, mAlert4 = true, mAlert5 = true;
    boolean mUseService = true;
    boolean mUseTime = false;
    boolean mFirstList = true;
    int nEventCount1 = 0, nEventCount2 = 0, nEventCount3 = 0, nEventCount4 = 0;
    int mFontSize = 0;
    int mHour = 9, mMinute = 0, mEndHour = 18, mEndMinute = 0;
    int miUnitHour = 0, miUnitMinute = 0, mCurrentTime = 0;
    int nStartTime = 0, nEndTime = 0;
    private Socket socket;
    int nSocketResult = 0;
    int nMaxSeq = 0;
    int nMinSeq = 2147483647;
    //Timer timer;
    int nCurrentPosition = 0;
    ArrayList<String> mList = new ArrayList<String>();
    String phoneNumber = "";
    boolean bAccessResult = false;
    boolean bTokenSend = false;
    private LockSync mLock = new LockSync();
    boolean mStart = true;
    boolean m_bFirst = false;
    boolean mThreading = false;
    boolean mCounting = true;
    int nAlert1 = 0, nAlert2 = 0, nAlert3 = 0, nAlert4 = 0;

    private Intent foregroundServiceIntent = null;
    long sendAlertList = 0;

    String m_strCountry = "KR";

    final int m_nRequestCodePostNotification = 1100;

    private BroadcastReceiver gfsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String msg = intent.getStringExtra("message");
            fromAlertTitle = intent.getStringExtra("title");
            fromAlertAddress = intent.getStringExtra("address");
            fromAlertDateTime = intent.getStringExtra("datetime");
            fromAlertContext = intent.getStringExtra("context");
            fromAlertAction = intent.getStringExtra("action");

            try {
                mLock.lock();
            } catch (InterruptedException ex) {

            }

            if (System.currentTimeMillis() - sendAlertList > 1000) {
                Log.d("request alert list1", "time: " + sendAlertList);
                sendAlertList = System.currentTimeMillis();
                RequestAlertList(0);
            }
            mLock.unlock();
        }
    };

    @SuppressLint("InvalidWakeLockTag")
    static void acquireWakeLock(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "WAKELOCK");
        wakeLock.acquire();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wakeLock.release();
    }

    //@SuppressLint("InvalidWakeLockTag")
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*@Override
    public void onTaskRemoved() {
        super.onTaskRemoved();
        //SetAlarmManager();
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //20230828 GBM start - targetsdk 33으로 올리면서 추가 사항
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                //현재 알림 권한이 부여되어 있는 지가 잘 확인이 안되는 것으로 보여 교체 필요
//                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
//                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, m_nRequestCodePostNotification);
//                } else {
//                    int a = 0;
//                }
                //주의 : 앱 설치 후 사용자에 의해 알림 권한 거부가 2회 이상 있었다면 알림 권한을 묻는 창이 나타나지 않음
                if(NotificationManagerCompat.from(this).areNotificationsEnabled() == false) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, m_nRequestCodePostNotification);
                }
            }
        }
        //20230828 GBM end

        //국가 설정
        Locale systemLocale = getApplicationContext().getResources().getConfiguration().locale;
        m_strCountry = systemLocale.getCountry();

        Task<String> token = FirebaseMessaging.getInstance().getToken();

        context_main = this;

        text1 = (TextView) findViewById(R.id.textView1);
        text2 = (TextView) findViewById(R.id.textView2);
        text3 = (TextView) findViewById(R.id.textView3);
        text4 = (TextView) findViewById(R.id.textView4);

        dbHelper = new gfsDBHelper(this);
        sqlDB = dbHelper.getReadableDatabase();
        Cursor cursor;
        int version = 0;
        cursor = sqlDB.rawQuery("SELECT version from gfsm_version;", null);
        while (cursor.moveToNext()) {
            version = cursor.getInt(0);
        }
        cursor.close();
        sqlDB.close();

        if (nCurrentVersion != version) {
            sqlDB = dbHelper.getWritableDatabase();
            dbHelper.onUpgrade(sqlDB, 1, 2);

            sqlDB.execSQL("INSERT INTO gfsm_version(version) VALUES (" + nCurrentVersion + ")");
            sqlDB.close();
        }
        sqlDB = dbHelper.getReadableDatabase();
        cursor = sqlDB.rawQuery("SELECT * from gfsm_status;", null);
        while (cursor.moveToNext()) {
            String sKey = cursor.getString(0);
            int nValue = cursor.getInt(1);
            switch (sKey) {
                case "화재알림":
                    mAlert1 = (nValue == 0) ? false : true;
                    break;
                case "가스알림":
                    mAlert2 = (nValue == 0) ? false : true;
                    break;
                case "감시알림":
                    mAlert3 = (nValue == 0) ? false : true;
                    break;
                case "단선알림":
                    mAlert4 = (nValue == 0) ? false : true;
                    break;
                case "복구알림":
                    mAlert5 = (nValue == 0) ? false : true;
                    break;
                case "시간사용":
                    mUseTime = (nValue == 0) ? false : true;
                    break;
                case "시작시간":
                    mHour = nValue;
                    break;
                case "시작분":
                    mMinute = nValue;
                    break;
                case "종료시간":
                    mEndHour = nValue;
                    break;
                case "종료분":
                    mEndMinute = nValue;
                    break;
                case "서비스":
                    mUseService = (nValue == 0) ? false : true;
                    break;
                case "폰트사이즈":
                    mFontSize = nValue;
                    break;
                default:
                    break;
            }
        }
        cursor.close();

        cursor = sqlDB.rawQuery("SELECT * from gfsm_phone;", null);
        if (cursor.moveToNext()) {
            phoneNumber = cursor.getString(0);
        }

        sqlDB.close();

        switch (mFontSize) {
            case 0:
                text1.setTextSize(16);
                text2.setTextSize(16);
                text3.setTextSize(16);
                text4.setTextSize(16);
                break;
            case 1:
                text1.setTextSize(18);
                text2.setTextSize(18);
                text3.setTextSize(18);
                text4.setTextSize(18);
                break;
            case 2:
                text1.setTextSize(20);
                text2.setTextSize(20);
                text3.setTextSize(20);
                text4.setTextSize(20);
                break;
        }

        int nScreenWidth = getScreenWidth();
        int nScreenHeight = getScreenHeight();
        float ratio = (float) nScreenHeight / (float) nScreenWidth;

        if (ratio < 1.61) {
            LinearLayout linear = (LinearLayout) findViewById(R.id.contents);
            if (nScreenWidth >= 1600) {
                linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 210));
            } else {
                linear.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 140));
            }
        }

        setup = findViewById(R.id.setupButton);
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
                intent.putExtra("화재", mAlert1);
                intent.putExtra("가스", mAlert2);
                intent.putExtra("감시", mAlert3);
                intent.putExtra("단선", mAlert4);
                intent.putExtra("복구", mAlert5);
                intent.putExtra("use time", mUseTime);
                intent.putExtra("hour", mHour);
                intent.putExtra("min", mMinute);
                intent.putExtra("end hour", mEndHour);
                intent.putExtra("end min", mEndMinute);
                intent.putExtra("서비스", mUseService);
                intent.putExtra("font size", mFontSize);
                intent.putExtra("country", m_strCountry);

                startActivityForResult(intent, 101);
            }
        });

        toggle_cate1 = findViewById(R.id.checkBox1);
        toggle_cate1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (toggle_cate1.isChecked() == toggleState1) {
                    return;
                }
                setButtonDisplay();

                m_Adapter.SetShowType(/*toggle_cate.isChecked()*/false, toggle_cate1.isChecked(), toggle_cate2.isChecked(), toggle_cate3.isChecked(), toggle_cate4.isChecked());
                m_Adapter.notifyDataSetChanged();
                setTextUpdate();
            }
        });
        toggle_cate2 = findViewById(R.id.checkBox2);
        toggle_cate2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (toggle_cate2.isChecked() == toggleState2) {
                    return;
                }
                setButtonDisplay();

                m_Adapter.SetShowType(/*toggle_cate.isChecked()*/false, toggle_cate1.isChecked(), toggle_cate2.isChecked(), toggle_cate3.isChecked(), toggle_cate4.isChecked());
                m_Adapter.notifyDataSetChanged();
                setTextUpdate();
            }
        });
        toggle_cate3 = findViewById(R.id.checkBox3);
        toggle_cate3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (toggle_cate3.isChecked() == toggleState3) {
                    return;
                }
                setButtonDisplay();

                m_Adapter.SetShowType(/*toggle_cate.isChecked()*/false, toggle_cate1.isChecked(), toggle_cate2.isChecked(), toggle_cate3.isChecked(), toggle_cate4.isChecked());
                m_Adapter.notifyDataSetChanged();
                setTextUpdate();
            }
        });
        toggle_cate4 = findViewById(R.id.checkBox4);
        toggle_cate4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (toggle_cate4.isChecked() == toggleState4) {
                    return;
                }
                setButtonDisplay();

                m_Adapter.SetShowType(/*toggle_cate.isChecked()*/false, toggle_cate1.isChecked(), toggle_cate2.isChecked(), toggle_cate3.isChecked(), toggle_cate4.isChecked());
                m_Adapter.notifyDataSetChanged();
                setTextUpdate();
            }
        });

        AbsListView.OnScrollListener viewScrollListener = new AbsListView.OnScrollListener() {
            //private AbsListView view;
            //private int scrollState;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                switch (i) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE: // 정지
                        if (mLastListView && !mLockListView) {
                            mLockListView = true;
                            //getItem();
                            RequestAlertList(1);
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL: // 터치로 스크롤 중
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING: // 스크롤이 움직이는 중
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                int nPosition = m_ListView.getLastVisiblePosition();
                /*String scroll = Integer.toString(i) + "/" + Integer.toString(i1) + "/" + Integer.toString(i2) + " ==> " + Integer.toString(nPosition);
                Log.d("scroll ", scroll);*/

                if (i2 > 0 && nPosition + 1 == i2) {
                    mLastListView = true;
                } else mLastListView = false;
            }
        };
        LocalBroadcastManager.getInstance(this).registerReceiver
                (gfsReceiver, new IntentFilter("AlertServiceFilter"));

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();
                        mToken = token;

                        // Log and toast
                        //String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("TOKEN_TEST", token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

        /*String[] mRequiredPermissions = new String[1];
        // 사용자의 안드로이드 버전에 따라 권한을 다르게 요청합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 11 이상인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_NUMBERS;

        } else {
            // 10 이하인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_STATE;
        }

        bAccessResult = false;
        if (!hasPermissions(mRequiredPermissions)) {
            showDialogGuideForPermissionSettingGuide();
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, mRequiredPermissions[0])) {
                checkPhonePermission();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // 필수 권한을 가지고 있는지 확인한다.
                    long startTime = System.currentTimeMillis();
                    long endTime;
                    while (!hasPermissions(mRequiredPermissions)) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        endTime = System.currentTimeMillis();
                        if (endTime - startTime >= 10 * 1000) {
                            showDialogGuideForPermissionSettingGuide();
                            break;
                        }
                        if (bAccessResult) {
                            if (!hasPermissions(mRequiredPermissions)) {
                                showDialogGuideForPermissionSettingGuide();
                            }
                            break;
                        }
                    }
                }
            } else {
                showDialogGuideForPermissionSettingGuide();
            }
        }*/
        /*if (hasPermissions(mRequiredPermissions)) { //checkPhonePermission()) {//hasPermissions(mRequiredPermissions)) {
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            //if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                //showDialogGuideForPermissionSettingGuide();
            //}
            //else {
            try {
                phoneNumber = telManager.getLine1Number();
            }catch(SecurityException e) {};
            if(phoneNumber == null){
                phoneNumber = "";
            }
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            if (phoneNumber.startsWith("+82")) {
                phoneNumber = phoneNumber.replace("+82", "0");
            }
            if (phoneNumber.startsWith("+")) {
                phoneNumber = phoneNumber.replace("+", "");
            }
            phoneNumber = phoneNumber.replace("+", "");
            //Toast.makeText(MainActivity.this, phoneNumber, Toast.LENGTH_SHORT).show();
            //}
        }*/

        // 커스텀 어댑터 생성
        m_Adapter = new CustomAdapter();

        // Xml에서 추가한 ListView 연결
        m_ListView = findViewById(R.id.listView1);

        // ListView에 어댑터 연결
        m_ListView.setAdapter(m_Adapter);

        m_ListView.setOnScrollListener(viewScrollListener);

        m_Adapter.SetFontType(mFontSize);

        setTextUpdate();

        bFinishCreate = true;
    }
    /*int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            android.Manifest.permission.WAKE_LOCK,
            android.Manifest.permission.READ_PHONE_STATE,
            android.Manifest.permission.READ_PHONE_NUMBERS
    };*/

    public void showDialogGuideForPermissionSettingGuide(boolean bType) {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("권한 요청");
        if(bType) {
            builder.setMessage("전화 접근 권한은 필수 항목입니다.\n\n[인증을 위한 전화번호 수집]\n\n전화 접근 권한을 허용하시겠습니까?");
        }
        else {
            builder.setMessage("전화 접근 권한은 필수 항목입니다.\n\n[인증을 위한 전화번호 수집]\n\n전화 접근 권한을 허용하여 주시기 바랍니다.");
        }
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if(bType) {
                    Intent appDetail = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                    appDetail.addCategory(Intent.CATEGORY_DEFAULT);
                    appDetail.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(appDetail);
                    ActivityCompat.finishAffinity(MainActivity.this); // 액티비티를 종료하고
                }
                else{
                    checkPhonePermission();
                }
            }
        });
        if(bType) {
            builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //ActivityCompat.finishAffinity(MainActivity.this); // 액티비티를 종료하고
                }
            });
        }
        builder.create().show();
    }

    ;

    public boolean hasPermissions(String... permissions) {
        if (permissions != null) {
            for (String permission : permissions) {
                int nResult = ActivityCompat.checkSelfPermission(this, permission);
                if (nResult != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public int getScreenWidth() {
        int nValue = 0;
        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
            nValue = windowMetrics.getBounds().width();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            nValue = displayMetrics.widthPixels;
        }
        return nValue;
    }

    public int getScreenHeight() {
        int nValue = 0;
        WindowManager wm = (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = wm.getCurrentWindowMetrics();
            nValue = windowMetrics.getBounds().height();
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            nValue = displayMetrics.heightPixels;
        }
        return nValue;
    }

    public boolean checkPhonePermission() {
        boolean mPermissionsGranted = false;
        String[] mRequiredPermissions = new String[1];
        // 사용자의 안드로이드 버전에 따라 권한을 다르게 요청합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 11 이상인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_NUMBERS;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 필수 권한을 가지고 있는지 확인한다.
                mPermissionsGranted = hasPermissions(mRequiredPermissions);
            } else {
                mPermissionsGranted = true;
            }
        } else {
            // 10 이하인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_STATE;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // 필수 권한을 가지고 있는지 확인한다.
                mPermissionsGranted = hasPermissions(mRequiredPermissions);
                if (!mPermissionsGranted) {
                    ActivityCompat.requestPermissions(MainActivity.this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
                    mRequiredPermissions[0] = Manifest.permission.READ_PRECISE_PHONE_STATE;
                    mPermissionsGranted = hasPermissions(mRequiredPermissions);
                }
            } else {
                mPermissionsGranted = true;
            }
        }
        if (!mPermissionsGranted) {
            ActivityCompat.requestPermissions(MainActivity.this, mRequiredPermissions, PERMISSIONS_REQUEST_CODE);
        }
        return mPermissionsGranted;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        bAccessResult = true;

        //20230828 GBM start - 현재는 POST_NOTIFICATION에 대한 권한 요청 결과만 있어서 상관없지만 추후에 권한 요청이 많아지면 requestCode로 분기해야 함
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // 권한 허가된 경우 처리
            Log.d("permission result: ", "success");
        } else {
            // 권한 거절된 경우 처리
            Log.d("permission result: ", "fail");

            String strMsg;
            strMsg = getResources().getString(R.string.request_notification_permission);
            Toast toast = Toast.makeText(getApplicationContext(), strMsg, Toast.LENGTH_LONG);
            toast.show();
        }
        //20230828 GBM end
    }

    public void RequestAlertList(int nType) {
        if(mThreading && nType != 3){
            return;
        }
        while (!bFinishCreate) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (nSocketResult == 1) {
            mThreading = true;
            ListSendThread sendThread = new ListSendThread(nType);
            sendThread.start();
        } else {
            mThreading = true;
            ConnectThread thread = new ConnectThread(sServerIP);
            thread.start();

            ListSendThread sendThread = new ListSendThread(nType);
            sendThread.start();
        }
    }

    TimerTask TT = new TimerTask() {
        @Override
        public void run() {
            // 반복실행할 구문
            RequestAlertList(0);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        nSocketResult = 0;

        try {
            mLock.lock();
        } catch (InterruptedException ex) {

        }
        if (System.currentTimeMillis() - sendAlertList > 1000) {
            Log.d("request alert list2", "time: " + sendAlertList);
            sendAlertList = System.currentTimeMillis();
            RequestAlertList(0);
        }
        mLock.unlock();
        phoneNumber.trim();
        /*if (phoneNumber.length() == 0) {

        }*/
        /*
        String[] mRequiredPermissions = new String[1];
        // 사용자의 안드로이드 버전에 따라 권한을 다르게 요청합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 11 이상인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_NUMBERS;
        } else {
            // 10 이하인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_STATE;
        }
        if (!hasPermissions(mRequiredPermissions)) {
            if(m_bFirst){
                showDialogGuideForPermissionSettingGuide(false);
            }
            else{
                showDialogGuideForPermissionSettingGuide(true);
            }
        } else {
            TelephonyManager telManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            try {
                phoneNumber = telManager.getLine1Number();
            }catch(SecurityException e) {};
            if(phoneNumber == null){
                phoneNumber = "";
            }
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            if (phoneNumber.startsWith("+82")) {
                phoneNumber = phoneNumber.replace("+82", "0");
            }
            if (phoneNumber.startsWith("+")) {
                phoneNumber = phoneNumber.replace("+", "");
            }
            phoneNumber = phoneNumber.replace("+", "");
        }

        RequestAlertList(3);*/

        setButtonDisplay();

        toggleState = false;
        //toggleState = toggle_cate.isChecked();
        toggleState1 = toggle_cate1.isChecked();
        toggleState2 = toggle_cate2.isChecked();
        toggleState3 = toggle_cate3.isChecked();
        toggleState4 = toggle_cate4.isChecked();

        mStart = true;

        /*String[] mRequiredPermissions = new String[1];
        // 사용자의 안드로이드 버전에 따라 권한을 다르게 요청합니다
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // 11 이상인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_NUMBERS;

        } else {
            // 10 이하인 경우
            mRequiredPermissions[0] = Manifest.permission.READ_PHONE_STATE;
        }
        ActivityCompat.shouldShowRequestPermissionRationale(this, mRequiredPermissions[0]);*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onStop() {
        super.onStop();
        //timer.cancel();//타이머 종료
        try {
            if(nSocketResult == 1)
            {
                socket.close(); //소켓을 닫는다.
            }
            nSocketResult = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mStart = false;
    }

    public void setTextUpdate() {
        //m_Adapter.GetDataCount();
        int nTotalCount = nEventCount1 + nEventCount2 + nEventCount3 + nEventCount4;
        /*String sValue = String.format("   전체: %d건\n   화재(%d) 가스(%d) 감시(%d) 단선(%d)"
                , nTotalCount, nEventCount1, nEventCount2, nEventCount3, nEventCount4);*/
        String sValue = String.format("%d", nEventCount1);
        text1.setText(sValue);
        sValue = String.format("%d", nEventCount2);
        text2.setText(sValue);
        sValue = String.format("%d", nEventCount3);
        text3.setText(sValue);
        sValue = String.format("%d", nEventCount4);
        text4.setText(sValue);

        // 화재
        if(toggle_cate1.isChecked()){
            text1.setTextColor(Color.parseColor("#FFFFFF"));
            text1.setBackground(getResources().getDrawable(R.drawable.fire_cnt_bg2));
        }
        else {
            text1.setTextColor(Color.parseColor("#8B8B8B"));
            text1.setBackground(getResources().getDrawable(fire_cnt_bg1));
        }
        // 가스
        if(toggle_cate2.isChecked()){
            text2.setTextColor(Color.parseColor("#FFFFFF"));
            text2.setBackground(getResources().getDrawable(R.drawable.fire_cnt_bg2));
        }
        else {
            text2.setTextColor(Color.parseColor("#8B8B8B"));
            text2.setBackground(getResources().getDrawable(fire_cnt_bg1));
        }
        //감시
        if(toggle_cate3.isChecked()){
            text3.setTextColor(Color.parseColor("#FFFFFF"));
            text3.setBackground(getResources().getDrawable(R.drawable.fire_cnt_bg2));
        }
        else {
            text3.setTextColor(Color.parseColor("#8B8B8B"));
            text3.setBackground(getResources().getDrawable(fire_cnt_bg1));
        }
        // 단선
        if(toggle_cate4.isChecked()){
            text4.setTextColor(Color.parseColor("#FFFFFF"));
            text4.setBackground(getResources().getDrawable(R.drawable.fire_cnt_bg2));
        }
        else {
            text4.setTextColor(Color.parseColor("#8B8B8B"));
            text4.setBackground(getResources().getDrawable(fire_cnt_bg1));
        }
    };

    public void setButtonDisplay(){
        /*if(toggle_cate1.isChecked() || toggle_cate2.isChecked() || toggle_cate3.isChecked() || toggle_cate4.isChecked()){
            toggle_cate.setChecked(false);
        }
        else{
            toggle_cate.setChecked(true);
        }*/
        //toggleState = toggle_cate.isChecked();
        toggleState1 = toggle_cate1.isChecked();
        toggleState2 = toggle_cate2.isChecked();
        toggleState3 = toggle_cate3.isChecked();
        toggleState4 = toggle_cate4.isChecked();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101) {
            int nValue = 0;
            sqlDB = dbHelper.getWritableDatabase();
            if(sqlDB == null)
            {
                Toast.makeText(getApplicationContext(), "Error: 데이터베이스에 접근실패. 용량 또는 권한 문제일 가능성이 있습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            boolean bChange = false;
            if(mAlert1 != data.getBooleanExtra("화재", true))
            {
                mAlert1 = data.getBooleanExtra("화재", true);
                nValue = (mAlert1) ? 1 : 0;
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='화재알림'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('화재알림', " + nValue + ")");
                bChange = true;
            }
            if(mAlert2 != data.getBooleanExtra("가스", true))
            {
                mAlert2 = data.getBooleanExtra("가스", true);
                nValue = (mAlert2) ? 1 : 0;
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='가스알림'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('가스알림', " + nValue + ")");
                bChange = true;
            }
            if(mAlert3 != data.getBooleanExtra("감시", true))
            {
                mAlert3 = data.getBooleanExtra("감시", true);
                nValue = (mAlert3) ? 1 : 0;
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='감시알림'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('감시알림', " + nValue + ")");
                bChange = true;
            }
            if(mAlert4 != data.getBooleanExtra("단선", true))
            {
                mAlert4 = data.getBooleanExtra("단선", true);
                nValue = (mAlert4) ? 1 : 0;
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='단선알림'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('단선알림', " + nValue + ")");
                bChange = true;
            }
            if(mAlert5 != data.getBooleanExtra("복구", true))
            {
                mAlert5 = data.getBooleanExtra("복구", true);
                nValue = (mAlert5) ? 1 : 0;
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='복구알림'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('복구알림', " + nValue + ")");
                bChange = true;
            }
            boolean bTime = false;
            if(mUseTime != data.getBooleanExtra("use time", true))
            {
                bTime = true;
                mUseTime = data.getBooleanExtra("use time", true);
                nValue = (mUseTime) ? 1 : 0;
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='시간사용'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('시간사용', " + nValue + ")");
                bChange = true;
            }
            if(mHour != data.getIntExtra("hour", 9))
            {
                bTime = true;
                mHour = data.getIntExtra("hour", 9);
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='시작시간'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('시작시간', " + mHour + ")");
                bChange = true;
            }
            if(mMinute != data.getIntExtra("min", 0))
            {
                bTime = true;
                mMinute = data.getIntExtra("min", 0);
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='시작분'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('시작분', " + mMinute + ")");
                bChange = true;
            }
            if(mEndHour != data.getIntExtra("end hour", 18))
            {
                bTime = true;
                mEndHour = data.getIntExtra("end hour", 18);
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='종료시간'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('종료시간', " + mEndHour + ")");
                bChange = true;
            }
            if(mEndMinute != data.getIntExtra("end min", 0))
            {
                bTime = true;
                mEndMinute = data.getIntExtra("end min", 0);
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='종료분'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('종료분', " + mEndMinute + ")");
                bChange = true;
            }
            if(mUseService != data.getBooleanExtra("서비스", true))
            {
                mUseService = data.getBooleanExtra("서비스", true);
                nValue = (mUseService) ? 1 : 0;
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='서비스'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('서비스', " + nValue + ")");
                bChange = true;
            }
            if(mFontSize != data.getIntExtra("font size", 0))
            {
                mFontSize = data.getIntExtra("font size", 0);
                sqlDB.execSQL("DELETE FROM gfsm_status WHERE gName='폰트사이즈'");
                sqlDB.execSQL("INSERT INTO gfsm_status VALUES ('폰트사이즈', " + mFontSize + ")");

                switch(mFontSize)
                {
                    case 0: text1.setTextSize(16); text2.setTextSize(16); text3.setTextSize(16); text4.setTextSize(16); break;
                    case 1: text1.setTextSize(17); text2.setTextSize(17); text3.setTextSize(17); text4.setTextSize(17); break;
                    case 2: text1.setTextSize(19); text2.setTextSize(19); text3.setTextSize(19); text4.setTextSize(19); break;
                }
                setTextUpdate();
                m_Adapter.SetFontType(mFontSize);
            }
            sqlDB.close();

            if(bChange){
                RequestAlertList(3);
            }
        }
        else if(requestCode == 100){ // login

        }
    }

    private void CheckUseTime() {
        nStartTime = (mHour * 60) + mMinute;
        nEndTime = (mEndHour * 60) + mEndMinute;

        long now = System.currentTimeMillis();
        Date date = new Date(now);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        miUnitHour = cal.get(Calendar.HOUR_OF_DAY);
        miUnitMinute = cal.get(Calendar.MINUTE);

        mCurrentTime = (miUnitHour * 60) + miUnitMinute;
    }

    public class gfsDBHelper extends SQLiteOpenHelper {
        public gfsDBHelper(Context context) {
            super(context, "gfsm_db", null, 2);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            /*db.execSQL("CREATE TABLE  gfsm_msg ( "
                    + "_id integer primary key autoincrement, "
                    + "nType INTEGER, msg TEXT);");*/
            db.execSQL("CREATE TABLE gfsm_status(gName CHAR(20) PRIMARY KEY, gNumber INTEGER)");
            db.execSQL("CREATE TABLE gfsm_version(version INTEGER)");
            db.execSQL("CREATE TABLE gfsm_phone(phone CHAR(32))");

            m_bFirst = true;
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            //db.execSQL("DROP TABLE IF EXISTS gfsm_msg");
            db.execSQL("DROP TABLE IF EXISTS gfsm_status");
            db.execSQL("DROP TABLE IF EXISTS gfsm_version");
            db.execSQL("DROP TABLE IF EXISTS gfsm_phone");
            onCreate(db);
        }
    }

    public void showToast(final String toast)
    {
        runOnUiThread(()->Toast.makeText(this, toast, Toast.LENGTH_LONG).show());
    }

    public void AddAlertList(int nType, String title, String address, String dateTime, String context, String action)
    {
        String sText = "";
        String[] date = dateTime.split(" ");
        int nValue = 3;
        String msg = "";
        int nStringLength = 0;

        //핸드폰 국가 설정 얻기
//        Locale systemLocale = getApplicationContext().getResources().getConfiguration().locale;
//        String stringContry = systemLocale.getCountry();
        boolean bKorean = false;
        if (m_strCountry.equals("KR"))
            bKorean = true;
        //

        switch(title)
        {
            case "화재 복구":
                nValue = 3;
                msg = getResources().getString(R.string.fire_restoration_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "화재 발생":
                nValue = 3;
                msg = getResources().getString(R.string.fire_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "가스 복구":
                nValue = 4;
                msg = getResources().getString(R.string.gas_restoration_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;// + "\n[" + context + "]";
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "가스 발생":
                nValue = 4;
                msg = getResources().getString(R.string.gas_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;// + "\n[" + context + "]";
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "감시 복구":
                nValue = 5;
                msg = getResources().getString(R.string.survaillance_restoration_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;// + "\n[" + context + "]";
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "감시 발생":
                nValue = 5;
                msg = getResources().getString(R.string.survaillance_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;// + "\n[" + context + "]";
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "단선 복구":
                nValue = 6;
                msg = getResources().getString(R.string.disconnection_restoration_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;// + "\n[" + context + "]";
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "단선 발생":
                nValue = 6;
                msg = getResources().getString(R.string.disconnection_alarm_text);
                sText = msg + "\n" + dateTime + "\n" + address;// + "\n[" + context + "]";
                if(context.length() > 0){
                    sText += "\n[" + context + "]";
                }
                break;
            case "복구 완료":
                nValue = 7;
                msg = getResources().getString(R.string.restoration_completed_text);
                sText = msg + "\n" + dateTime + "\n" + context;
                break;
            case "end of list":
                msg = getResources().getString(R.string.end_of_list_text);
                m_Adapter.add(msg, 2);
                return;
        }

        // 한국 핸드폰 아니면 영문 문자열 크기를 구함
        if(bKorean == false)
        {
            nStringLength = msg.length();
        }
        //

        String[] temp = date[0].split(" ");
        String[] field = temp[0].split("/");
        String sInt = "0";
        if(field.length >= 3) {
            sInt = field[0] + field[1] + field[2];
        }
        int nDate = Integer.parseInt(sInt);
        if(nType == 0)
        {
            boolean bTrue = false;
            for(String s : mList){
                if(s.equals(date[0])){
                    bTrue = true;
                    break;
                }
            }
            if(!bTrue) {
                m_Adapter.insert(nStringLength, date[0], 2, nCurrentPosition);
                mList.add(date[0]);
                ++nCurrentPosition;
            }
            if(nCurrentPosition == 0 && m_Adapter.getCount() > 0){
                nCurrentPosition = 1;
            }
            m_Adapter.insert(nStringLength, sText, nValue, nCurrentPosition);
            ++nCurrentPosition;
        }
        else
        {
            boolean bTrue = false;
            for(String s : mList){
                if(s.equals(date[0])){
                    bTrue = true;
                    break;
                }
            }
            if(!bTrue) {
                m_Adapter.add(date[0], 2);
                mList.add(date[0]);
            }
            m_Adapter.add(sText, nValue);
        }
    }

    public void ParsingData(String sData, int nType)
    {
        String fieldSperate = "x";
        String[] Field = sData.split(fieldSperate);
        String strAction = Field[0];
        String strType = "";
        String strDateTime = "";
        String strAddress = "";
        String strContext = "";
        String strTitle = "";
        if(Field.length > 3){
            strType = Field[1];
            strDateTime = Field[2];
            strAddress = Field[3];
            if (sData.endsWith("x")) {
                strContext = "";
            } else {
                strContext = Field[4];
                strContext = strContext.replace("\n", "");
            }
            strTitle = GFSFirebaseMessagingService.GetTypeName(strType);
            if(GFSFirebaseMessagingService.GetType(Field[0])) {
                strTitle += " 복구";
            }
            else {
                strTitle += " 발생";
            }
        }
        else{
            strDateTime = Field[1];

            //수신기 복구 타이틀만 보이게 (한전무님)
            //strContext = Field[2];

            strTitle = "복구 완료";
        }

        AddAlertList(nType, strTitle, strAddress, strDateTime, strContext, strAction);
    }

    class ConnectThread extends Thread {
        String hostname;
        public ConnectThread(String addr) {
            hostname = addr;
        }
        public void run() {
            long startTime = SystemClock.currentThreadTimeMillis();
            try { //클라이언트 소켓 생성

                int port = 10234;
                socket = new Socket(hostname, port);
                Log.d("socket ", "Socket 생성, 연결.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        InetAddress sockAddr = socket.getInetAddress();
                        String tmp = sockAddr.getHostAddress();
                        nSocketResult = 1;
                        //Toptext.setText(tmp + " 연결 완료");
                        //Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_LONG).show();
                    }
                });
            } catch (UnknownHostException uhe) { // 소켓 생성 시 전달되는 호스트(www.unknown-host.com)의 IP를 식별할 수 없음.
                Log.e("socket ", " 생성 Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)");
                nSocketResult = -1;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)", Toast.LENGTH_SHORT).show();
                        //Toptext.setText("Error : 호스트의 IP 주소를 식별할 수 없음.(잘못된 주소 값 또는 호스트 이름 사용)");
                    }
                });
            } catch (IOException ioe) { // 소켓 생성 과정에서 I/O 에러 발생.
                Log.e("socket ", " 생성 Error : 네트워크 응답 없음");
                nSocketResult = -2;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 네트워크 응답 없음", Toast.LENGTH_SHORT).show();
                        //Toptext.setText("네트워크 연결 오류");
                    }
                });
            } catch (SecurityException se) { // security manager에서 허용되지 않은 기능 수행.
                Log.e("socket ", " 생성 Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)");
                nSocketResult = -3;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)", Toast.LENGTH_SHORT).show();
                        //Toptext.setText("Error : 보안(Security) 위반에 대해 보안 관리자(Security Manager)에 의해 발생. (프록시(proxy) 접속 거부, 허용되지 않은 함수 호출)");
                    }
                });
            } catch (IllegalArgumentException le) { // 소켓 생성 시 전달되는 포트 번호(65536)이 허용 범위(0~65535)를 벗어남.
                Log.e("socket ", " 생성 Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)");
                nSocketResult = -4;
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), " Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)", Toast.LENGTH_SHORT).show();
                        //Toptext.setText("Error : 메서드에 잘못된 파라미터가 전달되는 경우 발생.(0~65535 범위 밖의 포트 번호 사용, null 프록시(proxy) 전달)");
                    }
                });
            } finally {
                long endTime = SystemClock.currentThreadTimeMillis();
                Log.d("Socket ", "elapsedTime : " + (endTime - startTime));
            }
        }
    }

    class ListSendThread extends Thread{
        int nType = 0;
        int nTemp = 0;
        public ListSendThread(int type){
            nType = type; // 0: 최근 목록, 1: 최종 목록 하위 20개 더, 2: SendToken
        }
        public String byteArrayToHex(byte[] a) {
            StringBuilder sb = new StringBuilder();
            for(final byte b: a)
                sb.append(String.format("%02x ", b&0xff));
            return sb.toString();
        }
        public synchronized void run(){
            long startTime = SystemClock.uptimeMillis();
            long endTime = 0;
            if(nType == 1 && nMinSeq == -1) {
                mLockListView = false;
                mThreading = false;
                return;
            }
            while(nSocketResult <= 0 || socket.isConnected() == false)
            {
                endTime = SystemClock.uptimeMillis();
                if(nSocketResult < 0 || endTime - startTime >= 6000) {
                    showToast("서버에 접속 할 수 없습니다. 인터넷 연결을 확인하시거나 관리자에게 문의하세요.");
                    mThreading = false;
                    return;
                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if(nType != 3) {
                // 데이터 송신
                try {
                    byte[] dtoByteArray = null;
                    short protocol = 42;//6;
                    int nSize = 54;
                    int nSeq = 0;
                    int nUserIndex = 1;
                    if (nType == 0) {
                        nSeq = nMaxSeq;
                    } else if (nType == 1) {
                        nSeq = nMinSeq;
                    }

                    byte buf[] = new byte[16];
                    System.arraycopy(phoneNumber.getBytes(), 0, buf, 0, phoneNumber.length());
                    //phoneNumber.getBytes();
                    // 초기화 벡터 - 사용자가 지정하는 초기화 벡터(16 BYTE)
                    byte v[] = new byte[16];
                    for(int i = 0; i < 16; i++) {
                        v[i] = (byte)(Math.random() * 24);
                    }

                    try {
                        ByteBuffer buffer = ByteBuffer.allocate(46);
                        buffer.order(ByteOrder.LITTLE_ENDIAN);
                        buffer.putShort(protocol);
                        buffer.putInt(nSize);
                        buffer.put(v, 0, 16);
                        buffer.putInt(nType);
                        buffer.putInt(nSeq);
                        buffer.put(buf, 0, 16);
                        //buffer.putInt(nUserIndex);
                        dtoByteArray = buffer.array();
                    } finally {
                    }
                    // 암호화
                    byte pbszIV[] = {0x26, 0x6d, 0x66, 0x47, 0x35, 0x18, 0x1a, 0x61, 0x6f, 0x0a, 0x39, 0x7a, 0x36, 0x16, 0x25, 0x01, 0x00};

                    KISA_SEED_CBC encode = new KISA_SEED_CBC();
                    byte value[] = encode.SEED_CBC_Encrypt(pbszIV, v, dtoByteArray, 22, dtoByteArray.length-22);
                    //byte vvvv[] = encode.SEED_CBC_Decrypt(pbszIV, v, value, 0, value.length);

                    byte sendData[] = new byte[54];
                    System.arraycopy(dtoByteArray, 0, sendData, 0, 22);
                    System.arraycopy(value, 0, sendData, 22, 32);

                    OutputStream output = socket.getOutputStream();
                    output.write(sendData);
                    Log.d("Transfer ", "리스트 요청 프로토콜 송신");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Transfer ", "데이터 송신 오류");
                    mThreading = false;
                    return;
                }
            }
            else {
                mToken.trim();
                phoneNumber.trim();
                if (!bTokenSend && mToken.length() > 0 && phoneNumber.length() > 0) {
                    try {
                        // 초기화 벡터 - 사용자가 지정하는 초기화 벡터(16 BYTE)
                        byte v[] = new byte[16];
                        for(int i = 0; i < 16; i++) {
                            v[i] = (byte)(Math.random() * 24);
                        }

                        byte[] dtoByteArray = null;
                        short protocol = 40;
                        int nSize = 342;    // GFSM_MCS에서 받을 때 구조체 크기가 342
                        byte phone[] = new byte[16];
                        System.arraycopy(phoneNumber.getBytes(), 0, phone, 0, phoneNumber.length());
                        byte token[] = new byte[256];
                        System.arraycopy(mToken.getBytes(), 0, token, 0, mToken.length());

                        try {
                            ByteBuffer buffer = ByteBuffer.allocate(322+16/*278+44*/);
                            buffer.order(ByteOrder.LITTLE_ENDIAN);
                            buffer.putShort(protocol);
                            buffer.putInt(nSize);
                            buffer.put(v, 0, 16);
                            buffer.put(phone, 0, 16);
                            buffer.put(token, 0, 256);
                            nTemp = 0; // mobile type android
                            buffer.putInt(nTemp);
                            nTemp = mAlert1 ? 1 : 0;
                            buffer.putInt(nTemp);
                            nTemp = mAlert2 ? 1 : 0;
                            buffer.putInt(nTemp);
                            nTemp = mAlert3 ? 1 : 0;
                            buffer.putInt(nTemp);
                            nTemp = mAlert4 ? 1 : 0;
                            buffer.putInt(nTemp);
                            nTemp = mUseService ? 1 : 0;
                            buffer.putInt(nTemp);
                            buffer.putInt(mHour);
                            buffer.putInt(mMinute);
                            buffer.putInt(mEndHour);
                            buffer.putInt(mEndMinute);
                            nTemp = mUseTime ? 1 : 0;
                            buffer.putInt(nTemp);
                            dtoByteArray = buffer.array();
                        } finally {
                        }

                        // 암호화
                        byte pbszIV[] = {0x26, 0x6d, 0x66, 0x47, 0x35, 0x18, 0x1a, 0x61, 0x6f, 0x0a, 0x39, 0x7a, 0x36, 0x16, 0x25, 0x01, 0x00};

                        KISA_SEED_CBC encode = new KISA_SEED_CBC();
                        byte value[] = encode.SEED_CBC_Encrypt(pbszIV, v, dtoByteArray, 22, dtoByteArray.length-22);
                        //byte vvvv[] = encode.SEED_CBC_Decrypt(pbszIV, v, value, 0, value.length);

                        //value의 길이는 320byte

                        byte sendData[] = new byte[342];
                        System.arraycopy(dtoByteArray, 0, sendData, 0, 22);
                        System.arraycopy(value, 0, sendData, 22, 320);

                        OutputStream output = socket.getOutputStream();
                        output.write(sendData, 0, 342);
                        Log.d("Transfer ", "토큰, 전화번호 프로토콜 송신");
                        //bTokenSend = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.d("Transfer ", "데이터 송신 오류");
                        mThreading = false;
                        return;
                    }
                }
                mThreading = false;
                return;
            }

            // 데이터 수신
            try {
                Log.d("Transfer ", "데이터 수신 준비");

                byte[] buffer = new byte[7200];
                InputStream input = socket.getInputStream();

                int nTotal = 0;
                int bytes = 0;
                int nSize = 1;
                int nCount = 0;
                int nTypeValue = 0;
                long sCheckTime = System.currentTimeMillis();
                while(nTotal < nSize){
                    bytes = input.read(buffer, nTotal, 7200-nTotal);

                    if(nTotal == 0){
                        nCount = ConvertLittleEndian.byteToInt(buffer, 10);
                        nTypeValue = ConvertLittleEndian.byteToInt(buffer, 6);
                        nSize = ConvertLittleEndian.byteToInt(buffer, 2);
                        nEventCount1 = ConvertLittleEndian.byteToInt(buffer, 14);
                        nEventCount2 = ConvertLittleEndian.byteToInt(buffer, 18);
                        nEventCount3 = ConvertLittleEndian.byteToInt(buffer, 22);
                        nEventCount4 = ConvertLittleEndian.byteToInt(buffer, 26);
                    }
                    nTotal += bytes;
                    if(System.currentTimeMillis() - sCheckTime > 5000)
                    {
                        mLockListView = false;
                        mThreading = false;
                        return;
                    }
                }
                String arrayString = new String(buffer, 30, nTotal - 30, "UTF-8");
                //String arrayString = new String(buffer, 14, nTotal - 14, "UTF-8");

                if (nCount > 0 && nCount <= 20) {
                    mFirstList = false;
                    StringTokenizer st = new StringTokenizer(arrayString, ";");
                    String[] message = new String[st.countTokens()];
                    int i = 0;
                    nCurrentPosition = 0;
                    while (st.hasMoreTokens()) {
                        message[i] = st.nextToken();
                        if (i % 2 == 0) {
                            if (message[i].length() == 0) {
                                Log.e("Transfer ", "잘못된 seq value");
                                throw new IOException();
                            }
                            int nSeq = Integer.parseInt(message[i]);
                            if (nSeq > nMaxSeq) {
                                nMaxSeq = nSeq;
                            }
                            if (nSeq < nMinSeq) {
                                nMinSeq = nSeq;
                            }
                            Log.d("Token ", "Sequence value: " + message[i]);
                        } else {
                            Log.d("Token ", "Data value: " + message[i]);
                            ParsingData(message[i], nTypeValue);
                        }
                        i++;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                m_Adapter.notifyDataSetChanged();
                                setTextUpdate();
                            }
                        });
                    }
                } else if (nCount == 0 && (mFirstList || nTypeValue == 1)) {
                    mFirstList = false;
                    nMinSeq = -1;
                    AddAlertList(0, "end of list", ", ", ", ", "", "");
                    runOnUiThread(new Runnable() {
                        public void run() {
                            m_Adapter.notifyDataSetChanged();
                            setTextUpdate();
                        }
                    });
                }
                mLockListView = false;

                Log.d("Transfer ", "byte = " + nTotal);
                Log.d("Transfer ", "count: " + nCount + ", Type: " + nTypeValue);
                //Log.d("Transfer ", "msg: " + arrayString);
                Log.d("Transfer ", "size: " + arrayString.length());
            }catch(IOException e) {
                e.printStackTrace();
                Log.e("Transfer ", "수신 에러");
                if (nType == 0 || nType == 1) {
                    mLockListView = false;
                    try {
                        if (nSocketResult == 1) {
                            socket.close(); //소켓을 닫는다.
                        }
                        nSocketResult = 0;
                    } catch (IOException ee) {
                        ee.printStackTrace();
                    }
                }
            }
            mThreading = false;
        }
    }
}
