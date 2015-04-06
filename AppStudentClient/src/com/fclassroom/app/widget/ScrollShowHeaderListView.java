package com.fclassroom.app.widget;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.ListView.FixedViewInfo;
import android.widget.RelativeLayout;

import com.fclassroom.appstudentclient.R;

public class ScrollShowHeaderListView extends RelativeLayout {
    private View headerView;
    private ListView mList;
    private Context context;
    private boolean isHeaderShow = true;
    private boolean pullable;
    private OnScrollUpListener scrollUpListener;

    public interface OnScrollUpListener {
        public void onScrollUp(AbsListView view);
    }

    public ScrollShowHeaderListView(Context context) {
        this(context, null);
    }

    public ScrollShowHeaderListView(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.listViewStyle);
    }

    public ScrollShowHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Customize);
        pullable = a.getBoolean(R.styleable.Customize_pullable, false);
        this.context = context;
        if (pullable) {
            mList = new PullToRefreshListView(context);
        } else {
            mList = new ListView(context);
        }
        mList.setOverScrollMode(ListView.OVER_SCROLL_NEVER);
        LayoutParams listParam = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mList.setDividerHeight(35);
        mList.setLayoutParams(listParam);
        addView(mList, listParam);
        a.recycle();
    }

    public View getHeaderView() {
        return headerView;
    }

    public ListView getListView() {
        return mList;
    }

    public void setUpHeaderViews(View headerView) {
        this.headerView = headerView;
        LayoutParams headerParam = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        headerParam.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        this.addView(headerView, headerParam);

        View listHeader = new View(context);
        int w = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int h = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        headerView.measure(w, h);
        int height = headerView.getMeasuredHeight();
        int width = headerView.getMeasuredWidth();
        listHeader.setLayoutParams(new ListView.LayoutParams(width, height));
        mList.addHeaderView(listHeader);
        if (pullable) {
            //两个header,顺序调换
            Class<ListView> clz = ListView.class;
            Class<AbsListView> absClz = AbsListView.class;
            Field mHeaderViewInfos;
            Field observer;
            try {
                mHeaderViewInfos = clz.getDeclaredField("mHeaderViewInfos");
                mHeaderViewInfos.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<FixedViewInfo> imageEngine = (List<FixedViewInfo>) mHeaderViewInfos.get(mList);
                Collections.reverse(imageEngine);
                observer = absClz.getDeclaredField("mDataSetObserver");
                observer.setAccessible(true);
                DataSetObserver mDataSetObserver = (DataSetObserver) observer.get(mList);
                if (mDataSetObserver != null) {
                    mDataSetObserver.onChanged();
                }
            } catch (NoSuchFieldException e1) {
                e1.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }

        mList.setOnScrollListener(new OnScrollListener() {

            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mLastFirstVisibleItem < firstVisibleItem) {
                    hideHeader();
                    if (getScrollUpListener() != null) {
                        getScrollUpListener().onScrollUp(view);
                    }
                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    showHeader();
                }
                mLastFirstVisibleItem = firstVisibleItem;
            }
        });

    }

    public void showHeader() {
        if (!isHeaderShow) {
            isHeaderShow = true;
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    -1, Animation.RELATIVE_TO_SELF, 0);
            ta.setFillAfter(true);
            ta.setDuration(200);
            headerView.startAnimation(ta);
            headerView.setVisibility(View.VISIBLE);
            headerView.setEnabled(true);
            headerView.setClickable(true);
        }
    }

    public void hideHeader() {
        if (isHeaderShow) {
            isHeaderShow = false;
            TranslateAnimation ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                    0, Animation.RELATIVE_TO_SELF, -1);
            ta.setFillAfter(true);
            ta.setDuration(200);
            ta.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    headerView.clearAnimation();
                }
            });
            headerView.startAnimation(ta);
            headerView.setVisibility(View.GONE);
            headerView.setEnabled(false);
            headerView.setClickable(false);
        }
    }

    public OnScrollUpListener getScrollUpListener() {
        return scrollUpListener;
    }

    public void setScrollUpListener(OnScrollUpListener scrollUpListener) {
        this.scrollUpListener = scrollUpListener;
    }

}
