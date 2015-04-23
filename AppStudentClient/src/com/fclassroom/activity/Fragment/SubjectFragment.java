package com.fclassroom.activity.Fragment;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.DetailActivity;
import com.fclassroom.activity.HomeActivity;
import com.fclassroom.activity.NotebookActivity;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ExamBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.ScrollShowHeaderListView;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * HomeFragment主页下半部viewpager所包含fragment
 */
public class SubjectFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private List<HashMap<String, Object>> list;
    private ArrayList<HashMap<String, Object>> list2;
    private int position;
    private CheckBox checkBox;
    private TranslateAnimation mShowAnimation;
    private TranslateAnimation mHideAnimation;
    private boolean isHeaderShow = true;
    private LayoutInflater inflater;
    private ListView listview;
    private ScrollShowHeaderListView scrollShowHeaderListView;
    // loading view
    private ProgressBar mLoadingView;
    private TextView mEmptyView;

    private View headview;
    private LinearLayout bottomView;
    private RelativeLayout topView;
    //试题和试卷标记，true时为试题，false为试卷
    private boolean squareChecked = true;
    public SubjectAdapter subjectAdapter;
    private WorkAdapter workAdapter;
    private TextView selectAll, haveSelected, collate, print, delete;
    public static HashSet<Integer> selectIdSet = new HashSet<Integer>();
    public static HashMap<Integer, View> mViews;
    private AppContext appContext;
    private List<PageBean.SubjectItemBean> listSubject;
    private List<ExamBean> listExam;

    //错题列表需要做的请求参数
    private String accessToken;
    private int gradeId;
    private int subjectId;
    private int noteBookId;
    private int unOrganize;
    private String orderTime = "orderTime";
    private String orderRate = "orderRate";
    private String orderLevel = "orderLevel";
    private String orderBy = orderTime;
    private final static String orderUp = "ASC";
    private final static String orderDown = "DESC";
    private static String orderUpOrDown = orderUp;
    private int pageNo1;
    private int pageNo2;
    private static int pageSize = 20;
    //按时间排序，按难度排序，按重要度排序
    private boolean timeOrder = true;
    private boolean difficultOrder = true;
    private boolean importantOrder = true;

    /**
     * 隐藏Top榜接口，listview滑动触发，homeactivity继承实现
     */
    public interface HideTop {
        void DoHideTop();
    }

    public SubjectFragment() {
        // Required empty public constructor
    }

    public static SubjectFragment newInstance(int position) {
        SubjectFragment f = new SubjectFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
        listSubject = new ArrayList<PageBean.SubjectItemBean>();
        listExam = new ArrayList<ExamBean>();
//        list = new ArrayList<>();
//        for (int i = 0; i < 15; i++) {
//            HashMap<String, Object> data = new HashMap<>();
//            data.put("examdate", "2015年3月3日");
//            data.put("examname", "梅村中学第二次模拟考试");
//            data.put("rating", (float) 3.0);
//            data.put("examsrc", R.drawable.subject);
//            list.add(data);
//        }
//        list2 = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            HashMap<String, Object> data = new HashMap<>();
//            data.put("workname", "梅村中学高一数学期末第三次模拟测试");
//            data.put("workdate", "2015年3月10日");
//            data.put("worktime", "共38题");
//            list2.add(data);
//        }
        appContext = (AppContext) getActivity().getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
    }

    private void initData() {
        if (position == 0) {
            //获取全部错题数据，unOrganize为0，查询全部数据
            pageNo1 = 1;
            unOrganize = 0;
            orderBy = orderTime;
            getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
        } else if (position == 1) {
            //当unorganize为1时为未整理
            pageNo2 = 1;
            unOrganize = 1;
            orderBy = orderTime;
            getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
        }
    }

    /**
     * 获取单题列表数据
     *
     * @param accessToken
     * @param gradeId
     * @param subjectId
     * @param pageSize
     * @param pageNo
     * @param unOrganize    0为全部，1为未整理
     * @param orderBy       根据什么排序，orderTime,orderLevel,orderRate
     * @param orderUpOrDown 升序或降序
     */
    public void getSubjectList(final String accessToken, final int gradeId, final int subjectId, final int pageSize, final int pageNo, final int unOrganize, final String orderBy, final String orderUpOrDown) {
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "正在加载。。。");
        showLoading(scrollShowHeaderListView, mLoadingView, mEmptyView);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PageBean> response = (BaseResponseBean<PageBean>) msg.obj;
                    List<PageBean.SubjectItemBean> list = response.getData().getList();
                    listSubject.clear();
                    listSubject.addAll(list);
