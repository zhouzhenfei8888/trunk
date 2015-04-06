package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/2 0002.
 */
public class ErrorTagBean implements Serializable {
    int id;
    String name;
    int errorQuestionCount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getErrorQuestionCount() {
        return errorQuestionCount;
    }

    public void setErrorQuestionCount(int errorQuestionCount) {
        this.errorQuestionCount = errorQuestionCount;
    }
}
