package com.fclassroom.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ErrorBookBean;
import com.fclassroom.app.bean.ErrorTagBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchActivity extends BaseActivity {
    private static String data[] = {"Dell Inspiron", "HTC One X", "HTC Wildfire S", "HTC Sense", "HTC Sensation XE",
            "iPhone 4S", "Samsung Galaxy Note 800",
            "Samsung Galaxy S3", "MacBook Air", "Mac Mini", "MacBook Pro"};
    private ListView lvSearchRecord;
    private ArrayAdapter<String> arrayAdapterRecord, arrayAdapterTags, arrayAdapterNoteBooks;
    private EditText editText;
    private ImageView delete, cleanRecord;
    private TextView cancle;
    private LinearLayout linearTimeSearch, linearSearchRecord, linearTags, linearNoteBooks;
    private ListView lvTags, lvNoteBooks;
    private AppContext appContext;
    private String accessToken;
    private int gradeId, subjectId;
    private List<String> TagNameList;
    private List<String> ErrorBookNameList;
    List<ErrorTagBean> ErrorTagBeanlist;
    List<ErrorBookBean> ErrorBookBeanList;
    List<String> historylist;

    private void getTags() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<List<ErrorTagBean>> responseBean = (BaseResponseBean<List<ErrorTagBean>>) msg.obj;
                    ErrorTagBeanlist = responseBean.getData();
                    for (ErrorTagBean errorTagBean : ErrorTagBeanlist) {
                        TagNameList.add(errorTagBean.getName());
                    }
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(SearchActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(SearchActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<List<ErrorTagBean>> responseBean = appContext.getErrorTagList(accessToken, subjectId);
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
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    //获取错题本名字列表
    private void getErrorBookList(final String accessToken, final int gradeId, final int subjectId) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<ArrayList<ErrorBookBean>> responseBean = (BaseResponseBean<ArrayList<ErrorBookBean>>) msg.obj;
                    ErrorBookBeanList = responseBean.getData();
                    for (ErrorBookBean errorBookBean : ErrorBookBeanList) {
                        ErrorBookNameList.add(errorBookBean.getName());
                    }
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(SearchActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(SearchActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<ArrayList<ErrorBookBean>> responseBean = appContext.getNoteBookList(accessToken, gradeId, subjectId);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        TagNameList = new ArrayList<String>();
        ErrorBookNameList = new ArrayList<String>();
        getTags();
        getErrorBookList(accessToken, gradeId, subjectId);
        initViews();
    }

    private void initViews() {
        editText = (EditText) findViewById(R.id.et_search);
        lvSearchRecord = (ListView) findViewById(R.id.lv_searchrecoder);
        delete = (ImageView) findViewById(R.id.iv_delete);
        cancle = (TextView) findViewById(R.id.cancle);
        linearTimeSearch = (LinearLayout) findViewById(R.id.linear_timesearch);
        linearSearchRecord = (LinearLayout) findViewById(R.id.linear_searchrecord);
        linearTags = (LinearLayout) findViewById(R.id.linear_tags);
        linearNoteBooks = (LinearLayout) findViewById(R.id.linear_notebooks);
        lvTags = (ListView) findViewById(R.id.lv_tags);
        lvNoteBooks = (ListView) findViewById(R.id.lv_notebooks);
        cleanRecord = (ImageView) findViewById(R.id.iv_cleanrecord);
        String history = PreferenceUtils.getString(appContext, PreferenceUtils.HISTORY);
        if(!"".equals(history)){
            history = history.substring(1,history.length());
        }
        arrayAdapterRecord = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, Arrays.asList(history.split(" ")));
        arrayAdapterTags = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, TagNameList);
        arrayAdapterNoteBooks = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ErrorBookNameList);
        lvSearchRecord.setAdapter(arrayAdapterRecord);
        lvTags.setAdapter(arrayAdapterTags);
        lvNoteBooks.setAdapter(arrayAdapterNoteBooks);
        linearTimeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(SearchActivity.this,TimeSearch.class);
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.getAppManager().finishActivity();
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if ("".equals(editText.getText().toString().trim())) {
                    visable1();
                } else {
                    visable2();
                    lvTags.setVisibility(View.VISIBLE);
                    lvNoteBooks.setVisibility(View.VISIBLE);
                    SearchActivity.this.arrayAdapterTags.getFilter().filter(s);
                    SearchActivity.this.arrayAdapterNoteBooks.getFilter().filter(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
//                    lvSearchRecord.setVisibility(View.GONE);
                }
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        lvSearchRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editText.setText(((TextView) view).getText());
            }
        });
        lvTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String etName = ((TextView) view).getText().toString();
                int tagId = 0;
                editText.setText(etName);
                for (ErrorTagBean errorTagBean : ErrorTagBeanlist) {
                    if (errorTagBean.getName() == etName) {
                        tagId = errorTagBean.getId();
                        break;
                    }
                }
                //保存收索过的信息
                String history = PreferenceUtils.getString(appContext, PreferenceUtils.HISTORY, "");
                if (!Arrays.asList(history.split(" ")).contains(etName)) {
                    history += " " + etName;
                    PreferenceUtils.putString(appContext, PreferenceUtils.HISTORY,
                            history);
                }
                arrayAdapterTags.notifyDataSetChanged();
                history = history.substring(1,history.length());
                arrayAdapterRecord = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, Arrays.asList(history.split(" ")));
                lvSearchRecord.setAdapter(arrayAdapterRecord);
                UIHelper.jump2Activity(SearchActivity.this, NotebookActivity.class, tagId, etName, "search");
                editText.setText("");
            }
        });
        cleanRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferenceUtils.putString(appContext, PreferenceUtils.HISTORY, "");
                String history = "";
                arrayAdapterRecord = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, Arrays.asList(history.split(" ")));
                lvSearchRecord.setAdapter(arrayAdapterRecord);
            }
        });
        lvNoteBooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String etName = ((TextView) view).getText().toString().trim();
                int bookId = 0;
                editText.setText(etName);
                for (ErrorBookBean errorBookBean : ErrorBookBeanList) {
                    if (errorBookBean.getName() == etName) {
                        bookId = errorBookBean.getId();
                        break;
                    }
                }
                String history = PreferenceUtils.getString(appContext, PreferenceUtils.HISTORY, "");
                if (!Arrays.asList(history.split(" ")).contains(etName)) {
                    history += " " + etName;
                    PreferenceUtils.putString(appContext, PreferenceUtils.HISTORY,
                            history);
                }
                arrayAdapterNoteBooks.notifyDataSetChanged();
                history = history.substring(1,history.length());
                arrayAdapterRecord = new ArrayAdapter<String>(SearchActivity.this, android.R.layout.simple_list_item_1, Arrays.asList(history.split(" ")));
                lvSearchRecord.setAdapter(arrayAdapterRecord);
                UIHelper.jump2Activity(SearchActivity.this, NotebookActivity.class, bookId, etName, "search");
                editText.setText("");
            }
        });
    }

    public void visable1() {
        linearTimeSearch.setVisibility(View.VISIBLE);
        linearSearchRecord.setVisibility(View.VISIBLE);
        lvSearchRecord.setVisibility(View.VISIBLE);
        linearTags.setVisibility(View.GONE);
        linearNoteBooks.setVisibility(View.GONE);
        lvTags.setVisibility(View.GONE);
        lvNoteBooks.setVisibility(View.GONE);
        linearTimeSearch.setFocusable(true);
        linearSearchRecord.setFocusable(true);
        linearTags.setFocusable(false);
        linearNoteBooks.setFocusable(false);
    }

    public void visable2() {
        linearTimeSearch.setVisibility(View.GONE);
        linearSearchRecord.setVisibility(View.GONE);
        lvSearchRecord.setVisibility(View.GONE);
        linearTags.setVisibility(View.VISIBLE);
        linearNoteBooks.setVisibility(View.VISIBLE);
        linearTimeSearch.setFocusable(false);
        linearSearchRecord.setFocusable(false);
        linearTags.setFocusable(true);
        linearNoteBooks.setFocusable(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
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
