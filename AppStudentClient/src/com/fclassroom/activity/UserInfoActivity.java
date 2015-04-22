package com.fclassroom.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.LoginResponseBean;
import com.fclassroom.app.common.MD5Util;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

public class UserInfoActivity extends BaseActivity {

    private Toolbar mToolbar;
    TextView name, jikeid, schoolname, gradename, classname, nickname, openidQQ, telephone;
    TextView modifiednickname, modifiedqq, modifiedtelephone, modifiedpassword;
    ImageView headView;
    AppContext appContext;
    String accessToken;
    private static final String APP_ID = "1104307228";
    private static final String APP_KEY = "pOno3P7CgPh4KSCr";
    private Tencent mTencent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        initToolbar();
        initViews();
    }

    private void initViews() {
        name = (TextView) findViewById(R.id.tv_name);
        jikeid = (TextView) findViewById(R.id.tv_jikeid);
        schoolname = (TextView) findViewById(R.id.tv_schoolname);
        gradename = (TextView) findViewById(R.id.tv_grade);
        classname = (TextView) findViewById(R.id.tv_class);
        nickname = (TextView) findViewById(R.id.tv_nickname);
        openidQQ = (TextView) findViewById(R.id.tv_qq);
        telephone = (TextView) findViewById(R.id.tv_telephone_number);
        headView = (ImageView) findViewById(R.id.iv_headview);
        modifiednickname = (TextView) findViewById(R.id.modified_nickname);
        modifiedqq = (TextView) findViewById(R.id.modified_qq);
        modifiedtelephone = (TextView) findViewById(R.id.modified_telephone);
        modifiedpassword = (TextView) findViewById(R.id.modified_password);
        name.setText(PreferenceUtils.getString(appContext, PreferenceUtils.STUDENT_NAME));
        jikeid.setText("极课ID：" + PreferenceUtils.getString(appContext, PreferenceUtils.JIKE_NUM));
        schoolname.setText("学校：" + PreferenceUtils.getString(appContext, PreferenceUtils.SCHOOL_NAME));
        gradename.setText("年级：" + PreferenceUtils.getString(appContext, PreferenceUtils.GRADE_NAME));
        classname.setText("班级：" + PreferenceUtils.getString(appContext, PreferenceUtils.CLASS_NAME));
        if("女".equals(PreferenceUtils.getString(appContext,PreferenceUtils.SEX))){
            headView.setImageResource(R.drawable.head_girl);
        }else{
            headView.setImageResource(R.drawable.head_boy);
        }
        if (null == PreferenceUtils.getString(appContext, PreferenceUtils.NICKNAME) || "".equals(PreferenceUtils.getString(appContext, PreferenceUtils.NICKNAME))) {
            nickname.setText("昵称：" + PreferenceUtils.getString(appContext, PreferenceUtils.STUDENT_NAME));
        } else {
            nickname.setText("昵称：" + PreferenceUtils.getString(appContext, PreferenceUtils.NICKNAME));
        }
        if (null == PreferenceUtils.getString(appContext, PreferenceUtils.OPEN_ID) || "".equals(PreferenceUtils.getString(appContext, PreferenceUtils.OPEN_ID))) {
            openidQQ.setHint("暂未绑定qq号");
        } else {
            openidQQ.setText("已绑定qq号");
        }
        if (null == PreferenceUtils.getString(appContext, PreferenceUtils.ACCOUNT_LOGINPHONE) || "".equals(PreferenceUtils.getString(appContext, PreferenceUtils.ACCOUNT_LOGINPHONE))) {
            telephone.setHint("暂未绑定手机号");
            modifiedtelephone.setEnabled(true);
        } else {
            telephone.setText("手机号：" + PreferenceUtils.getString(appContext, PreferenceUtils.ACCOUNT_LOGINPHONE));
            modifiedtelephone.setEnabled(false);
            modifiedtelephone.setText("已绑定");
            modifiedtelephone.setTextColor(Color.GRAY);
        }
        modifiednickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditNickNameDialog();
            }
        });
        modifiedqq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initqq();
            }
        });

        modifiedtelephone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(UserInfoActivity.this,BindtelephoneActivity.class,"kbv");
            }
        });
        modifiedpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(UserInfoActivity.this,Changepassword.class);
            }
        });
    }

    private void initqq() {
        String openid = null;
        mTencent = Tencent.createInstance(APP_ID, this.getApplication());
        mTencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                try {
                    String openid = ((JSONObject) o).getString("openid");
                    PreferenceUtils.putString(appContext, PreferenceUtils.OPEN_ID, openid);
//                    UIHelper.jump2Activity(EnterActivity.this, BindActivity.class);
                    bindQQ(PreferenceUtils.getString(appContext,PreferenceUtils.JIKE_NUM), PreferenceUtils.getString(appContext,PreferenceUtils.LOGINPASSWORD), openid);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                UIHelper.ToastMessage(UserInfoActivity.this, "登入失败");
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void bindQQ(final String name, final String pwd, final String string) {
        final ProgressDialog dialog = ProgressDialog.show(UserInfoActivity.this,"","等待绑定。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<LoginResponseBean> account = (BaseResponseBean<LoginResponseBean>) msg.obj;
                    // appContext.saveAccountInfo(account.getData());
                    if (account.getData() != null) {
//                        UIHelper.jump2Activity(UserInfoActivity.this, CheckinforActivity.class);
//                        AppManager.getAppManager().finishActivity();
                        UIHelper.ToastMessage(UserInfoActivity.this, "更改绑定成功！");
                    } else {
                        UIHelper.ToastMessage(UserInfoActivity.this, "该账号还未绑定");
                    }
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(UserInfoActivity.this);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(UserInfoActivity.this, msg.obj.toString());
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
    private void EditNickNameDialog() {
        View view = LayoutInflater.from(UserInfoActivity.this).inflate(R.layout.dialog_notebook, null, false);
        final EditText etName = (EditText) view.findViewById(R.id.et_notebookname);
        etName.setHint("呢称");
        TextView textView = (TextView) view.findViewById(R.id.tv_tite_dialog);
        textView.setText("修改昵称");
        AlertDialog.Builder builder = new AlertDialog.Builder(UserInfoActivity.this);
        builder.setTitle("");
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // changeNoteName(accessToken, id, etName.getText().toString().trim());
                String propName = "nickname", propValue = etName.getText().toString().trim();
                modifiednickname(accessToken, propName, propValue);
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void modifiednickname(final String accessToken, final String propName, final String propValue) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    UIHelper.ToastMessage(UserInfoActivity.this, "修改成功！");
                    nickname.setText("昵称："+propValue);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(UserInfoActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(UserInfoActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.modifiednickname(accessToken, propName, propValue);
                    if (responseBean.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = responseBean;
                    } else {
                        msg.what = 0;
                        msg.obj = responseBean.getError_msg();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_info, menu);
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
