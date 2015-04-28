package com.fclassroom.app.bean;

public class FileBean
{
	private int _id;
	private int parentId;
	private String name;
	private long length;
	private String desc;

	public FileBean(int _id, int parentId, String name)
	{
		super();
		this._id = _id;
		this.parentId = parentId;
		this.name = name;
	}

}
