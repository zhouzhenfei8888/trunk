package com.fclassroom.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fclassroom.activity.Fragment.PrintplanFragment;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 2015/3/10.
 */
public class PrintplanAdapter extends BaseAdapter {

    List<PageBean.SubjectItemBean> list;
    LayoutInflater inflater;
    int res;
    Context context;
    private static boolean moreisclose = true;

    public PrintplanAdapter(Context context,int res,List<PageBean.SubjectItemBean> list) {
        this.context = context;
        this.list = list;
        this.res = res;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ListItemView listItemView = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listviewitem, null);
            listItemView = new ListItemView();
            listItemView.DateTime = (TextView) convertView.findViewById(R.id.tv_date_listviewitem);
            listItemView.examname = (TextView) convertView.findViewById(R.id.tv_from);
            listItemView.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar_listviewitem);
            listItemView.subject = (ImageView) convertView.findViewById(R.id.iv_subject);
            listItemView.more = (ImageView) convertView.findViewById(R.id.iv_more);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
//        HashMap<String,Object> data = list.get(position);
//        listItemView.DateTime.setText((String)data.get("examdate"));
//        listItemView.examname.setText((String)data.get("examname"));
//        listItemView.subject.setImageResource((int)data.get("examsrc"));
//        listItemView.ratingBar.setRating((float)data.get("rating"));
        return convertView;
    }

    static class ListItemView {
        TextView DateTime;
        TextView examname;
        RatingBar ratingBar;
        ImageView subject;
        ImageView more;
    }
}
