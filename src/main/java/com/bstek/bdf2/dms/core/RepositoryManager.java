package com.bstek.bdf2.dms.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.core.RepositoryImpl;
import org.apache.jackrabbit.core.config.ConfigurationException;
import org.apache.jackrabbit.core.config.RepositoryConfig;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.value.DateValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.bstek.bdf2.dms.model.Folder;
import com.bstek.dorado.core.Configure;
import com.bstek.dorado.web.DoradoContext;

public class RepositoryManager {
	static Logger logger = LoggerFactory.getLogger(RepositoryManager.class);
	private static RepositoryImpl repository = null;

	public static RepositoryImpl getRepository() throws RepositoryException {
		if (repository == null) {
			initRepository();
		}
		return repository;
	}

	protected static void initRepository() throws RepositoryException {
		String repositoryXmlLocation = Configure
				.getString("dms.repositoryXmlLocation");
		if (!StringUtils.hasText(repositoryXmlLocation)) {
			repositoryXmlLocation = DoradoContext.getAttachedServletContext()
					.getRealPath("")
					+ java.io.File.separator
					+ "WEB-INF"
					+ java.io.File.separator
					+ "dorado-home"
					+ java.io.File.separator + "repository.xml";
		}
		String repositoryHomeLocation = Configure
				.getString("dms.repositoryHomeLocation");
		if (!StringUtils.hasText(repositoryHomeLocation)) {

			repositoryHomeLocation = System.getProperty("user.dir")
					+ java.io.File.separator + "dmsRespositoryHome";
		}

		logger.info(">>>>>>初始化资源库[" + repositoryHomeLocation + "]。。。。。。");
		RepositoryConfig config;
		try {
			config = RepositoryConfig.create(repositoryXmlLocation,
					repositoryHomeLocation);
			config.getWorkspaceConfig(config.getDefaultWorkspaceName());
			repository = RepositoryImpl.create(config);
			logger.info(">>>>>>初始化资源库成功！！！");
		} catch (ConfigurationException e) {
			logger.error("初始化资源库配置文件失败：[" + repositoryXmlLocation + "]\n"
					+ e.getMessage());
			throw new RuntimeException(e.getMessage());
		} catch (RepositoryException e) {
			logger.error("初始化资源库失败：" + e.getMessage());
			throw new RuntimeException(e.getMessage());
		}
		// TODO admin user and password confirgure.getString();
		String adminId = Configure.getString("dms.adminId");
		String pwd = Configure.getString("dms.adminIdPwd");
		// String adminId = "ubpAdmin";
		// String pwd = "1fa1930303437dc1da2c4f10a9ce0d20327a0b91";
		Session systemSession = JcrSessionManager.getSession(adminId, pwd);
		initDms(systemSession);
		systemSession.logout();
	}

