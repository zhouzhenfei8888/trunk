package com.fclassroom.app.common;

import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fclassroom.AppContext;
import com.fclassroom.AppManager;
import com.fclassroom.activity.StartActivity;
import com.fclassroom.appstudentclient.R;

/**
 * 应用程序UI工具包：封装UI相关的一些操作
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class UIHelper {
    private final static String TAG = "UIHelper";

    public final static int LISTVIEW_ACTION_INIT = 0x01;
    public final static int LISTVIEW_ACTION_REFRESH = 0x02;
    public final static int LISTVIEW_ACTION_SCROLL = 0x03;
    public final static int LISTVIEW_ACTION_CHANGE_CATALOG = 0x04;

    public final static int LISTVIEW_DATA_MORE = 0x01;
    public final static int LISTVIEW_DATA_LOADING = 0x02;
    public final static int LISTVIEW_DATA_FULL = 0x03;
    public final static int LISTVIEW_DATA_EMPTY = 0x04;

    public final static int LISTVIEW_DATATYPE_NEWS = 0x01;
    public final static int LISTVIEW_DATATYPE_BLOG = 0x02;
    public final static int LISTVIEW_DATATYPE_POST = 0x03;
    public final static int LISTVIEW_DATATYPE_TWEET = 0x04;
    public final static int LISTVIEW_DATATYPE_ACTIVE = 0x05;
    public final static int LISTVIEW_DATATYPE_MESSAGE = 0x06;
    public final static int LISTVIEW_DATATYPE_COMMENT = 0x07;

    public final static int REQUEST_CODE_FOR_RESULT = 0x01;
    public final static int REQUEST_CODE_FOR_REPLY = 0x02;

    /**
     * 表情图片匹配
     */
    private static Pattern facePattern = Pattern
            .compile("\\[{1}([0-9]\\d*)\\]{1}");

    /**
     * 全局web样式
     */
    // 链接样式文件，代码块高亮的处理
    public final static String linkCss = "<script type=\"text/javascript\" src=\"file:///android_asset/shCore.js\"></script>"
            + "<script type=\"text/javascript\" src=\"file:///android_asset/brush.js\"></script>"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shThemeDefault.css\">"
            + "<link rel=\"stylesheet\" type=\"text/css\" href=\"file:///android_asset/shCore.css\">"
            + "<script type=\"text/javascript\">SyntaxHighlighter.all();</script>";
    public final static String WEB_STYLE = linkCss + "<style>* {font-size:14px;line-height:20px;} p {color:#333;} a {color:#3E62A6;} img {max-width:310px;} "
            + "img.alignleft {float:left;max-width:120px;margin:0 10px 5px 0;border:1px solid #ccc;background:#fff;padding:2px;} "
            + "pre {font-size:9pt;line-height:12pt;font-family:Courier New,Arial;border:1px solid #ddd;border-left:5px solid #6CE26C;background:#f6f6f6;padding:5px;overflow: auto;} "
            + "a.tag {font-size:15px;text-decoration:none;background-color:#bbd6f3;border-bottom:2px solid #3E6D8E;border-right:2px solid #7F9FB6;color:#284a7b;margin:2px 2px 2px 0;padding:2px 4px;white-space:nowrap;}</style>";

    // an array of countries to display in the list
    public static final String[] ITEMS = new String[]{"函数曲线", "集合与元素", "三角函数", "复数的基本概念", "一元二次不等式", "等差和等比公式", "集合与常用逻辑用语",
            "集合的概念",
            "集合的运算",
            "常用逻辑用语",
            "命题及其关系",
            "充分条件与必要条件",
            "简单的逻辑联结词",
            "全称量词与存在量词",
            "函数与导数",
            "函数",
            "函数及其表示",
            "函数的定义域与值域",
            "函数的单调性与最值",
            "函数的奇偶性",
            "函数与方程",
            "反函数",
            "函数图象"};

    /**
     * 显示首页
     *
     * @param activity
     */
    public static void showHome(Activity activity) {
        Intent intent = new Intent(activity, StartActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    public static <T> void jump2Activity(Activity activity, Class<T> cls) {
        Intent intent = new Intent(activity, cls);
        activity.startActivity(intent);
    }

    public static <T> void jump2Activity(Activity activity, Class<T> cls, Object obj) {
        Intent intent = new Intent(activity, cls);
        Bundle bundle = new Bundle();
        bundle.putSerializable("value", (java.io.Serializable) obj);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    /**
     * dip转换为px
     * */
    public static int dipToPxInt(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * dip转换为px
     * */
    public static float dipToPxFloat(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dipValue * scale + 0.5f;
    }
    /**
     * 打开浏览器
     *
     * @param context
     * @param url
     */
    public static void openBrowser(Context context, String url) {
        try {
            Uri uri = Uri.parse(url);
            Intent it = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(it);
        } catch (Exception e) {
            e.printStackTrace();
            ToastMessage(context, "无法浏览此网页", 500);
        }
    }

    /**
     * 获取TextWatcher对象
     *
     * @param context
     * @return
     */
    public static TextWatcher getTextWatcher(final Activity context,
                                             final String temlKey) {
        return new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // 保存当前EditText正在编辑的内容
                ((AppContext) context.getApplication()).setProperty(temlKey,
                        s.toString());
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        };
    }

    /**
     * 清除文字
     *
     * @param cont
     * @param editer
     */
    public static void showClearWordsDialog(final Context cont,
                                            final EditText editer, final TextView numwords) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setTitle(R.string.clearwords);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 清除文字
                        editer.setText("");
                        numwords.setText("160");
                    }
                });
        builder.setNegativeButton(R.string.cancle,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    /**
     * 弹出Toast消息
     *
     * @param msg
     */
    public static void ToastMessage(Context cont, String msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, int msg) {
        Toast.makeText(cont, msg, Toast.LENGTH_SHORT).show();
    }

    public static void ToastMessage(Context cont, String msg, int time) {
        Toast.makeText(cont, msg, time).show();
    }

    /**
     * 点击返回监听事件
     *
     * @param activity
     * @return
     */
    public static View.OnClickListener finish(final Activity activity) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                activity.finish();
            }
        };
    }

    /**
     * 文章是否加载图片显示
     *
     * @param activity
     */
    public static void changeSettingIsLoadImage(Activity activity) {
        AppContext ac = (AppContext) activity.getApplication();
        if (ac.isLoadImage()) {
            ac.setConfigLoadimage(false);
            ToastMessage(activity, "已设置文章不加载图片");
        } else {
            ac.setConfigLoadimage(true);
            ToastMessage(activity, "已设置文章加载图片");
        }
    }

    public static void changeSettingIsLoadImage(Activity activity, boolean b) {
        AppContext ac = (AppContext) activity.getApplication();
        ac.setConfigLoadimage(b);
    }

    /**
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(Activity activity) {
        final AppContext ac = (AppContext) activity.getApplication();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    ToastMessage(ac, "缓存清除成功");
                } else {
                    ToastMessage(ac, "缓存清除失败");
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    ac.clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    /**
     * 发送App异常崩溃报告
     *
     * @param cont
     * @param crashReport
     */
    public static void sendAppCrashReport(final Context cont,
                                          final String crashReport) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_error);
        builder.setMessage(R.string.app_error_message);
        builder.setPositiveButton(R.string.submit_report,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 发送异常报告
                        Intent i = new Intent(Intent.ACTION_SEND);
                        // i.setType("text/plain"); //模拟器
                        i.setType("message/rfc822"); // 真机
                        i.putExtra(Intent.EXTRA_EMAIL,
                                new String[]{"zhouzhengfei@fclassroom.com"});
                        i.putExtra(Intent.EXTRA_SUBJECT,
                                "Android客户端 - 错误报告");
                        i.putExtra(Intent.EXTRA_TEXT, crashReport);
                        cont.startActivity(Intent.createChooser(i, "发送错误报告"));
                        // 退出
                        AppManager.getAppManager().AppExit(cont);
                    }
                });
        builder.setNegativeButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 退出
                        AppManager.getAppManager().AppExit(cont);
                    }
                });
        builder.show();
    }

    /**
     * 退出程序
     *
     * @param cont
     */
    public static void Exit(final Context cont) {
        AlertDialog.Builder builder = new AlertDialog.Builder(cont);
        builder.setIcon(android.R.drawable.ic_dialog_info);
        builder.setTitle(R.string.app_menu_surelogout);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // 退出
                        AppManager.getAppManager().AppExit(cont);
                    }
                });
        builder.setNegativeButton(R.string.cancle,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

}
