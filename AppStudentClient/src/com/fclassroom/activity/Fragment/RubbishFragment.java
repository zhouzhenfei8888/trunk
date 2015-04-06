package com.fclassroom.activity.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.app.adapter.RubbishAdapter;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ErrorSubjectNumBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.ScrollShowHeaderListView;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RubbishFragment extends Fragment {

    private ScrollShowHeaderListView listViewRubbish;
    private LayoutInflater inflater;
    private List<PageBean.SubjectItemBean> list;
    private View headview;
    private ListView listView;
    private TextView headNum;
    private SubjectAdapter subjectAdapter;
    private AppContext appContext;
    private String accessToken;
    private int gradeId;
    private int subjectId;
    private int pageNo;
    private static int pageSize = 20;

    public RubbishFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        list = new ArrayList<>();
//        for(int i=0;i<20;i++){
//            HashMap<String,Object> data= new HashMap<String,Object>();
//            data.put("examdate","2015年3月3日");
//            data.put("examname","梅村中学第三次模拟考试");
//            data.put("examsrc",R.drawable.subject);
//            data.put("rating",(float)4.0);
//            list.add(data);
//        }
        appContext = (AppContext) getActivity().getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
    }

    private void initData() {
        pageNo = 1;
        getRubbishSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo);
        getRubbishSubjectNum(accessToken, gradeId, subjectId);
    }

    private void getRubbishSubjectNum(final String accessToken, final int gradeId, final int subjectId) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<List<ErrorSubjectNumBean>> responseBean = (BaseResponseBean<List<ErrorSubjectNumBean>>) msg.obj;
                    List<ErrorSubjectNumBean> ErrorNumList = responseBean.getData();
                    int ErrorNum = 0;
                    for (ErrorSubjectNumBean errorSubjectNumBean : ErrorNumList) {
                        if (errorSubjectNumBean.getCatelog() == 3) {
                            ErrorNum = errorSubjectNumBean.getErrorQuestionCount();
                        }
                    }
                    headNum.setText("共计" + ErrorNum + "题");
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
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
                    BaseResponseBean<List<ErrorSubjectNumBean>> responseBean = appContext.getRubbishSubjectNum(accessToken, gradeId, subjectId);
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

    private void getRubbishSubjectList(final String accessToken, final int gradeId, final int subjectId, final int pageSize, final int pageNo) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PageBean> responseBean = (BaseResponseBean<PageBean>) msg.obj;
                    List<PageBean.SubjectItemBean> rubbishList = responseBean.getData().getList();
                    list.addAll(rubbishList);
                    subjectAdapter.notifyDataSetChanged();
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
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
                    BaseResponseBean<PageBean> responseBean = appContext.getRubbishSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo);
                    if (responseBean.getError_code() == 0) {
                        msg.what = 1;
                        msg.obj = responseBean;
                    } else {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_rubbish, container, false);
        listViewRubbish = (ScrollShowHeaderListView) view.findViewById(R.id.listview_rubbish);
        headview = inflater.inflate(R.layout.listview_head_rubbish, null, false);
        headNum = (TextView) headview.findViewById(R.id.tv_head);
        listViewRubbish.setUpHeaderViews(headview);
        listView = listViewRubbish.getListView();
        list = new ArrayList<PageBean.SubjectItemBean>();
        subjectAdapter = new SubjectAdapter(getActivity(), list);
        listView.setAdapter(subjectAdapter);
        initData();
        return view;
    }


}
