package com.bstek.bdf2.dms.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrType = "dms:resource", discriminator = false)
public class Resource implements Serializable {

	private static final long serialVersionUID = 1L;
	@Field(uuid = true)
	private String uuid;
	@Field(path = true)
	private String path;
	@Field(jcrName = "dms:size")
	private Long size;
	@Field(jcrName = "dms:author")
	private String author;
	@Field(jcrName = "dms:fileName")
	private String fileName;
	@Field(jcrName = "dms:versionComment")
	private String versionComment;
	@Field(jcrName = "jcr:data")
	private InputStream contents;
	@Field(jcrName = "dms:createDate")
	private Date createDate;
	@Field(jcrName = "dms:lastModifiedDate")
	private Date lastModifiedDate;

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getVersionComment() {
		return versionComment;
	}

	public void setVersionComment(String versionComment) {
		this.versionComment = versionComment;
	}

	public InputStream getContents() {
		return contents;
	}

	public void setContents(InputStream contents) {
		this.contents = contents;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(Date lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
