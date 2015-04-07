package com.fclassroom.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
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
import com.fclassroom.AppManager;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.URLs;
import com.fclassroom.app.common.BitmapCache;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.TagView.Tag;
import com.fclassroom.app.widget.TagView.TagView;
import com.fclassroom.appstudentclient.R;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        subjectItemBean = (PageBean.SubjectItemBean) getIntent().getSerializableExtra("value");
        System.out.println(subjectItemBean.getExamName() + subjectItemBean.getId());
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        toolbar = (RelativeLayout) findViewById(R.id.rl_toolbar);
        iv_back = (ImageView) findViewById(R.id.iv_back);
        iv_rubbish = (ImageView) findViewById(R.id.iv_rubbish);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        //图片处理
        imageLoader = new ImageLoader(mQueue,new BitmapCache());
        ImageLoader.ImageListener imageListener = imageLoader.getImageListener(subject,R.drawable.default_jpeg,R.drawable.iv_nodata);
        imageLoader.get(URLs.HOST_NOTE+subjectItemBean.getContentImage(),imageListener);

        ImageLoader.ImageListener imageListenerAnswer = imageLoader.getImageListener(answer,R.drawable.default_jpeg,R.drawable.iv_nodata);
        imageLoader.get(URLs.HOST_NOTE + subjectItemBean.getAnswerImg(), imageListenerAnswer);
        //错因标签
        String tagnames = subjectItemBean.getTagNames();
        String [] tags = null;
        if(tagnames.contains(",")){
            tags = tagnames.split(",");
        }
        for(String name:tags){
            Tag tag = new Tag(name);
            tagView.add(tag);
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

    private void showDialogChange(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("新建")
                .setItems(arr, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
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
