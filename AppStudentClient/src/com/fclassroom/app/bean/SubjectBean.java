package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/3/31.
 */
public class SubjectBean implements Serializable {
    private int id;
    private String subjectName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
