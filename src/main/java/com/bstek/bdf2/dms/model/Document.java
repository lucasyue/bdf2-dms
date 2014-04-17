package com.bstek.bdf2.dms.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.apache.jackrabbit.ocm.manager.collectionconverter.impl.MultiValueCollectionConverterImpl;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Bean;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Collection;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Field;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.Node;

@Node(jcrType = "dms:document", discriminator = false)
public class Document implements Serializable {
	/**
	 * 
	 */
	public final static String FOLDER_UUID_ = "dms:folderUUID";
	private static final long serialVersionUID = 1L;
	@Field(uuid = true)
	private String uuid;
	@Field(path = true)
	private String path;
	@Field(jcrName = "dms:author")
	private String author;
	@Field(jcrName = "dms:name")
	private String name;
	@Field(jcrName = "dms:folderUUID")
	private String folderUUID;
	@Collection(jcrName = "dms:keywords", collectionConverter = MultiValueCollectionConverterImpl.class)
	private List<String> keywords;
	@Collection(jcrName = "dms:tags", collectionConverter = MultiValueCollectionConverterImpl.class)
	private List<String> tags;
	@Bean(jcrName = "dms:content", proxy = true)
	private Resource resource;
	@Field(jcrName = "dms:mimeType")
	private String mimeType;
	@Field(jcrName = "dms:createDate")
	private Date createDate;
	@Field(jcrName = "dms:lastModifiedDate")
	private Date lastModifiedDate;

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFolderUUID() {
		return folderUUID;
	}

	public void setFolderUUID(String folderUUID) {
		this.folderUUID = folderUUID;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
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

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	@Override
	public String toString() {
		return this.name + "|" + this.path + "|" + this.folderUUID;
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
