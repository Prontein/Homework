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
public class ShowSelectedCatalog implements CommandService {

    private final Path serverCloud = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY")).toAbsolutePath();
    private final ServerHandler serverHandler = Factory.getServerHandler();

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {
        MessageDTO serverMessage = new MessageDTO();
        collectMessage(serverMessage, clientMessage);
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private void collectMessage(MessageDTO serverMessage, MessageDTO clientMessage) {
        serverMessage.setMessageType(MessageType.SERVER_CATALOG);
        serverMessage.setServerCatalogList(serverHandler.showMyCatalog(getSelectedCatalogPath(clientMessage)));
        serverMessage.setFileDirectorySelectTo(convertCatalogPath(getSelectedCatalogPath(clientMessage)));
    }

    private Path getSelectedCatalogPath(MessageDTO clientMessage) {
        Path selectCatalogPath = Paths.get(clientMessage.getFileDirectorySelectTo(), clientMessage.getCatalogName());
        return serverCloud.resolve(selectCatalogPath);
    }

    private String convertCatalogPath(Path selectedPath) {
        return serverCloud.relativize(selectedPath).normalize().toString();
    }

    @Override
    public MessageType getCommand() {
        return MessageType.SERVER_CATALOG_SHOW_SELECTED;
    }

}