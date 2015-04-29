package com.fclassroom.app.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DecimalFormat;
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
import android.os.Environment;

import com.fclassroom.AppContext;
import com.fclassroom.AppException;
import com.fclassroom.app.bean.Archivement;
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
import com.fclassroom.app.bean.TopBind;
import com.fclassroom.app.bean.TreeBean;
import com.fclassroom.app.bean.URLs;
import com.fclassroom.app.bean.Update;
import com.fclassroom.app.common.FileUtils;
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
        httpGet.setRequestHeader("locale", "zh");
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
        httpPost.setRequestHeader("locale", "zh");
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

    /**
     * 获取网络文件
     *
     * @param url
     * @return
     */
    public static void getNetFile(String url) throws AppException {
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
                FileUtils.write2SDFromInput("JK", "a.doc", inStream);
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
    }

    /**
     * 从网络Url中下载文件
     *
     * @param urlStr
     * @param fileName
     * @param savePath
     * @throws IOException
     */
    public static void downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置超时间为3秒
        conn.setConnectTimeout(3 * 1000);
        //防止屏蔽程序抓取而返回403错误
//        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        //得到输入流
        InputStream inputStream = conn.getInputStream();
        //获取自己数组
        byte[] getData = readInputStream(inputStream);

        //文件保存位置
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            saveDir.mkdir();
        }
        File file = new File(saveDir + File.separator + fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(getData);
        if (fos != null) {
            fos.close();
        }
        if (inputStream != null) {
            inputStream.close();
        }


        System.out.println("info:" + url + " download success");

    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }


    public static BaseResponseBean<LoginResponseBean> login(AppContext appContext, String account, String pwd) throws AppException {
        // TODO Auto-generated method stub
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("loginName", account);
        params.put("password", pwd);
        String url = _MakeURL(URLs.Login, params);
        String response = http_get(appContext, url);
        System.out.println(response);
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
        System.out.println("44444"+response);
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
        params.put(orderBy, orderUpOrDown);
        System.out.println(orderBy + " " + orderUpOrDown);
        params.put("pageSize", pageSize);
        params.put("pageNo", pageNo);
        String url = _MakeURL(URLs.GetSubjectDetail, params);
        String response = http_get(appContext, url);
        System.out.println("kkkkkk"+response);
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
        System.out.println("aaa" + response);
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
        System.out.println("bbb" + response);
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
        BaseResponseBean<PrintNumBean> responseBean = JsonUtils.getPrintNum(response);
        return responseBean;
    }

    public static BaseResponseBean<String> sendFeedBack(AppContext appContext, String accessToken, String msg) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("message", msg);
        String url = _MakeURL(URLs.SendFeedBack, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> addNoteBookToPrintPlan(AppContext appContext, String accessToken, int gradeId, int subjectId, int id) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("noteBookId", id);
        String url = _MakeURL(URLs.AddNoteBookToPrintPlan, params);
        String response = http_get(appContext, url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<PageBean> getExamQuestionsByExam(AppContext appContext, String accessToken, int gradeId, int subjectId, int examId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("examId", examId);
        String url = _MakeURL(URLs.GetExamQuestionsByExam, params);
        String response = http_get(appContext, url);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }

    public static BaseResponseBean<String> setQuestionSignLevel(AppContext appContext, String accessToken, int examQuestionId, int signLevel) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionId", examQuestionId);
        params.put("signLevel", signLevel);
        String url = _MakeURL(URLs.AddQuestionSignLevel, params);
        String response = http_get(appContext, url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> deleteErrorQuestion(AppContext appContext, String accessToken, int examQuestionId, int delFlag) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionId", examQuestionId);
        params.put("delFlag", delFlag);
        String url = _MakeURL(URLs.DelErrorQuestion, params);
        String response = http_get(appContext, url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static String AddErrorQuestionToNoteBook(AppContext appContext, String accessToken, int examQuestionId, int id) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionId", examQuestionId);
        params.put("noteBookId", id);
        String url = _MakeURL(URLs.AddErrorQuestionToNoteBook, params);
        String response = http_get(appContext, url);
        return response;
    }

    public static BaseResponseBean<PageBean> getPrintHistoryErrorQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, int printHistoryId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("printHistoryId", printHistoryId);
        String url = _MakeURL(URLs.GetPrintHistoryErrorQuestions, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }

    public static BaseResponseBean<Boolean> SendAuthCode(AppContext appContext, String accessToken, String telephoneNum, int flag) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("phone", telephoneNum);
        params.put("flag",flag);
        String url = _MakeURL(URLs.SendAuthCode, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<Boolean> responseBean = JsonUtils.praseBoolean(response);
        return responseBean;
    }

    public static BaseResponseBean<Boolean> bindphone(AppContext appContext, String accessToken, String authCode, String telephone) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("phone", telephone);
        params.put("code", authCode);
        String url = _MakeURL(URLs.BindPhone, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<Boolean> responseBean = JsonUtils.praseBoolean(response);
        System.out.println(responseBean.getData());
        return responseBean;
    }

    public static BaseResponseBean<PageBean> getNoteBookQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, int noteBookId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("noteBookId", noteBookId);
        String url = _MakeURL(URLs.GetNoteBookQuestions, params);
        String response = http_get(appContext, url);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }

    public static BaseResponseBean<Integer> printSelected(AppContext appContext, String accessToken, int gradeId, int subjectId, String examQuestionIds) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("examQuestionIds", examQuestionIds);
        String url = _MakeURL(URLs.PrintSelected, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<Integer> responseBean = JsonUtils.praseInt(response);
        return responseBean;
    }

    public static BaseResponseBean<String> deleteSelected(AppContext appContext, String accessToken, String examQuestionIds, int delFlag) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionIds", examQuestionIds);
        params.put("delFlag", delFlag);
        String url = _MakeURL(URLs.DeleteSelected, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> recoverErrorQuestions(AppContext appContext, String accessToken, String examQuestionIds) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionIds", examQuestionIds);
        String url = _MakeURL(URLs.RecoverErrorQuestions, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<PageBean> getErrorTagQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, int tagId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("tagIds", tagId);
        String url = _MakeURL(URLs.GetSubjectDetail, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }

    public static BaseResponseBean<PageBean> searchQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, String keyword) throws AppException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("keyword", URLEncoder.encode(keyword, "utf-8"));
        String url = _MakeURL(URLs.GetSubjectDetail, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getPrintplanList(response);
        return responseBean;
    }

    public static BaseResponseBean<PageBean> getTimeSearchSubjectDetail(AppContext appContext, String accessToken, int gradeId, int subjectId, int noteBookId, int unOrganize, String orderBy, String orderUpOrDown, int pageNo, int pageSize, long startTime, long endTime) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("noteBookId", noteBookId);
        params.put("unOrganize", unOrganize);
        params.put(orderBy, orderUpOrDown);
        System.out.println(orderBy + " " + orderUpOrDown);
        params.put("pageSize", pageSize);
        params.put("pageNo", pageNo);
        params.put("startTime", startTime);
        params.put("endTime", endTime);
        System.out.println(startTime + "::" + endTime);
        String url = _MakeURL(URLs.GetSubjectDetail, params);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<PageBean> responseBean = JsonUtils.getSubjectDetail(response);
        return responseBean;
    }

    public static void getKnowledgePoint(AppContext appContext, String accessToken, int gradeId, int subjectId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        String url = _MakeURL(URLs.GetKnowledgePoint, params);
        System.out.println(url);
        String response = http_get(appContext, url);
        System.out.println(response);
    }

    public static void addErrorQuestionTag(AppContext appContext, String accessToken, int subjectId, int examQuestionId, String tagname) throws AppException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("subjectId", subjectId);
        params.put("examQuestionId", examQuestionId);
        params.put("tagName", URLEncoder.encode(tagname, "utf-8"));
        String url = _MakeURL(URLs.AddErrorQuestionTag, params);
        System.out.println(url);
        String response = http_get(appContext, url);
        System.out.println("response:" + response);
    }

    public static void delPrintCartErrorQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, String examQuestionIds) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("examQuestionIds", examQuestionIds);
        String url = _MakeURL(URLs.DelPrintCartErrorQuestions, params);
        System.out.println(url);
        String response = http_get(appContext, url);
        System.out.println(response);
    }

    public static BaseResponseBean<String> downloadErrorQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, int downloadType, int printHistoryId, String examQuestionIds) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("downloadType", downloadType);
        params.put("printHistoryId", printHistoryId);
        params.put("examQuestionIds", examQuestionIds);
        String url = _MakeURL(URLs.DownloadErrorQuestions, params);
        System.out.println(url);
        String response = http_get(appContext, url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static void downloadfile(AppContext appContext, String fileurl) throws AppException, IOException {
//        getNetFile(url);
//        downLoadFromUrl(url,"aa.doc", Environment.getExternalStorageDirectory().getAbsolutePath()+"/JK");
        String fileName = FileUtils.getName(fileurl);
        String encodeFileName = URLEncoder.encode(fileName,"utf-8");
        String enfileurl = fileurl.replace(fileName,encodeFileName);
        System.out.println(enfileurl);
        URL url = null;
        String savePath;
        String FilePath = null;
        InputStream inputStream = null;
        try {
            url = new URL(enfileurl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                FileUtils.write2SDFromInput("JIKE", fileName, inputStream);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BaseResponseBean<List<TreeBean>> getTopLevelKnos(AppContext appContext, String accessToken, int gradeId, int subjectId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        String url = _MakeURL(URLs.GetTopLevelKnos, params);
        System.out.println(url);
        String response = http_get(appContext, url);
        System.out.println(response);
        BaseResponseBean<List<TreeBean>> responseBean = JsonUtils.getTopLevelKnos(response);
        return responseBean;
    }

    public static BaseResponseBean<String> EditRemark(AppContext appContext, String accessToken, int examQuestionId, String editRemark) throws AppException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("examQuestionId",examQuestionId);
        params.put("remark",URLEncoder.encode(editRemark, "utf-8"));
        String url = _MakeURL(URLs.EditRemark,params);
        System.out.println(url);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> modifiednickname(AppContext appContext, String accessToken, String propName, String propValue) throws AppException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("propValue",URLEncoder.encode(propValue, "utf-8"));
        params.put("propName",propName);
        String url = _MakeURL(URLs.SaveSettings,params);
        System.out.println(url);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> changepassword(AppContext appContext, String accessToken, String oldpassword, String newpassword) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("oldPassword",oldpassword);
        params.put("newPassword",newpassword);
        String url = _MakeURL(URLs.SavePassword,params);
        System.out.println(url);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<Boolean> checkPhone(AppContext appContext, String telephoneNum) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("phone",telephoneNum);
        String url = _MakeURL(URLs.CheckPhone,params);
        System.out.println(url);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<Boolean> responseBean = JsonUtils.praseBoolean(response);
        return responseBean;
    }

    public static BaseResponseBean<Archivement> getArchivement(AppContext appContext, String accessToken, int gradeId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId",gradeId);
        String url = _MakeURL(URLs.GetArchivement,params);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<Archivement> responseBean = JsonUtils.getArchivement(response);
        return responseBean;
    }

    public static BaseResponseBean<List<TopBind>> getRank(AppContext appContext, String accessToken, int rankType) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("rankType",rankType);
        String url = _MakeURL(URLs.GetRank,params);
        String response = http_get(appContext,url);
        BaseResponseBean<List<TopBind>> responseBean = JsonUtils.getRank(response);
        System.out.println(response);
        return responseBean;
    }

    public static BaseResponseBean<String> updatepassword(AppContext appContext, String telephone, String authcode, String newpassword) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("phone",telephone);
        params.put("validcode",authcode);
        params.put("newpassword",newpassword);
        String url = _MakeURL(URLs.UpdatePassword,params);
        String response = http_get(appContext,url);
        System.out.println(telephone);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> downloadAllErrorQuestions(AppContext appContext, String accessToken, int gradeId, int subjectId, int downloadType) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("downloadType", downloadType);
        String url = _MakeURL(URLs.DownloadErrorQuestions, params);
        System.out.println(url);
        String response = http_get(appContext, url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> editTagNameDialog(AppContext appContext, String accessToken, int tagId, String name) throws AppException, UnsupportedEncodingException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("id",tagId);
        params.put("name",URLEncoder.encode(name, "utf-8"));
        String url = _MakeURL(URLs.EditTag,params);
        String response  = http_get(appContext,url);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> delTag(AppContext appContext, String accessToken, int id) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("tagId",id);
        String url = _MakeURL(URLs.DelTag,params);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<String> printTag(AppContext appContext,String accessToken, int gradeId, int subjectId, int id) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("gradeId", gradeId);
        params.put("subjectId", subjectId);
        params.put("tagId", id);
        String url = _MakeURL(URLs.PrintTag,params);
        String response = http_get(appContext,url);
        System.out.println("mmmmmm"+response);
        BaseResponseBean<String> responseBean = JsonUtils.praseString(response);
        return responseBean;
    }

    public static BaseResponseBean<List<TreeBean>> getKnosByParent(AppContext appContext, String accessToken, int parentId) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        params.put("parentId",parentId);
        String url = _MakeURL(URLs.GetKnosByParent,params);
        String response = http_get(appContext,url);
        System.out.println(response);
        BaseResponseBean<List<TreeBean>> responseBean = JsonUtils.getTopLevelKnos(response);
        return responseBean;
    }

    public static BaseResponseBean<String> updatePortrait(AppContext appContext, String accessToken,File protraitFile) throws AppException {
        Map<String, Object> params = new HashMap<>();
        params.put("accessToken", accessToken);
        Map<String, File> files = new HashMap<String, File>();
        files.put("dbFile", protraitFile);
        String response = http_post(appContext,URLs.Upload,params, files);
        System.out.println("vvvvvvvvvvvvvvvvvvvvvvvvvvv"+response);
        return JsonUtils.praseString(response);
    }
}
