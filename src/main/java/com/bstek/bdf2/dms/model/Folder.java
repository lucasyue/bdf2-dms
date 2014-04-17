package com.bstek.bdf2.dms.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrType = Folder.TYPE_, discriminator = false)
public class Folder implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public final static String TYPE_ = "dms:folder";
	public final static String AUTHOR_ = "dms:author";
	public final static String NAME_ = "dms:name";
	public final static String ICON_ = "dms:icon";
	public final static String CREATE_DATE_ = "dms:createDate";
	public final static String LAST_MODIFIED_ = "jcr:lastModified";
	public final static String ORIGINAL_PATH_ = "dms:originalPath";
	public final static String TAGS_ = "dms:tags";
	public final static String FOLDER_BASE_ROOT_ = "dms:root";
	public final static String FOLDER_BASE_ROOT_NAME_ = "根目录";
	public final static String FOLDER_BASE_PERSONAL_ = "dms:personal";
	public final static String FOLDER_BASE_PERSONAL_NAME_ = "私有文件夹";
	public final static String FOLDER_BASE_SHARE_ = "dms:share";
	public final static String FOLDER_BASE_SHARE_PATH_ = "/dms:root/dms:share";
	public final static String FOLDER_BASE_SHARE_NAME_ = "默认文件夹";
	public final static String FOLDER_BASE_TRASH_ = "dms:trash";
	public final static String FOLDER_BASE_TRASH_PATH_ = "/dms:root/dms:trash";
	public final static String FOLDER_BASE_TRASH_NAME_ = "回收站";
	@Field(uuid = true)
	private String uuid;
	@Field(path = true)
	private String storePath;
	@Field(jcrName = "dms:path2")
	private String path;// storePath=originalPath+path
	@Field(jcrName = Folder.AUTHOR_)
	private String author;
	@Field(jcrName = Folder.NAME_)
	private String name;
	@Field(jcrName = Folder.ICON_)
	private String icon;
	@Field(jcrName = Folder.ORIGINAL_PATH_)
	private String originalPath;
	@Field(jcrName = Folder.CREATE_DATE_, jcrMandatory = true)
	private Date createDate;
	@Field(jcrName = Folder.LAST_MODIFIED_, jcrMandatory = true)
	private Date lastModified;

	@Collection(jcrName = Folder.TYPE_, jcrElementName = "child")
	private List<Folder> children;

	public Folder() {
	}

	public Folder(String UUID) {
		this.uuid = UUID;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return this.name + "|" + this.storePath + "|" + this.uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOriginalPath() {
		return originalPath;
	}

	public void setOriginalPath(String originalPath) {
		this.originalPath = originalPath;
	}

	public String getStorePath() {
		return storePath;
	}

	public void setStorePath(String storePath) {
		this.storePath = storePath;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public List<Folder> getChildren() {
		return children;
	}

	public void setChildren(List<Folder> children) {
		this.children = children;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

}
