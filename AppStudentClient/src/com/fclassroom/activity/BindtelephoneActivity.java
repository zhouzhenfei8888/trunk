package com.fclassroom.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

public class BindtelephoneActivity extends BaseActivity {

    private Toolbar mtoolbar;
    private Button bnSure;
    private TextView title;
    //bindphoneORfindpassword 0为bindphone,1为findpassword；
    private int bindphoneORfindpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bindtelephone);
        System.out.println(getIntent().getStringExtra("value"));
        //判断是bindphone或findpassword页面
        if ("findpassword".equals(getIntent().getStringExtra("value"))) {
            bindphoneORfindpassword = 1;
            System.out.println(bindphoneORfindpassword);
        } else {
            bindphoneORfindpassword = 0;
        }
        initToolbar();
        initViews();
    }

    private void initViews() {
        bnSure = (Button) findViewById(R.id.bn_sure);
        bnSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(BindtelephoneActivity.this, HomeActivity.class);
            }
        });
    }

    private void initToolbar() {
        mtoolbar = (Toolbar) findViewById(R.id.mtoolbar);
        title = (TextView)mtoolbar.findViewById(R.id.tv_title);
        if(bindphoneORfindpassword ==1 ){
            title.setVisibility(View.GONE);
            mtoolbar.setTitle("找回密码");
        }else if(bindphoneORfindpassword == 0){
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
}
