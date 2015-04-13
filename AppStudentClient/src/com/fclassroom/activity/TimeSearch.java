package com.fclassroom.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TimeSearch extends ActionBarActivity {
    TextView startTimeTv;
    TextView endTimeTv;
    Toolbar mToolbar;
    ListView listView;
    AppContext appContext;
    String accessToken;
    int gradeId;
    int subjectId;
    int pageSize = 20;
    int pageNo1 = 0;
    int unOrganize = 0;
    int noteBookId;
    private String orderBy = "orderTime";
    private final static String orderUp = "ASC";
    private static String orderUpOrDown = orderUp;
    List<PageBean.SubjectItemBean> listData;
    SubjectAdapter subjectAdapter;
    public Calendar sinceTime = Calendar.getInstance();
    public Calendar maxTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_search);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        initToolbar();
        listData = new ArrayList<PageBean.SubjectItemBean>();
        startTimeTv = (TextView) findViewById(R.id.start_time_tv);
        endTimeTv = (TextView) findViewById(R.id.end_time_tv);
        listView = (ListView) findViewById(R.id.listview);
        subjectAdapter = new SubjectAdapter(this, listData, listView);
        listView.setAdapter(subjectAdapter);
        startTimeTv.setOnClickListener(new View.OnClickListener() {


            private int year;
            private int month;
            private int day;
            private Calendar mycalendar;

            @Override
            public void onClick(final View v) {
                // 初始化Calendar日历对象
                if (mycalendar == null) {
                    mycalendar = Calendar.getInstance(Locale.CHINA);
                    Date mydate = new Date(); // 获取当前日期Date对象
                    mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期

                    year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
                    month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
                    day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
                }
                // 创建DatePickerDialog对象
                DatePickerDialog dpd = new DatePickerDialog(TimeSearch.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int myyear,
                                                  int monthOfYear, int dayOfMonth) {
                                // 可能会触发两次 过滤一次
                                if (view.isShown()) {
                                    // 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
                                    year = myyear;
                                    month = monthOfYear;
                                    day = dayOfMonth;
                                    // 更新日期
                                    updateDate(v, year, month, day);
                                }
                            }
                        }, year, month, day);
                dpd.show();// 显示DatePickerDialog组件
            }

        });
        endTimeTv.setOnClickListener(new View.OnClickListener() {

            private int year;
            private int month;
            private int day;
            private Calendar mycalendar;

            @Override
            public void onClick(final View v) {
                // 初始化Calendar日历对象
                if (mycalendar == null) {
                    mycalendar = Calendar.getInstance(Locale.CHINA);
                    Date mydate = new Date(); // 获取当前日期Date对象
                    mycalendar.setTime(mydate);// //为Calendar对象设置时间为当前日期

                    year = mycalendar.get(Calendar.YEAR); // 获取Calendar对象中的年
                    month = mycalendar.get(Calendar.MONTH);// 获取Calendar对象中的月
                    day = mycalendar.get(Calendar.DAY_OF_MONTH);// 获取这个月的第几天
                }
                // 创建DatePickerDialog对象
                DatePickerDialog dpd = new DatePickerDialog(TimeSearch.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int myyear,
                                                  int monthOfYear, int dayOfMonth) {
                                // 可能会触发两次 过滤一次
                                if (view.isShown()) {
                                    // 修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
                                    year = myyear;
                                    month = monthOfYear;
                                    day = dayOfMonth;
                                    // 更新日期
                                    updateDate(v, year, month, day);
                                }
                            }
                        }, year, month, day);
                dpd.show();// 显示DatePickerDialog组件
            }

        });
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("自然月搜索");
        mToolbar.setNavigationIcon(R.drawable.ic_launcher);
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
    }

    /**
     * 当DatePickerDialog关闭时，更新日期显示
     */
    public void updateDate(View v, int year, int month, int day) {
        switch (v.getId()) {
            case R.id.start_time_tv:
                sinceTime.set(year, month, day);
                break;
            case R.id.end_time_tv:
                maxTime.set(year, month, day);
                break;
        }
        if (sinceTime.after(maxTime)) {
            UIHelper.ToastMessage(this, "结束时间不能早于开始时间，请重新选择");
            return;
        }
        ((TextView) v).setText(year + "." + (month + 1) + "." + day);
//        mController.getExamListFromServer();
//        new BigDecimal(sinceTime.getTimeInMillis() + "").divide(new BigDecimal("1000")).longValue();
        Date starttime = sinceTime.getTime();
        Date endtime = maxTime.getTime();
        int start = (int) starttime.getTime()/1000;
        int end = (int) endtime.getTime()/1000;
        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown,start,end);
    }

    private void getSubjectList(final String accessToken, final int gradeId, final int subjectId, final int pageSize, final int pageNo, final int unOrganize, final String orderBy, final String orderUpOrDown, final int startTime, final int endTime) {
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "正在加载。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PageBean> response = (BaseResponseBean<PageBean>) msg.obj;
                    List<PageBean.SubjectItemBean> list = response.getData().getList();
                    listData.clear();
                    listData.addAll(list);
//                    subjectAdapter.notifyDataSetChanged();
                    subjectAdapter = new SubjectAdapter(TimeSearch.this, listData, listView);
                    listView.setAdapter(subjectAdapter);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(TimeSearch.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(TimeSearch.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<PageBean> response = appContext.getTimeSearchSubjectDetail(accessToken, gradeId, subjectId, noteBookId, unOrganize, orderBy, orderUpOrDown, pageNo, pageSize, startTime, endTime);
                    if (response.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = response;
                    } else if (response.getError_code() != 0) {
                        msg.what = 0;
                        msg.obj = response.getError_msg();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_search, menu);
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
