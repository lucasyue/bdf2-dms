package com.bstek.bdf2.dms.security.auth;

import java.io.IOException;
import java.security.Principal;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.jackrabbit.core.security.AnonymousPrincipal;
import org.apache.jackrabbit.core.security.SystemPrincipal;
import org.apache.jackrabbit.core.security.UserPrincipal;
import org.apache.jackrabbit.core.security.principal.AdminPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import com.bstek.bdf2.core.service.IUserService;
import com.bstek.bdf2.dms.core.ContextHolder;

public class DefaultLoginModule implements LoginModule {
	private boolean isAuthenticated = false;
	private CallbackHandler callbackHandler;
	private Subject subject;
	private Principal principal;
	private String adminId;
	private String anonymousId;

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map<String, ?> sharedState, Map<String, ?> options) {
		this.subject = subject;
		this.callbackHandler = callbackHandler;
	}

	public boolean login() throws LoginException {
		NameCallback nameCk = new NameCallback("nameCk");
		PasswordCallback pCk = new PasswordCallback("pwdCk", false);
		Callback[] calls = new Callback[] { nameCk, pCk };
		try {
			callbackHandler.handle(calls);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (UnsupportedCallbackException e) {
			e.printStackTrace();
		}
		String name = nameCk.getName();
		String pwd = String.valueOf(pCk.getPassword());
		// TODO 是否为管理员或匿名用户 可能移到在权限判断时
		IUserService userService = ContextHolder.getBean(IUserService.BEAN_ID);
		UserDetails user = userService.loadUserByUsername(name);
		// DefaultUser user = new DefaultUser();
		// user.setUsername(name);
		// user.setPassword(pwd);
		if (user == null) {
			throw new LoginException("用户[" + name + "]不存在！");
		} else if (user.getPassword().equals(pwd)) {
			// check username and password
			// TODO
			if (this.getAdminId().equals(name)) {
				principal = new AdminPrincipal(name);
			} else if (this.getAnonymousId().equals(name)) {
				principal = new AnonymousPrincipal();
			} else if ("system".equals(name)) {
				principal = new SystemPrincipal();
			} else {
				principal = new UserPrincipal(name);
			}
			isAuthenticated = true;
		} else {
			throw new LoginException("登录失败，用户名或密码错误！");
		}

		return isAuthenticated;
	}

	public boolean commit() throws LoginException {
		if (isAuthenticated) {
			subject.getPrincipals().add(principal);
		} else {
			throw new LoginException("登录认证失败！");
		}
		return isAuthenticated;
	}

	public boolean abort() throws LoginException {
		return false;
	}

	public boolean logout() throws LoginException {
		subject.getPrincipals().remove(principal);
		principal = null;
		return true;
	}

	public String getAdminId() {
		return adminId;
	}

	public void setAdminId(String adminId) {
		this.adminId = adminId;
	}

	public String getAnonymousId() {
		return anonymousId;
	}

	public void setAnonymousId(String anonymousId) {
		this.anonymousId = anonymousId;
	}

}
