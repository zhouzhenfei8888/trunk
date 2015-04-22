package com.fclassroom.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.activity.Fragment.SubjectFragment;
import com.fclassroom.activity.Fragment.TopFragment;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.widget.PagerSlidingTabStrip;
import com.fclassroom.appstudentclient.R;

public class TopRankActivity extends BaseActivity {

    private Toolbar mToolbar;
    private PagerSlidingTabStrip mTabs;
    private ViewPager mPagers;
    private TextView textView;
    AppContext appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_rank);
        initToolbar();
        initViews();
    }

    private void initViews() {
        appContext = (AppContext) getApplication();
        textView = (TextView)findViewById(R.id.tv_schoolname);
        textView.setText(PreferenceUtils.getString(appContext,PreferenceUtils.SCHOOL_NAME));
        mTabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        mPagers = (ViewPager) findViewById(R.id.pagers);
        mPagers.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabs.setViewPager(mPagers);
        mTabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        initTabsValue();
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public final String TITLES[] = {"周榜", "月榜"};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return TopFragment.newInstance(i);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }

    /**
     * mPagerSlidingTabStrip默认值配置
     */
    private void initTabsValue() {
        // 底部游标颜色
        mTabs.setIndicatorColor(Color.parseColor("#5dc6a2"));
        // tab的分割线颜色
        mTabs.setDividerColor(Color.WHITE);
        // tab背景
        mTabs.setBackgroundColor(Color.WHITE);
        // tab底线高度
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        // 游标高度
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        mTabs.setSelectedTextColor(Color.parseColor("#5dc6a2"));
        // 正常文字颜色
        mTabs.setTextColor(getResources().getColor(R.color.black));
        mTabs.setTextSize(50);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("TOP排行榜");
        mToolbar.setTitleTextColor(Color.WHITE);
        mToolbar.setNavigationIcon(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_top_rank, menu);
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
