package com.fclassroom.activity.Fragment;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.NotebookActivity;
import com.fclassroom.app.adapter.PrintRecordAdapter;
import com.fclassroom.app.adapter.PrintplanAdapter;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.PrintNumBean;
import com.fclassroom.app.bean.PrintRecoderBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.ScrollShowHeaderListView;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrintplanFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p/>
 * PrintplanFragment为PrintFragment中viewpager的fragment
 */
public class PrintplanFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private int mParam1;
    private ScrollShowHeaderListView scrollShowHeaderListView;
    private ListView lv;
    private LayoutInflater inflater;
    private ArrayList listPrintPlan;
    private ArrayList listPrintRecord;
    private View headView;
    private AppContext appContext;
    private String accessToken;
    private int gradeId;
    private int subjectId;
    private int pageSize = 20;
    private int pageNo;
    private String orderTime;
    private List<PageBean.SubjectItemBean> PrintPlanList;
    private List<PrintRecoderBean> PrintRecordList;
    SubjectAdapter printplanAdapter;
    PrintRecordAdapter printRecordAdapter;
    private TextView tv;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment PrintplanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrintplanFragment newInstance(int param1) {
        PrintplanFragment fragment = new PrintplanFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public PrintplanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mParam1 = getArguments().getInt(ARG_PARAM1);
//        listPrintPlan = new ArrayList<>();
        PrintPlanList = new ArrayList<PageBean.SubjectItemBean>();
        PrintRecordList = new ArrayList<PrintRecoderBean>();
//        for (int i = 0; i < 15; i++) {
//            HashMap<String, Object> data = new HashMap<>();
//            data.put("examdate", "2015年3月3日");
//            data.put("examname", "梅村中学第二次模拟考试");
//            data.put("rating", (float) 3.0);
//            data.put("examsrc", R.drawable.subject);
//            listPrintPlan.add(data);
//        }
//        listPrintRecord = new ArrayList();
//        for (int i = 0; i < 15; i++) {
//            HashMap<String, Object> data = new HashMap<String, Object>();
//            data.put("printdate", "2015年3月3日");
//            data.put("printnumber", 80);
//            listPrintRecord.add(data);
//        }
        appContext = (AppContext) getActivity().getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_printplan, container, false);
        initViews(view);
        if (mParam1 == 0) {
            printplanAdapter = new SubjectAdapter(getActivity(), PrintPlanList,lv);
            lv.setAdapter(printplanAdapter);
        } else if (mParam1 == 1) {
            printRecordAdapter = new PrintRecordAdapter(getActivity(), R.layout.listview_itemprintrecord, PrintRecordList);
            lv.setAdapter(printRecordAdapter);
        }
        initData();
        return view;
    }

    private void initData() {
        pageNo = 1;
        orderTime = "ASC";
        if (mParam1 == 0) {
            getPrintplanList(accessToken, gradeId, subjectId, pageSize, pageNo);
        } else if (mParam1 == 1) {
            getPrintRecoderList(accessToken, gradeId, subjectId, orderTime);
        }
        getPrintNum(accessToken, gradeId, subjectId);
    }

    private void getPrintNum(final String accessToken, final int gradeId, final int subjectId) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PrintNumBean> responseBean = (BaseResponseBean<PrintNumBean>) msg.obj;
                    int printCartQuestionCount = responseBean.getData().getPrintCartQuestionCount();
                    int downloadCount = responseBean.getData().getDownloadCount();
                    if (mParam1 == 0) {
                        tv.setText("待打印" + printCartQuestionCount + "题");
                    } else if (mParam1 == 1) {
                        tv.setText("打印记录" + downloadCount + "条");
                    }
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
                    BaseResponseBean<PrintNumBean> responseBean = appContext.getPrintNum(accessToken, gradeId, subjectId);
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

    private void getPrintRecoderList(final String accessToken, final int gradeId, final int subjectId, final String orderTime) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<List<PrintRecoderBean>> responseBean = (BaseResponseBean<List<PrintRecoderBean>>) msg.obj;
                    List<PrintRecoderBean> list = responseBean.getData();
                    PrintRecordList.addAll(list);
                    printRecordAdapter = new PrintRecordAdapter(getActivity(), R.layout.listview_itemprintrecord, PrintRecordList);
                    lv.setAdapter(printRecordAdapter);
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
                    BaseResponseBean<List<PrintRecoderBean>> responseBean = appContext.getPrintRecoderList(accessToken, gradeId, subjectId, orderTime);
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

    private void getPrintplanList(final String accessToken, final int gradeId, final int subjectId, final int pageSize, final int pageNo) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PageBean> responseBean = (BaseResponseBean<PageBean>) msg.obj;
                    List<PageBean.SubjectItemBean> list = responseBean.getData().getList();
                    PrintPlanList.addAll(list);
                    printplanAdapter = new SubjectAdapter(getActivity(), PrintPlanList,lv);
                    lv.setAdapter(printplanAdapter);
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
                    BaseResponseBean<PageBean> responseBean = appContext.getPrintplanList(accessToken, gradeId, subjectId, pageSize, pageNo);
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

    private void initViews(View view) {
        scrollShowHeaderListView = (ScrollShowHeaderListView) view.findViewById(R.id.listview_fragment);
        inflater = LayoutInflater.from(getParentFragment().getActivity());
        headView = inflater.inflate(R.layout.listview_head_rubbish, null, false);
        tv = (TextView) headView.findViewById(R.id.tv_head);
//        if (mParam1 == 0) {
//            tv.setText("待打印100题");
//        } else if (mParam1 == 1) {
//            tv.setText("打印记录20条");
//        }
        scrollShowHeaderListView.setUpHeaderViews(headView);
        lv = scrollShowHeaderListView.getListView();
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mParam1 == 0) {

                } else if (mParam1 == 1) {
                    TextView textView = (TextView) view.findViewById(R.id.tv_itemdate);
                    int printHistoryId = (int) textView.getTag();
                    String name = textView.getText().toString();
                    UIHelper.jump2Activity(getActivity(), NotebookActivity.class, printHistoryId, name, "print");
                }
            }
        });
    }
}
