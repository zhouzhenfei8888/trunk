package com.fclassroom.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.AppManager;
import com.fclassroom.activity.Fragment.CollateFragment;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ErrorBookBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.URLs;
import com.fclassroom.app.common.BitmapCache;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.TagView.Tag;
import com.fclassroom.app.widget.TagView.TagView;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 题目详细页
 */
public class DetailActivity extends BaseActivity {
    private TextView title;
    private TextView bookname;
    private TextView change;
    private RatingBar ratingBar;
    private TextView difficult;
    private TextView knowledgePoint;
    private TextView examfrom;
    private ImageView subject;
    private ImageView answer;
    private ImageView checkResult;
    private LinearLayout addWrongTag;
    private LinearLayout remark;
    private TextView remarkInfo;
    private ImageView previous;
    private ImageView next;
    private TextView titleBottom;
    private RelativeLayout toolbar;
    private ImageView iv_back;
    private ImageView iv_rubbish;
    private TagView tagView;
    String arr[] = {"精题本", "自定义A本", "自定义B本"};
    private PageBean.SubjectItemBean subjectItemBean;
    private float diffRecord;
    private String diffStr;
    private RequestQueue mQueue;
    private ImageLoader imageLoader;
    private AppContext appContext;
    private String accessToken;
    private int gradeId;
    private int subjectId;
    private CollateFragment.BookAdapter bookAdapter;
    private List<ErrorBookBean> ErrorBookList;
    private List<String> stringList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        subjectItemBean = (PageBean.SubjectItemBean) getIntent().getSerializableExtra("value");
        appContext = (AppContext) getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        initData();
        initToolbar();
        initViews();
    }

    private void initData() {
        ErrorBookList = new ArrayList<ErrorBookBean>();
        stringList = new ArrayList<String>();
        getErrorBookList(accessToken,gradeId,subjectId);
    }

    //获取错题本名字列表
    private void getErrorBookList(final String accessToken, final int gradeId, final int subjectId) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<ArrayList<ErrorBookBean>> responseBean = (BaseResponseBean<ArrayList<ErrorBookBean>>) msg.obj;
                    ErrorBookList.clear();
                    ErrorBookList.addAll(responseBean.getData());
                    for(ErrorBookBean errorBookBean:ErrorBookList){
                        stringList.add(errorBookBean.getName());
                    }
                    arr = (String[])stringList.toArray(new String[stringList.size()]);
