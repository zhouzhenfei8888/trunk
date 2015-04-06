package com.fclassroom.activity.Fragment;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.activity.NotebookActivity;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ErrorTagBean;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.Trans2PinYin;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.app.widget.PinnedHeaderListView.IndexBarView;
import com.fclassroom.app.widget.PinnedHeaderListView.PinnedHeaderAdapter;
import com.fclassroom.app.widget.PinnedHeaderListView.PinnedHeaderListView;
import com.fclassroom.appstudentclient.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * 错因标签页
 */
public class ErrortagFragment extends Fragment {

    private ListView mlistView;
    private AppContext appContext;
    private int gradeId,subjectId;
    private String accessToken;
    private List<HashMap<String, Object>> list;
    private List<String> TagNameList;
    // unsorted list items
    ArrayList<String> mItems;

    // array list to store section positions
    ArrayList<Integer> mListSectionPos;

    // array list to store listView data
    ArrayList<String> mListItems;

    // custom list view with pinned header
    PinnedHeaderListView mListView;

    // custom adapter
    PinnedHeaderAdapter mAdaptor;

    // search box
    EditText mSearchView;

    // loading view
    ProgressBar mLoadingView;

    // empty view
    TextView mEmptyView;

    Bundle savedInstanceState;

    public ErrortagFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        appContext = (AppContext) getActivity().getApplication();
        accessToken = PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN);
        gradeId = PreferenceUtils.getInt(appContext, PreferenceUtils.GRADE_ID);
        subjectId = PreferenceUtils.getInt(appContext, PreferenceUtils.SUBJECT_ID);
        initData();
    }

    private void initData() {
        TagNameList = new ArrayList<String>();
        getTags();
    }

    private void getTags() {
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    BaseResponseBean<List<ErrorTagBean>> responseBean = (BaseResponseBean<List<ErrorTagBean>>) msg.obj;
                    List<ErrorTagBean> list = responseBean.getData();
                    for(ErrorTagBean errorTagBean:list){
                        TagNameList.add(errorTagBean.getName());
                    }
                }else if(msg.what == 0){
                    UIHelper.ToastMessage(getActivity(),msg.obj.toString());
                }else if(msg.what == -1){
                    ((AppException)msg.obj).makeToast(getActivity());
                }
            }
        };
        new Thread(){
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    BaseResponseBean<List<ErrorTagBean>> responseBean = appContext.getErrorTagList(accessToken,subjectId);
                    if(responseBean.getError_code() == 0){
                        msg.what = 1;
                        msg.obj = responseBean;
                    }else {
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
        View view = inflater.inflate(R.layout.fragment_errortag, container, false);
        initViews(view);
        // Array to ArrayList
//        mItems = new ArrayList<String>(Arrays.asList(UIHelper.ITEMS));
        mItems = new ArrayList<String>();
        mItems.addAll(TagNameList);
        mListSectionPos = new ArrayList<Integer>();
        mListItems = new ArrayList<String>();
        // for handling configuration change
        if (savedInstanceState != null) {
            mListItems = savedInstanceState.getStringArrayList("mListItems");
            mListSectionPos = savedInstanceState.getIntegerArrayList("mListSectionPos");

            if (mListItems != null && mListItems.size() > 0 && mListSectionPos != null && mListSectionPos.size() > 0) {
                setListAdaptor();
            }

            String constraint = savedInstanceState.getString("constraint");
            if (constraint != null && constraint.length() > 0) {
                mSearchView.setText(constraint);
                setIndexBarViewVisibility(constraint);
            }
        } else {
            new Poplulate().execute(mItems);
        }
        return view;
    }

    private void initViews(View view) {
        mSearchView = (EditText) view.findViewById(R.id.search_view);
        mLoadingView = (ProgressBar) view.findViewById(R.id.loading_view);
        mListView = (PinnedHeaderListView) view.findViewById(R.id.list_view);
        mEmptyView = (TextView) view.findViewById(R.id.empty_view);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIHelper.ToastMessage(getActivity(),mListItems.get(position));
            }
        });
    }



    private void setListAdaptor() {
        // create instance of PinnedHeaderAdapter and set adapter to list view
        mAdaptor = new PinnedHeaderAdapter(getActivity(), mListItems, mListSectionPos);
        mListView.setAdapter(mAdaptor);

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        // set header view
        View pinnedHeaderView = inflater.inflate(R.layout.section_row_view, mListView, false);
        mListView.setPinnedHeaderView(pinnedHeaderView);

        // set index bar view
        IndexBarView indexBarView = (IndexBarView) inflater.inflate(R.layout.index_bar_view, mListView, false);
        indexBarView.setData(mListView, mListItems, mListSectionPos);
        mListView.setIndexBarView(indexBarView);

        // set preview text view
        View previewTextView = inflater.inflate(R.layout.preview_view, mListView, false);
        mListView.setPreviewView(previewTextView);

        // for configure pinned header view on scroll change
        mListView.setOnScrollListener(mAdaptor);
    }

    public class ListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread,
            // and
            // not the UI thread.
            String constraintStr = constraint.toString().toLowerCase(Locale.getDefault());
            FilterResults result = new FilterResults();

            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<String> filterItems = new ArrayList<String>();

                synchronized (this) {
                    for (String item : mItems) {
                        if (item.toLowerCase(Locale.getDefault()).startsWith(constraintStr)) {
                            filterItems.add(item);
                        }
                    }
                    result.count = filterItems.size();
                    result.values = filterItems;
                }
            } else {
                synchronized (this) {
                    result.count = mItems.size();
                    result.values = mItems;
                }
            }
            return result;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<String> filtered = (ArrayList<String>) results.values;
            setIndexBarViewVisibility(constraint.toString());
            // sort array and extract sections in background Thread
            new Poplulate().execute(filtered);
        }
    }

    private void setIndexBarViewVisibility(String constraint) {
        // hide index bar for search results
        if (constraint != null && constraint.length() > 0) {
            mListView.setIndexBarVisibility(false);
        } else {
            mListView.setIndexBarVisibility(true);
        }
    }

    private class Poplulate extends AsyncTask<ArrayList<String>, Void, Void> {

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

        private void showEmptyText(View contentView, View loadingView, View emptyView) {
            contentView.setVisibility(View.GONE);
            loadingView.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPreExecute() {
            // show loading indicator
            showLoading(mListView, mLoadingView, mEmptyView);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(ArrayList<String>... params) {
            mListItems.clear();
            mListSectionPos.clear();
            ArrayList<String> items = params[0];
            if (mItems.size() > 0) {

                // NOT forget to sort array
                Collections.sort(items, new SortIgnoreCase());

                String prev_section = "";
                for (String current_item : items) {
                    String item2PinYin = Trans2PinYin.getInstance().convert(current_item.substring(0,1));
                    String current_section = item2PinYin.substring(0, 1).toUpperCase(Locale.getDefault());

                    if (!prev_section.equals(current_section)) {
                        mListItems.add(current_section);
                        mListItems.add(current_item);
                        // array list of section positions
                        mListSectionPos.add(mListItems.indexOf(current_section));
                        prev_section = current_section;
                    } else {
                        mListItems.add(current_item);
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (!isCancelled()) {
                if (mListItems.size() <= 0) {
                    showEmptyText(mListView, mLoadingView, mEmptyView);
                } else {
                    setListAdaptor();
                    showContent(mListView, mLoadingView, mEmptyView);
                }
            }
            super.onPostExecute(result);
        }
    }

    public class SortIgnoreCase implements Comparator<String> {
        public int compare(String s1, String s2) {
            s1 = Trans2PinYin.getInstance().convertAll(s1);
            s2 = Trans2PinYin.getInstance().convertAll(s2);
            return s1.compareToIgnoreCase(s2);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mListItems != null && mListItems.size() > 0) {
            outState.putStringArrayList("mListItems", mListItems);
        }
        if (mListSectionPos != null && mListSectionPos.size() > 0) {
            outState.putIntegerArrayList("mListSectionPos", mListSectionPos);
        }
        String searchText = mSearchView.getText().toString();
        if (searchText != null && searchText.length() > 0) {
            outState.putString("constraint", searchText);
        }
        super.onSaveInstanceState(outState);
    }

    //    public class TagAdapter extends BaseAdapter{
//        private  Context context;
//        private  int res;
//        private  List<HashMap<String,Object>> list;
//        private LayoutInflater inflater;
//
//        public TagAdapter(Context context, List<HashMap<String, Object>> list, int listview_itemtag) {
//            this.context = context;
//            this.list = list;
//            this.res = listview_itemtag;
//            this.inflater = LayoutInflater.from(context);
//        }
//
//        @Override
//        public int getCount() {
//            return list.size();
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return position;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//            Holder holder = null;
//            if (convertView == null){
//                holder = new Holder();
//                convertView = inflater.inflate(res,parent,false);
//                holder.tagName = (TextView)convertView.findViewById(R.id.tv_tagname);
//                holder.tagNumber = (TextView)convertView.findViewById(R.id.tv_tagnumber);
//                holder.setting = (ImageView)convertView.findViewById(R.id.iv_setting);
//                convertView.setTag(holder);
//            }else {
//                holder = (Holder)convertView.getTag();
//            }
//            holder.tagName.setText((String)list.get(position).get("tagname"));
//            holder.tagNumber.setText((String)list.get(position).get("tagnumber"));
//            holder.setting.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    UIHelper.ToastMessage(context,"setting");
//                }
//            });
//            return convertView;
//        }
//
//        class Holder {
//            TextView tagName;
//            TextView tagNumber;
//            ImageView setting;
//        }
//    }

}
