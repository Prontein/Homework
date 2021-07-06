package ru.gb.server.core.commandhandler;

import domain.FileInfo;
import ru.gb.server.util.PropertyUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ServerHandler {

    private final Path serverCloud = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY")).toAbsolutePath();


    public Path getServerPath(String login) {
        Path userPath = serverCloud.resolve(Paths.get(login));
        return  userPath.normalize().toAbsolutePath();
    }

    public boolean fileIsExists (String fileName, String pathToServer) {
        Path selectPath = serverCloud.resolve(Paths.get(pathToServer));

        try {
            List<FileInfo> serverCatalog = Files.list(selectPath).map(FileInfo::new).collect(Collectors.toList());
            for (FileInfo file : serverCatalog) {
                if (file.getFilename().equals(fileName)) return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<FileInfo> serverCatalogUp(Path serverCatalog) {
        Path upperPath = serverCatalog.getParent();

        if (upperPath != null && !upperPath.equals(serverCloud)) {
            return  showMyCatalog(upperPath);
        }
        return showMyCatalog(serverCatalog);
    }

    public List<FileInfo> showMyCatalog (Path currentPath) {
        try {
            List<FileInfo> serverCatalog = Files.list(currentPath).map(FileInfo::new).collect(Collectors.toList());
            return serverCatalog;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Path getServerCatalogPathUp(Path path) {
        Path currentPath = path.getParent();

        if (currentPath != null && !currentPath.equals(serverCloud)) {
            return  currentPath.normalize().toAbsolutePath();
        }
        return  path.normalize().toAbsolutePath();
    }

    public Path createUserCatalog (String login) {
        Path userPath = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY"), login).toAbsolutePath();

        try {
            Files.createDirectories(userPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userPath;
    }
}
