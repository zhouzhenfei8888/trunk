package com.fclassroom.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.widget.RoundProgressBarSmall;
import com.fclassroom.appstudentclient.R;


public class AchievementActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ViewPager viewPager;
    ImageView imageView;
    int saveTime,DefeatRate,OrgRate;
    AppContext appContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        initToolbar();
        initView();
    }

    private void initView() {
        appContext = (AppContext) getApplicationContext();
        saveTime = PreferenceUtils.getInt(appContext,PreferenceUtils.SAVETIME);
        DefeatRate = PreferenceUtils.getInt(appContext,PreferenceUtils.DEFEATRATE);
        OrgRate = PreferenceUtils.getInt(appContext,PreferenceUtils.ORGRATE);
        imageView = (ImageView)findViewById(R.id.iv_number);
        viewPager = (ViewPager)findViewById(R.id.viewpager);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                switch (i){
                    case 0:
                        imageView.setImageResource(R.drawable.three_first);
                        break;
                    case 1:
                        imageView.setImageResource(R.drawable.three_second);
                        break;
                    case 2:
                        imageView.setImageResource(R.drawable.three_third);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(this);
        viewPager.setAdapter(myPagerAdapter);
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("我的成就");
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

    class MyPagerAdapter extends PagerAdapter {

        private Context context;
        public MyPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.my_pager,container,false);
            TextView textView = (TextView) view.findViewById(R.id.tv_message);
            RoundProgressBarSmall roundProgressBarSmall = (RoundProgressBarSmall) view.findViewById(R.id.roundprogressbarsmall);
            switch (position){
                case 0:
                    roundProgressBarSmall.setTextColor(Color.parseColor("#c1c0c0"));
                    roundProgressBarSmall.setTextColor2(Color.parseColor("#5e5e5e"));
                    roundProgressBarSmall.setTextSize(60);
                    roundProgressBarSmall.setProgress(100);
                    roundProgressBarSmall.setText("累计");
                    roundProgressBarSmall.setText2(""+saveTime+"分钟");
                    textView.setText("节约抄题时间");
                    textView.setTextColor(Color.parseColor("#5dc6a2"));
                    break;
                case 1:
                    roundProgressBarSmall.setTextColor(Color.parseColor("#c1c0c0"));
                    roundProgressBarSmall.setTextColor2(Color.parseColor("#5e5e5e"));
                    roundProgressBarSmall.setTextSize(60);
                    roundProgressBarSmall.setProgress(OrgRate);
                    roundProgressBarSmall.setCricleColor(Color.parseColor("#ffba43"));
                    roundProgressBarSmall.setCricleProgressColor(Color.parseColor("#ffcf73"));
                    roundProgressBarSmall.setText("整理完整度");
                    roundProgressBarSmall.setText2(""+OrgRate+"%");
                    textView.setText("已整理"+OrgRate+"%的错题");
                    textView.setTextColor(Color.parseColor("#ffcf73"));
                    break;
                case 2:
                    roundProgressBarSmall.setTextColor(Color.parseColor("#c1c0c0"));
                    roundProgressBarSmall.setTextColor2(Color.parseColor("#5e5e5e"));
                    roundProgressBarSmall.setTextSize(60);
                    roundProgressBarSmall.setProgress(DefeatRate);
                    roundProgressBarSmall.setCricleColor(Color.parseColor("#3b8dfe"));
                    roundProgressBarSmall.setCricleProgressColor(Color.parseColor("#73aeff"));
                    roundProgressBarSmall.setText("超过");
                    roundProgressBarSmall.setText2(""+DefeatRate+"%的同学");
                    textView.setText("超过"+DefeatRate+"%的同学");
                    textView.setTextColor(Color.parseColor("#73aeff"));
                    break;
            }
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view == o;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_achievement, menu);
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
