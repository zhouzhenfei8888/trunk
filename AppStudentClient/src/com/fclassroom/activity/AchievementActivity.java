package com.fclassroom.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.fclassroom.app.widget.RoundProgressBar;
import com.fclassroom.app.widget.RoundProgressBarSmall;
import com.fclassroom.appstudentclient.R;

import java.util.zip.Inflater;

public class AchievementActivity extends BaseActivity {

    private Toolbar mToolbar;
    private ViewPager viewPager;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        initToolbar();
        initView();
    }

    private void initView() {
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
//                        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.three_second));
                        break;
                    case 2:
//                        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.three_third));
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        MyPagerAdapter myPagerAdapter = new MyPagerAdapter(AchievementActivity.this);
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
            LayoutInflater inflater  = LayoutInflater.from(context);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(context,R.layout.my_pager,null);
//            RoundProgressBarSmall roundProgressBarSmall = (RoundProgressBarSmall) findViewById(R.id.roundprogressbarsmall);
            switch (position){
                case 0:
//                    roundProgressBarSmall.setText("累计");
//                    roundProgressBarSmall.setText2("2510分钟");
                    break;
                case 1:
                    break;
                case 2:
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
