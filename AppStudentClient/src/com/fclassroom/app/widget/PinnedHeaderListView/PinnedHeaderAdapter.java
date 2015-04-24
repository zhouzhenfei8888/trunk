// @author Bhavya Mehta
package com.fclassroom.app.widget.PinnedHeaderListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.Fragment.ErrortagFragment;
import com.fclassroom.activity.HomeActivity;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ErrorTagBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.Trans2PinYin;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import static com.fclassroom.activity.Fragment.ErrortagFragment.*;

// Customized adaptor to populate data in PinnedHeaderListView
public class PinnedHeaderAdapter extends BaseAdapter implements OnScrollListener, IPinnedHeader, Filterable {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_SECTION = 1;
    private static final int TYPE_MAX_COUNT = TYPE_SECTION + 1;

    LayoutInflater mLayoutInflater;
    int mCurrentSectionPosition = 0, mNextSectionPostion = 0;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store list view data
    ArrayList<String> mListItems;
    List<ErrorTagBean> errorTagBeans;

    // context object
    Context mContext;
    ErrorTagBean merrorTagBean = null;

    String accessToken;
    AppContext appContext;
    ViewHolder holder;
    ErrortagFragment errortagFragment;
    int subjectId;
    int gradeId;
    List<ErrorTagBean> errorTagBeanlist = new ArrayList<ErrorTagBean>();
    private ArrayList<String> TagNameList = new ArrayList<String>();

    public PinnedHeaderAdapter(ErrortagFragment errortagFragment,String accessToken, AppContext appContext, Context context, ArrayList<String> listItems, ArrayList<Integer> listSectionPos, List<ErrorTagBean> errorTagBeans) {
        this.mContext = context;
        this.mListItems = listItems;
        this.mListSectionPos = listSectionPos;
        this.errorTagBeans = errorTagBeans;
        this.accessToken = accessToken;
        this.appContext = appContext;
        this.errortagFragment = errortagFragment;
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        gradeId = PreferenceUtils.getInt(appContext,PreferenceUtils.GRADE_ID);
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mListItems.size();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return !mListSectionPos.contains(position);
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        return mListSectionPos.contains(position) ? TYPE_SECTION : TYPE_ITEM;
    }

