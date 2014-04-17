package com.bstek.bdf2.dms.dao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.springframework.util.StringUtils;

import com.bstek.bdf2.dms.core.JcrSessionManager;
import com.bstek.bdf2.dms.model.Document;
import com.bstek.dorado.data.provider.Page;

public class DocumentDao extends BaseDao {
	@SuppressWarnings("unchecked")
	public List<Document> findDocumentsByFolder(String folderUuid, Map<String, String> params) {
		QueryManager qm = this.getObjectContentManager().getQueryManager();
		Filter filter = qm.createFilter(Document.class);
		if (StringUtils.hasText(folderUuid)) {
			filter.addEqualTo("folderUUID", folderUuid);
		}
		if (params != null) {
			String uuid = params.get("uuid");
			String name = params.get("name");
			String path = params.get("path");
			if (StringUtils.hasText(uuid)) {
				List<Document> list = new ArrayList<Document>();
				Object doc;
				try {
					doc = this.findObjectByUUID(uuid);
					list.add((Document) doc);
				} catch (Exception e) {
				}
				return list;
			}
			if (StringUtils.hasText(name)) {
				filter.addLike("name", "%" + name + "%");
			}
			if (StringUtils.hasText(path)) {
				filter.addLike("path", "%" + path + "%");
			}
		}
		Query query = qm.createQuery(filter);
		query.addOrderByDescending("lastModifiedDate");
		return (List<Document>) objectContentManager.getObjects(query);
	}

	public void pagingDocumentsByFolder(Page<Document> page, String folderUuid, Map<String, String> params) {
		QueryManager qm = this.getObjectContentManager().getQueryManager();
		Filter filter = qm.createFilter(Document.class);
		if (StringUtils.hasText(folderUuid)) {
			filter.addEqualTo("folderUUID", folderUuid);
		}
		if (params != null) {
			String uuid = params.get("uuid");
			String name = params.get("name");
			String path = params.get("path");
			if (StringUtils.hasText(uuid)) {
				List<Document> list = new ArrayList<Document>();
				Object doc;
				try {
					doc = this.findObjectByUUID(uuid);
					list.add((Document) doc);
				} catch (Exception e) {
				}
				page.setEntities(list);
				page.setEntityCount(1);
				return;
			}
			if (StringUtils.hasText(name)) {
				filter.addLike("name", "%" + name + "%");
			}
			if (StringUtils.hasText(path)) {
				filter.addLike("path", "%" + path + "%");
			}
		}
		Query query = qm.createQuery(filter);
		query.addOrderByDescending("lastModifiedDate");
		@SuppressWarnings("unchecked")
		Iterator<Document> iterator = objectContentManager.getObjectIterator(query);
		List<Document> docPageList = new ArrayList<Document>();
		int count = 0;
		int pageNo = 1;
		if (params != null) {
			Object pageNoObj = params.get("pageNo");
			if (pageNoObj != null) {
				pageNo = (Integer) pageNoObj;
			}
		}
		int begin = (pageNo - 1) * page.getPageSize();// 10 0
		int end = begin + page.getPageSize() - 1;// 9
		while (iterator.hasNext()) {
			if (count < begin) {
				count++;
				iterator.next();
				continue;
			} else if (count >= begin && count <= end) {
				Document doc = iterator.next();
				docPageList.add(doc);
				count++;
			} else {
				if (page.getPageNo() > 1) {
					break;
				} else {
					iterator.next();
					count++;
				}
			}
		}
		if (page.getPageNo() <= 1) {
			page.setEntityCount(count);
		}
		page.setEntities(docPageList);
	}

	private ObjectContentManager objectContentManager = null;

	public DocumentDao(String userName, String password) {
		objectContentManager = JcrSessionManager.getObjectContentManager(JcrSessionManager.getSession(userName,
				password));
	}

	public ObjectContentManager getObjectContentManager() {
		return objectContentManager;
	}
}
