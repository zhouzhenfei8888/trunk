package com.fclassroom.activity;

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
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

public class BindtelephoneActivity extends BaseActivity {

    private Toolbar mtoolbar;
    private Button bnSure, bnSendauthCode;
    private EditText etAuthCode, ettelephoneNum;
    private TextView title;
    //bindphoneORfindpassword 0为bindphone,1为findpassword；
    private int bindphoneORfindpassword;
    AppContext appContext;
    String accessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindtelephone);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        System.out.println(getIntent().getStringExtra("value"));
        //判断是bindphone或findpassword页面
        if ("findpassword".equals(getIntent().getStringExtra("value"))) {
            bindphoneORfindpassword = 1;
            System.out.println(bindphoneORfindpassword);
        } else {
            bindphoneORfindpassword = 0;
        }
        initToolbar();
        initViews();
    }

    private void initViews() {
        etAuthCode = (EditText) findViewById(R.id.auth_code);
        ettelephoneNum = (EditText) findViewById(R.id.edit_telephone);
        bnSendauthCode = (Button) findViewById(R.id.bn_send_authcode);
        bnSure = (Button) findViewById(R.id.bn_sure);
        bnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindphone(accessToken,etAuthCode.getText().toString(),ettelephoneNum.getText().toString());
                UIHelper.jump2Activity(BindtelephoneActivity.this, HomeActivity.class);
            }
        });
        bnSendauthCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(ettelephoneNum.getText().toString().trim())) {
                    UIHelper.ToastMessage(BindtelephoneActivity.this, "请输入手机号码");
                } else if (ettelephoneNum.getText().length() != 11) {
                    UIHelper.ToastMessage(BindtelephoneActivity.this, "请输入正确的手机号码");
                } else {
                    SendAuthCode(accessToken, ettelephoneNum.getText().toString().trim());
                }
            }
        });
    }

    private void bindphone(final String accessToken, final String authCode, final String telephone) {
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        new Thread(){
            @Override
            public void run() {
                try {
                    appContext.bindphone(accessToken,authCode,telephone);
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void SendAuthCode(final String accessToken, final String telephoneNum) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    UIHelper.ToastMessage(BindtelephoneActivity.this, "发送成功！");
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(BindtelephoneActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    BaseResponseBean<Boolean> responseBean = appContext.SendAuthCode(accessToken, telephoneNum);
                    message.what = 1;
                    message.obj = responseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    message.what = -1;
                    message.obj = e;
                }
                handler.sendMessage(message);
            }
        }.start();
    }

    private void initToolbar() {
        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        title = (TextView) mtoolbar.findViewById(R.id.tv_title);
        if (bindphoneORfindpassword == 1) {
            title.setVisibility(View.GONE);
            mtoolbar.setTitle("找回密码");
        } else if (bindphoneORfindpassword == 0) {
            mtoolbar.setTitle("");
            title.setVisibility(View.VISIBLE);
        }
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
        getMenuInflater().inflate(R.menu.menu_bindtelephone, menu);
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