//                    bookAdapter.notifyDataSetChanged();
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(DetailActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(DetailActivity.this);
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

    private void initToolbar() {
        toolbar = (RelativeLayout) findViewById(R.id.rl_toolbar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_rubbish = (ImageView) findViewById(R.id.iv_rubbish);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(DetailActivity.this,HomeActivity.class);
                AppManager.getAppManager().finishActivity();
            }
        });
        iv_rubbish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDelete(DetailActivity.this);
            }
        });
    }

    private void initViews() {
        mQueue = Volley.newRequestQueue(this.getApplicationContext());
        bookname = (TextView) findViewById(R.id.tv_bookname);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        change = (TextView) findViewById(R.id.tv_change);
        difficult = (TextView) findViewById(R.id.tv_difficulty);
        knowledgePoint = (TextView) findViewById(R.id.tv_knowledgepoint);
        examfrom = (TextView) findViewById(R.id.tv_examfrom);
        subject = (ImageView) findViewById(R.id.iv_subject);
        answer = (ImageView) findViewById(R.id.iv_answer);
        addWrongTag = (LinearLayout) findViewById(R.id.linear_addwrongtag);
        remark = (LinearLayout) findViewById(R.id.linear_remark);
        remarkInfo = (TextView) findViewById(R.id.tv_remark);
        previous = (ImageView) findViewById(R.id.bn_previous);
        next = (ImageView) findViewById(R.id.bn_next);
        tagView = (TagView) findViewById(R.id.tagview);
        checkResult = (ImageView) findViewById(R.id.iv_checkresult);
        bookname.setText(subjectItemBean.getNotebookNames());
        knowledgePoint.setText("知识点：" + subjectItemBean.getKnoNames());
        examfrom.setText("来源：" + subjectItemBean.getExamName());
        diffRecord = Float.parseFloat(subjectItemBean.getScoreRate());
        if (diffRecord <= 0.1) {
            diffStr = "简单";
        } else if (diffRecord <= 0.25) {
            diffStr = "普通";
        } else if (diffRecord <= 0.4) {
            diffStr = "一般难";
        } else if (diffRecord <= 1) {
            diffStr = "难";
        }
        difficult.setText("难度：" + diffStr);
        ratingBar.setRating((float) (subjectItemBean.getSignLevel()));
        //图片处理
        imageLoader = new ImageLoader(mQueue, new BitmapCache());
        ImageLoader.ImageListener imageListener = imageLoader.getImageListener(subject, R.drawable.default_jpeg, R.drawable.iv_nodata);
        imageLoader.get(URLs.HOST_IMG + subjectItemBean.getContentImage(), imageListener);

        ImageLoader.ImageListener imageListenerAnswer = imageLoader.getImageListener(answer, R.drawable.default_jpeg, R.drawable.iv_nodata);
        imageLoader.get(URLs.HOST_IMG + subjectItemBean.getAnswerImg(), imageListenerAnswer);
        //错因标签
        String tagnames = subjectItemBean.getTagNames();
        if (!"".equals(tagnames)) {
            String[] tags = null;
            if (tagnames.contains(",")) {
                tags = tagnames.split(",");
            }
            for (String name : tags) {
                Tag tag = new Tag(name);
                tagView.add(tag);
            }
        }
        //remark
        remarkInfo.setText(subjectItemBean.getRemark());
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        checkResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogFromBottom();
            }
        });

        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChange(DetailActivity.this);
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                ratingBar.setRating(rating);
                int signLevel = (int) rating;
                SetQuestionSignLevel(accessToken, subjectItemBean.getExamQuestionId(), signLevel);
            }
        });
        addWrongTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivityFromBottom();
            }
        });
        remark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(DetailActivity.this, RemarkActivity.class);
//                overridePendingTransition(R.anim.openfrombottom,R.anim.nomove);
            }
        });
    }

    private void SetQuestionSignLevel(final String accessToken, final int examQuestionId, final int signLevel) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<String> responseBean = (BaseResponseBean<String>) msg.obj;
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(DetailActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(DetailActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.setQuestionSignLevel(accessToken, examQuestionId, signLevel);
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

    private void showActivityFromBottom() {
        UIHelper.jump2Activity(this, AddwrongtagActivity.class);
//        overridePendingTransition(R.anim.openfrombottom, R.anim.nomove);
    }

    /**
     * 实际用activity处理，dialog动画调用好像有些问题
     */
    private void showDialogFromBottom() {
        UIHelper.jump2Activity(this, DialogcheckanswerActivity.class);
//        overridePendingTransition(R.anim.openfrombottom, R.anim.nomove);
    }

    private void showDialogDelete(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dialog, null, false);
        TextView tv = (TextView) view.findViewById(R.id.tv_dialog);
        tv.setText("你确定要删除所选项目吗?");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int delFlag = 0;
                deleteErrorQuestion(accessToken, subjectItemBean.getExamQuestionId(), delFlag);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void deleteErrorQuestion(final String accessToken, final int examQuestionId, final int delFlag) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<String> responseBean = (BaseResponseBean<String>) msg.obj;
                    UIHelper.ToastMessage(DetailActivity.this, responseBean.getData().toString());
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(DetailActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(DetailActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.deleteErrorQuestion(accessToken, examQuestionId, delFlag);
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

    private void showDialogChange(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddErrorQuestionToNoteBook(accessToken,subjectItemBean.getExamQuestionId(),ErrorBookList.get(which).getId());
                        bookname.setText(subjectItemBean.getNotebookNames()+","+ErrorBookList.get(which).getName().toString());
                    }
                });
        builder.create().show();
    }

    private void AddErrorQuestionToNoteBook(final String accessToken, final int examQuestionId, final int id) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){

                }else if(msg.what ==-1){
                    ((AppException)msg.obj).makeToast(DetailActivity.this);
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    String str = appContext.AddErrorQuestionToNoteBook(accessToken,examQuestionId,id);
                    msg.what = 1;
                    msg.obj = str;
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
        getMenuInflater().inflate(R.menu.menu_detail, menu);
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
