package domain;

import com.google.gson.Gson;
import java.util.List;

public class MessageDTO {

    private List<FileInfoServer> serverCatalogList;
    private MessageType messageType;
    private String fileName;
    private String fileDirectorySelectTo;
    private String fileDirectorySelectFrom;
    private String serverCatalogDirectory;
    private Long fileSize;

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public List<FileInfoServer> getServerCatalogList() {
        return serverCatalogList;
    }

    public void setServerCatalogList(List<FileInfoServer> serverCatalogList) {
        this.serverCatalogList = serverCatalogList;
    }

    public static MessageDTO convertFromJson(String json) {
        return new Gson().fromJson(json, MessageDTO.class);
    }

    public String convertToJson() {
        return new Gson().toJson(this);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileDirectorySelectTo() {
        return fileDirectorySelectTo;
    }

    public void setFileDirectorySelectTo(String fileDirectorySelectTo) {
        this.fileDirectorySelectTo = fileDirectorySelectTo;
    }

    public String getFileDirectorySelectFrom() {
        return fileDirectorySelectFrom;
    }

    public void setFileDirectorySelectFrom(String fileDirectorySelectFrom) {
        this.fileDirectorySelectFrom = fileDirectorySelectFrom;
    }

    public String getServerCatalogDirectory() {
        return serverCatalogDirectory;
    }

    public void setServerCatalogDirectory(String serverCatalogDirectory) {
        this.serverCatalogDirectory = serverCatalogDirectory;
    }
}
