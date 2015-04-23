package com.fclassroom.activity.Fragment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.TopBind;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    ListView listView;
    int position;
    ArrayList<HashMap<String, Object>> list;
    AppContext appContext;
    String accessToken;

    public TopFragment() {
        // Required empty public constructor
    }

    public static TopFragment newInstance(int position) {
        TopFragment f = new TopFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        appContext = (AppContext) getActivity().getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        list = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("rank", i);
            data.put("name", "纪源资本");
            list.add(data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top, container, false);
        initViews(view);
        getRank(position);
        return view;
    }

    private void initViews(View view) {
        listView = (ListView) view.findViewById(R.id.listview_rank);
        topRankAdapter topRankAdapter = new topRankAdapter(getActivity(), R.layout.listview_item_toprank, list);
        listView.setAdapter(topRankAdapter);

    }

    public void getRank(final int rankType) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    BaseResponseBean<List<TopBind>> responseBean = (BaseResponseBean<List<TopBind>>) msg.obj;
                    List<TopBind> list = responseBean.getData();
                    ArrayList<HashMap<String,Object>> dataList = new ArrayList<HashMap<String, Object>>();
                    for (int i = 0; i < list.size(); i++) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("rank", i+1);
                        data.put("name", list.get(i).getStudentName());
                        dataList.add(data);
                    }
                    topRankAdapter topRankAdapter = new topRankAdapter(getActivity(), R.layout.listview_item_toprank, dataList);
                    listView.setAdapter(topRankAdapter);
                }else if(msg.what ==-1){
                    ((AppException)msg.obj).makeToast(getActivity());
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<List<TopBind>> responseBean = appContext.getRank(accessToken, rankType);
                    msg.what = 1;
                    msg.obj = responseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();

    }

    class topRankAdapter extends BaseAdapter {
        private ArrayList<HashMap<String, Object>> list;
        private Context context;
        private int res;
        private LayoutInflater inflater;

        public topRankAdapter(Context context, int res, ArrayList<HashMap<String, Object>> list) {
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
            if (convertView == null) {
                holder = new Holder();
                convertView = inflater.inflate(R.layout.listview_item_toprank, null, false);
                holder.rank = (TextView) convertView.findViewById(R.id.tv_rank);
                holder.name = (TextView) convertView.findViewById(R.id.tv_name);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if(position <3){
                holder.rank.setTextColor(Color.parseColor("#f19149"));
                holder.name.setTextColor(Color.parseColor("#f19149"));
            }
            HashMap<String, Object> data = list.get(position);
            holder.rank.setText("NO." + data.get("rank"));
            holder.name.setText(data.get("name").toString());
            return convertView;
        }

        class Holder {
            TextView rank;
            TextView name;
        }
    }
}
