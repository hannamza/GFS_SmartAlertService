package com.gfs.gfs_smartalert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;

public class SetupActivity extends Activity {
    ImageButton closeBtn;
    ImageButton downloadF3manual, downloadUsermanual;
    Switch mAlert, mAlert1, mAlert2, mAlert3, mAlert4, mAlert5;
    Button startTime, endTime;
    Button sendToken;
    CheckBox mTimeCheck;
    RadioButton mNormal, mBig, mBigger;
    int mHour = 0, mMinute = 0, mEndHour = 0, mEndMinute = 0, mFontSize = 0;
    boolean startEnd = true;
    private TextView textView_Date;
    private TimePickerDialog.OnTimeSetListener callbackMethod;

    //--------------------------------------------------------------------------------------------------------------
    private Context mContext;

    private long mDownloadReference;
    private DownloadManager mDownloadManager;
    private BroadcastReceiver receiverDownloadComplete;    //다운로드 완료 체크
    private BroadcastReceiver receiverNotificationClicked;    //다운로드 시작 체크

    String m_strCountry = "";

    private void URLDownloading(String data) {
        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        /*IntentFilter filter = new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED);

        receiverNotificationClicked = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String extraId = DownloadManager.EXTRA_NOTIFICATION_CLICK_DOWNLOAD_IDS;
                long[] references = intent.getLongArrayExtra(extraId);
                for (long reference : references) {send_phone

                }
            }
        };

        mContext.registerReceiver(receiverNotificationClicked, filter);*/


        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);

        receiverDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                if(mDownloadReference == reference){

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(reference);
                    Cursor cursor = mDownloadManager.query(query);

                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                    int status = cursor.getInt(columnIndex);
                    int reason = cursor.getInt(columnReason);

                    cursor.close();

                    switch (status){

                        case DownloadManager.STATUS_SUCCESSFUL :

                            Toast.makeText(mContext, "다운로드 완료.", Toast.LENGTH_SHORT).show();
                            break;

                        case DownloadManager.STATUS_PAUSED :

                            Toast.makeText(mContext, "다운로드 중지 : " + reason, Toast.LENGTH_SHORT).show();
                            break;

                        case DownloadManager.STATUS_FAILED :

                            Toast.makeText(mContext, "다운로드 취소 : " + reason, Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }
        };
        mContext.registerReceiver(receiverDownloadComplete, intentFilter);

        Uri uri = Uri.parse("http://www.manythings.org/anki/fra-eng.zip");        //data는 파일을 떨궈 주는 uri
        File file = getFilesDir();
        String getFile = file.getPath() + "/F3_user_manual.pdf";

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("F3 사용자 매뉴얼");    //다운로드 완료시 noti에 제목
        request.setNotificationVisibility(0);//DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setMimeType("application/pdf");
        request.setAllowedOverMetered(true);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);

