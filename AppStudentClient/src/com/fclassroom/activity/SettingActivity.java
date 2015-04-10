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

import com.fclassroom.AppContext;
import com.fclassroom.AppManager;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

public class SettingActivity extends BaseActivity {

    private Toolbar mToolbar;
    private LinearLayout linear_user;
    private LinearLayout linear_find;
    private LinearLayout linear_about;
    private LinearLayout linear_exit;
    AppContext appContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        appContext = (AppContext) getApplication();
        initToolbar();
        initViews();
    }

    private void initViews() {
        linear_user = (LinearLayout) findViewById(R.id.linear_user);
        linear_find = (LinearLayout) findViewById(R.id.linear_find);
        linear_about = (LinearLayout) findViewById(R.id.linear_aboutjike);
        linear_exit = (LinearLayout) findViewById(R.id.exit);
        linear_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(SettingActivity.this, UserInfoActivity.class);
            }
        });
        linear_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(SettingActivity.this, FindActivity.class);
            }
        });
        linear_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(SettingActivity.this, AboutActivity.class);
            }
        });
        linear_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.putString(appContext,PreferenceUtils.ACCESSTOKEN,"");
                AppManager.getAppManager().finishAllActivity();
            }
        });
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mtoolbar);
        mToolbar.setTitle("设置");
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
        getMenuInflater().inflate(R.menu.menu_setting, menu);
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
