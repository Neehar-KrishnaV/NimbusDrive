package com.neehar.nimbusdrive.dto;

public class DashboardResponse {

    private String email;
    private Long storageUsed;
    private Long storageLimit;
    private Long fileCount;
    private Double usagePercentage;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getStorageUsed() {
		return storageUsed;
	}
	public void setStorageUsed(Long storageUsed) {
		this.storageUsed = storageUsed;
	}
	public Long getStorageLimit() {
		return storageLimit;
	}
	public void setStorageLimit(Long storageLimit) {
		this.storageLimit = storageLimit;
	}
	public Long getFileCount() {
		return fileCount;
	}
	public void setFileCount(Long fileCount) {
		this.fileCount = fileCount;
	}
	public Double getUsagePercentage() {
		return usagePercentage;
	}
	public void setUsagePercentage(Double usagePercentage) {
		this.usagePercentage = usagePercentage;
	}

    // Generate Getters & Setters
}