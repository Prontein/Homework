package domain;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MessageDTO {
    private List<FileInfo> serverCatalogList;
    private MessageType messageType;
    private String fileName;
    private String catalogName;
    private String fileDirectorySelectTo;
    private String fileDirectorySelectFrom;
    private String serverCatalog;
    private Long fileSize;
    private String Login;
    private String Password;
    private String Error;

    public static MessageDTO convertFromJson(String json) {
        return new Gson().fromJson(json, MessageDTO.class);
    }

    public String convertToJson() {
        return new Gson().toJson(this);
    }
}
