import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.jcr.AccessDeniedException;
import javax.jcr.ImportUUIDBehavior;
import javax.jcr.InvalidItemStateException;
import javax.jcr.InvalidSerializedDataException;
import javax.jcr.ItemExistsException;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.ReferentialIntegrityException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.Workspace;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bstek.bdf2.dms.core.JcrSessionManager;
import com.bstek.bdf2.dms.dao.DocumentDao;
import com.bstek.bdf2.dms.dao.FolderDao;
import com.bstek.bdf2.dms.model.Document;
import com.bstek.bdf2.dms.model.Folder;
import com.bstek.bdf2.dms.model.Resource;

public class JcrStudy {
	static Logger logger = LoggerFactory.getLogger(JcrStudy.class);

	/**
	 * @param args
	 * @throws RepositoryException
	 * @throws LoginException
	 * @throws IOException
	 */
	public static void main(String[] args) throws LoginException, RepositoryException, IOException {
		try {
			String path = JcrStudy.class.getResource("/").getPath();
			System.out.println(path);
			JcrStudy js = new JcrStudy();
			JcrStudy.A a = js.new A();
			a.returnTest("ppp");
		} finally {
			System.out.println("end finally clean");
		}
		// addDocumentToFolder();
		// printDmsRoot();
		// printAllFolderDocuments();
		// String dmsAdmin = "dmsAdmin";
		// String pwd = "1fa1930303437dc1da2c4f10a9ce0d20327a0b91";
		// FolderDao fd = new FolderDao(dmsAdmin, pwd);
		// Node root = fd.getObjectContentManager().getSession().getRootNode();
		// printAllNode("", root.getNodes());
	}

	class A {
		public String returnTest(String param) {
			try {
				return "ABC>" + param;
			} finally {
				System.out.println("finally clean");
			}
		}
	}

	private static void printAllFolderDocuments() {
		String dmsAdmin = "dmsAdmin";
		String pwd = "1fa1930303437dc1da2c4f10a9ce0d20327a0b91";
		FolderDao fd = new FolderDao(dmsAdmin, pwd);
		Collection<Folder> folders = fd.queryChildren("/dms:root");
		DocumentDao dd = new DocumentDao("dmsAdmin", "123");
		for (Folder f : folders) {
			System.out.println(f.toString());
			List<Document> ds = dd.findDocumentsByFolder(f.getUuid(), null);
			if (ds.size() > 0) {
				System.out.println("文件夹" + f.getName() + "中文档：");
			}
			for (Document d : ds) {
				System.out.println("  " + d.toString());
			}
		}
	}

	private static void printDmsRoot() throws Exception {
		FolderDao fd = new FolderDao("dmsAdmin", "123");
		Folder d = fd.findObjectByPath("/dms:root/dms:personal");
		Node node = fd.getObjectContentManager().getSession().getNodeByUUID(d.getUuid());
		printAllNode("", node.getParent().getParent().getNodes());
	}

	private static void addDocumentToFolder() throws Exception {
		FolderDao fd = new FolderDao("dmsAdmin", "123");
		Folder personalFolder = fd.findObjectByPath("/dms:root/dms:personal");
		Document d = new Document();
		d.setUuid(UUID.randomUUID().toString());
		d.setName("测试文档");
		d.setMimeType("conf");
		d.setAuthor("dmsAdmin");
		d.setPath(personalFolder.getStorePath() + "/testDoc");
		d.setFolderUUID(personalFolder.getUuid());
		List<String> a = new ArrayList<String>();
		a.add("d");
		d.setKeywords(a);
		d.setTags(a);
		Resource res = new Resource();
		File f = new File(JcrStudy.class.getResource("/jaas.conf").getPath());
		InputStream fis = new FileInputStream(f);
		res.setUuid(UUID.randomUUID().toString());
		res.setPath("/testDoc/res");
		res.setAuthor("dmsAdmin");
		res.setContents(fis);
		res.setFileName(f.getName());
		res.setSize(Long.valueOf("" + fis.available()));
		d.setResource(res);
		fd.getObjectContentManager().insert(d);
		fd.getObjectContentManager().save();
	}

	private static void printNodeTree() throws RepositoryException, PathNotFoundException {
		Session ses = JcrSessionManager.getSession("dmsAdmin", "123");
		Node rootNode = ses.getRootNode();
		printAllNode("", rootNode.getNodes());
		ses.logout();
	}

	private static void saveFolder() {
		FolderDao fd = new FolderDao("dmsAdmin", "123");
		QueryManager qm = fd.getObjectContentManager().getQueryManager();
		// Query query = qm.createQuery(filter);
		Folder f = new Folder();
		f.setUuid(UUID.randomUUID().toString());
		f.setAuthor("lucas.yue");
		f.setIcon("url(>skin>common/icons.gif) -80px -0px");
		f.setName("测试名称");
		f.setOriginalPath("/dms:root");
		f.setPath("/test");
		fd.getObjectContentManager().insert(f);
		fd.getObjectContentManager().save();
	}

