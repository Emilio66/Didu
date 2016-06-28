package com.reflection.didu.didu.map;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.reflection.didu.didu.R;

/**
 * Created by penguin on 6/27/16.
 */
public class DialogInput extends DialogFragment implements View.OnClickListener{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_input,container);
        ImageView bnDelete, bnCommit;

        bnDelete = (ImageView)view.findViewById(R.id.bn_delete);
        bnCommit = (ImageView)view.findViewById(R.id.bn_commit);

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

        dismiss();
    }

}
