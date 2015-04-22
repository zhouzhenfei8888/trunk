package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/22.
 */
public class TopBind implements Serializable {
    float orgRate;
    int studentId;
    String studentName;
    float avgTime;

    public float getOrgRate() {
        return orgRate;
    }

    public void setOrgRate(float orgRate) {
        this.orgRate = orgRate;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public float getAvgTime() {
        return avgTime;
    }

    public void setAvgTime(float avgTime) {
        this.avgTime = avgTime;
    }
}