        //request.setDestinationUri(Uri.fromFile(new File(file.getPath(), "/F3_user_manual.pdf")));
        //request.setDestinationInExternalFilesDir(mContext, Environment.DIRECTORY_DOWNLOADS, "/F3_user_manual.pdf");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "F3_user_manual.pdf");
        mDownloadReference = mDownloadManager.enqueue(request);
    }






    /*private DownloadManager mDownloadManager;
    private Long mDownloadQueueId;

    private void URLDownloading(Uri url) {
        if (mDownloadManager == null) {
            mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        String outputFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS ) + "/1/111.pdf";
        File outputFile = new File(outputFilePath);
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }

        Uri downloadUri = url;
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        List<String> pathSegmentList = downloadUri.getPathSegments();
        request.setTitle("F3 사용자 매뉴얼");
        request.setDestinationUri(Uri.fromFile(outputFile));
        request.setAllowedOverMetered(true);

        mDownloadQueueId = mDownloadManager.enqueue(request);
    }

    private BroadcastReceiver downloadCompleteReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

            if(mDownloadQueueId == reference){
                DownloadManager.Query query = new DownloadManager.Query();  // 다운로드 항목 조회에 필요한 정보 포함
                query.setFilterById(reference);
                Cursor cursor = mDownloadManager.query(query);

                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);

                int status = cursor.getInt(columnIndex);
                int reason = cursor.getInt(columnReason);

                cursor.close();

                switch (status) {
                    case DownloadManager.STATUS_SUCCESSFUL :
                        Toast.makeText(mContext, "다운로드를 완료하였습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_PAUSED :
                        Toast.makeText(mContext, "다운로드가 중단되었습니다.", Toast.LENGTH_SHORT).show();
                        break;

                    case DownloadManager.STATUS_FAILED :
                        Toast.makeText(mContext, "다운로드가 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter completeFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadCompleteReceiver, completeFilter);
    }

    @Override
    public void onPause(){
        super.onPause();
        unregisterReceiver(downloadCompleteReceiver);
    }
*/
    //--------------------------------------------------------------------------------------------------------------

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        mContext = getApplicationContext();

        Intent intent = getIntent();
        boolean alert1 = intent.getBooleanExtra("화재", true);
        boolean alert2 = intent.getBooleanExtra("가스", true);
        boolean alert3 = intent.getBooleanExtra("감시", true);
        boolean alert4 = intent.getBooleanExtra("단선", true);
        boolean alert5 = intent.getBooleanExtra("복구", true);
        boolean useTime = intent.getBooleanExtra("use time", false);
        boolean useService = intent.getBooleanExtra("서비스", false);
        mHour = intent.getIntExtra("hour", 9);
        mMinute = intent.getIntExtra("min", 0);
        mEndHour = intent.getIntExtra("end hour", 18);
        mEndMinute = intent.getIntExtra("end min", 0);
        mFontSize = intent.getIntExtra("font size", 0);

        m_strCountry = intent.getStringExtra("country");

        mAlert1 = findViewById(R.id.switch1);
        mAlert1.setChecked(alert1);
        mAlert2 = findViewById(R.id.switch2);
        mAlert2.setChecked(alert2);
        mAlert3 = findViewById(R.id.switch3);
        mAlert3.setChecked(alert3);
        mAlert4 = findViewById(R.id.switch4);
        mAlert4.setChecked(alert4);
        /*mAlert5 = findViewById(R.id.switch5);
        mAlert5.setChecked(alert5);*/
        mAlert = findViewById(R.id.switch0);
        mAlert.setChecked(useService);

        mNormal = findViewById(R.id.radioButton);
        mBig = findViewById(R.id.radioButton2);
        mBigger = findViewById(R.id.radioButton3);
        switch(mFontSize)
        {
           default:
           case 0: mNormal.setChecked(true); break;
           case 1: mBig.setChecked(true); break;
           case 2: mBigger.setChecked(true); break;
        }

        mAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mAlert.isChecked()){
                }
                else {
                    Toast.makeText(getApplicationContext(), "전체 알림 받기 꺼짐", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callbackMethod = new TimePickerDialog.OnTimeSetListener()
        {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (startEnd) {
                    mHour = hourOfDay;
                    mMinute = minute;
                    String sTime;
                    sTime = String.format("%02d:%02d", mHour, mMinute);
                    startTime.setText(sTime);
                } else {
                    mEndHour = hourOfDay;
                    mEndMinute = minute;
                    String sTime;
                    sTime = String.format("%02d:%02d", mEndHour, mEndMinute);
                    endTime.setText(sTime);
                }
            }
        };

        if(!((MainActivity)MainActivity.context_main).phoneNumber.equals("0") && !((MainActivity)MainActivity.context_main).phoneNumber.equals(""))
        {
            EditText editText = (EditText)findViewById(R.id.phone_edit);
            editText.setText(((MainActivity)MainActivity.context_main).phoneNumber);
        }

        mTimeCheck = findViewById(R.id.checkBox);
        mTimeCheck.setChecked(useTime);

        startTime = findViewById(R.id.startTime);
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEnd = true;

                TimePickerDialog dialog = new TimePickerDialog(SetupActivity.this, R.style.time_picker_dialog_style_light, callbackMethod, mHour, mMinute, true);
                dialog.setTitle("시간 설정");
                //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

                /*Intent intent = new Intent(getApplicationContext(), TimePickerPopup.class);
                intent.putExtra("time", mHour);
                intent.putExtra("minute", mMinute);
                startActivityForResult(intent, 101);*/
            }
        });
        endTime = findViewById(R.id.endTime);
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEnd = false;
                TimePickerDialog dialog = new TimePickerDialog(SetupActivity.this, R.style.time_picker_dialog_style_light, callbackMethod, mEndHour, mEndMinute, true);
                dialog.setTitle("시간 설정");
                //dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();

               /* Intent intent = new Intent(getApplicationContext(), TimePickerPopupEx.class);
                intent.putExtra("time", mEndHour);
                intent.putExtra("minute", mEndMinute);
                startActivityForResult(intent, 102);*/
            }
        });
        String sTime;
        sTime = String.format("%02d:%02d", mHour, mMinute);
        startTime.setText(sTime);
        sTime = String.format("%02d:%02d",  mEndHour, mEndMinute);
        endTime.setText(sTime);

        downloadF3manual = findViewById(R.id.f3menualdown);
        downloadF3manual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean bKorean = false;
                if(m_strCountry.equals("KR")) {
                    bKorean = true;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if(bKorean) {
                    Uri uri = Uri.parse("http://160.202.162.3/file/f3/F3_user_manual.pdf");
                    intent.setData(uri);
                }
                else {
                    Uri uri = Uri.parse("http://160.202.162.3/file/f3/F3_user_manual_en.pdf");
                    intent.setData(uri);
                }
                startActivity(intent);

                //URLDownloading( "http://www.gfs.co.kr/upload/attachbase/201811/MP-X2_System_Eng_Manual-V2.02-181102.pdf" );
            }
        });

        downloadUsermanual = findViewById(R.id.usermenualdown);
        downloadUsermanual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean bKorean = false;
                if(m_strCountry.equals("KR")) {
                    bKorean = true;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW);
                if(bKorean) {
                    Uri uri = Uri.parse("http://160.202.162.3/file/app/GFSM_App_Manual-V1.pdf");
                    intent.setData(uri);
                }
                else {
                    Uri uri = Uri.parse("http://160.202.162.3/file/app/GFSM_App_Manual-V1_en.pdf");
                    intent.setData(uri);
                }
                startActivity(intent);

                //URLDownloading( "http://www.gfs.co.kr/upload/attachbase/201811/MP-X2_System_Eng_Manual-V2.02-181102.pdf" );
            }
        });



        sendToken = findViewById(R.id.send_phone);
        sendToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String strMsg = "";
                EditText editText = (EditText)findViewById(R.id.phone_edit);
                String sValue = editText.getText().toString();
                if(sValue.length() == 0) {
                    strMsg = getResources().getString(R.string.user_verification_text_1);
                    Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(SetupActivity.this);
                strMsg = getResources().getString(R.string.user_verification_text_2);
                builder.setTitle(strMsg);
                strMsg = getResources().getString(R.string.user_verification_text_3_1)
                        + "\n\n" + getResources().getString(R.string.user_verification_text_3_2)
                        + "\n\n" + getResources().getString(R.string.user_verification_text_3_3);
                builder.setMessage(strMsg);

                builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String strMsg = "";
                        String phoneNumber = editText.getText().toString();
                        phoneNumber = phoneNumber.replace(" ", "");
                        phoneNumber = phoneNumber.replace("-", "");
                        if (phoneNumber.startsWith("+82")) {
                            phoneNumber = phoneNumber.replace("+82", "0");
                        }
                        if (phoneNumber.startsWith("+")) {
                            phoneNumber = phoneNumber.replace("+", "");
                        }
                        phoneNumber = phoneNumber.replace("+", "");
                        ((MainActivity)MainActivity.context_main).phoneNumber = phoneNumber;
                        ((MainActivity)MainActivity.context_main).RequestAlertList(3);

                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        strMsg = getResources().getString(R.string.user_verification_text_4);
                        Toast.makeText(mContext, strMsg, Toast.LENGTH_SHORT).show();
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);

                        SQLiteDatabase sqlDB;
                        sqlDB = ((MainActivity)MainActivity.context_main).dbHelper.getWritableDatabase();
                        sqlDB.execSQL("DELETE FROM gfsm_phone");
                        sqlDB.execSQL("INSERT INTO gfsm_phone VALUES ('" + ((MainActivity)MainActivity.context_main).phoneNumber + "')");
                        sqlDB.close();
                    }
                });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //ActivityCompat.finishAffinity(MainActivity.this); // 액티비티를 종료하고
                        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                    }
                });
                builder.create().show();
            }
        });


        closeBtn = findViewById(R.id.closeButton);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("화재", mAlert1.isChecked());
                intent.putExtra("가스", mAlert2.isChecked());
                intent.putExtra("감시", mAlert3.isChecked());
                intent.putExtra("단선", mAlert4.isChecked());
                intent.putExtra("복구", true);//mAlert5.isChecked());
                intent.putExtra("서비스", mAlert.isChecked());

                intent.putExtra("hour", mHour);
                intent.putExtra("min", mMinute);
                intent.putExtra("end hour", mEndHour);
                intent.putExtra("end min", mEndMinute);
                intent.putExtra("use time", mTimeCheck.isChecked());
                if(mNormal.isChecked()) { mFontSize = 0;}
                else if(mBig.isChecked()) { mFontSize = 1;}
                else if(mBigger.isChecked()) { mFontSize = 2;}
                intent.putExtra("font size", mFontSize);

                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent();
        intent.putExtra("화재", mAlert1.isChecked());
        intent.putExtra("가스", mAlert2.isChecked());
        intent.putExtra("감시", mAlert3.isChecked());
        intent.putExtra("단선", mAlert4.isChecked());
        intent.putExtra("복구", true);//mAlert5.isChecked());
        intent.putExtra("서비스", mAlert.isChecked());

        intent.putExtra("hour", mHour);
        intent.putExtra("min", mMinute);
        intent.putExtra("end hour", mEndHour);
        intent.putExtra("end min", mEndMinute);
        intent.putExtra("use time", mTimeCheck.isChecked());
        if(mNormal.isChecked()) { mFontSize = 0;}
        else if(mBig.isChecked()) { mFontSize = 1;}
        else if(mBigger.isChecked()) { mFontSize = 2;}
        intent.putExtra("font size", mFontSize);

        setResult(Activity.RESULT_OK, intent);
        finish();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 101) // start time
        {
            mHour = data.getIntExtra("time", 0);
            mMinute = data.getIntExtra("minute", 0);
            String sTime;
            sTime = String.format("%02d:%02d", mHour, mMinute);
            startTime.setText(sTime);
        }
        else if(requestCode == 102) // end time
        {
            mEndHour = data.getIntExtra("time", 0);
            mEndMinute = data.getIntExtra("minute", 0);
            String sTime;
            sTime = String.format("%02d:%02d",  mEndHour, mEndMinute);
            endTime.setText(sTime);
        }
    }

    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------




    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------
}
