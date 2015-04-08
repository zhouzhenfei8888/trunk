package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/4/3.
 */
public class PrintNumBean implements Serializable {
    int printCartQuestionCount;
    int downloadCount;

    public int getPrintCartQuestionCount() {
        return printCartQuestionCount;
    }

    public void setPrintCartQuestionCount(int printCartQuestionCount) {
        this.printCartQuestionCount = printCartQuestionCount;
    }

    public int getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(int downloadCount) {
        this.downloadCount = downloadCount;
    }
}
