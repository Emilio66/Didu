package com.reflection.didu.didu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.reflection.didu.didu.map.MapActivity;


public class MainActivity extends Activity implements Runnable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Thread thread  = new Thread(this);
        thread.start();
    }

    @Override
    public void run(){
        try{
            Thread.sleep(2000);
            Intent intent = new Intent(MainActivity.this, MapActivity.class);
            startActivity(intent);
            finish();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

}
