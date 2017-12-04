package com.wgm.scaneqinfo.MyFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
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
import com.wgm.scaneqinfo.view.MainPhotoActivity;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

/**
 * Created by apple on 2017/11/16.
 */

public class Person_Fragment extends Fragment implements View.OnClickListener {

    private ImageView headimage;
    private GridView gv;
    private Button uploadpic;
    private Button duoxuan;

    private int columnWidth;
    private ArrayList<String> imagePaths = null;
    private Person_Fragment.GridAdapter gridAdapter;
    private ImageCaptureManager captureManager; // 相机拍照处理类
    private static final int REQUEST_CAMERA_CODE = 11;
    private HttpFileUpTool httpFileUpTool =new HttpFileUpTool();

    private View view;
    private Context ctext;

    public Person_Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_person,container,false);
        headimage = view.findViewById(R.id.headimage);
        duoxuan = view.findViewById(R.id.duoxuan);
        duoxuan.setOnClickListener(this);
        uploadpic = view.findViewById(R.id.upload);
        uploadpic.setOnClickListener(this);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.back);
        RoundedBitmapDrawable circleDrawable = RoundedBitmapDrawableFactory.create(getResources(), BitmapFactory.decodeResource(getResources(), R.drawable.back));
        circleDrawable.getPaint().setAntiAlias(true);
        circleDrawable.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()));
        headimage.setImageDrawable(circleDrawable);

        gv = view.findViewById(R.id.gv);
        if (ContextCompat.checkSelfPermission(ctext,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            String[] str = {Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions((Activity) ctext,
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
                PhotoPreviewIntent intent = new PhotoPreviewIntent(ctext);
                intent.setCurrentItem(position);
                intent.setPhotoPaths(imagePaths);
                startActivityForResult(intent, 22);
            }
        });

    return view;
    }

    public static  Person_Fragment newInstance(Bundle args, Context ctext) {

        Person_Fragment f = new Person_Fragment();
        f.ctext=ctext;
        f.setArguments(args);
        return f;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            //多选图片
            case R.id.duoxuan:

                PhotoPickerIntent intent1 = new PhotoPickerIntent(ctext);
                intent1.setSelectModel(SelectModel.MULTI);
                intent1.setShowCarema(true); // 是否显示拍照
                intent1.setMaxTotal(1); // 最多选择照片数量，默认为9
                intent1.setSelectedPaths(imagePaths); // 已选中的照片地址， 用于回显选中状态
                startActivityForResult(intent1, REQUEST_CAMERA_CODE);
                break;

            case R.id.upload:
                App app = (App)ctext;
                User user =app.getUser();
                JSONArray jsonArray = new JSONArray();

                Map params=new HashMap();
                params.put("method","uploadhead");
                params.put("imgtype","car_id");
                params.put("userid",user.getUser_id());
                params.put("token",user.getToken());
                Map<String,File> files = new HashMap<>();

                files.put("photo",new File(imagePaths.get(0)));
                httpFileUpTool.asyncRequestImg(Api.BASE_URL,params,files, new HttpFileUpTool.AsyncRequestCallBack() {
                    @Override
                    public void callBack() {

                    }
                });
                break;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            gridAdapter = new Person_Fragment.GridAdapter(imagePaths);
            gv.setAdapter(gridAdapter);
        }else {
            gridAdapter.notifyDataSetChanged();
        }
    }

    private class GridAdapter extends BaseAdapter {
        private ArrayList<String> listUrls;
        ImageView imageView;

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

            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_image, null);
                imageView = (ImageView) convertView.findViewById(R.id.imageView);
                convertView.setTag(imageView);
                // 重置ImageView宽高
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(columnWidth, columnWidth);
                imageView.setLayoutParams(params);
            } else {
                imageView = (ImageView) convertView.getTag();
            }
            //框架里自带glide
            Glide.with(Person_Fragment.this)
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
