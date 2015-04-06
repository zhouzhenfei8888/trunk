package com.fclassroom.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.Fragment.CollateFragment;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.NoteBookBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import java.io.UnsupportedEncodingException;

public class DialognewbookActivity extends Activity {

    private Button cancle, sure;
    private EditText bookname;
    private AppContext appContext;
    private String accessToken;
    private int gradeId;
    private int subjectId;
    private CollateFragment collateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialognewbook);
        initViews();
    }

    private void initViews() {
        //获取本地信息
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext,PreferenceUtils.SUBJECT_ID);
        //控件初始化
        cancle = (Button) findViewById(R.id.bn_cancle);
        sure = (Button) findViewById(R.id.bn_sure);
        bookname = (EditText) findViewById(R.id.edit_bookname);
        bookname.setFocusable(true);
        bookname.setFocusableInTouchMode(true);
        bookname.requestFocus();
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNoteBook(accessToken,gradeId,subjectId,bookname.getText().toString().trim());
            }
        });
    }

    private void addNoteBook(final String accessToken, final int gradeId, final int subjectId, final String bookname) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    BaseResponseBean<NoteBookBean> responseBean = (BaseResponseBean<NoteBookBean>) msg.obj;
                }else if(msg.what == 0){
                    UIHelper.ToastMessage(DialognewbookActivity.this,msg.obj.toString());
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(DialognewbookActivity.this);
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<NoteBookBean> responseBean = appContext.addNoteBook(accessToken,gradeId,subjectId,bookname);
                    if(responseBean.getError_code() == 0){
                        msg.what = 1;
                        msg.obj = responseBean;
                    }else if(responseBean.getError_code() != 0){
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_dialognewbook, menu);
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
