package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/22.
 */
public class Archivement implements Serializable {
    float defeatRate;
    float orgRate;
    int saveTime;

    public float getDefeatRate() {
        return defeatRate;
    }

    public void setDefeatRate(float defeatRate) {
        this.defeatRate = defeatRate;
    }

    public float getOrgRate() {
        return orgRate;
    }

    public void setOrgRate(float orgRate) {
        this.orgRate = orgRate;
    }

    public int getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(int saveTime) {
        this.saveTime = saveTime;
    }
}
