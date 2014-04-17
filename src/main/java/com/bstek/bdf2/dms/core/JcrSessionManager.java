package com.bstek.bdf2.dms.core;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.apache.jackrabbit.ocm.manager.impl.ObjectContentManagerImpl;
import org.apache.jackrabbit.ocm.mapper.Mapper;
import org.apache.jackrabbit.ocm.mapper.impl.annotation.AnnotationMapperImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bstek.bdf2.dms.model.Document;
import com.bstek.bdf2.dms.model.Folder;
import com.bstek.bdf2.dms.model.Resource;

public class JcrSessionManager {
	static Logger logger = LoggerFactory.getLogger(JcrSessionManager.class);
	private static Mapper mapper = null;

	public static Mapper getAnnotationMapper() {
		if (mapper == null) {
			initAnnotationMapper();
		}
		return mapper;
	}

	public static void initAnnotationMapper() {
		@SuppressWarnings("rawtypes")
		List<Class> classes = new ArrayList<Class>();
		classes.add(Resource.class);
		classes.add(Document.class);
		classes.add(Folder.class);
		mapper = new AnnotationMapperImpl(classes);
	}

	public static ObjectContentManager getObjectContentManager(Session session) {
		Mapper mapper = JcrSessionManager.getAnnotationMapper();
		ObjectContentManager objectContentManager = new ObjectContentManagerImpl(
				session, mapper);
		return objectContentManager;
	}

	public static Session getSession(String userName, String password) {
		Session session = ContextHolder.getJcrSessionThreadLocal().get();
		if (session == null || !session.isLive()) {
			try {
				session = RepositoryManager.getRepository()
						.login(new SimpleCredentials(userName, password
								.toCharArray()));
				ContextHolder.getJcrSessionThreadLocal().set(session);
			} catch (RepositoryException e) {
				logger.error("登录失败", e.getMessage());
				e.printStackTrace();
				throw new JcrSessionException("用户名[" + userName
						+ "]或密码不正确，登录获取jcrsession失败!");
			}
		}
		return session;
	}
}
