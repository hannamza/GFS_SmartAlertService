package com.gfs.gfs_smartalert;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.TimePicker;

public class TimePickerPopupEx  extends Activity {
    int hour = 0, minute = 0;
    TimePicker mTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_time);

        Intent intent = getIntent();
        hour = intent.getIntExtra("time", 0);
        minute = intent.getIntExtra("minute", 0);

        mTime = findViewById(R.id.timePicker);
        if (Build.VERSION.SDK_INT >= 23 ) {
            mTime.setHour(hour);
            mTime.setMinute(minute);
        }
        else {
            mTime.setCurrentHour(hour);
            mTime.setCurrentMinute(minute);
        }
        //mTime.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);
    }
    public void mOnClose(View v){
        if (Build.VERSION.SDK_INT >= 23 ) {
            mTime.clearFocus();
            //데이터 전달하기
            hour = mTime.getHour();
            minute = mTime.getMinute();
        }
        else {
            mTime.clearFocus();
            //데이터 전달하기
            hour = mTime.getCurrentHour();
            minute = mTime.getCurrentMinute();
        }

        Intent intent = new Intent();
        intent.putExtra("time", hour);
        intent.putExtra("minute", minute);
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}
