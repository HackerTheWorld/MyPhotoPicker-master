package com.wgm.scaneqinfo.view;

/**
 * Created by apple on 2017/11/7.
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.foamtrace.photopicker.ImageCaptureManager;
import com.foamtrace.photopicker.PhotoPickerActivity;
import com.foamtrace.photopicker.PhotoPreviewActivity;
import com.foamtrace.photopicker.SelectModel;
import com.foamtrace.photopicker.intent.PhotoPickerIntent;
import com.foamtrace.photopicker.intent.PhotoPreviewIntent;
import com.wgm.scaneqinfo.R;
import com.wgm.scaneqinfo.common.HttpFileUpTool;
import com.wgm.scaneqinfo.conf.Api;
import com.wgm.scaneqinfo.conf.App;
import com.wgm.scaneqinfo.entity.User;
import com.wgm.scaneqinfo.util.DialogUtil;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainPhotoActivity extends BaseActivity implements View.OnClickListener {
    private int columnWidth;
    private ArrayList<String> imagePaths = null;
    private GridAdapter gridAdapter;
    private GridView gv;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private static final int REQUEST_CAMERA_CODE = 11;
    private HttpFileUpTool httpFileUpTool =new HttpFileUpTool();
    private String id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        id = getIntent().getStringExtra("goodid");
        Button duoxuan = (Button) findViewById(R.id.duoxuan);
        duoxuan.setOnClickListener(this);
        Button upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(this);

        gv = (GridView) findViewById(R.id.gv);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] str = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(this,
                    str, 1);
        }



        //得到GridView中每个ImageView宽高
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        cols = cols < 3 ? 3 : cols;
        gv.setNumColumns(cols);
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
        columnWidth = (screenWidth - columnSpace * (cols-1)) / cols;


        //GridView item点击事件（浏览照片）
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PhotoPreviewIntent intent = new PhotoPreviewIntent(MainPhotoActivity.this);
                intent.setCurrentItem(position);
                intent.setPhotoPaths(imagePaths);
                startActivityForResult(intent, 22);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //多选图片
            case R.id.duoxuan:

                PhotoPickerIntent intent1 = new PhotoPickerIntent(MainPhotoActivity.this);
                intent1.setSelectModel(SelectModel.MULTI);
                intent1.setShowCarema(true); // 是否显示拍照
                intent1.setMaxTotal(100); // 最多选择照片数量，默认为9
                intent1.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent1, REQUEST_CAMERA_CODE);
                break;
            case R.id.upload:
                //App app = (App)getApplication();
                //User user =app.getUser();


                Map params=new HashMap();
               //params.put("method","uploadhead");
                params.put("good_id",id);
                params.put("name",getimgname().toString());
                //params.put("imgname",getimgname());
                params.put("userid","4");
                //params.put("token",user.getToken());
                Map<String,File> files = new HashMap<>();
                for (int i=0;i<imagePaths.size();i++) {
                    files.put(String.valueOf(i), new File(imagePaths.get(i)));
                }
                httpFileUpTool.asyncRequestImg("http://www.oldgerry.com/jnapp/action/File/panuploadimg",params,files, new HttpFileUpTool.AsyncRequestCallBack() {
                    @Override
                    public void callBack() {
                        DialogUtil.showAlertMsg(MainPhotoActivity.this,"上传成功");
                    }
                });
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                // 选择照片
                case REQUEST_CAMERA_CODE:
                    loadAdpater(data.getStringArrayListExtra(PhotoPickerActivity.EXTRA_RESULT));
                    break;
                //浏览照片
                case 22:
                    loadAdpater(data.getStringArrayListExtra(PhotoPreviewActivity.EXTRA_RESULT));
                    break;
                // 调用相机拍照
                case ImageCaptureManager.REQUEST_TAKE_PHOTO:
                    if(captureManager.getCurrentPhotoPath() != null) {
                        captureManager.galleryAddPic();

                        ArrayList<String> paths = new ArrayList<>();
                        paths.add(captureManager.getCurrentPhotoPath());
                        loadAdpater(paths);
                    }
                    break;
            }
        }
    }

    private void loadAdpater(ArrayList<String> paths){
        if(imagePaths == null){
            imagePaths = new ArrayList<>();
        }
        imagePaths.clear();
        imagePaths.addAll(paths);
        if(gridAdapter == null){
            gridAdapter = new GridAdapter(imagePaths);
            gv.setAdapter(gridAdapter);
        }else {
            gridAdapter.notifyDataSetChanged();
        }
    }


    private JSONArray getimgname(){
        JSONArray jsonArray = new JSONArray();
        for (int i = 0;i<gv.getCount();i++){
            EditText editText = gv.getChildAt(i).findViewById(R.id.imgname);
            jsonArray.put(String.valueOf(editText.getText()));
        }
        return jsonArray;
    }


    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        ImageView imageView;
        EditText editText;
        public GridAdapter(ArrayList<String> listUrls) {
            this.listUrls = listUrls;
        }

        @Override
        public int getCount() {
            return listUrls.size();
        }

        @Override
        public String getItem(int position) {
            return listUrls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                editText = (EditText)convertView.findViewById(R.id.imgname);
                convertView.setTag(imageView);
                // 重置ImageView宽高
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
                imageView.setLayoutParams(params);
            }else {
                imageView = (ImageView) convertView.getTag();
            }
            //框架里自带glide
            Glide.with(MainPhotoActivity.this)
                    .load(new File(getItem(position)))
                    .placeholder(R.mipmap.default_error)
                    .error(R.mipmap.default_error)
                    .centerCrop()
                    .crossFade()
                    .into(imageView);
            return convertView;
        }
    }

}