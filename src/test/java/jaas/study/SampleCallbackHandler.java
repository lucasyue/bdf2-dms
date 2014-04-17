package jaas.study;

import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

public class SampleCallbackHandler implements CallbackHandler {
	private String userName;
	private String password;

	public SampleCallbackHandler(String userName, String password) {
		this.userName = userName;
		this.password = password;
	}

	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		for (Callback c : callbacks) {
			if (c instanceof NameCallback) {
				((NameCallback) c).setName(this.userName);
			} else if (c instanceof PasswordCallback) {
				((PasswordCallback) c).setPassword(this.password.toCharArray());
			}
		}

	}
}
