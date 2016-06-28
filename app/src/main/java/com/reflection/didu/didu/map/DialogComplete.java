package com.reflection.didu.didu.map;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.reflection.didu.didu.R;

/**
 * Created by penguin on 6/27/16.
 */
public class DialogComplete extends DialogFragment implements View.OnClickListener, Runnable{
    private String PREFS_NAME = "PrefsSetting";
    SharedPreferences settings;
    Thread thread;
    TimeUpListener listener;
    private MediaPlayer mediaPlayer;
    private Vibrator vibrator;
    private long[] pattern = {1000,1000,1000,2000};
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        listener = (TimeUpListener)this.getActivity();
        View view = inflater.inflate(R.layout.dialog_complete,container);
        ImageView bnRemind, bnComplete;
        TextView textView;
        bnRemind = (ImageView)view.findViewById(R.id.bn_remind);
        bnComplete = (ImageView)view.findViewById(R.id.bn_complete);
        textView = (TextView)view.findViewById(R.id.complete_content);
        settings = getActivity().getSharedPreferences(PREFS_NAME,0);
        mediaPlayer = new MediaPlayer();
        vibrator = (Vibrator)getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        try {
            mediaPlayer.setDataSource(getActivity(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        }catch (Exception e){
            e.printStackTrace();
        }

        if(settings.getBoolean("ring",true)){
            ringOn();
        }
        if(settings.getBoolean("vibrate",true)){
            vibrateOn();
        }

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Bundle bundle = getArguments();
        textView.setText(bundle.getString("test"));

        bnRemind.setOnClickListener(this);
        bnComplete.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.bn_remind:
                bnFuncRemind();
                break;
            case R.id.bn_complete:
                bnFuncComplete();
                break;
            default:
                break;
        }
    }


    //再次提醒按钮事件
    private void bnFuncRemind(){
        //n分钟之后再弹出
        dismiss();
        ringStop();
        vibrateStop();
        thread = new Thread(this);
        thread.start();
    }

    //完成按钮事件
    private void bnFuncComplete(){
        //更新数据，将该记录移植已完成事件
        ringStop();
        vibrateStop();
        dismiss();
    }


    @Override
    public void run() {
        try{
            int timeInterval = settings.getInt("time", 5);
            Thread.sleep(1000*60*timeInterval);
            listener.onTimeUp();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public interface TimeUpListener{
        void onTimeUp();
    }

    //开始响铃
    private void ringOn(){
        try {
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }

        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    //停止响铃
    private void ringStop(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }

    //开始震动
    private void vibrateOn(){
        vibrator.vibrate(pattern, 0);
    }

    //停止震动
    private void vibrateStop(){
        vibrator.cancel();
    }
}
