package com.bstek.bdf2.dms.rest;

import org.springframework.stereotype.Service;

import com.bstek.dorado.core.Configure;
import com.rop.security.AppSecretManager;

@Service(AppkeyManager.BEAN_ID_)
public class AppkeyManager implements AppSecretManager {
	public final static String BEAN_ID_ = "dms.appkeyManager";

	public String getSecret(String appKey) {
		String appkeySecret = Configure.getString("dms.appkeySecret");
		return appkeySecret;
	}

	public boolean isValidAppKey(String appKey) {
		String dmsAppkey = Configure.getString("dms.appkey");
		if (dmsAppkey.equals(appKey)) {
			return true;
		}
		return false;
	}
}
