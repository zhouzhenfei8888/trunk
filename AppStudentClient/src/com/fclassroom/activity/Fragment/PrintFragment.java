package com.fclassroom.activity.Fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fclassroom.app.widget.PagerSlidingTabStrip;
import com.fclassroom.appstudentclient.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrintFragment extends Fragment {

    PagerSlidingTabStrip mTabs;
    ViewPager mViewPagers;
    public PrintFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_print, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        mTabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        mViewPagers = (ViewPager)view.findViewById(R.id.pagers);
        mViewPagers.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        mTabs.setViewPager(mViewPagers);
        initTabsValue();
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

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public final String TITLES[] = {"打印计划", "打印记录"};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return PrintplanFragment.newInstance(i);
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
