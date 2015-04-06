package com.fclassroom.activity.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.fclassroom.activity.HomeActivity;
import com.fclassroom.app.widget.PagerSlidingTabStrip;
import com.fclassroom.app.widget.RoundProgressBar;
import com.fclassroom.appstudentclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements HomeActivity.HideTopHomeFragment {

    private PagerSlidingTabStrip mTabs;
    private ViewPager mPagers;
    public RelativeLayout mRelativeLayoutHead;
    private LinearLayout mLinearHome;
    private RoundProgressBar roundProgressBar;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mRelativeLayoutHead = (RelativeLayout) view.findViewById(R.id.relativelayout_head);
        mLinearHome = (LinearLayout) view.findViewById(R.id.linear_home);
        mTabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mPagers = (ViewPager) view.findViewById(R.id.pager);
        mPagers.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
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

        //环形进度条设置
        roundProgressBar = (RoundProgressBar) view.findViewById(R.id.roundprogressbar);
        roundProgressBar.setProgress(60);
        roundProgressBar.setProgress2(40);
        roundProgressBar.setText("456");
    }

    /**
     * mPagerSlidingTabStrip默认值配置
     */
    private void initTabsValue() {
        // 底部游标颜色
        mTabs.setIndicatorColor(Color.YELLOW);
        // tab的分割线颜色
        mTabs.setDividerColor(getResources().getColor(R.color.toolbar_color));
        // tab背景
        mTabs.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
        // tab底线高度
        mTabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        // 游标高度
        mTabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        mTabs.setSelectedTextColor(Color.WHITE);
        // 正常文字颜色
        mTabs.setTextColor(getResources().getColor(R.color.text_color_green));
        mTabs.setTextSize(50);
    }

    @Override
    public void DoHideTopHomeFragment() {
        TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
        final int height = mRelativeLayoutHead.getHeight();
        animation.setDuration(500);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int left = mLinearHome.getLeft();
                int right = mLinearHome.getRight();
                int top = mLinearHome.getTop() - height;
                int bottom = top + mLinearHome.getHeight();
                mLinearHome.clearAnimation();
//                mLinearHome.layout(left, top, right, bottom);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mRelativeLayoutHead.setVisibility(View.GONE);
        mRelativeLayoutHead.startAnimation(animation);

    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public final String TITLES[] = {"所有", "未整理"};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return SubjectFragment.newInstance(i);
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

}
