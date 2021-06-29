package domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


public class FileInfoClient {

    private String filename;
    private long size;
    private FileType type;

    public FileType getType() {
        return type;
    }

    public String getFilename() {
        return filename;
    }

    public long getSize() {
        return size;
    }

    public FileInfoClient(Path path) {
        try {
            this.filename = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;
            if(this.type == FileType.DIRECTORY) {
                this.size = -1L;
            }
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при чтении файла");
        }
    }
}
