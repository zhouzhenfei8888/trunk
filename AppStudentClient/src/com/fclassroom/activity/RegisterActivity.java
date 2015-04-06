package com.fclassroom.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.XEditText;
import com.fclassroom.appstudentclient.R;

public class RegisterActivity extends BaseActivity {

    private Toolbar mtoolbar;
    private XEditText password;
    private Button register;
    private boolean eyeclose = true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
        initToolbar();
		initViews();
	}

    private void initToolbar() {
        mtoolbar = (Toolbar)findViewById(R.id.mtoolbar);
        mtoolbar.setTitle("");
        mtoolbar.setNavigationIcon(R.drawable.ic_launcher);
        setSupportActionBar(mtoolbar);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.this.finish();
            }
        });
    }

    private void initViews() {
		// TODO Auto-generated method stub
        password = (XEditText)findViewById(R.id.edit_password);
        register = (Button) findViewById(R.id.bn_register);
        //密码是否可见
        password.setDrawableRightListener(new XEditText.DrawableRightListener() {
            @Override
            public void onDrawableRightClick(View view) {
                if(eyeclose){
                    password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.eye_open,0);
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    eyeclose = false;
                }else{
                    password.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.eye_close,0);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    eyeclose = true;
                }
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.ToastMessage(RegisterActivity.this,"注册成功");
                UIHelper.jump2Activity(RegisterActivity.this,BindActivity.class);
            }
        });
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.register, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
