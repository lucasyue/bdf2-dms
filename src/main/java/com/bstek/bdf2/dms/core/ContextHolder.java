package com.bstek.bdf2.dms.core;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import com.bstek.bdf2.core.business.IUser;
import com.bstek.bdf2.core.exception.NoneLoginException;

public class ContextHolder extends com.bstek.bdf2.core.context.ContextHolder {
	private static ThreadLocal<Session> jcrSessionThreadLocal = new ThreadLocal<Session>();

	public static ThreadLocal<Session> getJcrSessionThreadLocal() {
		return jcrSessionThreadLocal;
	}
	
	public static IUser getLoginUser() {
		IUser user = com.bstek.bdf2.core.context.ContextHolder.getLoginUser();
		if (user == null) {
			throw new NoneLoginException("请先登录！");
		}
		return user;
	}

	public static void clean() {
		Session session = jcrSessionThreadLocal.get();
		if (session != null && session.isLive()) {
			try {
				session.save();
			} catch (AccessDeniedException e) {
				e.printStackTrace();
			} catch (ItemExistsException e) {
				e.printStackTrace();
			} catch (ReferentialIntegrityException e) {
				e.printStackTrace();
			} catch (ConstraintViolationException e) {
				e.printStackTrace();
			} catch (InvalidItemStateException e) {
				e.printStackTrace();
			} catch (VersionException e) {
				e.printStackTrace();
			} catch (LockException e) {
				e.printStackTrace();
			} catch (NoSuchNodeTypeException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
			session.logout();
		}
		jcrSessionThreadLocal.remove();
	}
}
