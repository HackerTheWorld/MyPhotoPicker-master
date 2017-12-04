package com.wgm.scaneqinfo.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wgm.scaneqinfo.R;
import com.wgm.scaneqinfo.operate.ShowAllPicOperate;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by apple on 2017/11/30.
 */

public class ShowAllPicActivity extends Activity implements OnClickListener {

    private Button btn_newgoods;
    ShowAllPicOperate showAllPicOperate = new ShowAllPicOperate();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_show_all_pic);
        btn_newgoods = this.findViewById(R.id.newgoods);
        btn_newgoods.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v==btn_newgoods){
            showAlertDialog();
        }
    }

    private void showAlertDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(LayoutInflater.from(this).inflate(R.layout.activity_total, null));
        dialog.show();
        dialog.getWindow().setContentView(R.layout.activity_total);
        Button btnPositive = (Button) dialog.findViewById(R.id.sure);
        final EditText etContent = (EditText) dialog.findViewById(R.id.goodtext);
        btnPositive.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String str = etContent.getText().toString();
                if (isNullEmptyBlank(str)) {
                    etContent.setError("输入内如不能为空");
                } else {
                    final String uuid =  java.util.UUID.randomUUID().toString();
                    Map<String,String> params = new HashMap<String,String>();

                    params.put("id",uuid);
                    params.put("userid","4");
                    params.put("text",str);
                    showAllPicOperate.asyncRequest(params,"http://www.oldgerry.com/jnapp/action/loan/newgoods",new ShowAllPicOperate.AsyncRequestCallBack() {
                        @Override
                        public void callBack() {
                            Intent intent = new Intent(ShowAllPicActivity.this,MainPhotoActivity.class);
                            intent.putExtra("goodid", uuid);
                            startActivity(intent);
                        }
                    });
                    Toast.makeText(ShowAllPicActivity.this, etContent.getText().toString(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private static boolean isNullEmptyBlank(String str) {
        if (str == null || "".equals(str) || "".equals(str.trim()))
            return true;
        return false;
    }

}
