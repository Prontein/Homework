package ru.gb.server.core.commandhandler;

import domain.FileInfoServer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class ServerHandler {
    public  List<FileInfoServer> showMyCatalog () {
        try {
            List<FileInfoServer> serverCatalog = Files.list(getServerCatalogPath()).map(FileInfoServer::new).collect(Collectors.toList());
            return serverCatalog;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Path getServerCatalogPath() {
        Path currentPath = Paths.get("cloud" );
        return  currentPath.normalize().toAbsolutePath();
    }

    public boolean fileIsExists (String fileName, String pathToServer) {
        try {
            List<FileInfoServer> hi = Files.list(Paths.get(pathToServer)).map(FileInfoServer::new).collect(Collectors.toList());
            for (FileInfoServer file : hi) {
                if (file.getFileName().equals(fileName)) return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public List<FileInfoServer> serverCatalogUp(String serverCatalogDirectory) {
        Path upperPath = Paths.get(serverCatalogDirectory).getParent();
        if (upperPath != null) {
            return  showMyCatalog(upperPath);
        }
        return showMyCatalog(Paths.get(serverCatalogDirectory));
    }

    public List<FileInfoServer> showMyCatalog (Path currentPath) {
        try {
            List<FileInfoServer> serverCatalog = Files.list(currentPath).map(FileInfoServer::new).collect(Collectors.toList());
            return serverCatalog;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Path getServerCatalogPath(String path) {
        Path currentPath = Paths.get(path).getParent();
        if (currentPath != null) {
            return  currentPath.normalize().toAbsolutePath();
        }
        return  Paths.get(path).normalize().toAbsolutePath();
    }
}
