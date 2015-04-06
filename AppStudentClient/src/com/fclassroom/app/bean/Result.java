package com.fclassroom.app.bean;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;

/**
 * 数据操作结果实体类
 * @version 1.0
 * @created 2012-3-21
 */
public class Result extends Base {

	private int errorCode;
	private String errorMessage;
	
	private Comment comment;
	
	public boolean OK() {
		return errorCode == 1;
	}

	/**
	 * 解析调用结果
	 * 
	 * @param stream
	 * @return
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	public int getErrorCode() {
		return errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Comment getComment() {
		return comment;
	}
	public void setComment(Comment comment) {
		this.comment = comment;
	}

	@Override
	public String toString(){
		return String.format("RESULT: CODE:%d,MSG:%s", errorCode, errorMessage);
	}
}
