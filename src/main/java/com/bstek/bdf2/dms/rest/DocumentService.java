package com.bstek.bdf2.dms.rest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.jackrabbit.ocm.exception.ObjectContentManagerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bstek.bdf2.dms.client.AbstractDocumentServiceRequest;
import com.bstek.bdf2.dms.client.ErrorConstants;
import com.bstek.bdf2.dms.client.impl.DocumentServiceDeleteRequest;
import com.bstek.bdf2.dms.client.impl.DocumentServiceDownloadRequest;
import com.bstek.bdf2.dms.client.impl.DocumentServiceDownloadResponse;
import com.bstek.bdf2.dms.client.impl.DocumentServiceResponse;
import com.bstek.bdf2.dms.client.impl.DocumentServiceUploadRequest;
import com.bstek.bdf2.dms.core.ContextHolder;
import com.bstek.bdf2.dms.dao.DocumentDao;
import com.bstek.bdf2.dms.dao.ObjectNotExsitException;
import com.bstek.bdf2.dms.model.Document;
import com.bstek.bdf2.dms.model.Folder;
import com.bstek.bdf2.dms.model.Resource;
import com.rop.annotation.NeedInSessionType;
import com.rop.annotation.ServiceMethod;
import com.rop.annotation.ServiceMethodBean;
import com.rop.request.UploadFile;
import com.rop.response.BusinessServiceErrorResponse;
import com.rop.response.ErrorResponse;
import com.rop.security.MainError;
import com.rop.security.SimpleMainError;

@ServiceMethodBean
public class DocumentService {
	private final static Logger logger = LoggerFactory.getLogger(DocumentService.class);

	@ServiceMethod(method = AbstractDocumentServiceRequest.METHOD_1_UPLOAD_, version = AbstractDocumentServiceRequest.METHOD_VERSION_1_, needInSession = NeedInSessionType.NO)
	public Object upload(DocumentServiceUploadRequest request) {
		logger.info("文档开始上传：" + request.getFileName());
		try {
			DocumentServiceResponse res = new DocumentServiceResponse();
			DocumentDao docDao = null;
			try {
				docDao = new DocumentDao(request.getUserName(), request.getPassword());
			} catch (Exception e1) {
				logger.error("获取docDao失败。");
				ErrorResponse error1 = new ErrorResponse();
				error1.setCode("dms.userNameOrPasswordError");
				error1.setMessage(e1.getMessage());
				error1.setSolution("请将传递正确的用户名密码！");
				return error1;
			}
			Folder folder = null;
			try {
				folder = docDao.findObjectByPath(request.getFolderPath());
			} catch (Exception e) {
				if (e instanceof ObjectNotExsitException) {
					try {
						folder = docDao.findObjectByPath(Folder.FOLDER_BASE_SHARE_PATH_);
					} catch (Exception e1) {
						logger.error("处理文档上传默认路径失败，" + e1.getMessage());
						MainError mainError = new SimpleMainError(ErrorConstants.DOCUMENT_FIND_ERROR_CODE_,
								"处理文档上传默认路径失败，" + e1.getMessage(), "请联系管理员！");
						ErrorResponse error2 = new ErrorResponse(mainError);
						return error2;
					}
				} else {
					ErrorResponse error3 = new ErrorResponse();
					error3.setCode(ErrorConstants.DOCUMENT_FIND_ERROR_CODE_);
					error3.setMessage(e.getMessage());
					error3.setSolution("请将文件上传至正确的目录！");
					return error3;
				}
			}
			Document doc = new Document();
			doc.setUuid(UUID.randomUUID().toString());
			doc.setAuthor(request.getUserName());
			doc.setFolderUUID(folder.getUuid());
			Date nowDate = new Date();
			doc.setCreateDate(nowDate);
			doc.setLastModifiedDate(nowDate);
			UploadFile uploadDoc = request.getUploadDocument();
			doc.setMimeType(uploadDoc.getFileType());// TODO check
			doc.setName(request.getFileName());
			doc.setPath(folder.getStorePath() + "/" + request.getFileName());
			Resource resource = new Resource();
			resource.setAuthor(request.getUserName());
			resource.setFileName(request.getFileName());
			resource.setPath(doc.getPath() + "/" + request.getFileName());
			List<String> keywords = new ArrayList<String>();
			keywords.add(doc.getName());
			keywords.add(uploadDoc.getFileType());
			doc.setKeywords(keywords);
			doc.setTags(keywords);
			try {
				resource.setSize(Long.valueOf("" + uploadDoc.getContent().length));
			} catch (NumberFormatException e) {
				logger.error(e.getMessage());
			}
			ByteArrayInputStream bis = new ByteArrayInputStream(uploadDoc.getContent());
			resource.setContents(bis);
			resource.setUuid(UUID.randomUUID().toString());
			resource.setCreateDate(nowDate);
			resource.setLastModifiedDate(nowDate);
			doc.setResource(resource);
			try {
				docDao.getObjectContentManager().insert(doc);
			} catch (ObjectContentManagerException e) {
				e.printStackTrace();
				BusinessServiceErrorResponse bsErrorRes = new BusinessServiceErrorResponse();
				bsErrorRes.setCode(ErrorConstants.DOCUMENT_UPLOAD_ERROR);
				if (e.getMessage().contains("Path already exists")) {
					bsErrorRes.setMessage("文件上传失败，文件夹[" + folder.getName() + "]下已存在文件[" + doc.getName() + "]！");
				} else {
					bsErrorRes.setMessage("文件上传失败，请联系管理员！");
				}
				bsErrorRes.setSolution(e.getMessage());
				return bsErrorRes;
			}
			res.setProcessFlag(true);
			res.setUuid(doc.getUuid());
			res.setMessage("文档上传成功！");
			logger.info("文档[" + request.getFileName() + "]上传成功！");
			return res;
		} finally {
			ContextHolder.clean();
		}
	}