	@SuppressWarnings("deprecation")
	private static String initDms(Session session) {
		String dmsRootNodePath = null;
		try {
			NamespaceRegistry nr = session.getWorkspace()
					.getNamespaceRegistry();
			String prefixes[] = nr.getPrefixes();
			boolean exist = false;
			for (String pre : prefixes) {
				if ("dms".equals(pre)) {
					exist = true;
					break;
				}
			}
			Node rootNode = session.getWorkspace().getSession().getRootNode();
			Node dmsRootNode = null;
			if (!exist) {
				logger.info(">>>>>>初始化DMS。。。");
				nr.registerNamespace("dms", "http://www.bsdn.org/2.0");
				NodeTypeManagerImpl ntm = (NodeTypeManagerImpl) session
						.getWorkspace().getNodeTypeManager();
				// InputStream cndIns = null;
				// cndIns = RepositoryManager.class
				// .getResourceAsStream("/customNodes.cnd");
				InputStream custNode = RepositoryManager.class
						.getResourceAsStream("/custom_nodetypes.xml");
				ntm.registerNodeTypes(custNode, NodeTypeManagerImpl.TEXT_XML);
				logger.info("初始化创建/dms:root节点");
				dmsRootNode = createDmsRootNode(session, rootNode);
				logger.info("初始化创建/dms:root/dms:share节点");
				createShareNode(session, dmsRootNode);
				logger.info("初始化创建/dms:root/dms:trash节点");
				createTrashNode(session, dmsRootNode);
				// logger.info("初始化创建/dms:root/dms:personal节点");
				// createPersonalNode(session, dmsRootNode);
				session.save();
				logger.info(">>>>>>初始化DMS配置成功。");
			} else {
				dmsRootNode = rootNode.getNode(Folder.FOLDER_BASE_ROOT_);
			}
			dmsRootNodePath = dmsRootNode.getPath();
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.info(">>>>>>DMS根路径:" + dmsRootNodePath);
		return dmsRootNodePath;
	}

	private static void createShareNode(Session session, Node rootNode)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		Node personalNode = rootNode.addNode(Folder.FOLDER_BASE_SHARE_,
				Folder.TYPE_);
		personalNode.setProperty(Folder.AUTHOR_, session.getUserID());
		personalNode.setProperty(Folder.NAME_, Folder.FOLDER_BASE_SHARE_NAME_);
		personalNode.setProperty(Folder.ORIGINAL_PATH_, "/"
				+ Folder.FOLDER_BASE_ROOT_);
		DateValue now = new DateValue(Calendar.getInstance());
		personalNode.setProperty(Folder.CREATE_DATE_, now);
		personalNode.setProperty(Folder.LAST_MODIFIED_, now);
		personalNode.setProperty(Folder.ICON_,
				"url(>skin>common/icons.gif) -200px -80px");

	}

	@SuppressWarnings("unused")
	private static void createPersonalNode(Session session, Node rootNode)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		Node personalNode = rootNode.addNode(Folder.FOLDER_BASE_PERSONAL_,
				Folder.TYPE_);
		personalNode.setProperty(Folder.AUTHOR_, session.getUserID());
		personalNode.setProperty(Folder.NAME_,
				Folder.FOLDER_BASE_PERSONAL_NAME_);
		personalNode.setProperty(Folder.ORIGINAL_PATH_, "/"
				+ Folder.FOLDER_BASE_ROOT_);
		DateValue now = new DateValue(Calendar.getInstance());
		personalNode.setProperty(Folder.CREATE_DATE_, now);
		personalNode.setProperty(Folder.LAST_MODIFIED_, now);
		personalNode.setProperty(Folder.ICON_,
				"url(>skin>common/icons.gif) -180px -40px");

	}

	private static void createTrashNode(Session session, Node rootNode)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException {
		Node trashNode = rootNode.addNode(Folder.FOLDER_BASE_TRASH_,
				Folder.TYPE_);
		trashNode.setProperty(Folder.AUTHOR_, session.getUserID());
		trashNode.setProperty(Folder.NAME_, Folder.FOLDER_BASE_TRASH_NAME_);
		trashNode.setProperty(Folder.ORIGINAL_PATH_, "/"
				+ Folder.FOLDER_BASE_ROOT_);
		DateValue now = new DateValue(Calendar.getInstance());
		trashNode.setProperty(Folder.CREATE_DATE_, now);
		trashNode.setProperty(Folder.LAST_MODIFIED_, now);
		trashNode.setProperty(Folder.ICON_,
				"url(>skin>common/icons.gif) -220px -40px");
	}

	private static Node createDmsRootNode(Session session, Node rootNode)
			throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException,
			ConstraintViolationException, RepositoryException,
			ValueFormatException {
		Node dmsRootNode = rootNode.addNode(Folder.FOLDER_BASE_ROOT_,
				Folder.TYPE_);
		// Add basic properties TODO check
		dmsRootNode.setProperty(Folder.AUTHOR_, session.getUserID());
		dmsRootNode.setProperty(Folder.NAME_, Folder.FOLDER_BASE_ROOT_NAME_);
		dmsRootNode.setProperty(Folder.ORIGINAL_PATH_, "0");
		DateValue now = new DateValue(Calendar.getInstance());
		dmsRootNode.setProperty(Folder.CREATE_DATE_, now);
		dmsRootNode.setProperty(Folder.LAST_MODIFIED_, now);
		return dmsRootNode;
	}

}
