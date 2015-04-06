package com.fclassroom.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageActivity extends BaseActivity {

    private ListView listView;
    private ArrayList<HashMap<String,Object>> list;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        list = new ArrayList<HashMap<String, Object>>();
        for(int i=0;i<20;i++){
            HashMap<String,Object> data = new HashMap<>();
            data.put("messagename","老师又布置新作业啦!");
            data.put("messagetime","14:"+i);
            list.add(data);
        }
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("我的消息");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        listView = (ListView)findViewById(R.id.listview_message);
        listView.setAdapter(new MessageAdapter(this,R.layout.listview_item_message,list));
    }

    class MessageAdapter extends BaseAdapter{
        Context context;
        int res;
        ArrayList<HashMap<String,Object>> list;
        LayoutInflater inflater;
        public MessageAdapter(Context context, int res, ArrayList<HashMap<String, Object>> list){
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
            if(null == convertView){
                convertView=inflater.inflate(res,null,false);
                holder = new Holder();
                holder.messagename = (TextView)convertView.findViewById(R.id.tv_messagename);
                holder.messagetime = (TextView)convertView.findViewById(R.id.tv_messagetime);
                convertView.setTag(holder);
            }else {
                holder = (Holder)convertView.getTag();
            }
            HashMap<String,Object> item = list.get(position);
            holder.messagename.setText(item.get("messagename").toString());
            holder.messagetime.setText(item.get("messagetime").toString());
            return convertView;
        }

        class Holder{
            TextView messagename;
            TextView messagetime;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_message, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
