package com.reflection.didu.didu.setting;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lukedeighton.wheelview.WheelView;
import com.lukedeighton.wheelview.adapter.WheelAdapter;
import com.reflection.didu.didu.R;

public class SettingActivity extends Activity implements View.OnClickListener{
    private LinearLayout layout;
    private ImageView bnTime, bnBack;
    private ImageView bnRing, bnVirate;
    private TextView timeSetting;
    private int timeInterval;
    private WheelView wheelView;
    private boolean flagRing, flagVibrate;


    private String PREFS_NAME = "PrefsSetting";

    SharedPreferences settings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_setting);
        initUI();
    }

    private void initUI(){
        layout = (LinearLayout)findViewById(R.id.layout_setting);
        bnTime = (ImageView)findViewById(R.id.bn_time);
        bnBack = (ImageView)findViewById(R.id.bn_back_setting);
        bnRing = (ImageView)findViewById(R.id.bn_ring);
        bnVirate = (ImageView)findViewById(R.id.bn_vibrate);
        timeSetting = (TextView)findViewById(R.id.time_setting);
        wheelView = (WheelView)findViewById(R.id.wheelview);

        layout.setOnClickListener(this);
        bnTime.setOnClickListener(this);
        bnBack.setOnClickListener(this);
        bnRing.setOnClickListener(this);
        bnVirate.setOnClickListener(this);
        wheelView.setVisibility(View.INVISIBLE);

        settings = getSharedPreferences(PREFS_NAME,0);
        flagRing = settings.getBoolean("ring", true);
        flagVibrate = settings.getBoolean("vibrate", true);
        timeInterval = settings.getInt("time", 5);
        setBnRing();
        setBnVirate();


        timeSetting.setText("每" + timeInterval + "分钟");

        wheelView.setAdapter(new WheelAdapter() {
            @Override
            public Drawable getDrawable(int position) {
                return new TextDrawable((position + 1) + "");
            }

            @Override
            public int getCount() {
                return 15;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }
        });


        wheelView.setOnWheelItemSelectedListener(new WheelView.OnWheelItemSelectListener() {
            @Override
            public void onWheelItemSelected(WheelView parent, Drawable itemDrawable, int position) {
                //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putInt("time", position + 1);
                editor.commit();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_setting:
                getTime();
                break;
            case R.id.bn_back_setting:
                back();
                break;
            case R.id.bn_ring:
                changeRing();
                break;
            case R.id.bn_vibrate:
                changeVibrate();
                break;
            case R.id.bn_time:
                setTime();
                break;
            default:
                break;
        }
    }

    //返回按钮
    private void back(){
        finish();
    }

    //改变响铃按钮
    private void changeRing(){
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        if(flagRing){
            bnRing.setImageDrawable(getResources().getDrawable(R.drawable.icon_ring_closed));
            flagRing = false;
        }else{
            bnRing.setImageDrawable(getResources().getDrawable(R.drawable.icon_ring));
            flagRing = true;
        }

        editor.putBoolean("ring",flagRing);
        editor.commit();
    }


    //改变震动按钮
    private void changeVibrate(){
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();

        if (flagVibrate){
            bnVirate.setImageDrawable(getResources().getDrawable(R.drawable.icon_vibrate_closed));
            flagVibrate = false;
        }else{
            bnVirate.setImageDrawable(getResources().getDrawable(R.drawable.icon_vibrate));
            flagVibrate=true;
        }

        editor.putBoolean("vibrate",flagVibrate);
        editor.commit();
    }


    //设置时间间隔
    private void setTime(){
        if(wheelView.getVisibility() == View.INVISIBLE){
            wheelView.setVisibility(View.VISIBLE);
        }
    }


    //获取时间间隔
    private void getTime(){
        if(wheelView.getVisibility() == View.VISIBLE){
            wheelView.setVisibility(View.INVISIBLE);
            timeSetting.setText("每"+ settings.getInt("time", 1) + "分钟");
        }
    }


    //设置响铃按钮
    private void setBnRing(){
        if(flagRing){
            bnRing.setImageDrawable(getResources().getDrawable(R.drawable.icon_ring));
        }else{
            bnRing.setImageDrawable(getResources().getDrawable(R.drawable.icon_ring_closed));
        }
    }



    //设置震动按钮
    private void setBnVirate(){
        if (flagVibrate){
            bnVirate.setImageDrawable(getResources().getDrawable(R.drawable.icon_vibrate));
        }else{
            bnVirate.setImageDrawable(getResources().getDrawable(R.drawable.icon_vibrate_closed));
        }
    }


}
