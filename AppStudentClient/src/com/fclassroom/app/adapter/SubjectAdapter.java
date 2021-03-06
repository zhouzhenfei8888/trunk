package com.fclassroom.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.Fragment.SubjectFragment;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.URLs;
import com.fclassroom.app.common.BitmapCache;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by Administrator on 2015/3/2.
 */
public class SubjectAdapter extends BaseAdapter {
    ArrayList<PageBean.SubjectItemBean> list;
    LayoutInflater inflater;
    Context context;
    RequestQueue mQueue;
    ImageLoader imageLoader;
    ListView listView;
    private boolean MulMode = false;
    public static HashMap<Integer, Boolean> isSelected;

    public SubjectAdapter(Context context, List<PageBean.SubjectItemBean> stringList, ListView listView) {
        this.list = (ArrayList) stringList;
        this.context = context;
        this.listView = listView;
        inflater = LayoutInflater.from(context);
        isSelected = new HashMap<>();
        mQueue = Volley.newRequestQueue(context.getApplicationContext());
        initData();
    }

    private void initData() {
        for (int i = 0; i < list.size(); i++) {
            isSelected.put(i, false);
        }
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
            listItemView.checkBox = (CheckBox) convertView.findViewById(R.id.checkbox_choice);
            listItemView.difficult = (TextView) convertView.findViewById(R.id.tv_difficulty);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        PageBean.SubjectItemBean data = list.get(position);
        listItemView.subject.setTag(data);
        listItemView.DateTime.setText((String) data.getExamTime());
        listItemView.examname.setText((String) data.getExamName());
        String diffStr = null;
        float diffRecord = Float.parseFloat(data.getScoreRate());
        if (diffRecord <= 0.1) {
            diffStr = "简单";
        } else if (diffRecord <= 0.25) {
            diffStr = "普通";
        } else if (diffRecord <= 0.4) {
            diffStr = "一般难";
        } else if (diffRecord <= 1) {
            diffStr = "难";
        }
        listItemView.difficult.setText("难度："+diffStr);
//        listItemView.subject.setImageResource((int) data.get("examsrc"));
        //图片处理
        imageLoader = new ImageLoader(mQueue, new BitmapCache());
        ImageLoader.ImageListener imageListener = imageLoader.getImageListener(listItemView.subject, R.drawable.default_jpeg, R.drawable.default_jpeg);
        imageLoader.get(URLs.HOST_IMG + data.getContentImage(), imageListener);
        listItemView.ratingBar.setRating((float) data.getSignLevel());
        if (MulMode) {
            listItemView.checkBox.setVisibility(VISIBLE);
            listItemView.checkBox.setFocusable(false);
            if (listView.isItemChecked(position + 1)) {
                listItemView.checkBox.setChecked(true);
            } else {
                listItemView.checkBox.setChecked(false);
            }
        } else {
            listItemView.checkBox.setVisibility(GONE);
            listItemView.checkBox.setFocusable(false);
            listItemView.checkBox.setClickable(false);
        }
        return convertView;
    }

    public void setMulMode(boolean mulMode) {
        this.MulMode = mulMode;
    }

    public static HashMap<Integer, Boolean> getIsSelected() {
        return isSelected;
    }

    public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {
        SubjectAdapter.isSelected = isSelected;
    }

    public class ListItemView {
        TextView DateTime;
        TextView examname;
        TextView difficult;
        RatingBar ratingBar;
        ImageView subject;
        CheckBox checkBox;
    }
}
