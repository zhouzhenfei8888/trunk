package com.fclassroom.app.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 评论实体类
 * 
 * @version 1.0
 * @created 2012-3-21
 */
public class Comment extends Entity {

	public final static int CLIENT_MOBILE = 2;
	public final static int CLIENT_ANDROID = 3;
	public final static int CLIENT_IPHONE = 4;
	public final static int CLIENT_WINDOWS_PHONE = 5;

	private String face;
	private String content;
	private String author;
	private int authorId;
	private String pubDate;
	private int appClient;
	private List<Reply> replies = new ArrayList<Reply>();
	private List<Refer> refers = new ArrayList<Refer>();

	public static class Reply implements Serializable {
		public String rauthor;
		public String rpubDate;
		public String rcontent;
	}

	public static class Refer implements Serializable {
		public String refertitle;
		public String referbody;
	}

	public int getAppClient() {
		return appClient;
	}

	public void setAppClient(int appClient) {
		this.appClient = appClient;
	}

	public String getFace() {
		return face;
	}

	public void setFace(String face) {
		this.face = face;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public List<Reply> getReplies() {
		return replies;
	}

	public void setReplies(List<Reply> replies) {
		this.replies = replies;
	}

	public List<Refer> getRefers() {
		return refers;
	}

	public void setRefers(List<Refer> refers) {
		this.refers = refers;
	}
}
