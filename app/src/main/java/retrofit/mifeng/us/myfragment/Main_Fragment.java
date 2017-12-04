package com.wgm.scaneqinfo.MyFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.wgm.scaneqinfo.R;
import com.wgm.scaneqinfo.common.MyCarousel;
import com.wgm.scaneqinfo.conf.Api;
import com.wgm.scaneqinfo.conf.App;
import com.wgm.scaneqinfo.entity.User;
import com.wgm.scaneqinfo.operate.MainOperate;
import com.wgm.scaneqinfo.MyAdapter.AppAdapter;
import com.wgm.scaneqinfo.util.DialogUtil;
import com.wgm.scaneqinfo.view.Voyage_lActivity;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static android.view.KeyEvent.KEYCODE_ENTER;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by apple on 2017/11/16.
 */

public class Main_Fragment extends Fragment implements View.OnClickListener{

    private EditText search;
    private ConvenientBanner id_cb;
    private Button Ibtn_getorder;
    private Button Ibtn_willget;
    private Button Ibtn_allorder;
    private ListView listView;
    private ImageButton picup;
    private LinearLayout piclay;
    private Boolean issee = false;

    private MainOperate mainOperate = new MainOperate();

    private View view;

    private Context ctext;

    public Main_Fragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = LayoutInflater.from(getActivity()).inflate(R.layout.activity_main_frame,container,false);

        piclay = view.findViewById(R.id.piclay);
        search = view.findViewById(R.id.search);
        search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(keyCode == KEYCODE_ENTER){

                    return true;
                }
                return false;
            }
        });

        id_cb =view.findViewById(R.id.id_cb);

        Ibtn_getorder = view.findViewById(R.id.getorder);
        Ibtn_getorder.setOnClickListener(this);
        Ibtn_willget = view.findViewById(R.id.willget);
        Ibtn_willget.setOnClickListener(this);
        Ibtn_allorder = view.findViewById(R.id.allorder);
        Ibtn_allorder.setOnClickListener(this);
        picup = view.findViewById(R.id.picup);
        picup.setOnClickListener(this);

        listView = view.findViewById(R.id.listview);

        initconvenientcanner();
        initlistview(0);
        return view;
    }

    @Override
    public void onClick(View v) {
        if (Ibtn_getorder == v){
            initlistview(0);
        }else if (Ibtn_willget == v){
            initlistview(1);
        }else if (Ibtn_allorder == v){
            initlistview(2);
        }else if(picup == v){
            if(issee){
                piclay.setVisibility(VISIBLE);
                issee = false;
            }else {
                piclay.setVisibility(GONE);
                issee = true;
            }

        }
    }

    protected void initconvenientcanner(){

        id_cb.setPages(new CBViewHolderCreator<MyCarousel>() {
            @Override
            public MyCarousel createHolder() {
                return new MyCarousel();
            }
        }, Arrays.asList(Api.images));
        id_cb.setPointViewVisible(true);
        id_cb.startTurning(2000);
        id_cb.setPageIndicator(new int[]{R.drawable.potin, R.drawable.inpotin});
        id_cb.setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL);
        id_cb.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                DialogUtil.showAlertMsg(view.getContext(), "测试");
            }
        });
        id_cb.setManualPageable(true);


        //设置翻页的效果，不需要翻页效果可用不设
        //setPageTransformer(Transformer.DefaultTransformer);   // 集成特效之后会有白屏现象，新版已经分离，如果要集成特效的例子可以看Demo的点击响应。


    }

    protected void initlistview(int i){

        //初始化list
        Map list_map = new HashMap();
        App app = (App)ctext;
        User user = app.getUser();
        list_map.put("method","getorder");
        list_map.put("type",String.valueOf(i));
        list_map.put("token", user.getToken());
        list_map.put("userid",user.getUser_id());
        mainOperate.asyncRequest(list_map,Api.BASE_URL,new MainOperate.AsyncRequestCallBack(){
            @Override
            public void callBack(){
                if(!mainOperate.getSuccess()){
                    DialogUtil.showAlertMsg(view.getContext(), "网络链接异常");
                    return;
                }else{
                    listView.setAdapter(null);
                    JSONArray jsonArray =mainOperate.getResponse().optJSONObject("resParam").optJSONArray("param");
                    final AppAdapter appAdapter =new AppAdapter(view.getContext(),jsonArray);
                    listView.setAdapter(appAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String str = appAdapter.getData().optJSONObject(position).optString("voyage_id");
                            Intent intent =new Intent(view.getContext(),Voyage_lActivity.class);
                            intent.putExtra("voyage_id",str);
                            startActivity(intent);
                        }
                    });
                }
            }
        });

    }

    public static Main_Fragment newInstance(Bundle args, Context ctext) {
        Main_Fragment f = new Main_Fragment();
        f.ctext = ctext;
        f.setArguments(args);

        return f;
    }

}
