package com.fclassroom.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
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
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.XEditText;
import com.fclassroom.appstudentclient.R;

public class BindtelephoneActivity extends BaseActivity {

    private Toolbar mtoolbar;
    private Button bnSure, bnSendauthCode;
    private EditText etAuthCode, ettelephoneNum;
    private TextView title;
    private TextView error;
    MyCountDownTimer mc;
    //bindphoneORfindpassword 0为bindphone,1为findpassword；
    private int bindphoneORfindpassword;
    AppContext appContext;
    String accessToken;
    XEditText newpassword;
    boolean eyeclose = true;
    TextView passwordmessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindtelephone);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        newpassword = (XEditText)findViewById(R.id.edit_newpassword);
        System.out.println(getIntent().getStringExtra("value"));
        //判断是bindphone或findpassword页面
        if ("findpassword".equals(getIntent().getStringExtra("value"))) {
            bindphoneORfindpassword = 1;
            newpassword.setVisibility(View.VISIBLE);
        } else {
            bindphoneORfindpassword = 0;
            newpassword.setVisibility(View.GONE);
        }
        initToolbar();
        initViews();
    }

    private void initViews() {
        etAuthCode = (EditText) findViewById(R.id.auth_code);
        ettelephoneNum = (EditText) findViewById(R.id.edit_telephone);
        bnSendauthCode = (Button) findViewById(R.id.bn_send_authcode);
        error = (TextView) findViewById(R.id.tv_error);
        bnSure = (Button) findViewById(R.id.bn_sure);
        passwordmessage = (TextView) findViewById(R.id.tv_password_message);
        mc = new MyCountDownTimer(60000,1000);
        bnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindphone(accessToken,etAuthCode.getText().toString(),ettelephoneNum.getText().toString());
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
                        if(bindphoneORfindpassword == 1){
                            //找回密码
                            checkPhone(ettelephoneNum.getText().toString().trim());
                        }else{
                            SendAuthCode(accessToken, ettelephoneNum.getText().toString().trim());
                        }
                    }

            }
        });
        etAuthCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                error.setVisibility(View.GONE);
            }
        });
        newpassword.setDrawableRightListener(new XEditText.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                if (eyeclose) {
                    newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_open, 0);
                    newpassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeclose = false;
                } else {
                    newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.eye_close, 0);
                    newpassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeclose = true;
                }
            }
        });
        newpassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                passwordmessage.setVisibility(View.VISIBLE);
            }
        });
    }

    private void checkPhone(final String telephoneNum) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    BaseResponseBean<Boolean> responseBean = (BaseResponseBean<Boolean>) msg.obj;
                    if(responseBean.getData()){
                        SendAuthCode(accessToken, ettelephoneNum.getText().toString().trim());
                    }
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(BindtelephoneActivity.this);
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<Boolean> responseBean = appContext.checkPhone(telephoneNum);
                    msg.what =1;
                    msg.obj = responseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void bindphone(final String accessToken, final String authCode, final String telephone) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(BindtelephoneActivity.this);
                }else{
                    BaseResponseBean<Boolean> responseBean = (BaseResponseBean<Boolean>) msg.obj;
                    boolean is = responseBean.getData();
                    if(is == true) {
                        UIHelper.jump2Activity(BindtelephoneActivity.this, HomeActivity.class);
                    }else {
                        error.setVisibility(View.VISIBLE);
                    }
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<Boolean> responseBean = appContext.bindphone(accessToken,authCode,telephone);
                    msg.what =1;
                    msg.obj = responseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void SendAuthCode(final String accessToken, final String telephoneNum) {
        mc.start();
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

    class MyCountDownTimer extends CountDownTimer{

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {
            bnSendauthCode.setText(millisUntilFinished/1000+"秒后重新发送");
            bnSendauthCode.setEnabled(false);
        }

        @Override
        public void onFinish() {
            bnSendauthCode.setText("点击重新发送");
            bnSendauthCode.setEnabled(true);
        }
    }
}
