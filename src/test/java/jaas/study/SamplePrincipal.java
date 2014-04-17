package jaas.study;

import java.security.Principal;

public class SamplePrincipal implements Principal {
	private String name;
	private String password;

	public SamplePrincipal(String name, String password) {
		this.name = name;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

}