	@ServiceMethod(method = AbstractDocumentServiceRequest.METHOD_2_DOWNLOAD_, version = AbstractDocumentServiceRequest.METHOD_VERSION_1_, needInSession = NeedInSessionType.NO)
	public Object download(DocumentServiceDownloadRequest request) {
		logger.info("文档[" + request.getDocumentUUID() + "]开始下载！");
		try {
			DocumentServiceDownloadResponse res = new DocumentServiceDownloadResponse();
			DocumentDao docDao = new DocumentDao(request.getUserName(), request.getPassword());
			Document doc = null;
			try {
				doc = docDao.findObjectByUUID(request.getDocumentUUID());
			} catch (Exception e1) {
				return processFindDocumentError(request.getDocumentUUID(), e1);
			}
			if (doc != null) {
				try {
					res.setProcessFlag(true);
					res.setMessage("文档下载成功！");
					InputStream ins = doc.getResource().getContents();
					byte content[];
					content = new byte[ins.available()];
					ins.read(content);
					res.setContent(content);
					res.setDocumentName(doc.getName());
				} catch (IOException e) {
					e.printStackTrace();
					BusinessServiceErrorResponse bsErrRes = new BusinessServiceErrorResponse();
					bsErrRes.setCode(ErrorConstants.DOCUMENT_DOWNLOAD_ERROR_);
					bsErrRes.setMessage("文档[" + request.getDocumentUUID() + "]下载失败！");
					bsErrRes.setSolution(e.getMessage());
					logger.error(bsErrRes.getMessage());
					return bsErrRes;
				}
			} else {
				res.setProcessFlag(false);
				res.setMessage("文档[" + request.getDocumentUUID() + "]下载失败，请联系管理员！");
				logger.error(res.getMessage());
			}
			return res;
		} finally {
			ContextHolder.clean();
		}
	}

	private Object processFindDocumentError(String document, Exception e) {
		if (e instanceof ObjectNotExsitException) {
			logger.error(e.getMessage());
			MainError mainError = new SimpleMainError(ErrorConstants.DOCUMENT_NOT_EXSIT_ERROR_CODE_, "文档[" + document
					+ "]不存在，查找失败！", "请传递正确的文档参数。");
			ErrorResponse error2 = new ErrorResponse(mainError);
			return error2;
		} else {
			MainError mainError = new SimpleMainError(ErrorConstants.DOCUMENT_FIND_ERROR_CODE_, "查找文档[" + document
					+ "]发生错误！", "请传递正确的文档参数。");
			ErrorResponse error2 = new ErrorResponse(mainError);
			return error2;
		}
	}

	@ServiceMethod(method = AbstractDocumentServiceRequest.METHOD_3_DELETE_, version = AbstractDocumentServiceRequest.METHOD_VERSION_1_, needInSession = NeedInSessionType.NO)
	public Object delete(DocumentServiceDeleteRequest request) {
		logger.info("开始删除文档[" + request.getDocumentUUID() + "]。");
		try {
			DocumentServiceResponse res = new DocumentServiceResponse();
			res.setUuid(request.getDocumentUUID());
			DocumentDao docDao = new DocumentDao(request.getUserName(), request.getPassword());
			Document doc = null;
			try {
				doc = docDao.findObjectByUUID(request.getDocumentUUID());
			} catch (Exception e) {
				return processFindDocumentError(request.getDocumentUUID(), e);
			}
			if (doc != null) {
				res.setProcessFlag(true);
				docDao.getObjectContentManager().remove(doc);
				docDao.getObjectContentManager().save();
				res.setMessage("文档[" + request.getDocumentUUID() + "]删除成功！");
			} else {
				res.setProcessFlag(false);
				res.setMessage("要删除的文档[" + request.getDocumentUUID() + "]不存在！");
			}
			return res;
		} finally {
			ContextHolder.clean();
		}
	}

}