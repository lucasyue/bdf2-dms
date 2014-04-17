package com.bstek.bdf2.dms.core;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

public class JcrServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		try {
			RepositoryManager.initRepository();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		JcrSessionManager.initAnnotationMapper();
	}

	@Override
	public void destroy() {
		super.destroy();
	}

}
