package com.fclassroom.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.TreeBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.TagView.Tag;
import com.fclassroom.app.widget.TagView.TagView;
import com.fclassroom.appstudentclient.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 标签搜索、增加页
 */
public class AddwrongtagActivity extends BaseActivity {

    private TextView cancle;
    private TextView sure;
    private RelativeLayout searchView;
    private ListView recommendListview;
    private ListView knowledge;
    private EditText editTextTag;
    private TextView addKnowledgePoint;
    private TagView tagView;
    private RelativeLayout rlTag;
    private LinearLayout view1, view2;
    AppContext appContext;
    String accessToken;
    int gradeId;
    int subjectId;
    Tag tag;
    int examQuestionId;
    StringBuilder stringBuilder = new StringBuilder("");
    static final String[] arr = {
            "abc", "good", "baidu", "ni ku", "mitu", "sldf", "android", "apk"
    };
    List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwrongtag);
        examQuestionId = (int) getIntent().getSerializableExtra("examQuestionId");
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        initView();
        initData();
    }

    private void initData() {
        getKnowledgePoint(accessToken, gradeId, subjectId);
    }

    private void getKnowledgePoint(final String accessToken, final int gradeId, final int subjectId) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    appContext.getKnowledgePoint(accessToken, gradeId, subjectId);
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initView() {
        cancle = (TextView) findViewById(R.id.tv_cancle);
        sure = (TextView) findViewById(R.id.tv_sure);
        recommendListview = (ListView) findViewById(R.id.listview_recommend);
        knowledge = (ListView) findViewById(R.id.lv_knowledge);
        editTextTag = (EditText) findViewById(R.id.edit_tag);
        tagView = (TagView) findViewById(R.id.tagview);
        rlTag = (RelativeLayout) findViewById(R.id.rl_tagview);
        addKnowledgePoint = (TextView) findViewById(R.id.tv_addKnowledgePoint);
        view1 = (LinearLayout) findViewById(R.id.view1);
        view2 = (LinearLayout) findViewById(R.id.view2);
        addKnowledgePoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view1.setVisibility(View.GONE);
                view2.setVisibility(View.VISIBLE);
                getTopLevelKnos(accessToken, gradeId, subjectId);
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("tags", stringBuilder.toString());
                setResult(1, intent);
                AppManager.getAppManager().finishActivity();
            }
        });
        editTextTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
//                    UIHelper.ToastMessage(AddwrongtagActivity.this, v.getText().toString());
                    tag = new Tag(v.getText().toString());
                    tagView.add(tag);
                    tagView.drawTags();
                    stringBuilder.append(v.getText().toString()).append(" ");
                    addErrorQuestionTag(accessToken, subjectId, examQuestionId, v.getText().toString());
                    editTextTag.setText("");
                    return true;
                }
                return false;
            }
        });
    }

    private void getTopLevelKnos(final String accessToken, final int gradeId, final int subjectId) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what ==1){
                    BaseResponseBean<List<TreeBean>> responseBean = (BaseResponseBean<List<TreeBean>>) msg.obj;
                    List<TreeBean> listTreeBean = responseBean.getData();
                    List<String> list = new ArrayList<String>();
                    for(TreeBean treeBean:listTreeBean){
                        list.add(treeBean.getName());
                        System.out.println(treeBean.getName());
                    }
                    ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddwrongtagActivity.this,android.R.layout.simple_list_item_1,list);
                    knowledge.setAdapter(arrayAdapter);
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(AddwrongtagActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<List<TreeBean>> responseBean =
                            appContext.getTopLevelKnos(accessToken, gradeId, subjectId);
                    msg.what = 1;
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

    private void addErrorQuestionTag(final String accessToken, final int subjectId, final int examQuestionId, final String tagname) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    appContext.addErrorQuestionTag(accessToken, subjectId, examQuestionId, tagname);
                } catch (AppException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addwrongtag, menu);
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
