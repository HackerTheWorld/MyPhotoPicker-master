package com.wgm.scaneqinfo.MyAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wgm.scaneqinfo.R;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by apple on 2017/11/2.
 */

public class AppAdapter extends BaseAdapter {

    private JSONArray data;
    private LayoutInflater layoutInflater;
    private Context context;

    public JSONArray getData() {
        return data;
    }

    public AppAdapter(Context context, JSONArray data) {

        this.context = context;
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);

    }

    public class Iteminfo{
        public TextView order_num;
        public TextView kuaidi_num;
        public TextView getorder_num;
        public TextView make_box_time;
        public String voyage_id;

    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object getItem(int position) {
        Object obj=null;
        try {
            obj = data.opt(position);
        }catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AppAdapter.Iteminfo iteminfo =null;
        JSONObject item = data.optJSONObject(position);
        if (convertView == null) {
            iteminfo = new AppAdapter.Iteminfo();
            convertView = layoutInflater.inflate(R.layout.activity_item, null);
            iteminfo.order_num = convertView.findViewById(R.id.order_num);
            iteminfo.kuaidi_num = convertView.findViewById(R.id.kuaidi_num);
            iteminfo.getorder_num = convertView.findViewById(R.id.getorder_num);
            iteminfo.make_box_time = convertView.findViewById(R.id.make_box_time);
            convertView.setTag(iteminfo);
        }else {
            iteminfo = (Iteminfo)convertView.getTag();
        }
        iteminfo.voyage_id = item.optString("voyage_id");
        iteminfo.order_num.setText(item.optString("order_num"));
        iteminfo.getorder_num.setText(item.optString("getorder_num"));
        iteminfo.kuaidi_num.setText(item.optString("kuaidi_num"));
        iteminfo.make_box_time.setText(item.optString("make_box_time"));

        return convertView;

    }
}
