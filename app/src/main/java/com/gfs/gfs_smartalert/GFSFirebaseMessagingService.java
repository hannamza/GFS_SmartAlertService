package com.gfs.gfs_smartalert;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class GFSFirebaseMessagingService extends FirebaseMessagingService {
    String fieldSperate = "x";
    String [] Field;

    String strAction;
    String strType;
    String strDateTime;
    String strAddress;
    String strContext;
    String strTitle;
    String message;

    @Override
    public void onNewToken(String s)
    {
        super.onNewToken(s);
        Log.d("FCM_TOKEN", s);
    }

    private void sendMessage(String message) {
        Intent intent = new Intent("AlertServiceFilter");
        intent.putExtra("title", strTitle);
        intent.putExtra("address", strAddress);
        intent.putExtra("action", strAction);
        intent.putExtra("datetime", strDateTime);
        intent.putExtra("context", strContext);
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static boolean GetType(String strType) {
        if(strType.equals("D")) {
            return true;
        }
        else if(strType.equals("A")) {
            return false;
        }
        else {
            return false;
        }
    }

    public static String GetTypeName(String strType)  {
        if (strType.equals("00")) {
            return "화재";
        }
        else if (strType.equals("01")) {
            return "가스";
        }
        else if (strType.equals("02")) {
            return "감시";
        }
        else if (strType.equals("03")) {
            return "단선";
        }

        return "";
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String rMessage = remoteMessage.getData().toString();
        //rMessage = "Ax00x2021/08/25 15:24:30x00-00-1-012x0번수신기 0유닛  1계통 AN-광전식 012";
        Map<String, String> data = remoteMessage.getData();
        String contents = data.get("contents");
        strTitle = data.get("title");
        String content = data.get("content");
        String rawData = data.get("event");
        boolean bAlert = false;
        String decodeData = "";
        try {
            decodeData = URLDecoder.decode(rawData, "UTF-8");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if(MainActivity.context_main == null){
            return;
        }

        if(decodeData.substring(0, 1).equals("R"))
        {
            /*long now = System.currentTimeMillis();
            Date date = new Date(now);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");*/
            Field = decodeData.split(fieldSperate);

            strTitle = "복구";
            rMessage = "복구 완료";
            strAddress = "";
            strAction = "";
            //strDateTime = dateFormat.format(date);
            strDateTime = Field[1];
            strContext = "복구 완료";

            bAlert = ((MainActivity) MainActivity.context_main).mAlert5;
        }
        else {
            message = decodeData.toString();

            Field = decodeData.split(fieldSperate);
            strAction = Field[0];
            strType = Field[1];
            strDateTime = Field[2];
            strAddress = Field[3];
            if (message.endsWith("x")) {
                strContext = "";
            } else {
                strContext = Field[4];
            }
            strTitle = GetTypeName(strType);
            switch (strTitle) {
                case "화재":
                    bAlert = ((MainActivity) MainActivity.context_main).mAlert1;
                    break;
                case "가스":
                    bAlert = ((MainActivity) MainActivity.context_main).mAlert2;
                    break;
                case "감시":
                    bAlert = ((MainActivity) MainActivity.context_main).mAlert3;
                    break;
                case "단선":
                    bAlert = ((MainActivity) MainActivity.context_main).mAlert4;
                    break;
            }
            strTitle += " 발생";
        }
        boolean bUseTime = ((MainActivity) MainActivity.context_main).mUseTime;
        int nHour = ((MainActivity) MainActivity.context_main).mHour;
        int nEndHour = ((MainActivity) MainActivity.context_main).mEndHour;
        int nMin = ((MainActivity) MainActivity.context_main).mMinute;
        int nEndMin = ((MainActivity) MainActivity.context_main).mEndMinute;

        int nStartTime = (nHour * 60) + nMin;
        int nEndTime = (nEndHour * 60) + nEndMin;

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int iUnitHour = cal.get(Calendar.HOUR_OF_DAY);
        int iUnitMinute = cal.get(Calendar.MINUTE);

        int nCurrentTime = (iUnitHour * 60) + iUnitMinute;


        ((MainActivity) MainActivity.context_main).RequestAlertList(0);

        if(!((MainActivity) MainActivity.context_main).mUseService){
            return;
        }

        if(!bAlert)
        {
            return;
        }
        if(bUseTime){
            if( (nCurrentTime < nStartTime) || (nCurrentTime > nEndTime) ){
                return;
            }
        }

        ((MainActivity) MainActivity.context_main).acquireWakeLock((MainActivity) MainActivity.context_main);

        if(true){
            return;
        }

        /*String title = remoteMessage.getData().get("title");//firebase에서 보낸 메세지의 title
        String message = remoteMessage.getData().get("message");//firebase에서 보낸 메세지의 내용
        String test = remoteMessage.getData().get("test");*/

        /*Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("title", strTitle);
        intent.putExtra("message", rMessage);
        intent.putExtra("address", strAddress);
        intent.putExtra("action", strAction);
        intent.putExtra("datetime", strDateTime);
        intent.putExtra("context", strContext);*/
        sendMessage(rMessage);

        message = strTitle + " " + strDateTime + " " + strAddress + " [" + strContext + "]";

        if(!((MainActivity) MainActivity.context_main).mUseService){
            return;
        }

        if(!bAlert)
        {
            return;
        }
        if(bUseTime){
            if( (nCurrentTime < nStartTime) || (nCurrentTime > nEndTime) ){
                return;
            }
        }

        ((MainActivity) MainActivity.context_main).acquireWakeLock((MainActivity) MainActivity.context_main);
        ((MainActivity) MainActivity.context_main).RequestAlertList(0);

        String msg;
        msg = strDateTime + "\r\n" + strContext;
        if(((MainActivity) MainActivity.context_main).mStart == false) {
            showNotification(strTitle, msg);
        }
        else{
            beepNotification();
            vibratorNotification();
        }

        /*PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String channel = "채널";
            String channel_nm = "채널명";

            NotificationManager notichannel = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channelMessage = new NotificationChannel(channel, channel_nm,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channelMessage.setDescription("채널에 대한 설명.");
            channelMessage.enableLights(true);
            channelMessage.enableVibration(true);
            channelMessage.setShowBadge(false);
            channelMessage.setVibrationPattern(new long[]{1000, 1000});
            notichannel.createNotificationChannel(channelMessage);

            //푸시알림을 Builder를 이용하여 만듭니다.
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, channel)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            //.setContent(getCustomDesign(strTitle, message))
                            .setContentTitle(strTitle)//푸시알림의 제목
                            .setContentText(message)//푸시알림의 내용
                            .setChannelId(channel)
                            .setAutoCancel(true)//선택시 자동으로 삭제되도록 설정.
                            .setContentIntent(pendingIntent)//알림을 눌렀을때 실행할 인텐트 설정.
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int iUnitSecond = cal.get(Calendar.SECOND);
            int nId = (iUnitHour * 60 * 60) + (iUnitMinute * 60) + iUnitSecond;
            notificationManager.notify(nId, notificationBuilder.build());

        } else {
            NotificationCompat.Builder notificationBuilder =
                    new NotificationCompat.Builder(this, "")
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(strTitle)
                            .setContentText(message)
                            .setAutoCancel(true)
                            .setContentIntent(pendingIntent)
                            .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            int iUnitSecond = cal.get(Calendar.SECOND);
            int nId = (iUnitHour * 60 * 60) + (iUnitMinute * 60) + iUnitSecond;
            notificationManager.notify(nCurrentTime, notificationBuilder.build());
        }*/
    }

    private void beepNotification(){
        Vibrator vi = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        vi.vibrate(200);
    }
    private void vibratorNotification(){
        Uri beep = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone rt = RingtoneManager.getRingtone(getApplicationContext(), beep);
        rt.play();
    }

    private RemoteViews getCustomDesign(String title, String message) {
        RemoteViews remoteViews = new RemoteViews(getApplicationContext().getPackageName(), R.layout.alarm_popup);
        remoteViews.setTextViewText(R.id.title, title);
        remoteViews.setTextViewText(R.id.message, message);
        remoteViews.setImageViewResource(R.id.logo, R.drawable.logo);
        return remoteViews;
    }

    public void showNotification(String title, String message) {
        //팝업 터치시 이동할 액티비티를 지정합니다.
        Intent intent = new Intent(this, MainActivity.class);
        //알림 채널 아이디 : 본인 하고싶으신대로...

        String channel_id = "GFSM_CHANNEL";
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //기본 사운드로 알림음 설정. 커스텀하려면 소리 파일의 uri 입력
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channel_id)
                .setSmallIcon(R.drawable.logo)
                .setSound(uri)
                .setAutoCancel(true)
                //.setVibrate(new long[]{1000, 1000, 1000}) //알림시 진동 설정 : 1초 진동, 1초 쉬고, 1초 진동
                //.setOnlyAlertOnce(true) //동일한 알림은 한번만.. : 확인 하면 다시 울림
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                .setContentIntent(pendingIntent);

       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) { //안드로이드 버전이 커스텀 알림을 불러올 수 있는 버전이면
            //커스텀 레이아웃 호출
            builder = builder.setContent(getCustomDesign(title, message));
        } else { //아니면 기본 레이아웃 호출*/
            builder = builder.setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(R.drawable.logo); //커스텀 레이아웃에 사용된 로고 파일과 동일하게..
        //}

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //알림 채널이 필요한 안드로이드 버전을 위한 코드
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "스마트 알림 서비스", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri, null);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //알림 표시 !
        notificationManager.notify(0, builder.build());
    }
}