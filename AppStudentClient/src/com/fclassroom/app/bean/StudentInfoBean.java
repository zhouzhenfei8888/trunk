package com.fclassroom.app.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2015/3/31.
 */
public class StudentInfoBean implements Serializable {
    private int studentId;
    private String studentName;
    private int schoolId;
    private String schoolName;
    private int gradeId;
    private String gradeName;
    private int classId;
    private String className;
    private int subjectId;
    private String subjectName;
    private String jikeNum;
    private String studentNo;
    private String sex;

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
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

    public int getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(int schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public int getGradeId() {
        return gradeId;
    }

    public void setGradeId(int gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getJikeNum() {
        return jikeNum;
    }

    public void setJikeNum(String jikeNum) {
        this.jikeNum = jikeNum;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    @Override
    public String toString() {
        return "StudentInfoBean{" +
                "studentId=" + studentId +
                ", studentName='" + studentName + '\'' +
                ", schoolId=" + schoolId +
                ", schoolName='" + schoolName + '\'' +
                ", gradeId=" + gradeId +
                ", gradeName='" + gradeName + '\'' +
                ", classId=" + classId +
                ", className='" + className + '\'' +
                ", subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", jikeNum='" + jikeNum + '\'' +
                ", studentNo='" + studentNo + '\'' +
                '}';
    }
}
