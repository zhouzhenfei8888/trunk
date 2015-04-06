package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/2.
 * 主页上按考试试卷排名，返回的试卷实体类
 */
public class ExamBean implements Serializable {
    int examId;
    String examName;
    String examTime;
    String errorQuestionCount;
    String notebookCount;

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getErrorQuestionCount() {
        return errorQuestionCount;
    }

    public void setErrorQuestionCount(String errorQuestionCount) {
        this.errorQuestionCount = errorQuestionCount;
    }

    public String getNotebookCount() {
        return notebookCount;
    }

    public void setNotebookCount(String notebookCount) {
        this.notebookCount = notebookCount;
    }
}
