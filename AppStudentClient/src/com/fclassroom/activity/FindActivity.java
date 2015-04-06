package com.fclassroom.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

public class FindActivity extends BaseActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private LinearLayout mMessage, machieve, topRanking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find);
        initToolbar();
        initViews();
    }

    private void initViews() {
        mMessage = (LinearLayout) findViewById(R.id.linear_message);
        machieve = (LinearLayout) findViewById(R.id.linear_achieve);
        topRanking = (LinearLayout) findViewById(R.id.linear_ranking_list);
        mMessage.setOnClickListener(this);
        machieve.setOnClickListener(this);
        topRanking.setOnClickListener(this);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("发现");
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_find, menu);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linear_message:
                UIHelper.jump2Activity(FindActivity.this,MessageActivity.class);
                break;
            case R.id.linear_achieve:
                UIHelper.jump2Activity(FindActivity.this,AchievementActivity.class);
                break;
            case R.id.linear_ranking_list:
                UIHelper.jump2Activity(FindActivity.this,TopRankActivity.class);
                break;
        }
    }
}
