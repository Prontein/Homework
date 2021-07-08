package ru.gb.server.core.command;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import ru.gb.server.core.command.commandservice.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.factory.Factory;
import ru.gb.server.util.PropertyUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@CommandServer
public class FileCopyToServerComplete implements CommandService {

    private final Path serverCloud = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY")).toAbsolutePath();
    private final ServerHandler serverHandler = Factory.getServerHandler();

    @Override
    public void processCommand(MessageDTO infoMessage, ChannelHandlerContext ctx) {
        MessageDTO serverMessage = new MessageDTO();
        showServerCatalog(serverMessage, infoMessage.getCatalogName());
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private void showServerCatalog(MessageDTO serverMessage, String serverPath) {
        serverMessage.setMessageType(MessageType.SERVER_CATALOG);
        serverMessage.setFileDirectorySelectTo(convertServerPath(serverPath));
        serverMessage.setServerCatalogList(serverHandler.showMyCatalog(collectServerPath(serverPath)));
    }

    private Path collectServerPath(String serverPath) {
        return Paths.get(serverPath).getParent();
    }

    private String convertServerPath(String serverPath) {
        return serverCloud.relativize(collectServerPath(serverPath)).toString();
    }

    @Override
    public MessageType getCommand() {
        return MessageType.FILE_COPY_COMPLETE;
    }

}
