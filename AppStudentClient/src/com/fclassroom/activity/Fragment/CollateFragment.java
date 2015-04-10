package com.fclassroom.activity.Fragment;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.DialognewbookActivity;
import com.fclassroom.activity.NotebookActivity;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ErrorBookBean;
import com.fclassroom.app.bean.NoteBookBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * 整理本
 */
public class CollateFragment extends Fragment {

    private ListView mlvbook;
    private List<HashMap<String, Object>> list;
    private LinearLayout add;
    private AppContext appContext;
    private String accessToken;
    private int gradeId;
    private int subjectId;
    private BookAdapter bookAdapter;
    private List<ErrorBookBean> ErrorBookList;

    public CollateFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("bookname", "自定义" + i);
            data.put("amount", "20" + i);
            list.add(data);
        }
        //获取本地信息
        appContext = (AppContext) getActivity().getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        initData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_collate, container, false);
        initVeiw(view);
        return view;
    }

    private void initData() {
        //获取错题本列表
        getErrorBookList(accessToken, gradeId, subjectId);
    }

    //获取错题本名字列表
    private void getErrorBookList(final String accessToken, final int gradeId, final int subjectId) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),"","正在加载。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<ArrayList<ErrorBookBean>> responseBean = (BaseResponseBean<ArrayList<ErrorBookBean>>) msg.obj;
                    ErrorBookList = responseBean.getData();
//                    bookAdapter.notifyDataSetChanged();
                    bookAdapter = new BookAdapter(getActivity(), ErrorBookList, R.layout.listview_itembook);
                    mlvbook.setAdapter(bookAdapter);
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
                    BaseResponseBean<ArrayList<ErrorBookBean>> responseBean = appContext.getNoteBookList(accessToken, gradeId, subjectId);
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

    private void initVeiw(View view) {
        //初始化控件
        add = (LinearLayout) view.findViewById(R.id.linear_add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UIHelper.jump2Activity(getActivity(), DialognewbookActivity.class);
                showDialog();
            }
        });
        mlvbook = (ListView) view.findViewById(R.id.listview_collate);
        if (ErrorBookList != null) {
            bookAdapter = new BookAdapter(getActivity(), ErrorBookList, R.layout.listview_itembook);
            mlvbook.setAdapter(bookAdapter);
        }
        mlvbook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                UIHelper.jump2Activity(getActivity(), NotebookActivity.class);
                TextView tv = (TextView) view.findViewById(R.id.tv_bookname);
                ErrorBookBean data = (ErrorBookBean) tv.getTag();
                UIHelper.jump2Activity(getActivity(), NotebookActivity.class,data.getId(),data.getName(),"collate");
            }
        });
    }

    private void showDialog() {
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_notebook, null, false);
        final EditText editText = (EditText) view.findViewById(R.id.et_notebookname);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("新建整理本");
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addNoteBook(accessToken, gradeId, subjectId, editText.getText().toString().trim());
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void addNoteBook(final String accessToken, final int gradeId, final int subjectId, final String bookname) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),"","正在加载。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<NoteBookBean> responseBean = (BaseResponseBean<NoteBookBean>) msg.obj;
                    initData();
//                    bookAdapter.notifyDataSetChanged();
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
                    BaseResponseBean<NoteBookBean> responseBean = appContext.addNoteBook(accessToken, gradeId, subjectId, bookname);
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
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public class BookAdapter extends BaseAdapter {
        private Context context;
        private List<ErrorBookBean> list;
        private int resource;
        private LayoutInflater inflater;

        public BookAdapter(Context context, List<ErrorBookBean> list, int i) {
            this.context = context;
            this.list = list;
            this.resource = i;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(resource, null);
                holder = new Holder();
                holder.bookname = (TextView) convertView.findViewById(R.id.tv_bookname);
                holder.subjectnumber = (TextView) convertView.findViewById(R.id.tv_subjectnumber);
                holder.setting = (ImageView) convertView.findViewById(R.id.iv_setting);
                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();
            }
            if (list != null) {
                final ErrorBookBean bookBean = list.get(position);
                holder.bookname.setTag(bookBean);
                holder.bookname.setText(bookBean.getName().toString().trim());
                holder.subjectnumber.setText("共" + bookBean.getQuestionCount() + "题");
                holder.setting.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showlistDialog(position, bookBean);
                    }
                });
            }
            return convertView;
        }

        class Holder {
            TextView bookname;
            TextView subjectnumber;
            ImageView setting;
        }
    }

    private void showlistDialog(int position, final ErrorBookBean bookBean) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("");
        builder.setItems(new String[]{"重命名", "打印精题本", "删除精题本"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    editBookNameDialog(accessToken, bookBean.getId());
                } else if (which == 1) {
                    addNoteBookToPrintPlan(accessToken,gradeId,subjectId,bookBean.getId());
                } else if (which == 2) {
                    deleteNoteBookDialog(accessToken, bookBean.getId());
                }
            }
        });
        builder.create().show();
    }

    private void addNoteBookToPrintPlan(final String accessToken, final int gradeId, final int subjectId, final int id) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),"","正在加载。。。");
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    BaseResponseBean<String> responseBean = (BaseResponseBean<String>) msg.obj;
                }else if(msg.what == 0){
                    UIHelper.ToastMessage(getActivity(),msg.obj.toString());
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(getActivity());
                }
                progressDialog.dismiss();
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.addNoteBookToPrintPlan(accessToken,gradeId,subjectId,id);
                    if(responseBean.getError_code() == 0){
                        msg.what = 1;
                        msg.obj = responseBean;
                    }else if(responseBean.getError_code()!=0){
                        msg.what = 0;
                        msg.obj = responseBean.getError_msg();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what =-1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void deleteNoteBookDialog(final String accessToken, final int id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("确定要删除此精题本？");
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteNoteBook(accessToken,id);
            }
        });
        builder.create().show();
    }

    private void deleteNoteBook(final String accessToken, final int id) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),"","正在加载。。。");
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what ==1){
                    String result = msg.obj.toString();
                    initData();
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(getActivity());
                }
                progressDialog.dismiss();
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    String data = appContext.deleteNoteBook(accessToken,id);
                    msg.what = 1;
                    msg.obj = data;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    private void editBookNameDialog(final String accessToken, final int id) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_notebook, null, false);
        final EditText etName = (EditText) view.findViewById(R.id.et_notebookname);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("");
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeNoteName(accessToken, id, etName.getText().toString().trim());
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void changeNoteName(final String accessToken, final int id, final String name) {
        final ProgressDialog progressDialog = ProgressDialog.show(getActivity(),"","正在加载。。。");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    String result = msg.obj.toString();
                    initData();
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
                String data = null;
                try {
                    data = appContext.changeNoteName(accessToken, id, name);
                    msg.what = 1;
                    msg.obj = data;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

}
