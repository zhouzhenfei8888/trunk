package com.fclassroom;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fclassroom.app.api.ApiClient;
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
import com.fclassroom.app.bean.Result;
import com.fclassroom.app.bean.StudentInfoBean;
import com.fclassroom.app.bean.SubjectBean;
import com.fclassroom.app.bean.User;
import com.fclassroom.app.common.CyptoUtils;
import com.fclassroom.app.common.FileUtils;
import com.fclassroom.app.common.ImageUtils;
import com.fclassroom.app.common.MethodsCompat;
import com.fclassroom.app.common.PreferenceUtils;
import com.fclassroom.app.common.StringUtils;
import com.fclassroom.app.common.UIHelper;
import com.fclassroom.appstudentclient.R;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.util.Log;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 *
 * @version 1.0
 * @created 2012-3-21
 */
public class AppContext extends Application {

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static final int PAGE_SIZE = 20;// 默认分页大小
    private static final int CACHE_TIME = 60 * 60000;// 缓存失效时间

    private boolean login = false; // 登录状态
    private int loginUid = 0; // 登录用户的id
    private Hashtable<String, Object> memCacheRegion = new Hashtable<String, Object>();
    private String saveImagePath;// 保存图片路径

    private Handler unLoginHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                UIHelper.ToastMessage(AppContext.this, getString(R.string.msg_login_error));
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册App异常崩溃处理器
        Thread.setDefaultUncaughtExceptionHandler(AppException.getAppExceptionHandler());
        init();
    }


    /**
     * 初始化
     */
    private void init() {
        // 设置保存图片的路径
        saveImagePath = getProperty(AppConfig.SAVE_IMAGE_PATH);
        if (StringUtils.isEmpty(saveImagePath)) {
            setProperty(AppConfig.SAVE_IMAGE_PATH, AppConfig.DEFAULT_SAVE_IMAGE_PATH);
            saveImagePath = AppConfig.DEFAULT_SAVE_IMAGE_PATH;
        }
    }

    /**
     * 检测当前系统声音是否为正常模式
     *
     * @return
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 应用程序是否发出提示音
     *
     * @return
     */
    public boolean isAppSound() {
        return isAudioNormal() && isVoice();
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
     */
    public int getNetworkType() {
        int netType = 0;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            String extraInfo = networkInfo.getExtraInfo();
            if (!StringUtils.isEmpty(extraInfo)) {
                if (extraInfo.toLowerCase().equals("cmnet")) {
                    netType = NETTYPE_CMNET;
                } else {
                    netType = NETTYPE_CMWAP;
                }
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = NETTYPE_WIFI;
        }
        return netType;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

    /**
     * 获取App唯一标识
     *
     * @return
     */
    public String getAppId() {
        String uniqueID = getProperty(AppConfig.CONF_APP_UNIQUEID);
        if (StringUtils.isEmpty(uniqueID)) {
            uniqueID = UUID.randomUUID().toString();
            setProperty(AppConfig.CONF_APP_UNIQUEID, uniqueID);
        }
        return uniqueID;
    }

    /**
     * 用户是否登录
     *
     * @return
     */
    public boolean isLogin() {
        return login;
    }

    /**
     * 获取登录用户id
     *
     * @return
     */
    public int getLoginUid() {
        return this.loginUid;
    }

    /**
     * 用户注销
     */
    public void Logout() {
        ApiClient.cleanCookie();
        this.cleanCookie();
        this.login = false;
        this.loginUid = 0;
    }

    /**
     * 未登录或修改密码后的处理
     */
    public Handler getUnLoginHandler() {
        return this.unLoginHandler;
    }

    /**
     * 初始化用户登录信息
     */
    public void initLoginInfo() {
        User loginUser = getLoginInfo();
        if (loginUser != null && loginUser.getUid() > 0 && loginUser.isRememberMe()) {
            this.loginUid = loginUser.getUid();
            this.login = true;
        } else {
            this.Logout();
        }
    }

    /**
     * 用户登录验证
     *
     * @param account
     * @param pwd
     * @return
     * @throws AppException
     */
    public BaseResponseBean<LoginResponseBean> loginVerify(String account, String pwd) throws AppException {
        return ApiClient.login(this, account, pwd);
    }

    public BaseResponseBean<LoginResponseBean> loginByToken(String accessToken) throws AppException {
        return ApiClient.loginByToken(this, accessToken);
    }

    public BaseResponseBean<LoginResponseBean> loginByqq(String openid) throws AppException {
        return ApiClient.loginByqq(this, openid);
    }

    public BaseResponseBean<LoginResponseBean> bindQQ(String name, String pwd, String openId) throws AppException {
        return ApiClient.bindQQ(this, name, pwd, openId);
    }

    /**
     * 保存登录信息
     *
     * @param
     * @param
     */
    public void saveLoginInfo(final LoginResponseBean loginResponseBean) {
        String accesstoken = loginResponseBean.getAccessToken();
        PreferenceUtils.putString(this, PreferenceUtils.ACCESSTOKEN, accesstoken);
    }

    public String getToken() {
        return PreferenceUtils.getString(this, PreferenceUtils.ACCESSTOKEN);
    }

    /**
     * 清除登录信息
     */
    public void cleanLoginInfo() {
        this.loginUid = 0;
        this.login = false;
        removeProperty("user.uid", "user.name", "user.face", "user.account", "user.pwd", "user.location",
                "user.followers", "user.fans", "user.score", "user.isRememberMe");
    }

    /**
     * 获取登录信息
     *
     * @return
     */
    public User getLoginInfo() {
        User lu = new User();
        lu.setUid(StringUtils.toInt(getProperty("user.uid"), 0));
        lu.setName(getProperty("user.name"));
        lu.setFace(getProperty("user.face"));
        lu.setAccount(getProperty("user.account"));
        lu.setPwd(CyptoUtils.decode("App", getProperty("user.pwd")));
        lu.setLocation(getProperty("user.location"));
        lu.setFollowers(StringUtils.toInt(getProperty("user.followers"), 0));
        lu.setFans(StringUtils.toInt(getProperty("user.fans"), 0));
        lu.setScore(StringUtils.toInt(getProperty("user.score"), 0));
        lu.setRememberMe(StringUtils.toBool(getProperty("user.isRememberMe")));
        return lu;
    }

    /**
     * 保存用户头像
     *
     * @param fileName
     * @param bitmap
     */
    public void saveUserFace(String fileName, Bitmap bitmap) {
        try {
            ImageUtils.saveImage(this, fileName, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取用户头像
     *
     * @param key
     * @return
     * @throws AppException
     */
    public Bitmap getUserFace(String key) throws AppException {
        FileInputStream fis = null;
        try {
            fis = openFileInput(key);
            return BitmapFactory.decodeStream(fis);
        } catch (Exception e) {
            throw AppException.run(e);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 是否加载显示文章图片
     *
     * @return
     */
    public boolean isLoadImage() {
        String perf_loadimage = getProperty(AppConfig.CONF_LOAD_IMAGE);
        // 默认是加载的
        if (StringUtils.isEmpty(perf_loadimage))
            return true;
        else
            return StringUtils.toBool(perf_loadimage);
    }

    /**
     * 设置是否加载文章图片
     *
     * @param b
     */
    public void setConfigLoadimage(boolean b) {
        setProperty(AppConfig.CONF_LOAD_IMAGE, String.valueOf(b));
    }

    /**
     * 是否发出提示音
     *
     * @return
     */
    public boolean isVoice() {
        String perf_voice = getProperty(AppConfig.CONF_VOICE);
        // 默认是开启提示声音
        if (StringUtils.isEmpty(perf_voice))
            return true;
        else
            return StringUtils.toBool(perf_voice);
    }

    /**
     * 设置是否发出提示音
     *
     * @param b
     */
    public void setConfigVoice(boolean b) {
        setProperty(AppConfig.CONF_VOICE, String.valueOf(b));
    }

    /**
     * 是否启动检查更新
     *
     * @return
     */
    public boolean isCheckUp() {
        String perf_checkup = getProperty(AppConfig.CONF_CHECKUP);
        // 默认是开启
        if (StringUtils.isEmpty(perf_checkup))
            return true;
        else
            return StringUtils.toBool(perf_checkup);
    }

    /**
     * 设置启动检查更新
     *
     * @param b
     */
    public void setConfigCheckUp(boolean b) {
        setProperty(AppConfig.CONF_CHECKUP, String.valueOf(b));
    }

    /**
     * 是否左右滑动
     *
     * @return
     */
    public boolean isScroll() {
        String perf_scroll = getProperty(AppConfig.CONF_SCROLL);
        // 默认是关闭左右滑动
        if (StringUtils.isEmpty(perf_scroll))
            return false;
        else
            return StringUtils.toBool(perf_scroll);
    }

    /**
     * 设置是否左右滑动
     *
     * @param b
     */
    public void setConfigScroll(boolean b) {
        setProperty(AppConfig.CONF_SCROLL, String.valueOf(b));
    }

    /**
     * 是否Https登录
     *
     * @return
     */
    public boolean isHttpsLogin() {
        String perf_httpslogin = getProperty(AppConfig.CONF_HTTPS_LOGIN);
        // 默认是http
        if (StringUtils.isEmpty(perf_httpslogin))
            return false;
        else
            return StringUtils.toBool(perf_httpslogin);
    }

    /**
     * 设置是是否Https登录
     *
     * @param b
     */
    public void setConfigHttpsLogin(boolean b) {
        setProperty(AppConfig.CONF_HTTPS_LOGIN, String.valueOf(b));
    }

    /**
     * 清除保存的缓存
     */
    public void cleanCookie() {
        removeProperty(AppConfig.CONF_COOKIE);
    }

    /**
     * 判断缓存数据是否可读
     *
     * @param cachefile
     * @return
     */
    private boolean isReadDataCache(String cachefile) {
        return readObject(cachefile) != null;
    }

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    private boolean isExistDataCache(String cachefile) {
        boolean exist = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists())
            exist = true;
        return exist;
    }

    /**
     * 判断缓存是否失效
     *
     * @param cachefile
     * @return
     */
    public boolean isCacheDataFailure(String cachefile) {
        boolean failure = false;
        File data = getFileStreamPath(cachefile);
        if (data.exists() && (System.currentTimeMillis() - data.lastModified()) > CACHE_TIME)
            failure = true;
        else if (!data.exists())
            failure = true;
        return failure;
    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {

        // 清除webview缓存
        //File file = CacheManager.getCacheFileBaseDir();
        File file = getCacheDir();
        if (file != null && file.exists() && file.isDirectory()) {
            for (File item : file.listFiles()) {
                item.delete();
            }
            file.delete();
        }
        deleteDatabase("webview.db");
        deleteDatabase("webview.db-shm");
        deleteDatabase("webview.db-wal");
        deleteDatabase("webviewCache.db");
        deleteDatabase("webviewCache.db-shm");
        deleteDatabase("webviewCache.db-wal");
        // 清除数据缓存
        clearCacheFolder(getFilesDir(), System.currentTimeMillis());
        clearCacheFolder(getCacheDir(), System.currentTimeMillis());
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            clearCacheFolder(MethodsCompat.getExternalCacheDir(this),
                    System.currentTimeMillis());
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }

    /**
     * 清除缓存目录
     *
     * @param dir 目录
     * @param
     * @return
     */
    private int clearCacheFolder(File dir, long curTime) {
        int deletedFiles = 0;
        if (dir != null && dir.isDirectory()) {
            try {
                for (File child : dir.listFiles()) {
                    if (child.isDirectory()) {
                        deletedFiles += clearCacheFolder(child, curTime);
                    }
                    if (child.lastModified() < curTime) {
                        if (child.delete()) {
                            deletedFiles++;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return deletedFiles;
    }

    /**
     * 将对象保存到内存缓存中
     *
     * @param key
     * @param value
     */
    public void setMemCache(String key, Object value) {
        memCacheRegion.put(key, value);
    }

    /**
     * 从内存缓存中获取对象
     *
     * @param key
     * @return
     */
    public Object getMemCache(String key) {
        return memCacheRegion.get(key);
    }

    /**
     * 保存磁盘缓存
     *
     * @param key
     * @param value
     * @throws IOException
     */
    public void setDiskCache(String key, String value) throws IOException {
        FileOutputStream fos = null;
        try {
            fos = openFileOutput("cache_" + key + ".data", Context.MODE_PRIVATE);
            fos.write(value.getBytes());
            fos.flush();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取磁盘缓存数据
     *
     * @param key
     * @return
     * @throws IOException
     */
    public String getDiskCache(String key) throws IOException {
        FileInputStream fis = null;
        try {
            fis = openFileInput("cache_" + key + ".data");
            byte[] datas = new byte[fis.available()];
            fis.read(datas);
            return new String(datas);
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 保存对象
     *
     * @param ser
     * @param file
     * @throws IOException
     */
    public boolean saveObject(Serializable ser, String file) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = openFileOutput(file, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(ser);
            oos.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                oos.close();
            } catch (Exception e) {
            }
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    /**
     * 读取对象
     *
     * @param file
     * @return
     * @throws IOException
     */
    public Serializable readObject(String file) {
        if (!isExistDataCache(file))
            return null;
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = openFileInput(file);
            ois = new ObjectInputStream(fis);
            return (Serializable) ois.readObject();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
            // 反序列化失败 - 删除缓存文件
            if (e instanceof InvalidClassException) {
                File data = getFileStreamPath(file);
                data.delete();
            }
        } finally {
            try {
                ois.close();
            } catch (Exception e) {
            }
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public boolean containsProperty(String key) {
        Properties props = getProperties();
        return props.containsKey(key);
    }

    public void setProperties(Properties ps) {
        AppConfig.getAppConfig(this).set(ps);
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }

    /**
     * 获取内存中保存图片的路径
     *
     * @return
     */
    public String getSaveImagePath() {
        return saveImagePath;
    }

    /**
     * 设置内存中保存图片的路径
     *
     * @return
     */
    public void setSaveImagePath(String saveImagePath) {
        this.saveImagePath = saveImagePath;
    }

    /**
     * 账号登入时，返回的用户信息
     *
     * @param accountData
     */
    public void saveAccountInfo(LoginResponseBean accountData) {
        PreferenceUtils.putInt(this, PreferenceUtils.ACCOUNT_ID, accountData.getId());
        PreferenceUtils.putString(this, PreferenceUtils.ACCOUNT_JIKENUM, accountData.getJikeNum());
        PreferenceUtils.putString(this, PreferenceUtils.ACCOUNT_NAME, accountData.getName());
        PreferenceUtils.putString(this, PreferenceUtils.ACCOUNT_SEX, accountData.getSex());
        PreferenceUtils.putString(this, PreferenceUtils.ACCOUNT_LOGINPHONE, accountData.getLoginPhone());
        PreferenceUtils.putString(this, PreferenceUtils.ACCESSTOKEN, accountData.getAccessToken());
    }

    public BaseResponseBean<ArrayList<GradeBean>> getGradeList(String accesstoken) throws AppException {
        return ApiClient.getGradeList(this, accesstoken);
    }

    public BaseResponseBean<StudentInfoBean> getStudentInfo(String accesstoken) throws AppException {
        return ApiClient.getStudentInfo(this, accesstoken);
    }

    /**
     * 从接口拿到的学生信息
     */
    public void saveStudentInfo(StudentInfoBean studentInfo) {
        PreferenceUtils.putInt(this, PreferenceUtils.STUDENT_ID, studentInfo.getStudentId());
        PreferenceUtils.putString(this, PreferenceUtils.STUDENT_NAME, studentInfo.getStudentName());
        PreferenceUtils.putInt(this, PreferenceUtils.SCHOOL_ID, studentInfo.getSchoolId());
        PreferenceUtils.putString(this, PreferenceUtils.SCHOOL_NAME, studentInfo.getStudentName());
        PreferenceUtils.putInt(this, PreferenceUtils.GRADE_ID, studentInfo.getGradeId());
        PreferenceUtils.putString(this, PreferenceUtils.GRADE_NAME, studentInfo.getGradeName());
        PreferenceUtils.putInt(this, PreferenceUtils.CLASS_ID, studentInfo.getClassId());
        PreferenceUtils.putString(this, PreferenceUtils.CLASS_NAME, studentInfo.getClassName());
        PreferenceUtils.putInt(this, PreferenceUtils.SUBJECT_ID, studentInfo.getSubjectId());
        PreferenceUtils.putString(this, PreferenceUtils.SUBJECT_NAME, studentInfo.getSubjectName());
        PreferenceUtils.putString(this, PreferenceUtils.JIKE_NUM, studentInfo.getJikeNum());
        PreferenceUtils.putString(this, PreferenceUtils.STUDENT_NO, studentInfo.getStudentNo());
    }

    public BaseResponseBean<ArrayList<SubjectBean>> getSubjectList(String accessToken, int gradeId) throws AppException {
        return ApiClient.getSubjectList(this, accessToken, gradeId);
    }

    public BaseResponseBean<NoteBookBean> addNoteBook(String accessToken, int gradeId, int subjectId, String bookname) throws AppException, UnsupportedEncodingException {
        return ApiClient.addNoteBook(this, accessToken, gradeId, subjectId, bookname);
    }

    public BaseResponseBean<ArrayList<ErrorBookBean>> getNoteBookList(String accessToken, int gradeId, int subjectId) throws AppException {
        return ApiClient.getNoteBookList(this, accessToken, gradeId, subjectId);
    }

    public String changeNoteName(String accessToken, int id, String name) throws AppException, UnsupportedEncodingException {
        return ApiClient.changeNoteName(this, accessToken, id, name);
    }

    public String deleteNoteBook(String accessToken, int id) throws AppException {
        return ApiClient.deleteNoteBook(this, accessToken, id);
    }

    public BaseResponseBean<PageBean> getSubjectDetail(String accessToken, int gradeId, int subjectId, int noteBookId, int unOrganize, String orderBy, String orderUpOrDown, int pageNo, int pageSize) throws AppException {
        return ApiClient.getSubjectDetail(this, accessToken, gradeId, subjectId, noteBookId, unOrganize, orderBy, orderUpOrDown, pageNo, pageSize);
    }

    public BaseResponseBean<ArrayList<ExamBean>> getAllExamPager(String accessToken, int gradeId, int subjectId,int unOrganize,String orderUpOrDown) throws AppException {
        return ApiClient.getAllExamPager(this, accessToken, gradeId, subjectId, unOrganize, orderUpOrDown);
    }

    public BaseResponseBean<List<ErrorTagBean>> getErrorTagList(String accessToken, int subjectId) throws AppException {
        return ApiClient.getErrorTagList(this, accessToken, subjectId);
    }

    public BaseResponseBean<PageBean> getRubbishSubjectList(String accessToken, int gradeId, int subjectId, int pageSize, int pageNo) throws AppException {
        return ApiClient.getRubbishSubjectList(this, accessToken, gradeId, subjectId, pageSize, pageNo);
    }

    public BaseResponseBean<List<ErrorSubjectNumBean>> getRubbishSubjectNum(String accessToken, int gradeId, int subjectId) throws AppException {
        return ApiClient.getRubbishSubjectNum(this,accessToken,gradeId,subjectId);
    }

    public BaseResponseBean<PageBean> getPrintplanList(String accessToken, int gradeId, int subjectId, int pageSize, int pageNo) throws AppException {
        return ApiClient.getPrintplanList(this,accessToken,gradeId,subjectId,pageSize,pageNo);
    }

    public BaseResponseBean<List<PrintRecoderBean>> getPrintRecoderList(String accessToken, int gradeId, int subjectId, String orderTime) throws AppException {
        return ApiClient.getPrintRecoderList(this,accessToken,gradeId,subjectId,orderTime);
    }

    public BaseResponseBean<PrintNumBean> getPrintNum(String accessToken, int gradeId, int subjectId) throws AppException {
        return ApiClient.getPrintNum(this,accessToken,gradeId,subjectId);
    }

    public BaseResponseBean sendFeedBack(String accessToken, String msg) throws AppException {
        return ApiClient.sendFeedBack(this,accessToken,msg);
    }
}
