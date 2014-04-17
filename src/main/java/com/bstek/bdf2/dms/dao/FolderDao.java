package com.bstek.bdf2.dms.dao;

import java.util.Collection;

import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;

import com.bstek.bdf2.dms.core.JcrSessionManager;
import com.bstek.bdf2.dms.model.Folder;

public class FolderDao extends BaseDao {

	@SuppressWarnings("unchecked")
	public Collection<Folder> query() {
		QueryManager qm = objectContentManager.getQueryManager();
		Filter filter = qm.createFilter(Folder.class);
		filter.addEqualTo("originalPath", "/" + Folder.FOLDER_BASE_ROOT_);
		Query q = qm.createQuery(filter);
		q.addOrderByAscending("createDate");
		return objectContentManager.getObjects(q);
	}

	@SuppressWarnings("unchecked")
	public Collection<Folder> queryChildren(String originalPath) {
		QueryManager qm = objectContentManager.getQueryManager();
		Filter filter = qm.createFilter(Folder.class);
		filter.addEqualTo("originalPath", originalPath);
		Query q = qm.createQuery(filter);
		q.addOrderByAscending("createDate");
		return objectContentManager.getObjects(q);
	}

	private ObjectContentManager objectContentManager = null;

	public FolderDao(String userName, String password) {
		objectContentManager = JcrSessionManager
				.getObjectContentManager(JcrSessionManager.getSession(userName,
						password));
	}

	public ObjectContentManager getObjectContentManager() {
		return objectContentManager;
	}
}
