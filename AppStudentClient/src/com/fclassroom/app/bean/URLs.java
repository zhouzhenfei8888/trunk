package com.fclassroom.app.bean;

import java.io.Serializable;

public class URLs implements Serializable {
    public final static String HOST = "http://192.168.0.130/student/";
    public final static String HOST_YH = "http://120.131.64.134:9080/";
    public final static String HOST_NOTE = "http://192.168.0.130/notebook/student/";
    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";

    public final static String URL_SPLITTER = "/";
    public final static String URL_UNDERLINE = "_";
    public final static String CheckAppVision = HOST_YH + "version/ckeckAppVersionState.json";
    public final static String SendFeedBack = HOST_YH+"message/feedback.json";
    public final static String Login = HOST_YH + "login.json";
    public final static String LoginByToken = HOST_YH + "loginByToken.json";
    public final static String LoginByqq = HOST_YH + "checkQQ.json";
    public final static String Bindqq = HOST_YH + "bindQQ.json";
    public final static String GetGradeList = HOST + "getGradeList.json";
    public final static String GetSubjectList = HOST + "getSubjectList.json";
    public final static String GetStudentInfo = HOST + "getStudentInfo.json";
    public final static String AddNoteBook = HOST_NOTE + "addNoteBook.json";
    public final static String GetNoteBook = HOST_NOTE + "getNoteBookList.json";
    public final static String ChangeNoteBook = HOST_NOTE + "editNoteBook.json";
    public final static String DeleteNoteBook = HOST_NOTE + "delNoteBook.json";
    public final static String GetSubjectDetail = HOST_NOTE + "getErrorQuestions.json";
    public final static String GetExamQuestionList = HOST_NOTE + "getExamQuestionList.json";
    public final static String GetErrorTagList = HOST_NOTE + "getTags.json";
    public final static String GetRubbishSubjectList = HOST_NOTE + "getRecycleBinErrorQuestions.json";
    public final static String GetRubbishSubjectNum = HOST_NOTE + "getQuestionCatelog.json";
    public final static String GetPrintplanList = HOST_NOTE + "getPrintCartErrorQuestions.json";
    public final static String GetPrintRecoderList = HOST_NOTE + "getPrintHistoryList.json";
    public final static String GetPrintNum = HOST_NOTE + "getPrintCatelog.json";
}
