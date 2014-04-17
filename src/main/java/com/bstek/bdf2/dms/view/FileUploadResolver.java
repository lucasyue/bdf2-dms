package com.bstek.bdf2.dms.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ModelAndView;

import com.bstek.bdf2.dms.core.ContextHolder;
import com.bstek.bdf2.dms.dao.DocumentDao;
import com.bstek.bdf2.dms.model.Document;
import com.bstek.bdf2.dms.model.Resource;
import com.bstek.dorado.web.resolver.AbstractResolver;

public class FileUploadResolver extends AbstractResolver {

	@Override
	protected ModelAndView doHandleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		MultipartHttpServletRequest multiReq = null;
		CommonsMultipartResolver comMultRes = new CommonsMultipartResolver();
		if (request instanceof MultipartHttpServletRequest) {
			multiReq = (MultipartHttpServletRequest) request;
		} else if (comMultRes.isMultipart(request)) {
			multiReq = comMultRes.resolveMultipart(request);
			request.getCharacterEncoding();
			String folderUUID = multiReq.getParameter("folderUUID");
			String folderPath = multiReq.getParameter("folderPath");
			MultipartFile multiFile = multiReq.getFile("file");
			String loginUser = ContextHolder.getLoginUser().getUsername();
			String password = ContextHolder.getLoginUser().getPassword();
			DocumentDao docDao = new DocumentDao(loginUser, password);
			Document d = docDao.findObjectByPath(folderPath + "/"
					+ multiFile.getOriginalFilename());
			if (d != null) {
				response.getWriter().print(
						"{result:false,message:'[" + d.getName() + "]文件已存在。'}");
				return null;
			}
			Document doc = new Document();
			doc.setAuthor("dmsAdmin");
			doc.setFolderUUID(folderUUID);
			doc.setName(multiFile.getOriginalFilename());
			doc.setUuid(UUID.randomUUID().toString());
			doc.setPath(folderPath + "/" + multiFile.getOriginalFilename());
			doc.setMimeType(multiFile.getContentType());
			Date nowDate = new Date();
			doc.setCreateDate(nowDate);
			doc.setLastModifiedDate(nowDate);
			List<String> keywords = new ArrayList<String>();
			keywords.add(multiFile.getOriginalFilename());
			Resource res = new Resource();
			doc.setKeywords(keywords);
			doc.setTags(keywords);
			res.setAuthor(doc.getAuthor());
			res.setContents(multiFile.getInputStream());
			res.setFileName(multiFile.getOriginalFilename());
			res.setPath(doc.getPath() + "/" + multiFile.getOriginalFilename());
			res.setSize(multiFile.getSize());
			res.setUuid(UUID.randomUUID().toString());
			res.setCreateDate(nowDate);
			res.setLastModifiedDate(nowDate);
			doc.setResource(res);
			docDao.getObjectContentManager().insert(doc);
			response.getWriter().print("{result:true,message:'文件上传成功！'}");
		}
		return null;
	}
}
