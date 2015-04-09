package com.fclassroom.app.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.app.bean.BaseResponseBean;
import com.fclassroom.app.bean.ErrorBookBean;
import com.fclassroom.app.bean.ErrorSubjectNumBean;
import com.fclassroom.app.bean.ErrorTagBean;
import com.fclassroom.app.bean.ExamBean;
import com.fclassroom.app.bean.GradeBean;
import com.fclassroom.app.bean.LoginResponseBean;
import com.fclassroom.app.bean.NoteBookBean;
import com.fclassroom.app.bean.PageBean;
import com.fclassroom.app.bean.PrintNumBean;
import com.fclassroom.app.bean.PrintRecoderBean;
import com.fclassroom.app.bean.StudentInfoBean;
import com.fclassroom.app.bean.SubjectBean;
import com.fclassroom.app.bean.URLs;
import com.fclassroom.app.bean.Update;
import com.fclassroom.app.common.JsonUtils;
import com.fclassroom.app.common.PreferenceUtils;

/**
 * API客户端接口：用于访问网络数据
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class ApiClient {
    public static final String UTF_8 = "UTF-8";
    public static final String DESC = "descend";
    public static final String ASC = "ascend";

    private final static int TIMEOUT_CONNECTION = 20000;
    private final static int TIMEOUT_SOCKET = 20000;
    private final static int RETRY_TIME = 3;

    private static String appCookie;
    private static String appUserAgent;

    public static void cleanCookie() {
        appCookie = "";
    }

    private static String getCookie(AppContext appContext) {
        if (appCookie == null || appCookie == "") {
            appCookie = appContext.getProperty("cookie");
        }
        return appCookie;
    }

    private static String getUserAgent(AppContext appContext) {
        if (appUserAgent == null || appUserAgent == "") {
            StringBuilder ua = new StringBuilder("OSChina.NET");
            ua.append('/' + appContext.getPackageInfo().versionName + '_' + appContext.getPackageInfo().versionCode);// App版本
            ua.append("/Android");// 手机系统平台
            ua.append("/" + android.os.Build.VERSION.RELEASE);// 手机系统版本
            ua.append("/" + android.os.Build.MODEL); // 手机型号
            ua.append("/" + appContext.getAppId());// 客户端唯一标识
            appUserAgent = ua.toString();
        }
        return appUserAgent;
    }

    private static HttpClient getHttpClient() {
        HttpClient httpClient = new HttpClient();
        // 设置 HttpClient 接收 Cookie,用与浏览器一样的策略
        httpClient.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // 设置 默认的超时重试处理策略
        httpClient.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler());
        // 设置 连接超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(TIMEOUT_CONNECTION);
        // 设置 读数据超时时间
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(TIMEOUT_SOCKET);
        // 设置 字符集
        httpClient.getParams().setContentCharset(UTF_8);
        return httpClient;
    }

    private static GetMethod getHttpGet(String url, String cookie, String userAgent) {
        GetMethod httpGet = new GetMethod(url);
        // 设置 请求超时时间
        httpGet.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpGet.setRequestHeader("Host", URLs.HOST);
        httpGet.setRequestHeader("Connection", "Keep-Alive");
        httpGet.setRequestHeader("Cookie", cookie);
        httpGet.setRequestHeader("User-Agent", userAgent);
        httpGet.setRequestHeader("jike-client-from", "APP");
        return httpGet;
    }

    private static PostMethod getHttpPost(String url, String cookie, String userAgent) {
        PostMethod httpPost = new PostMethod(url);
        // 设置 请求超时时间
        httpPost.getParams().setSoTimeout(TIMEOUT_SOCKET);
        httpPost.setRequestHeader("Host", URLs.HOST);
        httpPost.setRequestHeader("Connection", "Keep-Alive");
        httpPost.setRequestHeader("Cookie", cookie);
        httpPost.setRequestHeader("User-Agent", userAgent);
        httpPost.setRequestHeader("jike-client-from", "APP");
        return httpPost;
    }

    private static String _MakeURL(String p_url, Map<String, Object> params) {
        StringBuilder url = new StringBuilder(p_url);
        if (url.indexOf("?") < 0)
            url.append('?');

        for (String name : params.keySet()) {
            url.append('&');
            url.append(name);
            url.append('=');
            url.append(String.valueOf(params.get(name)));
            // 不做URLEncoder处理
            // url.append(URLEncoder.encode(String.valueOf(params.get(name)),
            // UTF_8));
        }

        return url.toString().replace("?&", "?");
    }

    /**
     * get请求URL
     *
     * @param url
     * @throws AppException
     */
    private static String http_get(AppContext appContext, String url) throws AppException {
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);

        HttpClient httpClient = null;
        GetMethod httpGet = null;

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, cookie, userAgent);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                responseBody = httpGet.getResponseBodyAsString();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);

        return responseBody;
    }

    /**
     * 公用post方法
     *
     * @param url
     * @param params
     * @param files
     * @throws AppException
     */
    private static String http_post(AppContext appContext, String url, Map<String, Object> params,
                                    Map<String, File> files) throws AppException {
        // System.out.println("post_url==> "+url);
        String cookie = getCookie(appContext);
        String userAgent = getUserAgent(appContext);

        HttpClient httpClient = null;
        PostMethod httpPost = null;

        // post表单参数处理
        int length = (params == null ? 0 : params.size()) + (files == null ? 0 : files.size());
        Part[] parts = new Part[length];
        int i = 0;
        if (params != null)
            for (String name : params.keySet()) {
                parts[i++] = new StringPart(name, String.valueOf(params.get(name)), UTF_8);
                // System.out.println("post_key==> "+name+"    value==>"+String.valueOf(params.get(name)));
            }
        if (files != null)
            for (String file : files.keySet()) {
                try {
                    parts[i++] = new FilePart(file, files.get(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                // System.out.println("post_key_file==> "+file);
            }

        String responseBody = "";
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpPost = getHttpPost(url, cookie, userAgent);
                httpPost.setRequestEntity(new MultipartRequestEntity(parts, httpPost.getParams()));
                int statusCode = httpClient.executeMethod(httpPost);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                } else if (statusCode == HttpStatus.SC_OK) {
                    Cookie[] cookies = httpClient.getState().getCookies();
                    String tmpcookies = "";
                    for (Cookie ck : cookies) {
                        tmpcookies += ck.toString() + ";";
                    }
                    // 保存cookie
                    if (appContext != null && tmpcookies != "") {
                        appContext.setProperty("cookie", tmpcookies);
                        appCookie = tmpcookies;
                    }
                }
                responseBody = httpPost.getResponseBodyAsString();
                // System.out.println("XMLDATA=====>"+responseBody);
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpPost.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return responseBody;
    }

    /**
     * 获取网络图片
     *
     * @param url
     * @return
     */
    public static Bitmap getNetBitmap(String url) throws AppException {
        HttpClient httpClient = null;
        GetMethod httpGet = null;
        Bitmap bitmap = null;
        int time = 0;
        do {
            try {
                httpClient = getHttpClient();
                httpGet = getHttpGet(url, null, null);
                int statusCode = httpClient.executeMethod(httpGet);
                if (statusCode != HttpStatus.SC_OK) {
                    throw AppException.http(statusCode);
                }
                InputStream inStream = httpGet.getResponseBodyAsStream();
                bitmap = BitmapFactory.decodeStream(inStream);
                inStream.close();
                break;
            } catch (HttpException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生致命的异常，可能是协议不对或者返回的内容有问题
                e.printStackTrace();
                throw AppException.http(e);
            } catch (IOException e) {
                time++;
                if (time < RETRY_TIME) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e1) {
                    }
                    continue;
                }
                // 发生网络异常
                e.printStackTrace();
                throw AppException.network(e);
            } finally {
                // 释放连接
                httpGet.releaseConnection();
                httpClient = null;
            }
        } while (time < RETRY_TIME);
        return bitmap;
    }

    public static BaseResponseBean<LoginResponseBean> login(AppContext appContext, String account, String pwd) throws AppException {
        // TODO Auto-generated method stub
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("loginName", account);
        params.put("password", pwd);
        String url = _MakeURL(URLs.Login, params);
        String response = http_get(appContext, url);
//        System.out.println(response);
        BaseResponseBean<LoginResponseBean> user = JsonUtils.getLoginBean(response);
        return user;
    }

    public static BaseResponseBean<Update> checkVersion(AppContext appContext) {
        // TODO Auto-generated method stub
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appType", 1);
        params.put("versionNo", appContext.getPackageInfo().versionCode);
        params.put("accessToken", PreferenceUtils.getString(appContext, PreferenceUtils.ACCESSTOKEN));
        String url = _MakeURL(URLs.CheckAppVision, params);
        BaseResponseBean<Update> responseBean = null;
        try {
            String response = http_get(appContext, url);
            responseBean = JsonUtils.getUpdateFromJson(response);
        } catch (AppException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return responseBean;
    }

    public static BaseResponseBean<LoginResponseBean> loginByqq(AppContext appContext, String openid) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("openId", openid);
        String url = _MakeURL(URLs.LoginByqq, params);
        String response = http_get(appContext, url);
        BaseResponseBean<LoginResponseBean> account = JsonUtils.getLoginBean(response);
        return account;
    }

    public static BaseResponseBean<LoginResponseBean> loginByToken(AppContext appContext, String accessToken) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        String url = _MakeURL(URLs.LoginByToken, params);
        String response = http_get(appContext, url);
        BaseResponseBean<LoginResponseBean> account = JsonUtils.getLoginBean(response);
        return account;
    }

    //绑定qq信息
    public static BaseResponseBean<LoginResponseBean> bindQQ(AppContext appContext, String name, String pwd, String openId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("jikeNum", name);
        params.put("password", pwd);
        params.put("openId", openId);
        String url = _MakeURL(URLs.Bindqq, params);
        String response = http_get(appContext, url);
        BaseResponseBean<LoginResponseBean> account = JsonUtils.getLoginBean(response);
        return account;
    }

    public static BaseResponseBean<ArrayList<GradeBean>> getGradeList(AppContext appContext, String accesstoken) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accesstoken);
        String url = _MakeURL(URLs.GetGradeList, params);
        String response = http_get(appContext, url);
        BaseResponseBean<ArrayList<GradeBean>> responseBean = JsonUtils.getGradeList(response);
        return responseBean;
    }

    public static BaseResponseBean<StudentInfoBean> getStudentInfo(AppContext appContext, String accesstoken) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accesstoken);
        String url = _MakeURL(URLs.GetStudentInfo, params);
        String response = http_get(appContext, url);
        BaseResponseBean<StudentInfoBean> studentInfo = JsonUtils.getStudentInfo(response);
        return studentInfo;
    }

    public static BaseResponseBean<ArrayList<SubjectBean>> getSubjectList(AppContext appContext, String accessToken, int gradeId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        String url = _MakeURL(URLs.GetSubjectList, params);
        String response = http_get(appContext, url);
        BaseResponseBean<ArrayList<SubjectBean>> responseBean = JsonUtils.getSubjectList(response);
        return responseBean;
    }

    public static BaseResponseBean<NoteBookBean> addNoteBook(AppContext appContext, String accessToken, int gradeId, int subjectId, String bookname) throws AppException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("name", URLEncoder.encode(bookname, "utf-8"));
        String url = _MakeURL(URLs.AddNoteBook, params);
        String response = http_get(appContext, url);
        BaseResponseBean<NoteBookBean> responseBean = JsonUtils.getAddNoteBook(response);
        return responseBean;
    }

    public static BaseResponseBean<ArrayList<ErrorBookBean>> getNoteBookList(AppContext appContext, String accessToken, int gradeId, int subjectId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        String url = _MakeURL(URLs.GetNoteBook, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<ArrayList<ErrorBookBean>> responseBean = JsonUtils.getNoteBookList(response);
        return responseBean;
    }

    public static String changeNoteName(AppContext appContext, String accessToken, int id, String bookname) throws AppException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("id", id);
        params.put("name", URLEncoder.encode(bookname, "utf-8"));
        String url = _MakeURL(URLs.ChangeNoteBook, params);
        String response = http_get(appContext, url);
        return response;
    }

    public static String deleteNoteBook(AppContext appContext, String accessToken, int id) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("id", id);
        String url = _MakeURL(URLs.DeleteNoteBook, params);
        String response = http_get(appContext, url);
        return response;
    }

    public static BaseResponseBean<PageBean> getSubjectDetail(AppContext appContext, String accessToken, int gradeId, int subjectId, int noteBookId, int unOrganize, String orderBy, String orderUpOrDown, int pageNo, int pageSize) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("noteBookId", noteBookId);
        params.put("unOrganize", unOrganize);
        params.put("orderBy", orderUpOrDown);
        params.put("pageSize", pageSize);
        params.put("pageNo", pageNo);
        String url = _MakeURL(URLs.GetSubjectDetail, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getSubjectDetail(response);
        return responseBean;
    }

    public static BaseResponseBean<ArrayList<ExamBean>> getAllExamPager(AppContext appContext, String accessToken, int gradeId, int subjectId, int unOrganize, String orderUpOrDown) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("unOrganize", unOrganize);
        params.put("orderTime", orderUpOrDown);
        String url = _MakeURL(URLs.GetExamQuestionList, params);
        String response = http_get(appContext, url);
        System.out.println(orderUpOrDown);
        System.out.println(response);
        BaseResponseBean<ArrayList<ExamBean>> responseBean = JsonUtils.getExamList(response);
        return responseBean;
    }

    public static BaseResponseBean<List<ErrorTagBean>> getErrorTagList(AppContext appContext, String accessToken, int subjectId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("subjectId", subjectId);
        String url = _MakeURL(URLs.GetErrorTagList, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<List<ErrorTagBean>> responseBean = JsonUtils.getErrorTagList(response);
        return responseBean;
    }

    public static BaseResponseBean<PageBean> getRubbishSubjectList(AppContext appContext, String accessToken, int gradeId, int subjectId, int pageSize, int pageNo) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("pageSize", pageSize);
        params.put("pageNo", pageNo);
        String url = _MakeURL(URLs.GetRubbishSubjectList, params);
        String response = http_get(appContext, url);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getRubbishSubjectList(response);
        return responseBean;
    }

    public static BaseResponseBean<List<ErrorSubjectNumBean>> getRubbishSubjectNum(AppContext appContext, String accessToken, int gradeId, int subjectId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        String url = _MakeURL(URLs.GetRubbishSubjectNum, params);
        String response = http_get(appContext, url);
        BaseResponseBean<List<ErrorSubjectNumBean>> responseBean = JsonUtils.getRubbishSubjectNum(response);
        return responseBean;
    }

    public static BaseResponseBean<PageBean> getPrintplanList(AppContext appContext, String accessToken, int gradeId, int subjectId, int pageSize, int pageNo) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("pageNo", pageNo);
        params.put("pageSize", pageSize);
        String url = _MakeURL(URLs.GetPrintplanList, params);
        String response = http_get(appContext, url);
        System.out.println("aaa"+response);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }

    public static BaseResponseBean<List<PrintRecoderBean>> getPrintRecoderList(AppContext appContext, String accessToken, int gradeId, int subjectId, String orderTime) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("orderTime", orderTime);
        String url = _MakeURL(URLs.GetPrintRecoderList, params);
        String response = http_get(appContext, url);
        System.out.println("bbb"+response);
        BaseResponseBean<List<PrintRecoderBean>> responseBean = JsonUtils.getPrintRecoderList(response);
        return responseBean;
    }

    public static BaseResponseBean<PrintNumBean> getPrintNum(AppContext appContext, String accessToken, int gradeId, int subjectId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        String url = _MakeURL(URLs.GetPrintNum, params);
        String response = http_get(appContext, url);
        System.out.println("ccc"+response);
        BaseResponseBean<PrintNumBean> responseBean = JsonUtils.getPrintNum(response);
        return responseBean;
    }

    public static BaseResponseBean sendFeedBack(AppContext appContext, String accessToken, String msg) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("message", msg);
        String url = _MakeURL(URLs.SendFeedBack, params);
        String response = http_get(appContext, url);
        BaseResponseBean responseBean = JsonUtils.getFeedBack(response);
        return responseBean;
    }

    public static BaseResponseBean<String> addNoteBookToPrintPlan(AppContext appContext, String accessToken, int gradeId, int subjectId, int id) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId",gradeId);
        params.put("subjectId",subjectId);
        params.put("noteBookId",id);
        String url = _MakeURL(URLs.AddNoteBookToPrintPlan,params);
        String response = http_get(appContext,url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<PageBean> getExamQuestionsByExam(AppContext appContext, String accessToken, int gradeId, int subjectId, int examId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId",gradeId);
        params.put("subjectId",subjectId);
        params.put("examId",examId);
        String url = _MakeURL(URLs.GetExamQuestionsByExam,params);
        String response = http_get(appContext,url);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }

    public static BaseResponseBean<String> setQuestionSignLevel(AppContext appContext, String accessToken, int examQuestionId, int signLevel) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionId",examQuestionId);
        params.put("signLevel",signLevel);
        String url = _MakeURL(URLs.AddQuestionSignLevel,params);
        String response = http_get(appContext,url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> deleteErrorQuestion(AppContext appContext, String accessToken, int examQuestionId, int delFlag) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionId",examQuestionId);
        params.put("delFlag",delFlag);
        String url = _MakeURL(URLs.DelErrorQuestion,params);
        String response = http_get(appContext,url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static String AddErrorQuestionToNoteBook(AppContext appContext, String accessToken, int examQuestionId, int id) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionId",examQuestionId);
        params.put("noteBookId",id);
        String url = _MakeURL(URLs.AddErrorQuestionToNoteBook,params);
        String response = http_get(appContext,url);
        return response;
    }

    public static BaseResponseBean<PageBean> getPrintHistoryErrorQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, int printHistoryId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId",gradeId);
        params.put("subjectId",subjectId);
        params.put("printHistoryId",printHistoryId);
        String url = _MakeURL(URLs.GetPrintHistoryErrorQuestions,params);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }
}
