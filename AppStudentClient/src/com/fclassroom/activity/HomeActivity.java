package com.fclassroom.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.Fragment.CollateFragment;
import com.fclassroom.activity.Fragment.ErrortagFragment;
import com.fclassroom.activity.Fragment.HomeFragment;
import com.fclassroom.activity.Fragment.PrintFragment;
import com.fclassroom.activity.Fragment.RubbishFragment;
import com.fclassroom.activity.Fragment.SubjectFragment;
import com.fclassroom.activity.Fragment.WorkFragment;
import com.fclassroom.app.adapter.LeftAdapter;
import com.fclassroom.app.adapter.PopupListAdapter;
import com.fclassroom.app.bean.Archivement;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.GradeBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.PrintNumBean;
import com.fclassroom.app.bean.StudentInfoBean;
import com.fclassroom.app.bean.SubjectBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.BadgeView;
import com.fclassroom.appstudentclient.R;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends BaseActivity implements SubjectFragment.HideTop {
    private String TAG = "HomeActivity";
    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private ListView mListView;
    private LinearLayout setting;
    private LinearLayout linearhome;
    private AppContext appContext;
    private TextView grades;
    private TextView subjects;
    private PopupWindow gradePopup;
    private PopupWindow subjectPopup;
    private ListView gradeListView;
    private ListView subjectListView;
    private String GRADELISTNAME = "GradeList.list";
    private String SUBJECTLISTNAME = "SubjectList.list";
    private TextView studentName;
    private TextView jikeNum;
    String arr[] = {"首页", "整理本", "错因标签", "错题小黑屋", "打印车"};
    int image[] = {R.drawable.icon_home, R.drawable.icon_collatebook, R.drawable.icon_tag, R.drawable.icon_wrong_home, R.drawable.icon_print};
    private List<GradeBean> gradeBeanList;
    private List<SubjectBean> subjectBeanList;
    String accessToken;
    int gradeId;
    int subjectId;
    BadgeView badgeView;
    static TextView notifCount;
    String printCartQuestionCount;

    public interface HideTopHomeFragment {
        void DoHideTopHomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        //获取学生信息
        getAccountInfo();
        initToolbar();
        initViews();
    }

    private void initToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle(" 极课同学");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_search:
                        UIHelper.jump2Activity(HomeActivity.this, SearchActivity.class);
                        break;
                    case R.id.action_print:
                        getSupportFragmentManager().beginTransaction().replace(R.id.linear_home, new PrintFragment()).commit();
                        mToolbar.setTitle(arr[4]);
                        //                        UIHelper.jump2Activity(HomeActivity.this,AddwrongtagActivity.class);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void initViews() {
        linearhome = (LinearLayout) findViewById(R.id.linear_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_home);
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mToggle.syncState();
        mDrawerLayout.setDrawerListener(mToggle);
        //侧滑栏中各元素设置
        mListView = (ListView) findViewById(R.id.listview_home);
        List<HashMap<String, Object>> data = new ArrayList<>();
        for (int i = 0; i < arr.length; i++) {
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("ivTitle", image[i]);
            hashMap.put("tvTitle", arr[i]);
            data.add(hashMap);
        }
        mListView.setAdapter(new LeftAdapter(this, data));
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (position) {
                    case 0:
                        fragmentTransaction.replace(R.id.linear_home, new HomeFragment(), "HomeFragment");
                        fragmentTransaction.commit();
                        //                        getSupportFragmentManager().executePendingTransactions();
                        mDrawerLayout.closeDrawers();
                        mToolbar.setTitle("极课同学");
                        //   System.out.println(getSupportFragmentManager().findFragmentByTag("HomeFragment"));
                        break;
                    case 1:
                        fragmentTransaction.replace(R.id.linear_home, new CollateFragment(), "CollateFragment");
                        fragmentTransaction.commit();
                        mToolbar.setTitle(arr[position]);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 2:
                        fragmentTransaction.replace(R.id.linear_home, new ErrortagFragment(), "ErrortagFragment");
                        fragmentTransaction.commit();
                        mToolbar.setTitle(arr[position]);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 3:
                        fragmentTransaction.replace(R.id.linear_home, new RubbishFragment());
                        fragmentTransaction.commit();
                        mToolbar.setTitle(arr[position]);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 4:
                        fragmentTransaction.replace(R.id.linear_home, new PrintFragment());
                        fragmentTransaction.commit();
                        mToolbar.setTitle(arr[position]);
                        mDrawerLayout.closeDrawers();
                        break;
                    case 5:
                        fragmentTransaction.replace(R.id.linear_home, new WorkFragment());
                        fragmentTransaction.commit();
                        mToolbar.setTitle(arr[position]);
                        mDrawerLayout.closeDrawers();
                        break;
                }
            }
        });

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.linear_home, new HomeFragment(), "HomeFragment");
        fragmentTransaction.commit();
        //        getSupportFragmentManager().executePendingTransactions();
        //        System.out.println(getSupportFragmentManager().findFragmentByTag("HomeFragment"));

        //设置页面入口
        setting = (LinearLayout) findViewById(R.id.linear_setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.jump2Activity(HomeActivity.this, SettingActivity.class);
            }
        });
        //年级、科目下拉菜单
        grades = (TextView) findViewById(R.id.tv_grade_home);
        subjects = (TextView) findViewById(R.id.tv_subject_home);
        gradeBeanList = new ArrayList<GradeBean>();
        subjectBeanList = new ArrayList<SubjectBean>();
        grades.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGradePopwindow(v);
            }
        });
        subjects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSubjectPopwindow(v);
            }
        });
        //学生姓名、id
        studentName = (TextView) findViewById(R.id.tv_name_home);
        jikeNum = (TextView) findViewById(R.id.tv_id_home);
        studentName.setText(PreferenceUtils.getString(appContext, PreferenceUtils.STUDENT_NAME));
        jikeNum.setText("ID:" + PreferenceUtils.getString(appContext, PreferenceUtils.JIKE_NUM));
    }

    //年级、学科选择
    private void showGradePopwindow(View view) {
        if (gradePopup == null) {
            View popupView = getLayoutInflater().inflate(R.layout.item_clzss_popup, new LinearLayout(this), false);
            gradePopup = new PopupWindow(popupView, UIHelper.dipToPxInt(this, 100), ViewGroup.LayoutParams.WRAP_CONTENT, true);
            gradePopup.setTouchable(true);
            gradePopup.setOutsideTouchable(true);
            gradePopup.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            gradeListView = (ListView) popupView.findViewById(R.id.clzss_listview);
            PopupListAdapter adapter = null;
            adapter = new PopupListAdapter(this, R.layout.listview_item_tv_alone, getGradeListfromlocal());
            gradeListView.setAdapter(adapter);
            gradeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    grades.setText(getGradeListfromlocal().get(position));
                    System.out.println(position);
                    List<GradeBean> list = getGradeListfromlocalBean();
                    PreferenceUtils.putInt(appContext, PreferenceUtils.GRADE_ID, list.get(position).getId());
                    gradePopup.dismiss();
                }
            });
            gradeListView.setItemChecked(0, true);
        }
        if (!gradePopup.isShowing()) {
            gradePopup.showAsDropDown(view, 0, 0);
        } else {
            gradePopup.dismiss();
        }
    }

    private void showSubjectPopwindow(View view) {
        if (subjectPopup == null) {
            View popupView = getLayoutInflater().inflate(R.layout.item_clzss_popup, new LinearLayout(this), false);
            subjectPopup = new PopupWindow(popupView, UIHelper.dipToPxInt(this, 100), ViewGroup.LayoutParams.WRAP_CONTENT, true);
            subjectPopup.setTouchable(true);
            subjectPopup.setOutsideTouchable(true);
            subjectPopup.setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
            subjectListView = (ListView) popupView.findViewById(R.id.clzss_listview);
            PopupListAdapter adapter = null;
            adapter = new PopupListAdapter(this, R.layout.listview_item_tv_alone, getSubjectListfromlocal());
            subjectListView.setAdapter(adapter);
            subjectListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    subjects.setText(getSubjectListfromlocal().get(position));
                    System.out.println(position);
                    List<SubjectBean> list = getSubjectListfromlocalBean();
                    PreferenceUtils.putInt(appContext, PreferenceUtils.SUBJECT_ID, list.get(position).getId());
                    subjectPopup.dismiss();
                }
            });
            subjectListView.setItemChecked(0, true);
        }
        if (!subjectPopup.isShowing()) {
            subjectPopup.showAsDropDown(view, 0, 0);
        } else {
            subjectPopup.dismiss();
        }
    }

    private void getAccountInfo() {
        appContext = (AppContext) getApplication();
        getStudentInfo();
        getGradeList();
        getSubjectList();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
    }

    private void getStudentInfo() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<StudentInfoBean> responseBean = (BaseResponseBean<StudentInfoBean>) msg.obj;
                    appContext.saveStudentInfo(responseBean.getData());
                    grades.setText(responseBean.getData().getGradeName());
                    subjects.setText(responseBean.getData().getSubjectName());
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(HomeActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(HomeActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<StudentInfoBean> responseBean = appContext.getStudentInfo(PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN));
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

    //从网络获取年级信息，并保存在本地
    private void getGradeList() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<ArrayList<GradeBean>> responseBean = (BaseResponseBean<ArrayList<GradeBean>>) msg.obj;
                    //                    List<GradeBean> list = responseBean.getData();
                    saveGradeBeanList(responseBean.getData(), GRADELISTNAME);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(HomeActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(HomeActivity.this);
                }
            }
        };

        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                BaseResponseBean<ArrayList<GradeBean>> responseBean = null;
                try {
                    responseBean = appContext.getGradeList(PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN));
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

    //从网络获取学科信息，并保存本地
    private void getSubjectList() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<ArrayList<SubjectBean>> subjectBeanList = (BaseResponseBean<ArrayList<SubjectBean>>) msg.obj;
                    saveSubjectBeanList(subjectBeanList.getData(), SUBJECTLISTNAME);
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(HomeActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(HomeActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    int gradId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
                    BaseResponseBean<ArrayList<SubjectBean>> response =
                            appContext.getSubjectList(PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN),
                                    PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID));
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

    private void saveSubjectBeanList(ArrayList<SubjectBean> list, String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getSubjectListfromlocal() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        ArrayList<SubjectBean> list = null;
        List<String> stringlist = new ArrayList<>();
        try {
            fis = openFileInput(SUBJECTLISTNAME);
            ois = new ObjectInputStream(fis);
            list = (ArrayList<SubjectBean>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (list != null) {
            for (SubjectBean subjectBean : list) {
                stringlist.add(subjectBean.getSubjectName());
            }
        }
        return stringlist;
    }

    private List<SubjectBean> getSubjectListfromlocalBean() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        ArrayList<SubjectBean> list = null;
        List<String> stringlist = new ArrayList<>();
        try {
            fis = openFileInput(SUBJECTLISTNAME);
            ois = new ObjectInputStream(fis);
            list = (ArrayList<SubjectBean>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private void saveGradeBeanList(ArrayList<GradeBean> list, String fileName) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(fileName, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(list);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null)
                    oos.close();
                if (fos != null)
                    fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<String> getGradeListfromlocal() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        ArrayList<GradeBean> list = null;
        List<String> stringlist = new ArrayList<>();
        try {
            fis = openFileInput(GRADELISTNAME);
            ois = new ObjectInputStream(fis);
            list = (ArrayList<GradeBean>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (list != null) {
            for (GradeBean gradeBean : list) {
                stringlist.add(gradeBean.getGradeName());
            }
        }
        return stringlist;
    }

    private List<GradeBean> getGradeListfromlocalBean() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        ArrayList<GradeBean> list = null;
        List<String> stringlist = new ArrayList<>();
        try {
            fis = openFileInput(GRADELISTNAME);
            ois = new ObjectInputStream(fis);
            list = (ArrayList<GradeBean>) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null)
                    ois.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        MenuItem item = menu.findItem(R.id.action_print);
        MenuItemCompat.setActionView(item, R.layout.print_count);
        RelativeLayout relativeLayout = (RelativeLayout) MenuItemCompat.getActionView(item);
        notifCount = (TextView) relativeLayout.findViewById(R.id.print_num);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction().replace(R.id.linear_home, new PrintFragment()).commit();
                mToolbar.setTitle(arr[4]);
            }
        });
        updateprintnum();
        return super.onCreateOptionsMenu(menu);
    }

    private void updateprintnum() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<PrintNumBean> responseBean = (BaseResponseBean<PrintNumBean>) msg.obj;
                    printCartQuestionCount = "" + responseBean.getData().getPrintCartQuestionCount();
                    notifCount.setText(printCartQuestionCount);
                    updateprintnum();
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(HomeActivity.this, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(HomeActivity.this);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    sleep(10*1000);
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * DoHideTopHomeFragment具体实现在HomeFragment中
     */
    @Override
    public void DoHideTop() {
        FragmentManager fm = getSupportFragmentManager();
        ((HomeFragment) fm.findFragmentByTag("HomeFragment")).DoHideTopHomeFragment();
    }
}
