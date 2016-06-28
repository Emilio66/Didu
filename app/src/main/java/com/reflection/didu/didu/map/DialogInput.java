package com.reflection.didu.didu.map;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.reflection.didu.didu.R;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by penguin on 6/27/16.
 */
public class DialogInput extends DialogFragment implements View.OnClickListener{
    private EditText reminder;
    private View view;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_input,container);
        ImageView bnDelete, bnCommit;

        bnDelete = (ImageView)view.findViewById(R.id.bn_delete);
        bnCommit = (ImageView)view.findViewById(R.id.bn_commit);
        reminder = (EditText)view.findViewById(R.id.remind_content);

        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        bnDelete.setOnClickListener(this);
        bnCommit.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bn_delete:
                bnFuncDelete();
                break;
            case R.id.bn_commit:
                bnFuncCommit();
                break;
            default:
                break;
        }
    }


    //删除按钮事件
    private void bnFuncDelete(){
        dismiss();
    }


    //提交按钮事件
    private void bnFuncCommit(){
        //更新数据，插入该数据
        //获得文本数据，得到path信息，存入json
        dismiss();
        //提醒输入
        String string = reminder.getEditableText().toString();
        Toast.makeText(getActivity().getApplicationContext(), " \"" + string+" \" 提醒创建成功", Toast.LENGTH_LONG ).show();
        //get path
        Bundle bundle = getArguments();
        try {
            JsonUtil util = new JsonUtil(view.getContext());
            //util.clear();
            util.insert(string,bundle.getString("path"));
        } catch (JSONException e) {
            e.printStackTrace();
        /*} catch (IOException e) {
            e.printStackTrace();*/
        }
    }

}
