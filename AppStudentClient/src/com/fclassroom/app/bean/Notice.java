package com.fclassroom.app.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.fclassroom.AppException;
import com.fclassroom.app.common.StringUtils;

/**
 * 通知信息实体类
 * 
 * @version 1.0
 * @created 2012-3-21
 */
public class Notice implements Serializable {

	public final static String UTF8 = "UTF-8";
	public final static String NODE_ROOT = "oschina";

	public final static int TYPE_ATME = 1;
	public final static int TYPE_MESSAGE = 2;
	public final static int TYPE_COMMENT = 3;
	public final static int TYPE_NEWFAN = 4;

	private int atmeCount;
	private int msgCount;
	private int reviewCount;
	private int newFansCount;

	@Override
	public String toString() {
		return "Notice [atmeCount=" + atmeCount + ", msgCount=" + msgCount + ", reviewCount=" + reviewCount
				+ ", newFansCount=" + newFansCount + "]";
	}

	public int getAtmeCount() {
		return atmeCount;
	}

	public void setAtmeCount(int atmeCount) {
		this.atmeCount = atmeCount;
	}

	public int getMsgCount() {
		return msgCount;
	}

	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}

	public int getReviewCount() {
		return reviewCount;
	}

	public void setReviewCount(int reviewCount) {
		this.reviewCount = reviewCount;
	}

	public int getNewFansCount() {
		return newFansCount;
	}

	public void setNewFansCount(int newFansCount) {
		this.newFansCount = newFansCount;
	}
}
