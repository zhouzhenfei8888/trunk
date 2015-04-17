package com.fclassroom.activity.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
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
    private RelativeLayout topView;
    private LinearLayout bottomView;
    private TextView selectAll, haveSelected, delete, recover;

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
                    list.clear();
                    list.addAll(rubbishList);
                    subjectAdapter = new SubjectAdapter(getActivity(),list,listView);
                    listView.setAdapter(subjectAdapter);
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

    private void deleteSelected(final String accessToken, final String examQuestionIds, final int delFlag) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    UIHelper.ToastMessage(getActivity(),"删除成功！");
                    unSelectedAll();
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

    private void recoverErrorQuestions(final String accessToken, final String examQuestionIds) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    UIHelper.ToastMessage(getActivity(),"已恢复！");
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
                    BaseResponseBean<String> responseBean = appContext.recoverErrorQuestions(accessToken, examQuestionIds);
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
        subjectAdapter = new SubjectAdapter(getActivity(), list, listView);
        listView.setAdapter(subjectAdapter);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mulMode();
                return true;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                if (listView.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                                                    ImageView iv = (ImageView) view.findViewById(R.id.iv_subject);
                                                    PageBean.SubjectItemBean subjectItemBean = (PageBean.SubjectItemBean) iv.getTag();
                                                    if (!listView.isItemChecked(position)) {
                                                        ((CheckBox) view.findViewById(R.id.checkbox_choice)).setChecked(false);
                                                        appContext.rubbishlist.remove(subjectItemBean);
                                                    } else {
                                                        ((CheckBox) view.findViewById(R.id.checkbox_choice)).setChecked(true);
                                                        appContext.rubbishlist.add(subjectItemBean);
                                                    }
                                                    updateSeletedCount();
                                                } else {
                                                }
                                            }
                                        }

        );
        //底部、顶部栏初始化
        topView = (RelativeLayout) view.findViewById(R.id.linear_top);
        bottomView = (LinearLayout) view.findViewById(R.id.linear_bottom);
        selectAll = (TextView) view.findViewById(R.id.tv_selectall);
        haveSelected = (TextView) view.findViewById(R.id.tv_haveselected);
        delete = (TextView) view.findViewById(R.id.tv_delete);
        recover = (TextView) view.findViewById(R.id.tv_recover);
        delete.setOnClickListener(new clickListener());
        recover.setOnClickListener(new clickListener());
        selectAll.setOnClickListener(new clickListener());
        initData();

        return view;
    }

    private void mulMode() {
        appContext.rubbishlist.clear();
        unSelectedAll();
        bottomView.setVisibility(View.VISIBLE);
        topView.setVisibility(View.VISIBLE);
        topView.setFocusable(true);
        bottomView.setFocusable(true);
        subjectAdapter.setMulMode(true);
        subjectAdapter.notifyDataSetChanged();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void singleMode() {
        listView.clearChoices();
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);
        topView.setFocusable(false);
        bottomView.setFocusable(false);
        subjectAdapter.setMulMode(false);
//        subjectAdapter.notifyDataSetChanged();
        pageNo = 0;
        getRubbishSubjectList(accessToken, gradeId, subjectId, pageSize, pageNo);
        listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    protected void updateSeletedCount() {
        // TODO Auto-generated method stub
        haveSelected.setText("已选择" + Integer.toString(listView.getCheckedItemCount()) + "题");
    }

    public void selectedAll() {
        for (int i = 0; i < subjectAdapter.getCount(); i++) {
            listView.setItemChecked(i+1, true);
        }
        appContext.rubbishlist.clear();
        appContext.rubbishlist = list;
        selectAll.setEnabled(false);
        updateSeletedCount();
    }

    public void unSelectedAll() {
        listView.clearChoices();
        updateSeletedCount();
    }

    class clickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.tv_delete:
                    int delFlag = 1;
                    StringBuffer stringBuffer2 = new StringBuffer("");
                    for (PageBean.SubjectItemBean subjectItemBean : appContext.rubbishlist) {
                        String examQuestionId = Integer.toString(subjectItemBean.getExamQuestionId());
                        stringBuffer2.append(examQuestionId).append(",");
                    }
                    stringBuffer2.deleteCharAt(stringBuffer2.length()-1);
                    String examQuestionIds2 = stringBuffer2.toString();
                    deleteSelected(accessToken,examQuestionIds2,delFlag);
                    break;
                case R.id.tv_recover:
                    StringBuffer stringBuffer = new StringBuffer("");
                    for (PageBean.SubjectItemBean subjectItemBean : appContext.rubbishlist) {
                        String examQuestionId = Integer.toString(subjectItemBean.getExamQuestionId());
                        stringBuffer.append(examQuestionId).append(",");
                    }
                    stringBuffer.deleteCharAt(stringBuffer.length()-1);
                    String examQuestionIds = stringBuffer.toString();
                    recoverErrorQuestions(accessToken, examQuestionIds);
                    break;
                case R.id.tv_selectall:
                    selectedAll();
                    break;
            }
        }
    }
}
