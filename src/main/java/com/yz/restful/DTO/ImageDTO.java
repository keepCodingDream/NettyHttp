package com.yz.restful.DTO;

public class ImageDTO {
    private String fileType;
    private byte[] content;
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public byte[] getContent() {
		return content;
	}
	public void setContent(byte[] content) {
		this.content = content;
	}
    
}
