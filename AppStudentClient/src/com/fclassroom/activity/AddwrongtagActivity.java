package com.fclassroom.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.TagView.Tag;
import com.fclassroom.app.widget.TagView.TagView;
import com.fclassroom.appstudentclient.R;

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
    private EditText editTextTag;
    private TagView tagView;
    static final String[] arr = {
            "abc", "good", "baidu", "ni ku", "mitu", "sldf", "android", "apk"
    };
    List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwrongtag);
        initView();
    }

    private void initView() {
        cancle = (TextView) findViewById(R.id.tv_cancle);
        sure = (TextView) findViewById(R.id.tv_sure);
        searchView = (RelativeLayout) findViewById(R.id.searchview);
        recommendListview = (ListView) findViewById(R.id.listview_recommend);
        editTextTag = (EditText) findViewById(R.id.edit_tag);
        tagView = (TagView) findViewById(R.id.tagview);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        editTextTag.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                    UIHelper.ToastMessage(AddwrongtagActivity.this, v.getText().toString());
                    return true;
                }
                return false;
            }
        });
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
