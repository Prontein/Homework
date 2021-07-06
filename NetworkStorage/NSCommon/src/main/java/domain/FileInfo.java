package domain;

import domain.allertsandexeption.ExceptionMessage;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Setter
public class FileInfo {

    private String filename;
    private long size;
    private FileType type;

    public FileInfo(Path path) {
        try {
            this.filename = path.getFileName().toString();
            this.size = Files.size(path);
            this.type = Files.isDirectory(path) ? FileType.DIRECTORY : FileType.FILE;

            if(this.type == FileType.DIRECTORY) {
                this.size = -1L;
            }
        } catch (IOException e) {
            throw new RuntimeException(ExceptionMessage.READ_FILE_ERROR);
        }
    }
}
