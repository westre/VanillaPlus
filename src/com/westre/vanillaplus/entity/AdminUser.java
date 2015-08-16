package com.westre.vanillaplus.entity;

public class AdminUser {
	private boolean onDuty;
	private int adminId;
	
	public boolean isOnDuty() {
		return onDuty;
	}
	
	public void setOnDuty(boolean toggle) {
		this.onDuty = toggle;
	}
	
	public int getAdminId() {
		return adminId;
	}

	public void setAdminId(int adminId) {
		this.adminId = adminId;
	}
}
