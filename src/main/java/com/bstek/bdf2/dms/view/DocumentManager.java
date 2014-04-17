package com.bstek.bdf2.dms.view;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.ocm.exception.ObjectContentManagerException;
import org.apache.jackrabbit.ocm.manager.ObjectContentManager;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import com.bstek.bdf2.dms.core.ContextHolder;
import com.bstek.bdf2.dms.dao.DocumentDao;
import com.bstek.bdf2.dms.dao.FolderDao;
import com.bstek.bdf2.dms.dao.ObjectNotExsitException;
import com.bstek.bdf2.dms.model.Document;
import com.bstek.bdf2.dms.model.Folder;
import com.bstek.bdf2.dms.model.Resource;
import com.bstek.dorado.annotation.DataProvider;
import com.bstek.dorado.annotation.DataResolver;
import com.bstek.dorado.annotation.Expose;
import com.bstek.dorado.core.Context;
import com.bstek.dorado.data.entity.EntityState;
import com.bstek.dorado.data.entity.EntityUtils;
import com.bstek.dorado.data.entity.FilterType;
import com.bstek.dorado.data.provider.Page;
import com.bstek.dorado.web.DoradoContext;

@Component("dms.documentManager")
public class DocumentManager {
	@DataProvider
	public Collection<Folder> findFolders() {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		FolderDao folderDao = new FolderDao(loginUser, password);
		return folderDao.query();
	}

	@DataProvider
	public Collection<Folder> findChildren(String originalPath) {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		FolderDao folderDao = new FolderDao(loginUser, password);
		return folderDao.queryChildren(originalPath);
	}

	@DataResolver
	public void saveFolders(Collection<Folder> folders) {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		FolderDao folderDao = new FolderDao(loginUser, password);
		ObjectContentManager ocm = folderDao.getObjectContentManager();
		for (Iterator<Folder> fI = EntityUtils.getIterator(folders, FilterType.ALL, Folder.class); fI.hasNext();) {
			Folder folder = fI.next();
			EntityState folderState = EntityUtils.getState(folder);
			if (EntityState.NEW.equals(folderState)) {
				Folder target = new Folder();
				folder.setAuthor("dmsAdmin");
				folder.setUuid(UUID.randomUUID().toString());
				folder.setStorePath(folder.getOriginalPath() + folder.getPath());
				folder.setCreateDate(new Date());
				folder.setLastModified(new Date());
				BeanUtils.copyProperties(folder, target);
				try {
					ocm.insert(target);
				} catch (ObjectContentManagerException e) {
					throw new RuntimeException("存储路径[" + target.getPath() + "]已经被占用，请输入新的存储路径！");
				}
			} else if (EntityState.MODIFIED.equals(folderState)) {
				// TODO checkin checkout
				Folder target;
				try {
					folder.setStorePath(folder.getOriginalPath() + folder.getPath());
					folder.setLastModified(new Date());
					target = folderDao.findObjectByUUID(folder.getUuid());
					BeanUtils.copyProperties(folder, target);
					ocm.update(target);
				} catch (Exception e) {
					e.printStackTrace();
				}
				// ocm.checkout(target.getPath());

				// ocm.checkin(target.getPath());
			} else if (EntityState.DELETED.equals(folderState)) {
				// TODO checkin checkout
				ocm.remove(folder.getStorePath());
			} else {
				Collection<Folder> children = folder.getChildren();
				if (children != null) {
					this.saveFolders(children);
				}
			}
		}
		ocm.save();
	}

	@Expose
	public String checkFolderPathUnique(String path) {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		FolderDao folderDao = new FolderDao(loginUser, password);
		try {
			Folder f = folderDao.findObjectByPath(path);
			if (f != null) {
				return "[" + path + "]路径名称已被占用，请输入新的路径。";
			}
		} catch (Exception e) {
			if (e instanceof ObjectNotExsitException) {
				return null;
			}
		}
		return null;
	}

	@Expose
	public String checkDocumentUnique(String folderPath, String docName) {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		DocumentDao docDao = new DocumentDao(loginUser, password);
		Document d;
		try {
			d = docDao.findObjectByPath(folderPath + "/" + docName);
			if (d != null) {
				return "[" + d.getName() + "]文件已存在。";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@DataProvider
	public void findDocumentsByFolder(Page<Document> page, String folderUUID, Map<String, String> params) {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		DocumentDao docDao = new DocumentDao(loginUser, password);
		// List<Document> docList =
		// docDao.findDocumentsByFolder(folderUUID,params);
		// page.setEntities(docList);
		// page.setEntityCount(docList.size());
		// TODO page
		docDao.pagingDocumentsByFolder(page, folderUUID, params);
		DoradoContext dct = (DoradoContext) Context.getCurrent();
		int pageNo = 1;
		if (params != null) {
			Object pageNoObj = params.get("pageNo");
			if (pageNoObj != null) {
				pageNo = (Integer) pageNoObj;
			}
		}
		dct.setAttribute(DoradoContext.VIEW, "pageInfo",
				"第" + pageNo + "页,共" + page.getPageCount() + "页(共" + page.getEntityCount() + "条记录)");
	}

	@Expose
	public void deleteDocuments(Collection<String> docPaths) {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		DocumentDao docDao = new DocumentDao(loginUser, password);
		ObjectContentManager ocm = docDao.getObjectContentManager();
		for (String docPath : docPaths) {
			try {
				// ocm.checkout(doc);
				Document docObj = docDao.findObjectByPath(docPath);
				Document newDoc = new Document();
				BeanUtils.copyProperties(docObj, newDoc);
				Folder trashFolder = docDao.findObjectByPath(Folder.FOLDER_BASE_TRASH_PATH_);
				newDoc.setPath(trashFolder.getStorePath() + "/" + docObj.getCreateDate().getTime() + "-"
						+ docObj.getName());
				newDoc.setFolderUUID(trashFolder.getUuid());
				newDoc.setKeywords(docObj.getKeywords());
				newDoc.setUuid(docObj.getUuid());
				Resource resource = docObj.getResource();
				Resource newResource = new Resource();
				BeanUtils.copyProperties(resource, newResource);
				if (resource != null) {
					newResource.setUuid(UUID.randomUUID().toString());
					newResource.setPath(newDoc.getPath() + "/" + newResource.getFileName());
				}
				newDoc.setResource(newResource);
				newDoc.setCreateDate(new Date());
				newDoc.setLastModifiedDate(new Date());
				ocm.insert(newDoc);
				ocm.remove(docPath);
				// ocm.checkin(doc);
			} catch (Exception e) {
				e.printStackTrace();
				// TODO
			}
		}
		ocm.save();
	}

	@Expose
	public void deleteTrashDocument() {
		String loginUser = ContextHolder.getLoginUser().getUsername();
		String password = ContextHolder.getLoginUser().getPassword();
		FolderDao folderDao = new FolderDao(loginUser, password);
		try {
			Node trashNode = folderDao.getObjectContentManager().getSession()
					.getNode("/" + Folder.FOLDER_BASE_ROOT_ + "/" + Folder.FOLDER_BASE_TRASH_);
			cascadeDeleteNode(trashNode);
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
	}

	private void cascadeDeleteNode(Node node) {
		try {
			for (NodeIterator nIt = node.getNodes(); nIt.hasNext();) {
				Node n = nIt.nextNode();
				this.cascadeDeleteNode(n);
				n.remove();
			}
		} catch (AccessDeniedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (VersionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LockException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ConstraintViolationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
