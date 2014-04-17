package com.bstek.bdf2.dms.security.auth;

import java.security.Principal;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.security.auth.Subject;

import org.apache.jackrabbit.core.id.ItemId;
import org.apache.jackrabbit.core.security.AMContext;
import org.apache.jackrabbit.core.security.AccessManager;
import org.apache.jackrabbit.core.security.authorization.AccessControlProvider;
import org.apache.jackrabbit.core.security.authorization.WorkspaceAccessManager;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.Path;

public class DefaultAccessManager implements AccessManager {

	public void init(AMContext context) throws AccessDeniedException, Exception {
		// TODO Auto-generated method stub

	}

	public void init(AMContext context, AccessControlProvider acProvider,
			WorkspaceAccessManager wspAccessMgr) throws AccessDeniedException,
			Exception {
		// TODO Auto-generated method stub
		Subject subject = context.getSubject();
		Set<Principal> principals = subject.getPrincipals();
		for (Principal p : principals) {
			System.out.println("loginPrincipal:" + p.getName());
		}
		Session session = context.getSession();

		System.out.println("loginUser:" + session.getUserID());
	}

	public void close() throws Exception {
		// TODO Auto-generated method stub

	}

	public void checkPermission(ItemId id, int permissions)
			throws AccessDeniedException, ItemNotFoundException,
			RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println(id + "------" + permissions);
	}

	public void checkPermission(Path absPath, int permissions)
			throws AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println(absPath + "------" + permissions);

	}

	public void checkRepositoryPermission(int permissions)
			throws AccessDeniedException, RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println("------" + permissions);

	}

	public boolean isGranted(ItemId id, int permissions)
			throws ItemNotFoundException, RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println(id + "------" + permissions);

		return true;
	}

	public boolean isGranted(Path absPath, int permissions)
			throws RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println(absPath + "------" + permissions);

		return true;
	}

	public boolean isGranted(Path parentPath, Name childName, int permissions)
			throws RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println(parentPath + "------" + childName + "------"
		// + permissions);

		return true;
	}

	public boolean canRead(Path itemPath, ItemId itemId)
			throws RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println(itemPath + "------" + itemId + "------");
		return true;
	}

	public boolean canAccess(String workspaceName) throws RepositoryException {
		// TODO Auto-generated method stub
		// System.out.println(workspaceName + "------------");
		return true;
	}
}
