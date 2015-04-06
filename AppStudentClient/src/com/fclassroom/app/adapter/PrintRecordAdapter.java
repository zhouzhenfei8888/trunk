package com.fclassroom.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.PrintRecoderBean;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/3/10.
 */
public class PrintRecordAdapter extends BaseAdapter {

    private List<PrintRecoderBean> list;
    private Context context;
    private LayoutInflater inflater;
    private int res;
    public PrintRecordAdapter(Context context,int res,List<PrintRecoderBean> list){
        this.context = context;
        this.res = res;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder = null;
        if(convertView ==null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.listview_itemprintrecord,parent,false);
            holder.imageView = (ImageView)convertView.findViewById(R.id.iv_itemprintrecord);
            holder.totalNum = (TextView)convertView.findViewById(R.id.tv_itemtotal);
            holder.printDate = (TextView)convertView.findViewById(R.id.tv_itemdate);
            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }
        PrintRecoderBean data = list.get(position);
        holder.totalNum.setText("共"+data.getExamQuestionCount()+"题");
        holder.printDate.setText(data.getCreateTime());
        return convertView;
    }

    class Holder{
        ImageView imageView;
        TextView totalNum;
        TextView printDate;
    }
}
