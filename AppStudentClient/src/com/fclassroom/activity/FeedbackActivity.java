package com.fclassroom.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

public class FeedbackActivity extends BaseActivity {

    private Toolbar toolbar;
    private EditText editText;
    private Button sure;
    private AppContext appContext;
    private String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        initToolbar();
        initViews();
    }

    private void initViews() {
        editText = (EditText) findViewById(R.id.et_feedback);
        sure = (Button) findViewById(R.id.bn_sure);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendFeedMessage(accessToken, editText.getText().toString());
            }
        });
    }

    private void sendFeedMessage(final String accessToken, final String content) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean responseBean = (BaseResponseBean) msg.obj;
                    UIHelper.ToastMessage(FeedbackActivity.this, "发送成功！");
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(FeedbackActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(FeedbackActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    appContext.sendFeedBack(accessToken, content);
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("用户反馈");
        toolbar.setNavigationIcon(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
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
