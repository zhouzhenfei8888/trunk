package com.fclassroom.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.TextView;

import com.fclassroom.app.common.UIHelper;
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
    private TextView checkResult;
    private TextView addWrongTag;
    private LinearLayout remark;
    private TextView remarkInfo;
    private Button previous;
    private Button next;
    private TextView titleBottom;
    private Toolbar toolbar;

    String arr[] = {"精题本", "自定义A本", "自定义B本"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("第5题");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_launcher);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initViews() {
        bookname = (TextView) findViewById(R.id.tv_bookname);
        ratingBar = (RatingBar) findViewById(R.id.ratingbar);
        change = (TextView) findViewById(R.id.tv_change);
        difficult = (TextView) findViewById(R.id.tv_difficulty);
        knowledgePoint = (TextView) findViewById(R.id.tv_knowledgepoint);
        examfrom = (TextView) findViewById(R.id.tv_examfrom);
        subject = (ImageView) findViewById(R.id.iv_subject);
        checkResult = (TextView) findViewById(R.id.tv_checkresult);
        addWrongTag = (TextView) findViewById(R.id.tv_addwrongtag);
        remark = (LinearLayout) findViewById(R.id.linear_remark);
        remarkInfo = (TextView) findViewById(R.id.tv_remark);
        previous = (Button) findViewById(R.id.bn_previous);
        next = (Button) findViewById(R.id.bn_next);
        titleBottom = (TextView) findViewById(R.id.tv_titlebottom);
      /*  back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDelete(DetailActivity.this);
            }
        });*/
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
        checkResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new AnimDialog(DetailActivity.this).showDialog(R.layout.dialog_checkresult, 80, 40);
                showDialogFromBottom();
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
                UIHelper.jump2Activity(DetailActivity.this,RemarkActivity.class);
                overridePendingTransition(R.anim.openfrombottom,R.anim.nomove);
            }
        });
    }

    private void showActivityFromBottom() {
        UIHelper.jump2Activity(this, AddwrongtagActivity.class);
        overridePendingTransition(R.anim.openfrombottom, R.anim.nomove);
    }

    /**
     * 实际用activity处理，dialog动画调用好像有些问题
     */
    private void showDialogFromBottom() {
        UIHelper.jump2Activity(this, DialogcheckanswerActivity.class);
        overridePendingTransition(R.anim.openfrombottom, R.anim.nomove);
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
