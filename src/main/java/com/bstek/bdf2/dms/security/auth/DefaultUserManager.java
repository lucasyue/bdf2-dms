package com.bstek.bdf2.dms.security.auth;

import java.security.Principal;
import java.util.Iterator;

import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.Query;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;

public class DefaultUserManager implements UserManager {

	public Authorizable getAuthorizable(String id) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Authorizable getAuthorizable(Principal principal)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Authorizable getAuthorizableByPath(String path)
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Authorizable> findAuthorizables(String relPath, String value)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Authorizable> findAuthorizables(String relPath,
			String value, int searchType) throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator<Authorizable> findAuthorizables(Query query)
			throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public User createUser(String userID, String password)
			throws AuthorizableExistsException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public User createUser(String userID, String password, Principal principal,
			String intermediatePath) throws AuthorizableExistsException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Group createGroup(String groupID)
			throws AuthorizableExistsException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Group createGroup(Principal principal)
			throws AuthorizableExistsException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Group createGroup(Principal principal, String intermediatePath)
			throws AuthorizableExistsException, RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public Group createGroup(String groupID, Principal principal,
			String intermediatePath) throws AuthorizableExistsException,
			RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isAutoSave() {
		// TODO Auto-generated method stub
		return false;
	}

	public void autoSave(boolean enable)
			throws UnsupportedRepositoryOperationException, RepositoryException {
		// TODO Auto-generated method stub

	}

}
