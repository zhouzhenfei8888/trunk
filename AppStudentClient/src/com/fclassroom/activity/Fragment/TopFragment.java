package com.fclassroom.activity.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopFragment extends Fragment {
    private static final String ARG_POSITION = "position";
    ListView listView;
    int position;
    ArrayList<HashMap<String, Object>> list;

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
        return view;
    }

    private void initViews(View view) {
        listView = (ListView) view.findViewById(R.id.listview_rank);
        listView.setAdapter(new topRankAdapter(getActivity(), R.layout.listview_item_toprank, list));

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
