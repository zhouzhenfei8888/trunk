package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/3.
 * 查询错题数量，catelog	目录(1:全部 2:未整理 3:回收站)
 */
public class ErrorSubjectNumBean implements Serializable {
    int errorQuestionCount;
    int catelog;

    public int getErrorQuestionCount() {
        return errorQuestionCount;
    }

    public void setErrorQuestionCount(int errorQuestionCount) {
        this.errorQuestionCount = errorQuestionCount;
    }

    public int getCatelog() {
        return catelog;
    }

    public void setCatelog(int catelog) {
        this.catelog = catelog;
    }
}
