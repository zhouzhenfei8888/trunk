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
import android.widget.LinearLayout;

import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.common.UpdateManager;
import com.fclassroom.appstudentclient.R;

public class AboutActivity extends BaseActivity {

    private Toolbar mtoolbar;
    private LinearLayout function;
    private LinearLayout feedback;
    private LinearLayout update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        initToolbar();
        initViews();
    }

    private void initViews() {
        function = (LinearLayout) findViewById(R.id.linear_function);
        feedback = (LinearLayout) findViewById(R.id.linear_feedback);
        update = (LinearLayout) findViewById(R.id.linear_update);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager.getUpdateManager().checkAppUpdate(AboutActivity.this, false);
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(AboutActivity.this, FeedbackActivity.class);
            }
        });
    }

    private void initToolbar() {
        mtoolbar = (Toolbar) findViewById(R.id.toolbar);
        mtoolbar.setTitle("关于极课同学");
        mtoolbar.setTitleTextColor(Color.WHITE);
        mtoolbar.setNavigationIcon(R.drawable.ic_launcher);
        setSupportActionBar(mtoolbar);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_about, menu);
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
