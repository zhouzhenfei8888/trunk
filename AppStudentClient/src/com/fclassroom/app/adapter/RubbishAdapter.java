package com.fclassroom.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.fclassroom.appstudentclient.R;

import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 2015/3/9.
 * rubbishFragment中listview的适配器
 */
public class RubbishAdapter extends BaseAdapter{
    List<HashMap<String,Object>> list;
    Context context;
    LayoutInflater inflater;
    private static boolean moreisclose = true;
    public RubbishAdapter(Context context,List list) {
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
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ListItemView listItemView = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_itemrubbish, null);
            listItemView = new ListItemView();
            listItemView.DateTime = (TextView) convertView.findViewById(R.id.tv_date_listviewitem);
            listItemView.examFrom = (TextView)convertView.findViewById(R.id.tv_from);
            listItemView.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar_listviewitem);
            listItemView.subject = (ImageView) convertView.findViewById(R.id.iv_subject);
            listItemView.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_choice);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        listItemView.DateTime.setText((String)list.get(position).get("examdate"));
        listItemView.subject.setImageResource((int)list.get(position).get("examsrc"));
        listItemView.examFrom.setText((String)list.get(position).get("examname"));
        listItemView.ratingBar.setRating((float)list.get(position).get("rating"));
        listItemView.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return convertView;
    }

    static class ListItemView {
        TextView DateTime;
        TextView examFrom;
        RatingBar ratingBar;
        ImageView subject;
        CheckBox checkBox;
    }
}
