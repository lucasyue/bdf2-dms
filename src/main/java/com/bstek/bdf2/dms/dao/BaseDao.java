package com.bstek.bdf2.dms.dao;

import org.apache.jackrabbit.ocm.manager.ObjectContentManager;

public abstract class BaseDao {
	@SuppressWarnings("unchecked")
	public <T> T findObjectByUUID(String uuid) throws Exception {
		try {
			ObjectContentManager ocm = getObjectContentManager();
			return (T) ocm.getObjectByUuid(uuid);
		} catch (Exception e) {
			if (e.getMessage().contains("Impossible to get the object")) {
				throw new ObjectNotExsitException("文件[" + uuid + "]不存在："
						+ e.getMessage());
			} else {
				throw e;
			}
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T findObjectByPath(String path) throws Exception {
		try {
			ObjectContentManager ocm = getObjectContentManager();
			return (T) ocm.getObject(path);
		} catch (Exception e) {
			if (e.getMessage().contains("Impossible to get the object")) {
				throw new ObjectNotExsitException("文件[" + path + "]不存在："
						+ e.getMessage());
			} else {
				throw e;
			}
		}
	}

	public abstract ObjectContentManager getObjectContentManager();
}