    @Override
    public Object getItem(int position) {
        return mListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mListItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        holder = null;

        if (convertView == null) {
            holder = new ViewHolder();
            int type = getItemViewType(position);

            switch (type) {
                case TYPE_ITEM:
                    convertView = mLayoutInflater.inflate(R.layout.listview_itemtag, null);
//                    convertView = mLayoutInflater.inflate(R.layout.row_view, null);
                    holder.tagNumber = (TextView) convertView.findViewById(R.id.tv_tagnumber);
                    holder.setting = (ImageView) convertView.findViewById(R.id.iv_setting);
                    break;
                case TYPE_SECTION:
                    convertView = mLayoutInflater.inflate(R.layout.section_row_view, null);
                    break;
            }
            holder.tagName = (TextView) convertView.findViewById(R.id.row_title);
//            holder.tagName = (TextView) convertView.findViewById(R.id.row_title);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (getItemViewType(position) == TYPE_ITEM) {
            for (ErrorTagBean errorTagBean : errorTagBeans) {
                if (mListItems.get(position).equals(errorTagBean.getName())) {
                    merrorTagBean = errorTagBean;
                    holder.tagNumber.setText("" + merrorTagBean.getErrorQuestionCount());
                    holder.setting.setTag(errorTagBean.getId());
                    break;
                }
                ;
            }
            holder.setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final int id = (int) v.getTag();
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("");
                    builder.setItems(new String[]{"重命名", "打印", "删除"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                editTagNameDialog(accessToken, id, merrorTagBean.getName());
                            } else if (which == 1) {
                                printTag(accessToken, gradeId, subjectId, id);
                            } else if (which == 2) {
                                deleteTagDialog(accessToken, id);
                            }
                        }
                    });
                    builder.create().show();
                }
            });
        }
        holder.tagName.setText(mListItems.get(position).toString());
        return convertView;
    }

    private void printTag(final String accessToken, final int gradeId, final int subjectId, final int id) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    BaseResponseBean<String> responseBean = (BaseResponseBean<String>) msg.obj;
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(mContext);
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.printTag(accessToken,gradeId,subjectId,id);
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

    private void deleteTagDialog(final String accessToken, final int id) {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    BaseResponseBean<String> responseBean = (BaseResponseBean<String>) msg.obj;
                    UIHelper.ToastMessage(mContext,responseBean.getData().toString());
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(mContext);
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> responseBean = appContext.delTag(accessToken,id);
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

    private void editTagNameDialog(final String accessToken, final int id, String name) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_notebook, null, false);
        final EditText etName = (EditText) view.findViewById(R.id.et_notebookname);
        etName.setHint("请输入错因标签");
        TextView textView = (TextView) view.findViewById(R.id.tv_tite_dialog);
        textView.setText("重命名");
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
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
                editTagName(errortagFragment,accessToken, id, etName.getText().toString().trim());
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void editTagName(final ErrortagFragment errortagFragment, final String accessToken, final int tagId, final String name) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<String> baseResponseBean = (BaseResponseBean<String>) msg.obj;
//                    getTags();
                    UIHelper.ToastMessage(mContext, "修改成功！");
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(mContext);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<String> baseResponseBean = appContext.editTagNameDialog(accessToken, tagId, name);
                    msg.what = 1;
                    msg.obj = baseResponseBean;
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    public void getTags() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    BaseResponseBean<List<ErrorTagBean>> responseBean = (BaseResponseBean<List<ErrorTagBean>>) msg.obj;
                    errorTagBeanlist = responseBean.getData();
                    for (ErrorTagBean errorTagBean : errorTagBeanlist) {
                        TagNameList.add(errorTagBean.getName());
                    }
                    notifyDataSetChanged();
                } else if (msg.what == 0) {
                    UIHelper.ToastMessage(mContext, msg.obj.toString());
                } else if (msg.what == -1) {
                    ((AppException) msg.obj).makeToast(mContext);
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<List<ErrorTagBean>> responseBean = appContext.getErrorTagList(accessToken, subjectId);
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
    public int getPinnedHeaderState(int position) {
        // hide pinned header when items count is zero OR position is less than
        // zero OR
        // there is already a header in list view
        if (getCount() == 0 || position < 0 || mListSectionPos.indexOf(position) != -1) {
            return PINNED_HEADER_GONE;
        }

        // the header should get pushed up if the top item shown
        // is the last item in a section for a particular letter.
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        mNextSectionPostion = getNextSectionPosition(mCurrentSectionPosition);
        if (mNextSectionPostion != -1 && position == mNextSectionPostion - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }

        return PINNED_HEADER_VISIBLE;
    }

    public int getCurrentSectionPosition(int position) {
        String convert2PinYin = Trans2PinYin.getInstance().convert(mListItems.get(position).toString().substring(0, 1));
        String listChar = convert2PinYin.substring(0, 1).toUpperCase(Locale.getDefault());
        return mListItems.indexOf(listChar);
    }

    public int getNextSectionPosition(int currentSectionPosition) {
        int index = mListSectionPos.indexOf(currentSectionPosition);
        if ((index + 1) < mListSectionPos.size()) {
            return mListSectionPos.get(index + 1);
        }
        return mListSectionPos.get(index);
    }

    @Override
    public void configurePinnedHeader(View v, int position) {
        // set text in pinned header
        TextView header = (TextView) v;
        mCurrentSectionPosition = getCurrentSectionPosition(position);
        header.setText(mListItems.get(mCurrentSectionPosition));
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView) view).configureHeaderView(firstVisibleItem);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
    }

    @Override
    public Filter getFilter() {
        ErrortagFragment ErrortagFragment = (ErrortagFragment) ((HomeActivity) mContext).getSupportFragmentManager().findFragmentByTag("ErrortagFragment");
        return ErrortagFragment.new ListFilter();
    }

    public static class ViewHolder {
        public TextView tagName;
        public TextView tagNumber;
        public ImageView setting;
    }
}
