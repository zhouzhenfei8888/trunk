package com.fclassroom.activity.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.activity.BaseActivity;
import com.fclassroom.app.widget.ScrollShowHeaderListView;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WaitforCompleteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WaitforCompleteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM = "param";

    // TODO: Rename and change types of parameters
    private int mParam;
    private ListView listView;
    private LinearLayout linearTitle;
    private ArrayList<HashMap<String, Object>> list;
    private ArrayList<HashMap<String, Object>> list2;
    private TranslateAnimation mShowAnimation;
    private TranslateAnimation mHideAnimation;
    private ScrollShowHeaderListView scrollShowHeaderListView;
    private LayoutInflater inflater;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WaitforCompleteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WaitforCompleteFragment newInstance(int param) {
        WaitforCompleteFragment fragment = new WaitforCompleteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    public WaitforCompleteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getInt(ARG_PARAM);
        }
        list = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("workname", "梅村中学高一数学期末第三次模拟测试");
            data.put("workdate", "2015年3月10日");
            data.put("worktime", "14:00截止交卷");
            list.add(data);
        }
        list2 = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("workname", "梅村中学高一数学期末第三次模拟测试");
            data.put("responsetime", "作答时限/分：30");
            data.put("opentime", "开放时间：2015年3月14日   13:00");
            data.put("closetime", "截止时间：2015年4月14日   16:00");
            list2.add(data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_waitfor_complete, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        scrollShowHeaderListView = (ScrollShowHeaderListView) view.findViewById(R.id.listview_waitforwork);
        inflater = LayoutInflater.from(getParentFragment().getActivity());
        View headView = inflater.inflate(R.layout.listview_head_rubbish, null, false);
        TextView tv = (TextView) headView.findViewById(R.id.tv_head);
        TextView checkbox_tv = (TextView) headView.findViewById(R.id.tv_checkbox);
        CheckBox checkBox = (CheckBox) headView.findViewById(R.id.checkbox);
        if (0 == mParam) {
            tv.setText("待完成作业（7项）");
        } else if (1 == mParam) {
            tv.setText("待完成作业（7项）");
            checkBox.setVisibility(View.VISIBLE);
            checkbox_tv.setText("是否显示未开放作业");
            checkbox_tv.setVisibility(View.VISIBLE);
        } else if (2 == mParam) {
        }
        scrollShowHeaderListView.setUpHeaderViews(headView);
        listView = scrollShowHeaderListView.getListView();
        if (0 == mParam) {
            listView.setAdapter(new WorkAdapter(getParentFragment().getActivity(), R.layout.listview_item_waitforcomplete, list));
        } else {
            listView.setAdapter(new WorkAdapter2(getParentFragment().getActivity(), R.layout.listview_item_notcomplete, list2));
        }
    }


    private void initAnimation() {
        // 从自已-1倍的位置移到自己原来的位置
        mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        mHideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, -1);
        mShowAnimation.setDuration(500);
        mHideAnimation.setDuration(500);
        mHideAnimation.setFillAfter(true);
        mShowAnimation.setFillAfter(true);
    }

    class WorkAdapter extends BaseAdapter {
        private Context context;
        private int res;
        private ArrayList<HashMap<String, Object>> list;
        private LayoutInflater inflater;

        public WorkAdapter(Context context, int res, ArrayList<HashMap<String, Object>> list) {
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
            if (null == convertView) {
                holder = new Holder();
                convertView = inflater.inflate(res, null, false);
                holder.workname = (TextView) convertView.findViewById(R.id.tv_workname);
                holder.workdate = (TextView) convertView.findViewById(R.id.tv_workdate);
                holder.worktime = (TextView) convertView.findViewById(R.id.tv_worktime);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            HashMap<String, Object> data = list.get(position);
            holder.workname.setText(data.get("workname").toString());
            holder.workdate.setText(data.get("workdate").toString());
            holder.worktime.setText(data.get("worktime").toString());
            return convertView;
        }

        class Holder {
            TextView workname;
            TextView workdate;
            TextView worktime;
            ImageView icon;
        }
    }

    class WorkAdapter2 extends BaseAdapter {
        private Context context;
        private int res;
        private ArrayList<HashMap<String, Object>> list;
        private LayoutInflater inflater;

        public WorkAdapter2(Context context, int res, ArrayList<HashMap<String, Object>> list) {
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
            if (null == convertView) {
                holder = new Holder();
                convertView = inflater.inflate(res, null, false);
                holder.workname = (TextView) convertView.findViewById(R.id.tv_workname);
                holder.responsetime = (TextView) convertView.findViewById(R.id.tv_response_time);
                holder.opentime = (TextView) convertView.findViewById(R.id.tv_opentime);
                holder.closetime = (TextView) convertView.findViewById(R.id.tv_closetime);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            HashMap<String, Object> data = list2.get(position);
            holder.workname.setText(data.get("workname").toString());
            holder.responsetime.setText(data.get("responsetime").toString());
            holder.opentime.setText(data.get("opentime").toString());
            holder.closetime.setText(data.get("closetime").toString());
            return convertView;
        }

        class Holder {
            TextView workname;
            TextView responsetime;
            TextView opentime;
            TextView closetime;
            ImageView icon;
        }
    }
}
