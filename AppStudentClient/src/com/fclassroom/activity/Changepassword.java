package com.fclassroom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.common.MD5Util;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.XEditText;
import com.fclassroom.appstudentclient.R;

public class Changepassword extends BaseActivity {

    XEditText newpassword, oldpassword;
    Button bnSendAuthCode, bnSure;
    TextView sure, cancle;
    AppContext appContext;
    String accessToken;
    TextView error;
    boolean eyeclose1 = true;
    boolean eyeclose2 = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        initViews();
        initToolbar();
    }

    private void initToolbar() {
        sure = (TextView) findViewById(R.id.sure);
        cancle = (TextView) findViewById(R.id.cancle);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stroldpassword = oldpassword.getText().toString();
                String strnewpassword = newpassword.getText().toString();
//                if(strnewpassword.length()<6||stroldpassword.length()<6||strnewpassword.contains(" ")||stroldpassword.contains(" ")){
//                    UIHelper.ToastMessage(Changepassword.this,"密码格式不符合规范");
//                }else{
                    changepassword(accessToken, MD5Util.encode(oldpassword.getText().toString()), MD5Util.encode(newpassword.getText().toString()));
//                }
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
    }

    private void initViews() {
        oldpassword = (XEditText) findViewById(R.id.oldpassword);
        newpassword = (XEditText) findViewById(R.id.newpassword);
        error = (TextView) findViewById(R.id.tv_password_message);
        newpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                error.setVisibility(View.VISIBLE);
            }
        });
        oldpassword.setDrawableRightListener(new XEditText.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                if (eyeclose1) {
                    oldpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    oldpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeclose1 = false;
                } else {
                    oldpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);
                    oldpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeclose1 = true;
                }
            }
        });
        newpassword.setDrawableRightListener(new XEditText.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                if (eyeclose2) {
                    newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    newpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeclose2 = false;
                } else {
                    newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);
                    newpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeclose2 = true;
                }
            }
        });
    }

    private void changepassword(final String accessToken, final String oldpassword, final String newpassword) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<String> responseBean = (BaseResponseBean<String>) msg.obj;
                    UIHelper.ToastMessage(Changepassword.this, responseBean.getData().toString());
                    AppManager.getAppManager().finishActivity();
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(Changepassword.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.changepassword(accessToken, oldpassword, newpassword);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_changepassword, menu);
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
