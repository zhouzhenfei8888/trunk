package com.fclassroom.app.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.JavaType;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.fclassroom.app.bean.Update;
import com.fclassroom.app.bean.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JsonUtils {

    private static ObjectMapper mapper = new ObjectMapper();
    private static SimpleDateFormat sdfDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.CHINA);

    static {
        mapper.setDateFormat(sdfDateFormat);
    }

    /**
     * 获取JSON中Data的数据
     *
     * @param jsonStr
     * @param
     * @return
     * @throws JSONException
     */
    public static JSONObject fetchJSONData(String jsonStr) throws JSONException {
        if (jsonStr == null || jsonStr.trim().length() == 0)
            return new JSONObject();
        JSONObject jsonObj = new JSONObject(jsonStr);
        return jsonObj;
    }

    public static User getUserFromJson(String jsonStr) {
        User user = null;
        if (jsonStr != null) {
            try {
                user = mapper.readValue(jsonStr, User.class);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                user = new User();
            }
        }
        return user;
    }

    public static BaseResponseBean<Update> getUpdateFromJson(String jsonString) {
        BaseResponseBean<Update> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, Update.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<Update>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<LoginResponseBean> getLoginBean(String jsonString) {
        BaseResponseBean<LoginResponseBean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, LoginResponseBean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<LoginResponseBean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    //qq第三方登入，返回数据
    public static BaseResponseBean<Boolean> getBaseResponseBean(String jsonString) {
        BaseResponseBean<Boolean> baseResponseBean = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, Boolean.class);
            baseResponseBean = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            baseResponseBean = new BaseResponseBean<Boolean>();
            baseResponseBean.setError_code(-1);
            e.printStackTrace();
        }
        return baseResponseBean;
    }

    public static BaseResponseBean<ArrayList<GradeBean>> getGradeList(String jsonString) {
        BaseResponseBean<ArrayList<GradeBean>> pojo = null;
        try {
            JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, GradeBean.class);
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, listType);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<ArrayList<GradeBean>>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<StudentInfoBean> getStudentInfo(String jsonString) {
        BaseResponseBean<StudentInfoBean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, StudentInfoBean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<StudentInfoBean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<ArrayList<SubjectBean>> getSubjectList(String jsonString) {
        BaseResponseBean<ArrayList<SubjectBean>> pojo = null;
        try {
            JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, SubjectBean.class);
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, listType);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<ArrayList<SubjectBean>>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<NoteBookBean> getAddNoteBook(String jsonString) {
        BaseResponseBean<NoteBookBean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, NoteBookBean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<NoteBookBean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<ArrayList<ErrorBookBean>> getNoteBookList(String jsonString) {
        BaseResponseBean<ArrayList<ErrorBookBean>> pojo = null;
        try {
            JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ErrorBookBean.class);
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, listType);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<ArrayList<ErrorBookBean>>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<PageBean> getSubjectDetail(String jsonString) {
        BaseResponseBean<PageBean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, PageBean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<PageBean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<ArrayList<ExamBean>> getExamList(String jsonString) {
        BaseResponseBean<ArrayList<ExamBean>> pojo = null;
        try {
            JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ExamBean.class);
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, listType);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<ArrayList<ExamBean>>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<List<ErrorTagBean>> getErrorTagList(String jsonString) {
        BaseResponseBean<List<ErrorTagBean>> pojo = null;
        try {
            JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ErrorTagBean.class);
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, listType);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<List<ErrorTagBean>>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<PageBean> getRubbishSubjectList(String jsonString) {
        BaseResponseBean<PageBean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, PageBean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<PageBean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<List<ErrorSubjectNumBean>> getRubbishSubjectNum(String jsonString) {
        BaseResponseBean<List<ErrorSubjectNumBean>> pojo = null;
        try {
            JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, ErrorSubjectNumBean.class);
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, listType);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<List<ErrorSubjectNumBean>>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<PageBean> getPrintplanList(String jsonString) {
        BaseResponseBean<PageBean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, PageBean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<PageBean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<List<PrintRecoderBean>> getPrintRecoderList(String jsonString) {
        BaseResponseBean<List<PrintRecoderBean>> pojo = null;
        try {
            JavaType listType = mapper.getTypeFactory().constructParametricType(ArrayList.class, PrintRecoderBean.class);
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, listType);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<List<PrintRecoderBean>>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<PrintNumBean> getPrintNum(String jsonString) {
        BaseResponseBean<PrintNumBean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, PrintNumBean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<PrintNumBean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean getFeedBack(String jsonString) {
        BaseResponseBean baseResponseBean = null;
        if (jsonString != null) {
            try {
                baseResponseBean = mapper.readValue(jsonString, BaseResponseBean.class);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                baseResponseBean = new BaseResponseBean();
            }
        }
        return baseResponseBean;
    }

    public static BaseResponseBean<String> praseString(String jsonString) {
        BaseResponseBean<String> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, String.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<String>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<Boolean> praseBoolean(String jsonString) {
        BaseResponseBean<Boolean> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, Boolean.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<Boolean>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }

    public static BaseResponseBean<Integer> praseInt(String jsonString) {
        BaseResponseBean<Integer> pojo = null;
        try {
            JavaType type = mapper.getTypeFactory().constructParametricType(BaseResponseBean.class, Integer.class);
            pojo = mapper.readValue(jsonString, type);
        } catch (Exception e) {
            pojo = new BaseResponseBean<Integer>();
            pojo.setError_code(-1);
            e.printStackTrace();
        }
        return pojo;
    }
}
