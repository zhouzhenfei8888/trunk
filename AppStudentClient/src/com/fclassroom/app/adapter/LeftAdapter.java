package com.fclassroom.app.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/3/3.
 */
public class LeftAdapter extends BaseAdapter {
    Context context;
    List<HashMap<String, Object>> list;
    LayoutInflater inflater;
    public LeftAdapter(Context context,List<HashMap<String, Object>> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.layout_leftmenu,null);
            holder = new Holder();
            holder.iv = (ImageView)convertView.findViewById(R.id.iv_leftmenu);
            holder.tv = (TextView)convertView.findViewById(R.id.tv_leftmenu);
            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }
        HashMap<String,Object> data = list.get(position);
        holder.iv.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), (int)(data.get("ivTitle")) ));
        holder.tv.setText((String)(data.get("tvTitle")));
        return convertView;
    }

    static class Holder{
        ImageView iv;
        TextView tv;
    }
}
