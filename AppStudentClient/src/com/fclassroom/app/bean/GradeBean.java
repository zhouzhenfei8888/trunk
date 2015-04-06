package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/3/30.
 */
public class GradeBean implements Serializable {
   private int id;
   private String gradeName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }
}