//                    subjectAdapter.notifyDataSetChanged();
                    appContext.mylist = listSubject;
                    subjectAdapter = new SubjectAdapter(getActivity(), listSubject, listview);
                    listview.setAdapter(subjectAdapter);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                }
                showContent(scrollShowHeaderListView, mLoadingView, mEmptyView);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<PageBean> response = appContext.getSubjectDetail(accessToken, gradeId, subjectId, noteBookId, unOrganize, orderBy, orderUpOrDown, pageNo, pageSize);
                    if (response.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = response;
                    } else if (response.getError_code() != 0) {
                        msg.what = 0;
                        msg.obj = response.getError_msg();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void getMoreSubjectList(final String accessToken, final int gradeId, final int subjectId, final int pageSize, final int pageNo, final int unOrganize, final String orderBy, final String orderUpOrDown) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "正在加载。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PageBean> response = (BaseResponseBean<PageBean>) msg.obj;
                    List<PageBean.SubjectItemBean> list = response.getData().getList();
                    listSubject.addAll(list);
                    subjectAdapter.notifyDataSetChanged();
//                    FileUtils.saveList(appContext, listSubject, "mylist.list");
                    appContext.mylist = listSubject;
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                }
                progressDialog.dismiss();
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<PageBean> response = appContext.getSubjectDetail(accessToken, gradeId, subjectId, noteBookId, unOrganize, orderBy, orderUpOrDown, pageNo, pageSize);
                    if (response.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = response;
                    } else if (response.getError_code() != 0) {
                        msg.what = 0;
                        msg.obj = response.getError_msg();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subject, container, false);
        initAnimation();
        initViews(view);
        initData();
        return view;
    }

    private void initViews(View view) {
        scrollShowHeaderListView = (ScrollShowHeaderListView) view.findViewById(R.id.listview_fragment);
        //headview隐藏或显示
        inflater = LayoutInflater.from(getParentFragment().getActivity());
        headview = inflater.inflate(R.layout.listview_head_home, null, false);
        final ImageView square = (ImageView) headview.findViewById(R.id.iv_square);
        final TextView time = (TextView) headview.findViewById(R.id.tv_fragment_time);
        final TextView difficulty = (TextView) headview.findViewById(R.id.tv_difficulty);
        final TextView importance = (TextView) headview.findViewById(R.id.tv_importance);
        //dialog显示或隐藏
        mLoadingView = (ProgressBar) view.findViewById(R.id.loading_view);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        scrollShowHeaderListView.setUpHeaderViews(headview);
        listview = scrollShowHeaderListView.getListView();
        subjectAdapter = new SubjectAdapter(getActivity(), listSubject, listview);
        listview.setAdapter(subjectAdapter);
        Drawable drawabledown = getResources().getDrawable(R.drawable.updown_down);
        drawabledown.setBounds(0, 0, drawabledown.getMinimumWidth(), drawabledown.getMinimumHeight());
        Drawable drawableup = getResources().getDrawable(R.drawable.updown_up);
        drawableup.setBounds(0, 0, drawableup.getMinimumWidth(), drawableup.getMinimumHeight());
        final Drawable drawablenone = getResources().getDrawable(R.drawable.updown_none);
        drawablenone.setBounds(0, 0, drawablenone.getMinimumWidth(), drawablenone.getMinimumHeight());
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    if (squareChecked == true) {
                        pageNo1 = 1;
                        pageSize = 20;
                        unOrganize = 0;
                        orderBy = orderTime;
                        if (timeOrder == true) {
                            orderUpOrDown = orderDown;
                            timeOrder = false;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null, null, drawable, null);
                            difficulty.setCompoundDrawables(null,null,drawablenone,null);
                            importance.setCompoundDrawables(null,null,drawablenone,null);
                            getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                        } else if (timeOrder == false) {
                            orderUpOrDown = orderUp;
                            timeOrder = true;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null,null,drawable,null);
                            difficulty.setCompoundDrawables(null,null,drawablenone,null);
                            importance.setCompoundDrawables(null,null,drawablenone,null);
                            getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                        }
                    } else if (squareChecked == false) {
                        unOrganize = 0;
                        if (timeOrder == true) {
                            timeOrder = false;
                            orderUpOrDown = orderDown;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null,null,drawable,null);
                            difficulty.setCompoundDrawables(null,null,drawablenone,null);
                            importance.setCompoundDrawables(null,null,drawablenone,null);
                            getAllExamPaper(accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
                        } else if (timeOrder == false) {
                            timeOrder = true;
                            orderUpOrDown = orderUp;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null,null,drawable,null);
                            difficulty.setCompoundDrawables(null,null,drawablenone,null);
                            importance.setCompoundDrawables(null,null,drawablenone,null);
                            getAllExamPaper(accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
                        }
                    }
                } else if (position == 1) {
                    if (squareChecked == true) {
                        pageNo2 = 1;
                        pageSize = 20;
                        unOrganize = 1;
                        orderBy = orderTime;
                        if (timeOrder == true) {
                            orderUpOrDown = orderDown;
                            timeOrder = false;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null,null,drawable,null);
                            getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                        } else if (timeOrder == false) {
                            orderUpOrDown = orderUp;
                            timeOrder = true;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null,null,drawable,null);
                            getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                        }
                    } else if (squareChecked == false) {
                        unOrganize = 1;
                        if (timeOrder == true) {
                            timeOrder = false;
                            orderUpOrDown = orderDown;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null,null,drawable,null);
                            difficulty.setCompoundDrawables(null,null,drawablenone,null);
                            importance.setCompoundDrawables(null,null,drawablenone,null);
                            getAllExamPaper(accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
                        } else if (timeOrder == false) {
                            timeOrder = true;
                            orderUpOrDown = orderUp;
                            Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                            time.setCompoundDrawables(null,null,drawable,null);
                            difficulty.setCompoundDrawables(null,null,drawablenone,null);
                            importance.setCompoundDrawables(null,null,drawablenone,null);
                            getAllExamPaper(accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
                        }
                    }
                }
            }
        });
        difficulty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    unOrganize = 0;
                    if (difficultOrder == true) {
                        difficultOrder = false;
                        orderBy = orderRate;
                        orderUpOrDown = orderDown;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        difficulty.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        importance.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                    } else if (difficultOrder == false) {
                        difficultOrder = true;
                        orderBy = orderRate;
                        orderUpOrDown = orderUp;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        difficulty.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        importance.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                    }
                } else if (position == 1) {
                    unOrganize = 1;
                    if (difficultOrder == true) {
                        difficultOrder = false;
                        orderBy = orderRate;
                        orderUpOrDown = orderDown;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        difficulty.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        importance.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                    } else if (difficultOrder == false) {
                        difficultOrder = true;
                        orderBy = orderRate;
                        orderUpOrDown = orderUp;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        difficulty.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        importance.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                    }
                }
            }
        });
        importance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    unOrganize = 0;
                    if (importantOrder == true) {
                        importantOrder = false;
                        orderUpOrDown = orderDown;
                        orderBy = orderLevel;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        importance.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        difficulty.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                    } else if (importantOrder == false) {
                        importantOrder = true;
                        orderUpOrDown = orderUp;
                        orderBy = orderLevel;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        importance.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        difficulty.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                    }
                } else if (position == 1) {
                    unOrganize = 1;
                    if (importantOrder == true) {
                        importantOrder = false;
                        orderUpOrDown = orderDown;
                        orderBy = orderLevel;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_down);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        importance.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        difficulty.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                    } else if (importantOrder == false) {
                        importantOrder = true;
                        orderUpOrDown = orderUp;
                        orderBy = orderLevel;
                        Drawable drawable = getResources().getDrawable(R.drawable.updown_up);
                        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                        importance.setCompoundDrawables(null,null,drawable,null);
                        time.setCompoundDrawables(null,null,drawablenone,null);
                        difficulty.setCompoundDrawables(null,null,drawablenone,null);
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                    }
                }
            }
        });
        //底部、顶部栏初始化
        topView = (RelativeLayout) view.findViewById(R.id.linear_top);
        bottomView = (LinearLayout) view.findViewById(R.id.linear_bottom);
        selectAll = (TextView) view.findViewById(R.id.tv_selectall);
        haveSelected = (TextView) view.findViewById(R.id.tv_haveselected);
        collate = (TextView) view.findViewById(R.id.tv_collate);
        delete = (TextView) view.findViewById(R.id.tv_delete);
        print = (TextView) view.findViewById(R.id.tv_print);
        print.setOnClickListener(new chickListener());
        delete.setOnClickListener(new chickListener());
        collate.setOnClickListener(new chickListener());
        selectAll.setOnClickListener(new chickListener());

        scrollShowHeaderListView.setScrollUpListener(new ScrollShowHeaderListView.OnScrollUpListener() {
            @Override
            public void onScrollUp(AbsListView view) {
                if (isHeaderShow) {
                    if (view.getFirstVisiblePosition() > 0) {
                        ((HomeActivity) getParentFragment().getActivity()).DoHideTop();
                        isHeaderShow = false;
                    }
                }
                // 判断滚动到底部
                if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                    // 然后 经行一些业务操作
                    if (squareChecked == true) {
                        if (position == 0) {
                            //获取全部错题数据，unOrganize为0，查询全部数据
                            pageNo1++;
                            unOrganize = 0;
                            getMoreSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                        } else if (position == 1) {
                            //当unorganize为1时为未整理
                            pageNo2++;
                            unOrganize = 1;
                            getMoreSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                        }
                    }
                }
            }
        });
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mulMode();
//                selectIdSet.clear();
                return true;
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listview.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                    ImageView iv = (ImageView) view.findViewById(R.id.iv_subject);
                    PageBean.SubjectItemBean subjectItemBean = (PageBean.SubjectItemBean) iv.getTag();
                    if (!listview.isItemChecked(position)) {
                        ((CheckBox) view.findViewById(R.id.checkbox_choice)).setChecked(false);
                        appContext.myselectlist.remove(subjectItemBean);
                    } else {
                        ((CheckBox) view.findViewById(R.id.checkbox_choice)).setChecked(true);
                        appContext.myselectlist.add(subjectItemBean);
                    }
                    updateSeletedCount();
                } else {
                    if (squareChecked == true) {
                        PageBean.SubjectItemBean subjectItemBean = null;
                        if (view instanceof ImageView) {
                            subjectItemBean = (PageBean.SubjectItemBean) view.getTag();
                        } else {
                            ImageView imageView = (ImageView) view.findViewById(R.id.iv_subject);
                            subjectItemBean = (PageBean.SubjectItemBean) imageView.getTag();
                        }
                        if (subjectItemBean == null) {
                            return;
                        }
                        UIHelper.jump2Activity(getActivity(), DetailActivity.class, subjectItemBean);
                       /* Intent intent = new Intent(getParentFragment().getActivity(),DetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("value", subjectItemBean);
                        intent.putExtras(bundle);
                        startActivityForResult(intent,5);*/
                    } else {
                        int examBeanId = 0;
                        String workname = null;
                        if (view instanceof TextView) {
                            examBeanId = (int) view.getTag();
                        } else {
                            TextView textView = (TextView) view.findViewById(R.id.tv_workname);
                            workname = textView.getText().toString();
                            examBeanId = (int) textView.getTag();
                        }
                        if (examBeanId == 0) {
                            return;
                        }
                        UIHelper.jump2Activity(getActivity(), NotebookActivity.class, examBeanId, workname, "subject");
                    }
                }
            }
        });

        //设置不同的adapter，subjectabapter为题目item，workadapter为考试名item
        square.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (squareChecked) {
                    square.setImageResource(R.drawable.square2);
                    squareChecked = false;
                    difficulty.setVisibility(View.GONE);
                    importance.setVisibility(View.GONE);
//                    listview.setAdapter(new WorkAdapter(getParentFragment().getActivity(), R.layout.listview_item_waitforcomplete, list2));
                    if (position == 0) {
                        //所有题目，按试卷分类,unOranize为0时为所有
                        unOrganize = 0;
                        orderUpOrDown = orderUp;
                        getAllExamPaper(accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
                    } else if (position == 1) {
                        //未整理题目，按试卷分类,unOranize为1时为未整理
                        unOrganize = 1;
                        orderUpOrDown = orderUp;
                        getAllExamPaper(accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
                    }
                } else {
                    square.setImageResource(R.drawable.square);
                    squareChecked = true;
                    difficulty.setVisibility(View.VISIBLE);
                    importance.setVisibility(View.VISIBLE);
//                    listview.setAdapter(new SubjectAdapter(getActivity(), listSubject));
                    if (position == 0) {
                        //所有题目，按题目分类,unOranize为0时为所有
                        unOrganize = 0;
                        orderUpOrDown = orderUp;
                        pageNo1 = 1;
                        pageSize = 20;
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
                    } else if (position == 1) {
                        //未整理题目，按题目分类,unOranize为1时为未整理
                        unOrganize = 1;
                        orderUpOrDown = orderUp;
                        pageNo2 = 1;
                        pageSize = 20;
                        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo2, unOrganize, orderBy, orderUpOrDown);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("kkk");
        if(requestCode == 20){
            System.out.println("rrr");
            unOrganize = 0;
            orderUpOrDown = orderUp;
            pageNo1 = 1;
            pageSize = 20;
            getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
        }
    }

    private void mulMode() {
        appContext.myselectlist.clear();
        unSelectedAll();
        bottomView.setVisibility(View.VISIBLE);
        topView.setVisibility(View.VISIBLE);
        topView.setFocusable(true);
        bottomView.setFocusable(true);
        subjectAdapter.setMulMode(true);
        subjectAdapter.notifyDataSetChanged();
        listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void singleMode() {
        listview.clearChoices();
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);
        topView.setFocusable(false);
        bottomView.setFocusable(false);
        subjectAdapter.setMulMode(false);
//        subjectAdapter.notifyDataSetChanged();
        pageNo1 = 1;
        unOrganize = 0;
        getSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo1, unOrganize, orderBy, orderUpOrDown);
        listview.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    protected void updateSeletedCount() {
        // TODO Auto-generated method stub
        haveSelected.setText("已选择" + Integer.toString(listview.getCheckedItemCount()) + "题");
    }

    public void selectedAll() {
        for (int i = 0; i < subjectAdapter.getCount(); i++) {
            listview.setItemChecked(i, true);
        }
        appContext.myselectlist.clear();
        appContext.myselectlist = listSubject;
        selectAll.setEnabled(false);
        updateSeletedCount();
    }

    public void unSelectedAll() {
        listview.clearChoices();
        updateSeletedCount();
    }

    private void updateSingleRow(ListView listView, long id) {

        if (listView != null) {
            int start = listView.getFirstVisiblePosition();
            for (int i = start, j = listView.getLastVisiblePosition(); i <= j; i++)
                if (id == ((View) listView.getItemAtPosition(i)).getId()) {
                    View view = listView.getChildAt(i - start);
                    subjectAdapter.getView(i, view, listView);
                    break;
                }
        }
    }

    /**
     * 获取试卷列表信息
     *
     * @param accessToken
     * @param gradeId
     * @param subjectId
     * @param unOrganize    0为全部信息，1为未整理
     * @param orderUpOrDown 升序或降序
     */
    private void getAllExamPaper(final String accessToken, final int gradeId, final int subjectId, final int unOrganize, final String orderUpOrDown) {
//        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(), "", "正在加载。。。");
        showLoading(scrollShowHeaderListView, mLoadingView, mEmptyView);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    List<ExamBean> list = ((BaseResponseBean<ArrayList<ExamBean>>) msg.obj).getData();
                    listExam.clear();
                    listExam.addAll(list);
                    workAdapter = new WorkAdapter(getParentFragment().getActivity(), R.layout.listview_item_waitforcomplete, listExam);
                    listview.setAdapter(workAdapter);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                }
                showContent(scrollShowHeaderListView, mLoadingView, mEmptyView);
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<ArrayList<ExamBean>> responseBean = appContext.getAllExamPager(accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
                    if (responseBean.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = responseBean;
                    } else if (responseBean.getError_code() != 0) {
                        msg.what = 0;
                        msg.obj = responseBean.getError_msg();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    class WorkAdapter extends BaseAdapter {
        private Context context;
        private int res;
        private List<ExamBean> list;
        private LayoutInflater inflater;

        public WorkAdapter(Context context, int res, List<ExamBean> list) {
            this.context = context;
            this.res = res;
            this.list = list;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (null == convertView) {
                holder = new Holder();
                convertView = inflater.inflate(res, null, false);
                holder.workname = (TextView) convertView.findViewById(R.id.tv_workname);
                holder.workdate = (TextView) convertView.findViewById(R.id.tv_workdate);
                holder.worktime = (TextView) convertView.findViewById(R.id.tv_worktime);
                holder.icon = (ImageView) convertView.findViewById(R.id.iv_right);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            ExamBean data = list.get(position);
            holder.workname.setText(data.getExamName());
            holder.workdate.setText(data.getExamTime());
            holder.workname.setTag(data.getExamId());
            holder.workdate.setTag(data.getExamId());
//            holder.worktime.setText(data.get("worktime").toString());
            holder.icon.setImageResource(R.drawable.right_gray_arrow);
            return convertView;
        }

        class Holder {
            TextView workname;
            TextView workdate;
            TextView worktime;
            ImageView icon;
        }
    }

    private void initAnimation() {
        // 从自已-1倍的位置移到自己原来的位置
        mShowAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF,
                0.0f);
        mHideAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF,
                0, Animation.RELATIVE_TO_SELF, -1);
        mShowAnimation.setDuration(500);
        mHideAnimation.setDuration(1000);
        mHideAnimation.setFillAfter(true);
    }

    private void showLoading(View contentView, View loadingView, View emptyView) {
        contentView.setVisibility(View.GONE);
        loadingView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
    }

    private void showContent(View contentView, View loadingView, View emptyView) {
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
    }

    class chickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_collate:
                    if (appContext.myselectlist.size() != 0) {
                        UIHelper.jump2Activity(getActivity(), DetailActivity.class, appContext.myselectlist.get(0));
                    }
                    singleMode();
                    break;
                case R.id.tv_selectall:
                    selectedAll();
                    break;
                case R.id.tv_print:
                    StringBuffer stringBuffer = new StringBuffer("");
                    for (PageBean.SubjectItemBean subjectItemBean : appContext.myselectlist) {
                        String examQuestionId = Integer.toString(subjectItemBean.getExamQuestionId());
                        stringBuffer.append(examQuestionId).append(",");
                    }
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                    String examQuestionIds = stringBuffer.toString();
                    printSelected(accessToken, gradeId, subjectId, examQuestionIds);
                    break;
                case R.id.tv_delete:
                    int delFlag = 0;
                    StringBuffer stringBuffer2 = new StringBuffer("");
                    for (PageBean.SubjectItemBean subjectItemBean : appContext.myselectlist) {
                        String examQuestionId = Integer.toString(subjectItemBean.getExamQuestionId());
                        stringBuffer2.append(examQuestionId).append(",");
                    }
                    stringBuffer2.deleteCharAt(stringBuffer2.length()-1);
                    String examQuestionIds2 = stringBuffer2.toString();
                    deleteSelected(accessToken,examQuestionIds2,delFlag);
                    break;
            }
        }
    }

    private void deleteSelected(final String accessToken, final String examQuestionIds, final int delFlag) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    UIHelper.ToastMessage(getActivity(),"删除成功！");
                    singleMode();
                }else {
                    ((AppException)msg.obj).makeToast(getActivity());
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.deleteSelected(accessToken,examQuestionIds,delFlag);
                    msg.what = 1;
                    msg.obj = responseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void printSelected(final String accessToken, final int gradeId, final int subjectId, final String examQuestionIds) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    UIHelper.ToastMessage(getActivity(), "打印成功！");
                    singleMode();
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<Integer> responseBean = appContext.printSelected(accessToken,gradeId,subjectId,examQuestionIds);
                    msg.what = 1;
                    msg.obj = responseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

}