	private static void getFile() throws Exception {
		FolderDao fd = new FolderDao("dmsAdmin", "123");
		QueryManager qm = fd.getObjectContentManager().getQueryManager();
		Filter filter = qm.createFilter(Folder.class);
		Query query = qm.createQuery(filter);
		Document d = fd.findObjectByPath("/dms:personal/testDoc");
		Resource r = d.getResource();
		InputStream in = r.getContents();
		FileOutputStream fos = new FileOutputStream(System.getProperty("user.dir") + File.separator + "src"
				+ File.separator + r.getFileName());
		byte[] c = new byte[in.available()];
		in.read(c);
		fos.write(c);
		r.getContents();
		System.out.println(d.getName());
	}

	@SuppressWarnings({ "unused", "deprecation" })
	private static void registerNodeTypes(Workspace wk) throws RepositoryException {
		NodeTypeManagerImpl ntM = (NodeTypeManagerImpl) wk.getNodeTypeManager();
		InputStream cndInp = null;
		try {
			cndInp = JcrStudy.class.getResourceAsStream("/customNodes.cnd");
			ntM.registerNodeTypes(cndInp, NodeTypeManagerImpl.TEXT_X_JCR_CND, false);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (cndInp != null) {
				try {
					cndInp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private static void addNode(Session ses, Node rootNode) throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException,
			RepositoryException, ValueFormatException, AccessDeniedException, ReferentialIntegrityException,
			InvalidItemStateException {
		Node newr = rootNode.addNode("decmNoteTest", "decm:note");
		newr.setProperty("decm:user", "lucas.yue");
		newr.setProperty("decm:date", Calendar.getInstance());
		newr.setProperty("decm:text", "test......");
		ses.save();
		Node newr2 = rootNode.addNode("decmNoteTest2", "decm:note");
		newr2.setProperty("decm:user", "lucas.yue");
		newr2.setProperty("decm:date", Calendar.getInstance());
	}

	@SuppressWarnings("unused")
	private static void queryManagerTest(Workspace wk) throws RepositoryException, ItemExistsException,
			PathNotFoundException, VersionException, ConstraintViolationException, LockException {
		javax.jcr.query.QueryManager qm = wk.getQueryManager();
		@SuppressWarnings("deprecation")
		javax.jcr.query.Query q = qm.createQuery("//rep:security/rep:authorizables/rep:users",
				javax.jcr.query.Query.XPATH);
		javax.jcr.query.QueryResult qr = q.execute();
		if (qr.getNodes().hasNext()) {
			Node parent = qr.getNodes().nextNode();
			printAllNode("", parent.getNodes());
			parent.remove();
		}
		wk.getSession().save();
	}

	@SuppressWarnings("unused")
	private static void addXMLFile(Session ses, Node rootNode) throws ItemExistsException, PathNotFoundException,
			NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException,
			RepositoryException, InvalidSerializedDataException, AccessDeniedException, ReferentialIntegrityException,
			InvalidItemStateException {
		Node importXML = rootNode.addNode("importXML", "nt:unstructured");
		try {
			FileInputStream fis = new FileInputStream(JcrStudy.class.getResource("/xml.xml").getPath());
			ses.importXML(importXML.getPath(), fis, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
			fis.close();
			ses.save();
		} catch (FileNotFoundException e) { // TODO Auto-generated catch
			e.printStackTrace();
		} catch (IOException e) {
			// TODO
			e.printStackTrace();
		}
	}

	private static void printAllNode(String pre, NodeIterator nodeIterator) throws RepositoryException,
			PathNotFoundException {
		int level = 1;
		while (nodeIterator.hasNext()) {
			int levelTemp = level++;
			Node node = nodeIterator.nextNode();
			String t = "";
			if (pre.length() < 8) {
				for (int i = 0; i < 8 - pre.length(); i++) {
					t += " ";
				}
			}
			logger.info("{} {}......{}", new Object[] { pre + " " + levelTemp + t, node.getName(), node.getPath() });
			PropertyIterator pIterator = node.getProperties();
			int j = 0;
			while (pIterator.hasNext()) {
				Property p = pIterator.nextProperty();
				// Value[] values = p.getValues();
				if (p.getType() == PropertyType.STRING && !p.isMultiple()) {
					logger.info("            {} {}={}", new Object[] { ++j, p.getName(), p.getString() });
				} else if (p.getType() == PropertyType.DATE) {
					logger.info("            {} {}={}", new Object[] { ++j, p.getName(), p.getDate() });
				}
			}
			NodeIterator childrenIt = node.getNodes();
			while (childrenIt.hasNext()) {
				logger.info("   ----{}子节点----", node.getName());
				printAllNode(pre + " " + levelTemp, childrenIt);
			}
		}
	}

	@SuppressWarnings("unused")
	private static int printRepositoryConfigs(Repository res, String[] descs) throws ValueFormatException,
			RepositoryException {
		int i = 0;
		for (String d : descs) {
			Value v = res.getDescriptorValue(d);
			String vStr = null;
			if (v != null) {
				if (PropertyType.STRING == v.getType())
					vStr = v.getString();
				else if (PropertyType.BOOLEAN == v.getType()) {
					vStr = v.getBoolean() + "";
				} else {
					vStr = v.toString();
				}
			}
			System.out.println(i + ". " + d + "[" + vStr + "]");
			i++;
		}
		return i;
	}
}
