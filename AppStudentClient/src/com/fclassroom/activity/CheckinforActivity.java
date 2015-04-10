package com.fclassroom.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.StudentInfoBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

public class CheckinforActivity extends BaseActivity {

    private Button yes;
    private Button no;
    private static boolean fromenter = false;
    AppContext appContext;
    String accessToken;
    TextView tv_name, tv_idnumber, tv_schoolname, tv_gradename, tv_classname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkinfor);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        try {
            //判断是否从登入页面进入，如果是则还需要进行手机绑定
            fromenter = (boolean) getIntent().getSerializableExtra("value");
        } catch (Exception e) {
            e.printStackTrace();
        }
        initViews();
        initData();
    }

    private void initData() {
        getStudentInfo();
    }

    private void getStudentInfo() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<StudentInfoBean> responseBean = (BaseResponseBean<StudentInfoBean>) msg.obj;
                    appContext.saveStudentInfo(responseBean.getData());
                    tv_name.setText(responseBean.getData().getStudentName());
                    tv_idnumber.setText(responseBean.getData().getStudentNo());
                    tv_schoolname.setText(responseBean.getData().getSchoolName());
                    tv_gradename.setText(responseBean.getData().getGradeName());
                    tv_classname.setText(responseBean.getData().getClassName());
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(CheckinforActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(CheckinforActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<StudentInfoBean> responseBean = appContext.getStudentInfo(PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN));
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

    private void initViews() {
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_idnumber = (TextView) findViewById(R.id.tv_idnumber);
        tv_schoolname = (TextView) findViewById(R.id.tv_schoolname);
        tv_gradename = (TextView) findViewById(R.id.tv_gradename);
        tv_classname = (TextView) findViewById(R.id.tv_classname);
        yes = (Button) findViewById(R.id.bn_yes);
        no = (Button) findViewById(R.id.bn_no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PreferenceUtils.getString(appContext, PreferenceUtils.ACCOUNT_LOGINPHONE)==null) {
                    UIHelper.jump2Activity(CheckinforActivity.this, BindtelephoneActivity.class,"bindtelephone");
                    AppManager.getAppManager().finishActivity();
                } else {
                    UIHelper.jump2Activity(CheckinforActivity.this, HomeActivity.class);
                    AppManager.getAppManager().finishActivity();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.putString(CheckinforActivity.this, PreferenceUtils.ACCESSTOKEN, "");
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
