package com.fclassroom.activity.Fragment;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.activity.AchievementActivity;
import com.fclassroom.activity.HomeActivity;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.PagerSlidingTabStrip;
import com.fclassroom.app.widget.RoundProgressBar;
import com.fclassroom.appstudentclient.R;

/**
 * A simple {@link Fragment} subclass.
 * test
 */
public class HomeFragment extends Fragment implements HomeActivity.HideTopHomeFragment {

    private PagerSlidingTabStrip mTabs;
    private ViewPager mPagers;
    public RelativeLayout mRelativeLayoutHead;
    private LinearLayout mLinearHome;
    private RoundProgressBar roundProgressBar;
    private TextView havecollect;
    private TextView lead;
    private int progress = 0;
    private int progress2 = 0;
    AppContext appContext;
    String accessToken;
    int gradeId;
    int subjectId;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //kbv
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        appContext = (AppContext) getActivity().getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        havecollect = (TextView)view.findViewById(R.id.tv_have_collated);
        lead = (TextView)view.findViewById(R.id.tv_lead);
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
        roundProgressBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(getActivity(), AchievementActivity.class);
            }
        });
        new Thread() {
            @Override
            public void run() {
                while (progress <= 60) {
                    progress += 1;
                    roundProgressBar.setProgress(progress);
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                while (progress2 <= 40) {
                    progress2 += 1;
                    roundProgressBar.setProgress2(progress2);
                    try {
                        Thread.sleep(40);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        System.out.println("HomeFragment");
//        int pageSize = 20,pageNo1=0,unOrganize=0;String orderBy ="orderTime",orderUpOrDown="ASC";
//            SubjectFragment.newInstance(0).getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
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

 /*   public interface Refresh{
        public void refreshSubjectListView(SubjectAdapter subjectAdapter);
        public void refreshPagerListView(SubjectFragment.WorkAdapter workAdapter);
    };*/
}
