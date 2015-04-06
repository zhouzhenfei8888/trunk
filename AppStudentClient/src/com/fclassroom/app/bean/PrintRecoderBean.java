package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/3.
 */
public class PrintRecoderBean implements Serializable {
    int id;
    String createTime;
    int examQuestionCount;
    int downloadCount;
    String examQuestionIds;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getExamQuestionCount() {
        return examQuestionCount;
    }

    public void setExamQuestionCount(int examQuestionCount) {
        this.examQuestionCount = examQuestionCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }

    public String getExamQuestionIds() {
        return examQuestionIds;
    }

    public void setExamQuestionIds(String examQuestionIds) {
        this.examQuestionIds = examQuestionIds;
    }
}
