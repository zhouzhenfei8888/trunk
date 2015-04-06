package com.fclassroom.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.fclassroom.app.adapter.RubbishAdapter;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotebookActivity extends BaseActivity {

    private Toolbar mtoolBar;
    private ListView mlistView;
    private List list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);
        initData();
        initToolbar();
        initViews();
    }

    private void initData() {
        list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("examdate", "2015年3月3日");
            data.put("examname", "梅村中学第三次模拟考试");
            data.put("examsrc", R.drawable.subject);
            data.put("rating", (float) 3.0);
            list.add(data);
        }
    }

    private void initToolbar() {
        mtoolBar = (Toolbar) findViewById(R.id.toolbar);
        mtoolBar.setNavigationIcon(R.drawable.ic_launcher);
        mtoolBar.setTitle("精题本");
        mtoolBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mtoolBar);
        mtoolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        mlistView = (ListView) findViewById(R.id.listview_notebook);
        mlistView.setAdapter(new SubjectAdapter(NotebookActivity.this, list));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notebook, menu);
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
