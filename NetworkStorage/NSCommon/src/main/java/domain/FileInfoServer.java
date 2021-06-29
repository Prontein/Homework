package domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileInfoServer {
    private String fileName;
    private Long fileSize;
    private String fileType;


    public FileInfoServer(Path path) {
        try {
            this.fileName = path.getFileName().toString();
            this.fileSize = Files.size(path);
            this.fileType = Files.isDirectory(path) ? "[DIR]" : "FILE" ;
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return this.fileName + " " + this.fileSize;
    }
}
