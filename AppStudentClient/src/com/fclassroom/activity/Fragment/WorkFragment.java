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
public class WorkFragment extends Fragment {

    private PagerSlidingTabStrip tabs;
    private ViewPager pagers;

    public WorkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_work, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        pagers = (ViewPager) view.findViewById(R.id.pagers);
        pagers.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
        tabs.setViewPager(pagers);
        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

    /**
     * mPagerSlidingTabStrip默认值配置
     */
    private void initTabsValue() {
        // 底部游标颜色
        tabs.setIndicatorColor(Color.YELLOW);
        // tab的分割线颜色
        tabs.setDividerColor(getResources().getColor(R.color.toolbar_color));
        // tab背景
        tabs.setBackgroundColor(getResources().getColor(R.color.toolbar_color));
        // tab底线高度
        tabs.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics()));
        // 游标高度
        tabs.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, getResources().getDisplayMetrics()));
        // 选中的文字颜色
        tabs.setSelectedTextColor(Color.WHITE);
        // 正常文字颜色
        tabs.setTextColor(getResources().getColor(R.color.text_color_green));
        tabs.setTextSize(50);
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public final String TITLES[] = {"待完成", "未完成", "已完成"};

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int i) {
            return WaitforCompleteFragment.newInstance(i);
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
