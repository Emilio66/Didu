package com.reflection.didu.didu.news;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.reflection.didu.didu.R;

import java.util.List;

/**
 * Created by penguin on 6/25/16.
 */
public class MyAdapter extends BaseSwipeAdapter {
    private Context context;
    private List<String> datas;

    public MyAdapter(Context context, List<String> datas){
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swip;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.listview_item,null);
        final SwipeLayout swipeLayout = (SwipeLayout)view.findViewById(R.id.swip);

        view.findViewById(R.id.delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipeLayout.close();
                datas.remove(position);
                notifyDataSetChanged();
                Log.e("position", position + "");
            }
        });

        return view;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView textView = (TextView)convertView.findViewById(R.id.content);
        textView.setText(datas.get(position));
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
