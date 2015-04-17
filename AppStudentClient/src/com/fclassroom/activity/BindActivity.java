package com.fclassroom.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.fclassroom.app.bean.LoginResponseBean;
import com.fclassroom.app.common.MD5Util;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.StringUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.XEditText;
import com.fclassroom.appstudentclient.R;

public class BindActivity extends BaseActivity {

    private Toolbar mToolbar;
    private XEditText editJikepassword;
    private EditText editJikeNumber;
    private TextView jike;
    private TextView jikeContent;
    private boolean jikeContentVisible = false;
    private boolean eyeclose = true;
    private Button bind;
    AppContext appContext;
    AppManager appManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind);
        initToolbar();
        initViews();
    }

    private void initViews() {
        appContext = (AppContext) getApplication();
        appManager = AppManager.getAppManager();
        bind = (Button) findViewById(R.id.bn_bind);
        jike = (TextView) findViewById(R.id.tv_jike);
        jikeContent = (TextView) findViewById(R.id.tv_jikecontent);
        editJikeNumber = (EditText) findViewById(R.id.edit_jike_number);
        editJikepassword = (XEditText) findViewById(R.id
                .edit_jike_password);
        editJikepassword.setDrawableRightListener(new XEditText.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                if (eyeclose) {
                    editJikepassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    editJikepassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeclose = false;
                } else {
                    editJikepassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);
                    editJikepassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeclose = true;
                }
            }
        });
        jike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (jikeContentVisible == false) {
                    jikeContent.setVisibility(View.VISIBLE);
                    jikeContentVisible = true;
                } else {
                    jikeContent.setVisibility(View.GONE);
                    jikeContentVisible = false;
                }
            }
        });
   /*     editJikeNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() != 0) {
                    checkEditText();
                }
            }
        });
        editJikepassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() != 0) {
                    checkEditText();
                }
            }
        });*/
        bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(editJikeNumber.getText().toString()) && !StringUtils.isEmpty(editJikepassword.getText().toString())) {
                    bindQQ(editJikeNumber.getText().toString(), MD5Util.encode(editJikepassword.getText().toString()), PreferenceUtils.getString(appContext, PreferenceUtils.OPEN_ID));
                }else {
                    UIHelper.ToastMessage(BindActivity.this,"账号或密码不能为空");
                }
            }
        });
    }

    private void bindQQ(final String name, final String pwd, final String string) {
        final ProgressDialog dialog = ProgressDialog.show(BindActivity.this,"","等待绑定。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<LoginResponseBean> account = (BaseResponseBean<LoginResponseBean>) msg.obj;
                   // appContext.saveAccountInfo(account.getData());
                    if (account.getData() != null) {
                        UIHelper.jump2Activity(BindActivity.this, CheckinforActivity.class);
                        appManager.finishActivity(BindActivity.this);
                    } else {
                        UIHelper.ToastMessage(BindActivity.this, "该账号还未绑定");
                    }
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(BindActivity.this);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(BindActivity.this, msg.obj.toString());
                }
                dialog.dismiss();
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                BaseResponseBean<LoginResponseBean> account = null;
                try {
                    account = appContext.bindQQ(name, pwd, string);
                    if (account.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = account;
                    } else if (account.getError_code() != 0) {
                        msg.what = 0;
                        msg.obj = account.getError_msg();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void checkEditText() {
        if (!TextUtils.isEmpty(editJikeNumber.getText().toString().trim()) && !TextUtils.isEmpty(editJikepassword.getText().toString().trim())) {
            bind.setEnabled(true);
        } else {
            bind.setEnabled(false);
        }
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.mtoolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bind, menu);
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
