package com.fclassroom.app.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/4/1 0001.
 * 从服务器拉一页的数据
 */
public class PageBean {
    int pageNum;
    int pageSize;
    int size;
    int startRow;
    int endRow;
    int total;
    int pages;
    int firstPage;
    int prePage;
    int nextPage;
    int lastPage;
    boolean isFirstPage;
    boolean isLastPage;
    boolean hasPreviousPage;
    boolean hasNextPage;
    int navigatePages;
    List<Integer> navigatepageNums;
    List<SubjectItemBean> list;

    public static class SubjectItemBean implements Serializable{
        int id;
        String contentImage;
        String notebookIds;
        String tagNames;
        int signLevel;
        String remark;
        String tagIds;
        String scoreRate;
        String answer;
        int notebookCount;
        String examName;
        String examTime;
        String answerImg;
        String notebookNames;
        String knoNames;
        int examQuestionId;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContentImage() {
            return contentImage;
        }

        public void setContentImage(String contentImage) {
            this.contentImage = contentImage;
        }

        public String getNotebookIds() {
            return notebookIds;
        }

        public void setNotebookIds(String notebookIds) {
            this.notebookIds = notebookIds;
        }

        public String getTagNames() {
            return tagNames;
        }

        public void setTagNames(String tagNames) {
            this.tagNames = tagNames;
        }

        public int getSignLevel() {
            return signLevel;
        }

        public void setSignLevel(int signLevel) {
            this.signLevel = signLevel;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getTagIds() {
            return tagIds;
        }

        public void setTagIds(String tagIds) {
            this.tagIds = tagIds;
        }


        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }

        public int getNotebookCount() {
            return notebookCount;
        }

        public void setNotebookCount(int notebookCount) {
            this.notebookCount = notebookCount;
        }

        public String getAnswerImg() {
            return answerImg;
        }

        public void setAnswerImg(String answerImg) {
            this.answerImg = answerImg;
        }

        public String getNotebookNames() {
            return notebookNames;
        }

        public void setNotebookNames(String notebookNames) {
            this.notebookNames = notebookNames;
        }

        public String getKnoNames() {
            return knoNames;
        }

        public void setKnoNames(String knoNames) {
            this.knoNames = knoNames;
        }

        public int getExamQuestionId() {
            return examQuestionId;
        }

        public void setExamQuestionId(int examQuestionId) {
            this.examQuestionId = examQuestionId;
        }

        public String getExamName() {
            return examName;
        }

        public void setExamName(String examName) {
            this.examName = examName;
        }

        public String getScoreRate() {
            return scoreRate;
        }

        public void setScoreRate(String scoreRate) {
            this.scoreRate = scoreRate;
        }

        public String getExamTime() {
            return examTime;
        }

        public void setExamTime(String examTime) {
            this.examTime = examTime;
        }
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public int getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    public int getLastPage() {
        return lastPage;
    }

    public void setLastPage(int lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isFirstPage() {
        return isFirstPage;
    }

    public void setisFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setisLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public List<Integer> getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(List<Integer> navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public List<SubjectItemBean> getList() {
        return list;
    }

    public void setList(List<SubjectItemBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageBean{" +
                "pageNum=" + pageNum +
                ", pageSize=" + pageSize +
                ", size=" + size +
                ", startRow=" + startRow +
                ", endRow=" + endRow +
                ", total=" + total +
                ", pages=" + pages +
                ", firstPage=" + firstPage +
                ", prePage=" + prePage +
                ", nextPage=" + nextPage +
                ", lastPage=" + lastPage +
                ", isFirstPage=" + isFirstPage +
                ", isLastPage=" + isLastPage +
                ", hasPreviousPage=" + hasPreviousPage +
                ", hasNextPage=" + hasNextPage +
                ", navigatePages=" + navigatePages +
                ", navigatepageNums=" + navigatepageNums +
                ", list=" + list +
                '}';
    }
}
