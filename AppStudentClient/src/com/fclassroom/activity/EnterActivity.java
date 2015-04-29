package com.fclassroom.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.GradeBean;
import com.fclassroom.app.bean.LoginResponseBean;
import com.fclassroom.app.common.MD5Util;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.StringUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.XEditText;
import com.fclassroom.appstudentclient.R;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.fclassroom.appstudentclient.R.layout.fclassroom_infor;

public class EnterActivity extends BaseActivity {

    private Button enter;
    private LinearLayout qqEnter;
    private static final String APP_ID = "1104307228";
    private static final String APP_KEY = "pOno3P7CgPh4KSCr";
    private Tencent mTencent;
    private static boolean fromEnter = true;
    private XEditText username;
    private XEditText password;
    private LinearLayout error;
    private TextView getpassword;
    private BaseResponseBean<LoginResponseBean> user;
    private ProgressDialog dialog;
    private LayoutInflater inflater;
    AppContext appContext;
    AppManager appManager;
    boolean eyeclose = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);
        initViews();
    }

    private void initViews() {
        // TODO Auto-generated method stub
        appContext = (AppContext) getApplication();
        appManager = AppManager.getAppManager();
        enter = (Button) findViewById(R.id.enter);
//        enter.setEnabled(false);
        qqEnter = (LinearLayout) findViewById(R.id.linear_qq);
        username = (XEditText) findViewById(R.id.username_et);
        password = (XEditText) findViewById(R.id.password_et);
        getpassword = (TextView) findViewById(R.id.tv_getpassword);
        error = (LinearLayout) findViewById(R.id.linear_error);
        inflater = LayoutInflater.from(EnterActivity.this);
//        username.setSelection("1".length());
//        password.setSelection(4);
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                error.setVisibility(View.GONE);
            }
        });
        username.setDrawableRightListener(new XEditText.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                View viewinfo = inflater.inflate(R.layout.fclassroom_infor, null, false);
                AlertDialog.Builder builder = new AlertDialog.Builder(EnterActivity.this);
                builder.setView(viewinfo);
                builder.create().show();
            }
        });
/*        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 || password.getText().toString() == null) {
                    enter.setEnabled(false);
                    enter.setBackgroundColor(Color.parseColor("#2eb48f"));
                }else{
                    enter.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0 || username.getText().toString() == null) {
                    enter.setEnabled(false);
                    enter.setBackgroundColor(Color.parseColor("#2eb48f"));
                }else{
                    enter.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });*/

        password.setDrawableRightListener(new XEditText.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                if (eyeclose) {
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeclose = false;
                } else {
                    password.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeclose = true;
                }
            }
        });
        //如果有token，直接token登入
        if (PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN) != null) {
            loginbyAccesstoken(PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN));
        }
        enter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (!StringUtils.isEmpty(username.getText().toString().trim()) && !StringUtils.isEmpty(password.getText().toString().trim())) {
                    login(username.getText().toString(), MD5Util.encode(password.getText().toString()));
                } else {
                    UIHelper.ToastMessage(EnterActivity.this, "请输入用户名或密码");
                }
//                UIHelper.jump2Activity(EnterActivity.this,HomeActivity.class);
            }
        });
        qqEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initqq();
            }
        });
        getpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(EnterActivity.this, BindtelephoneActivity.class, "findpassword");
            }
        });
    }


    public void checkEditText() {
        if (!TextUtils.isEmpty(username.getText().toString().trim()) && !TextUtils.isEmpty(password.getText().toString().trim())) {
            enter.setEnabled(true);
        } else {
            enter.setEnabled(false);
            enter.setBackgroundColor(Color.parseColor("#2eb48f"));
        }
    }

    private void login(final String account, final String pwd) {
        dialog = ProgressDialog.show(EnterActivity.this, "", "正在登录中");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                dialog.dismiss();
                if (msg.what == 1) {
                    user = (BaseResponseBean<LoginResponseBean>) msg.obj;
                    appContext.saveLoginInfo(user.getData());
                    System.out.println("telephone:" + user.getData().getLoginPhone());
                    if (user.getData().getLoginPhone() == null && user.getData().getOpenIdQQ() == null) {
                        UIHelper.jump2Activity(EnterActivity.this, CheckinforActivity.class);
                    } else {
                        UIHelper.jump2Activity(EnterActivity.this, HomeActivity.class);
                        appManager.finishActivity();
                    }
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(EnterActivity.this);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(EnterActivity.this, "登录失败:" + msg.obj);
                    error.setVisibility(View.VISIBLE);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<LoginResponseBean> responseBean = appContext.loginVerify(account, pwd);
                    if (responseBean.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = responseBean;
                    } else if (responseBean.getError_code() != 0) {
                        msg.what = 0;
                        msg.obj = responseBean.getError_msg();
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

    private void loginbyAccesstoken(final String accessToken) {
        dialog = ProgressDialog.show(EnterActivity.this, "", "正在登录中");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    UIHelper.ToastMessage(EnterActivity.this, "登录成功");
                    UIHelper.jump2Activity(EnterActivity.this, HomeActivity.class);
                    appManager.finishActivity(EnterActivity.this);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(EnterActivity.this, "登录失败" + msg.obj);
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(EnterActivity.this);
                }
                dialog.dismiss();
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                BaseResponseBean<LoginResponseBean> responseBean = null;
                try {
                    responseBean = appContext.loginByToken(accessToken);
                    if (responseBean.getError_code() == 0) {
                        message.what = 1;
                        message.obj = responseBean;
                    } else {
                        message.what = 0;
                        message.obj = responseBean.getError_msg();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    message.what = -1;
                    message.obj = e;
                }
                handler.sendMessage(message);
            }
        }.start();
    }

    private void loginbyQQ(final String openid) {
        final ProgressDialog progressDialog = ProgressDialog.show(EnterActivity.this, "", "QQ登录。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<LoginResponseBean> baseResponseBean = (BaseResponseBean<LoginResponseBean>) msg.obj;
                    LoginResponseBean loginResponseBean = baseResponseBean.getData();
                    if (loginResponseBean != null) {
                        UIHelper.jump2Activity(EnterActivity.this, HomeActivity.class);
                        appManager.finishActivity(EnterActivity.this);
                    } else {
                        UIHelper.ToastMessage(EnterActivity.this, "请先绑定极课号");
                        UIHelper.jump2Activity(EnterActivity.this, BindActivity.class);
                        appManager.finishActivity(EnterActivity.this);
                    }
                }
                progressDialog.dismiss();
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                BaseResponseBean<LoginResponseBean> baseResponseBean = null;
                try {
                    baseResponseBean = appContext.loginByqq(openid);
                    msg.obj = baseResponseBean;
                    msg.what = 1;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    //拿到openid
    private void initqq() {
        String openid = null;
        mTencent = Tencent.createInstance(APP_ID, this.getApplication());
        mTencent.login(this, "all", new IUiListener() {
            @Override
            public void onComplete(Object o) {
                try {
                    String openid = ((JSONObject) o).getString("openid");
                    PreferenceUtils.putString(appContext, PreferenceUtils.OPEN_ID, openid);
                    loginbyQQ(openid);
                    UIHelper.jump2Activity(EnterActivity.this, BindActivity.class);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(UiError uiError) {
                UIHelper.ToastMessage(EnterActivity.this, "登录失败");
            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.enter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
