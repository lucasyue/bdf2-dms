package com.bstek.bdf2.dms.view;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import com.bstek.bdf2.core.context.ContextHolder;
import com.bstek.bdf2.dms.dao.DocumentDao;
import com.bstek.bdf2.dms.model.Document;
import com.bstek.bdf2.dms.model.Resource;
import com.bstek.dorado.web.resolver.AbstractResolver;

public class FileDownloadResolver extends AbstractResolver {

	@Override
	protected ModelAndView doHandleRequest(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		String docPath = request.getParameter("docPath");
		String docUUID = request.getParameter("docUUID");
		DocumentDao docDao = null;
		Document doc = null;
		boolean flag = false;
		if (StringUtils.hasText(docUUID) || StringUtils.hasText(docPath)) {
			String loginUser = ContextHolder.getLoginUser().getUsername();
			String password = ContextHolder.getLoginUser().getPassword();
			docDao = new DocumentDao(loginUser, password);
		}
		if (StringUtils.hasText(docUUID)) {
			try {
				doc = docDao.findObjectByUUID(docUUID);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (StringUtils.hasText(docPath)) {
			try {
				doc = docDao.findObjectByPath(docPath);
				flag = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (flag) {
			Resource docRes = doc.getResource();
			if (docRes != null) {
				InputStream inputS = docRes.getContents();
				if (inputS != null) {
					byte[] fI = new byte[inputS.available()];
					inputS.read(fI);
					String filename = new String(docRes.getFileName().getBytes(
							"utf-8"), "ISO8859-1");
					response.setHeader("content-disposition",
							"attachment;filename=" + filename);
					response.setContentType(doc.getMimeType());
					response.getOutputStream().write(fI);
					request.getSession().setAttribute("DOWNLOAD_FLAG",
							"DOWNLOAD_TRUE");
					response.flushBuffer();
				}
			}
		} else {
			request.getSession()
					.setAttribute("DOWNLOAD_FLAG", "DOWNLOAD_FALSE");
			response.getWriter().println("该文件不存在。");
		}
		return null;
	}

}
