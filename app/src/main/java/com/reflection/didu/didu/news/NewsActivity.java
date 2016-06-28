package com.reflection.didu.didu.news;

import android.app.TabActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.reflection.didu.didu.R;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends TabActivity{
    private TabHost tabHost;
    private ImageView back;

    private ListView lvUnfinished, lvFinished;
    private MyAdapter unfinishedAdapter, finishedAdapter;
    private List<String> dataUnfinished, dataFinished;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_news);

        initUI();
        addTab();


    }

    private void initUI(){
        tabHost = getTabHost();
        back = (ImageView)findViewById(R.id.bn_back_icon);
        lvUnfinished = (ListView)findViewById(R.id.ls_unfinished);
        lvFinished = (ListView)findViewById(R.id.ls_finished);
        dataUnfinished = getDataUnfinished();
        dataFinished = getDataFinished();

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                upDateTab(tabHost);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        unfinishedAdapter = new MyAdapter(this, dataUnfinished);
        //unfinishedAdapter.setOnDeleteItemLisener(this);
        lvUnfinished.setAdapter(unfinishedAdapter);

        finishedAdapter = new MyAdapter(this,dataFinished);
        lvFinished.setAdapter(finishedAdapter);

        //lvUnfinished.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, dataUnfinished));
        //lvFinished.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,dataFinished));
    }

    //添加tab对应的的内容
    private void addTab(){
        tabHost.addTab(tabHost.newTabSpec("finished")
                .setIndicator(getResources().getString(R.string.unfinished)).setContent(R.id.ls_unfinished));
        tabHost.addTab(tabHost.newTabSpec("unfinished")
                .setIndicator(getResources().getString(R.string.finished)).setContent(R.id.ls_finished));

        tabHost.setCurrentTab(0);
        upDateTab(tabHost);
    }

    //切换“当前事件“和”历史事件“时改变颜色
    private void upDateTab(TabHost tabHost){
        for(int i = 0; i < tabHost.getTabWidget().getChildCount(); i ++){
            View view = tabHost.getTabWidget().getChildAt(i);
            TextView textView = (TextView)view.findViewById(android.R.id.title);
            textView.setTextSize(15);
            if(tabHost.getCurrentTab() == i){
                textView.setTextColor(getResources().getColor(R.color.purple));
            }else{
                textView.setTextColor(getResources().getColor(R.color.gray));
            }
        }
    }


    //测试数据
    private List<String> getDataUnfinished(){
        List<String> data = new ArrayList<String>();
        data.add("未完成事件1");
        data.add("未完成事件2");
        data.add("未完成事件3");
        data.add("未完成事件4");
        data.add("未完成事件5");

        return data;
    }

    private List<String> getDataFinished(){
        List<String> data = new ArrayList<String>();
        data.add("完成事件1");
        data.add("完成事件2");
        data.add("完成事件3");

        return data;
    }

//    @Override
//    public void OnDeleteItem(int position) {
//        Log.e("position", position + "");
//        dataUnfinished.remove(position);
//        unfinishedAdapter.notifyDataSetChanged();
//    }
}
