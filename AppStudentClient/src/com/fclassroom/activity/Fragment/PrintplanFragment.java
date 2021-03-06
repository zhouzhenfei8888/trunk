package com.fclassroom.activity.Fragment;


import android.content.Intent;
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
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.NotebookActivity;
import com.fclassroom.app.adapter.PrintRecordAdapter;
import com.fclassroom.app.adapter.SubjectAdapter;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.PrintNumBean;
import com.fclassroom.app.bean.PrintRecoderBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.ScrollShowHeaderListView;
import com.fclassroom.appstudentclient.R;

import java.io.IOException;
import java.util.ArrayList;
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
    private RelativeLayout topView;
    private LinearLayout bottomView;
    private TextView selectAll, print, haveSelected, share, delete;
    int downloadType = 0;
    int printHistoryId = 0;
    String printCartQuestionCount;
    String str;

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
            printplanAdapter = new SubjectAdapter(getActivity(), PrintPlanList, lv);
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
                    printCartQuestionCount = "" + responseBean.getData().getPrintCartQuestionCount();
                    String downloadCount = "" + responseBean.getData().getDownloadCount();
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
                    if (null != responseBean.getData()) {
                        List<PageBean.SubjectItemBean> list = responseBean.getData().getList();
                        PrintPlanList.clear();
                        PrintPlanList.addAll(list);
                        printplanAdapter = new SubjectAdapter(getActivity(), PrintPlanList, lv);
                        lv.setAdapter(printplanAdapter);
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
        topView = (RelativeLayout) view.findViewById(R.id.linear_top);
        bottomView = (LinearLayout) view.findViewById(R.id.linear_bottom);
//        if (mParam1 == 0) {
//            tv.setText("待打印100题");
//        } else if (mParam1 == 1) {
//            tv.setText("打印记录20条");
//        }
        selectAll = (TextView) view.findViewById(R.id.tv_selectall);
        haveSelected = (TextView) view.findViewById(R.id.tv_haveselected);
        print = (TextView) view.findViewById(R.id.tv_print);
        delete = (TextView) view.findViewById(R.id.tv_delete);
        share = (TextView) view.findViewById(R.id.tv_share);
        share.setOnClickListener(new clickListener());
        delete.setOnClickListener(new clickListener());
        print.setOnClickListener(new clickListener());
        selectAll.setOnClickListener(new clickListener());
        scrollShowHeaderListView.setUpHeaderViews(headView);
        lv = scrollShowHeaderListView.getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Mulmode();
                return true;
            }
        });
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mParam1 == 0) {
                    if (lv.getChoiceMode() == ListView.CHOICE_MODE_MULTIPLE) {
                        ImageView iv = (ImageView) view.findViewById(R.id.iv_subject);
                        PageBean.SubjectItemBean subjectItemBean = (PageBean.SubjectItemBean) iv.getTag();
                        if (!lv.isItemChecked(position)) {
                            ((CheckBox) view.findViewById(R.id.checkbox_choice)).setChecked(false);
                            appContext.printlist.remove(subjectItemBean);
                        } else {
                            ((CheckBox) view.findViewById(R.id.checkbox_choice)).setChecked(true);
                            appContext.printlist.add(subjectItemBean);
                        }
                        updateSeletedCount();
                    } else {

                    }

                } else if (mParam1 == 1) {
                    TextView textView = (TextView) view.findViewById(R.id.tv_itemdate);
                    int printHistoryId = (int) textView.getTag();
                    String name = textView.getText().toString();
                    UIHelper.jump2Activity(getActivity(), NotebookActivity.class, printHistoryId, name, "print");
                }
            }
        });
    }

    private void updateSeletedCount() {
        haveSelected.setText("已选择" + Integer.toString(lv.getCheckedItemCount()) + "题");
    }

    private void Mulmode() {
        appContext.printlist.clear();
        unSelectedAll();
        bottomView.setVisibility(View.VISIBLE);
        topView.setVisibility(View.VISIBLE);
        topView.setFocusable(true);
        bottomView.setFocusable(true);
        printplanAdapter.setMulMode(true);
        printplanAdapter.notifyDataSetChanged();
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    private void singleMode() {
        lv.clearChoices();
        topView.setVisibility(View.GONE);
        bottomView.setVisibility(View.GONE);
        topView.setFocusable(false);
        bottomView.setFocusable(false);
        printplanAdapter.setMulMode(false);
        printplanAdapter.notifyDataSetChanged();
        lv.setChoiceMode(ListView.CHOICE_MODE_NONE);
    }

    public void selectedAll() {
        for (int i = 0; i < printplanAdapter.getCount(); i++) {
            lv.setItemChecked(i + 1, true);
        }
        appContext.printlist.clear();
        appContext.printlist = PrintPlanList;
        updateSeletedCount();
    }

    public void unSelectedAll() {
        lv.clearChoices();
        updateSeletedCount();
    }

    class clickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_selectall:
                    selectedAll();
                    break;
                case R.id.tv_print:
                    StringBuilder examQuestionIdsBuilder2 = new StringBuilder("");
                    for (PageBean.SubjectItemBean subjectItemBean : appContext.printlist) {
                        examQuestionIdsBuilder2.append(subjectItemBean.getExamQuestionId()).append(",");
                    }
                    examQuestionIdsBuilder2.deleteCharAt(examQuestionIdsBuilder2.length() - 1);
                    String examQuestionIds2 = examQuestionIdsBuilder2.toString();
                    System.out.println(lv.getCheckedItemCount());
                    System.out.println(printCartQuestionCount);
                    if (("" + lv.getCheckedItemCount()).equals(printCartQuestionCount)) {
                        downloadAllErrorQuestions(accessToken, gradeId, subjectId, downloadType);
                    } else {
                        downloadErrorQuestions(accessToken, gradeId, subjectId, downloadType, printHistoryId, examQuestionIds2);
                    }
                    break;
                case R.id.tv_share:
                    StringBuilder examQuestionIdsBuilder3 = new StringBuilder("");
                    for (PageBean.SubjectItemBean subjectItemBean : appContext.printlist) {
                        examQuestionIdsBuilder3.append(subjectItemBean.getExamQuestionId()).append(",");
                    }
                    examQuestionIdsBuilder3.deleteCharAt(examQuestionIdsBuilder3.length() - 1);
                    String examQuestionIds3 = examQuestionIdsBuilder3.toString();
                    getStr(accessToken, gradeId, subjectId, downloadType, printHistoryId, examQuestionIds3);
                    break;
                case R.id.tv_delete:
                    StringBuilder examQuestionIdsBuilder = new StringBuilder("");
                    for (PageBean.SubjectItemBean subjectItemBean : appContext.printlist) {
                        examQuestionIdsBuilder.append(subjectItemBean.getExamQuestionId()).append(",");
                    }
                    examQuestionIdsBuilder.deleteCharAt(examQuestionIdsBuilder.length() - 1);
                    String examQuestionIds = examQuestionIdsBuilder.toString();
                    delPrintCartErrorQuestions(accessToken, gradeId, subjectId, examQuestionIds);
                    break;

            }
        }
    }

    private void downloadAllErrorQuestions(final String accessToken, final int gradeId, final int subjectId, final int downloadType) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                } else if (msg.what == 1) {
                    str = ((BaseResponseBean<String>) msg.obj).getData();
                    downloadfile(str);
                    UIHelper.ToastMessage(getActivity(), "下载成功！已保存在SD卡JIKE目录下");
                    int pageNo = 1;
//                    getPrintplanList(accessToken, gradeId, subjectId, pageSize, pageNo);
                    PrintPlanList.clear();
                    printplanAdapter.notifyDataSetChanged();
                    getPrintNum(accessToken, gradeId, subjectId);
//                    getPrintRecoderList(accessToken,gradeId,subjectId,orderTime);
                    singleMode();
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.downloadAllErrorQuestions(accessToken, gradeId, subjectId, downloadType);
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

    private void downloadErrorQuestions(final String accessToken, final int gradeId, final int subjectId, final int downloadType, final int printHistoryId, final String examQuestionIds) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                } else if (msg.what == 1) {
                    str = ((BaseResponseBean<String>) msg.obj).getData();
                    downloadfile(str);
                    UIHelper.ToastMessage(getActivity(), "下载成功！已保存在SD卡JIKE目录下");
                    int pageNo = 1;
                    getPrintplanList(accessToken, gradeId, subjectId, pageSize, pageNo);
                    getPrintNum(accessToken, gradeId, subjectId);
                    singleMode();
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.downloadErrorQuestions(accessToken, gradeId, subjectId, downloadType, printHistoryId, examQuestionIds);
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

    private void getStr(final String accessToken, final int gradeId, final int subjectId, final int downloadType, final int printHistoryId, final String examQuestionIds) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                } else if (msg.what == 1) {
                    str = ((BaseResponseBean<String>) msg.obj).getData();
                    Intent sendIntent = new Intent();
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_TEXT, str);
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                    int pageNo = 1;
                    getPrintplanList(accessToken, gradeId, subjectId, pageSize, pageNo);
                    getPrintNum(accessToken, gradeId, subjectId);
                    singleMode();
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(getActivity(), msg.obj.toString());
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.downloadErrorQuestions(accessToken, gradeId, subjectId, downloadType, printHistoryId, examQuestionIds);
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

    private void downloadfile(final String filename) {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
            }
        };
        new Thread() {
            @Override
            public void run() {
                try {
                    appContext.downloadfile(filename);
                } catch (AppException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void delPrintCartErrorQuestions(final String accessToken, final int gradeId, final int subjectId, final String examQuestionIds) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(getActivity());
                } else {
                    int pageNo = 1;
                    getPrintplanList(accessToken, gradeId, subjectId, pageSize, pageNo);
                    getPrintNum(accessToken, gradeId, subjectId);
                    singleMode();
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    appContext.delPrintCartErrorQuestions(accessToken, gradeId, subjectId, examQuestionIds);
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
