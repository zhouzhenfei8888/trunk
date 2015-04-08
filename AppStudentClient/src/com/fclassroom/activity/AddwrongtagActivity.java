package com.fclassroom.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签搜索、增加页
 */
public class AddwrongtagActivity extends BaseActivity {

    private TextView cancle;
    private TextView sure;
    private EditText searchView;
    private ListView relateListview;
    private ListView recommendListview;
    static final String[] arr = {
            "abc", "good", "baidu", "ni ku", "mitu", "sldf", "android", "apk"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addwrongtag);
        initView();
    }

    private void initView() {
        cancle = (TextView) findViewById(R.id.tv_cancle);
        sure = (TextView) findViewById(R.id.tv_sure);
        searchView = (EditText) findViewById(R.id.searchview);
        relateListview = (ListView) findViewById(R.id.listview_relate);
        recommendListview = (ListView) findViewById(R.id.listview_recommend);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        relateListview.setAdapter(new ArrayAdapter<String>(AddwrongtagActivity.this, android.R.layout.simple_list_item_1, arr));
        relateListview.setTextFilterEnabled(true);
        relateListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) ((TextView)view).getText();
//                searchView.setQuery(item,false);
            }
        });
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                UIHelper.ToastMessage(AddwrongtagActivity.this,"你选的是"+query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                if (TextUtils.isEmpty(newText)) {
//                    //清楚ListView的过滤
//                    relateListview.clearTextFilter();
//                    relateListview.setVisibility(View.GONE);
//                } else {
//                    //使用用户输入的内容对ListView的列表项进行过滤
//                    relateListview.setFilterText(newText);
//                    relateListview.setVisibility(View.VISIBLE);
//                }
//                return true;
//            }
//        });
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
