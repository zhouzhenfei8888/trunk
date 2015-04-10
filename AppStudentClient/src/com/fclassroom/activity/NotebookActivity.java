package com.fclassroom.activity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.List;

public class NotebookActivity extends BaseActivity {

    private Toolbar mtoolBar;
    private ListView mlistView;
    private TextView total, printall;
    private List<PageBean.SubjectItemBean> list;
    private int id;
    private String name;
    private String from;
    private SubjectAdapter subjectAdapter;
    private AppContext appContext;
    private String accessToken;
    private int gradeId;
    private int subjectId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notebook);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        id = (int) getIntent().getSerializableExtra("value");
        name = (String) getIntent().getSerializableExtra("value2");
        from = (String) getIntent().getSerializableExtra("from");
        initToolbar();
        initViews();
        initData();
    }

    private void initData() {
//        for (int i = 0; i < 20; i++) {
//            HashMap<String, Object> data = new HashMap<String, Object>();
//            data.put("examdate", "2015年3月3日");
//            data.put("examname", "梅村中学第三次模拟考试");
//            data.put("examsrc", R.drawable.subject);
//            data.put("rating", (float) 3.0);
//            list.add(data);
//        }
        if ("subject".equals(from)) {
            getExamQuestionsByExam(accessToken, gradeId, subjectId, id);
            printall.setVisibility(View.GONE);
            printall.setClickable(false);
        } else if ("print".equals(from)) {
            getPrintHistoryErrorQuestions(accessToken, gradeId, subjectId, id);
        }else if("collate".equals(from)){

        }
    }

    private void getPrintHistoryErrorQuestions(final String accessToken, final int gradeId, final int subjectId, final int printHistoryId) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "正在加载。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PageBean> responseBean = (BaseResponseBean<PageBean>) msg.obj;
                    List<PageBean.SubjectItemBean> newlist = responseBean.getData().getList();
                    list.addAll(newlist);
                    subjectAdapter = new SubjectAdapter(NotebookActivity.this, list, mlistView);
                    mlistView.setAdapter(subjectAdapter);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(NotebookActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(NotebookActivity.this);
                }
                progressDialog.dismiss();
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<PageBean> responseBean = appContext.getPrintHistoryErrorQuestions(accessToken, gradeId, subjectId, printHistoryId);
                    if (msg.what == 0) {
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
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void getExamQuestionsByExam(final String accessToken, final int gradeId, final int subjectId, final int examId) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "正在加载。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PageBean> responseBean = (BaseResponseBean<PageBean>) msg.obj;
                    List<PageBean.SubjectItemBean> newlist = responseBean.getData().getList();
                    list.addAll(newlist);
                    subjectAdapter = new SubjectAdapter(NotebookActivity.this, list, mlistView);
                    mlistView.setAdapter(subjectAdapter);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(NotebookActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(NotebookActivity.this);
                }
                progressDialog.dismiss();
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<PageBean> responseBean = appContext.getExamQuestionsByExam(accessToken, gradeId, subjectId, examId);
                    if (msg.what == 0) {
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
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void initToolbar() {
        mtoolBar = (Toolbar) findViewById(R.id.toolbar);
        mtoolBar.setNavigationIcon(R.drawable.ic_launcher);
        mtoolBar.setTitle(name);
        mtoolBar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mtoolBar);
        mtoolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initViews() {
        list = new ArrayList<PageBean.SubjectItemBean>();
        mlistView = (ListView) findViewById(R.id.listview_notebook);
        total = (TextView) findViewById(R.id.tv_total);
        printall = (TextView) findViewById(R.id.tv_printall);
        subjectAdapter = new SubjectAdapter(NotebookActivity.this, list, mlistView);
        mlistView.setAdapter(subjectAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notebook, menu);
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
