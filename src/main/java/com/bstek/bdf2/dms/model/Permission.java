
package com.bstek.bdf2.dms.model;

import java.io.Serializable;

public class Permission implements Serializable {
	private static final long serialVersionUID = -6594786775079108975L;

	public static final String USERS_READ = "dms:authUsersRead";
	public static final String USERS_WRITE = "dms:authUsersWrite";
	public static final String USERS_DELETE = "dms:authUsersDelete";
	public static final String USERS_SECURITY = "dms:authUsersSecurity";
	public static final String ROLES_READ = "dms:authRolesRead";
	public static final String ROLES_WRITE = "dms:authRolesWrite";
	public static final String ROLES_DELETE = "dms:authRolesDelete";
	public static final String ROLES_SECURITY = "dms:authRolesSecurity";
	
	public static final byte NONE = 0;
	public static final byte READ = 1;
	public static final byte WRITE = 2;
	public static final byte DELETE = 4;
	public static final byte SECURITY = 8; 
	
	private String item;
	private int permissions;
	
	public String getItem() {
		return item;
	}
	
	public void setItem(String item) {
		this.item = item;
	}
	
	public int getPermissions() {
		return permissions;
	}
	
	public void setPermissions(int permissions) {
		this.permissions = permissions;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("item="); sb.append(item);
		sb.append(", permissions="); sb.append(permissions);
		sb.append("}");
		return sb.toString();
	}
}
