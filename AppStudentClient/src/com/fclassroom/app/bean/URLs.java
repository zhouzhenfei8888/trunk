package com.fclassroom.app.bean;

import java.io.Serializable;

public class URLs implements Serializable {
//    public final static String HOST_YH = "http://120.131.64.134:20002/";
    //马丰的地址
    public final static String HOST_YH = "http://192.168.1.125:8090/";
//    public final static String HOST_YH = "http://192.168.1.102/";
    public final static String HOST = HOST_YH + "student/";
    public final static String HOST_NOTE = HOST_YH + "notebook/student/";
    //    public final static String HOST_IMG = "http://120.131.64.134:60002";
    public final static String HOST_IMG = "http://img.fclassroom.com";
    public final static String HTTP = "http://";
    public final static String HTTPS = "https://";

    public final static String URL_SPLITTER = "/";
    public final static String URL_UNDERLINE = "_";
    public final static String CheckAppVision = HOST_YH + "version/ckeckAppVersionState.json";
    public final static String SendFeedBack = HOST_YH + "message/feedback.json";
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
    public final static String AddNoteBookToPrintPlan = HOST_NOTE + "addNoteBookToPrintCart.json";
    public final static String GetExamQuestionsByExam = HOST_NOTE + "getExamQuestionsByExam.json";
    public final static String AddQuestionSignLevel = HOST_NOTE + "addQuestionSignLevel.json";
    public final static String DelErrorQuestion = HOST_NOTE + "delErrorQuestion.json";
    public final static String AddErrorQuestionToNoteBook = HOST_NOTE + "addErrorQuestionToNoteBook.json";
    public final static String GetPrintHistoryErrorQuestions = HOST_NOTE + "getPrintHistoryErrorQuestions.json";
    public final static String SendAuthCode = HOST_YH + "sendCode.json";
    //    public final static String SendAuthCode = "http://192.168.0.53:8080/Fclassroom-data-central/" + "sendCode.json";
    public final static String BindPhone = HOST_YH + "bindPhone.json";
    public final static String GetNoteBookQuestions = HOST_NOTE + "getErrorQuestionsByNoteBook.json";
    public final static String PrintSelected = HOST_NOTE + "addMultiErrorQuestionToPrintCart.json";
    public final static String DeleteSelected = HOST_NOTE + "delErrorQuestions.json";
    public final static String RecoverErrorQuestions = HOST_NOTE + "recoverErrorQuestions.json";
    public final static String GetKnowledgePoint = HOST + "getTopLevelKnos.json";
    public final static String AddErrorQuestionTag = HOST_NOTE + "addErrorQuestionTag.json";
    public final static String DelPrintCartErrorQuestions = HOST_NOTE + "delPrintCartErrorQuestions.json";
    public final static String DownloadErrorQuestions = HOST_NOTE + "downloadErrorQuestions.json";
    public final static String GetTopLevelKnos = HOST + "getTopLevelKnos.json";
    public final static String EditRemark = HOST_NOTE + "editRemark.json";
    public final static String SaveSettings = HOST + "saveSettings.json";
    public final static String SavePassword = HOST + "savePassword.json";
    public final static String CheckPhone = HOST_YH + "checkPhone.json";
    public final static String GetArchivement = HOST + "getArchivement.json";
    public final static String GetRank = HOST + "getRank.json";
    public final static String UpdatePassword = HOST_YH + "findPassword.json";
    public final static String EditTag = HOST_NOTE + "editTag.json";
    public final static String DelTag = HOST_NOTE + "delTag.json";
    public final static String PrintTag = HOST_NOTE + "addTagQuestionToPrintCar.json";
}
