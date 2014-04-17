package jaas.study;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

public class LoginModule {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			LoginContext lc = new LoginContext("JaasSample",
					new SampleCallbackHandler("lucas", "123"));
			lc.login();
			System.out.println("登录成功！");
		} catch (LoginException e) {
			e.printStackTrace();
			throw new RuntimeException("登录失败！");
		}

	}

}
