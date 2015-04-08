package com.fclassroom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.fclassroom.AppManager;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

public class CheckinforActivity extends BaseActivity {

    private Button yes;
    private Button no;
    private static boolean fromenter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkinfor);
        try {
            //判断是否从登入页面进入，如果是则还需要进行手机绑定
            fromenter = (boolean) getIntent().getSerializableExtra("value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
    }

    private void initViews() {
        yes = (Button) findViewById(R.id.bn_yes);
        no = (Button) findViewById(R.id.bn_no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromenter) {
                    UIHelper.jump2Activity(CheckinforActivity.this,BindtelephoneActivity.class);
                } else {
                    UIHelper.jump2Activity(CheckinforActivity.this, HomeActivity.class);
                    AppManager.getAppManager().finishActivity();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.putString(CheckinforActivity.this,PreferenceUtils.ACCESSTOKEN,"");
                AppManager.getAppManager().finishActivity();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_checkinfor, menu);
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
