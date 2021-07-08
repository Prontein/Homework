package ru.gb.server.core.command;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import ru.gb.server.core.command.commandservice.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.database.DatabaseHandler;
import ru.gb.server.factory.Factory;

import java.nio.file.Path;

@CommandServer
public class AuthorizationUser implements CommandService {

    private final ServerHandler serverHandler = Factory.getServerHandler();

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {
        boolean isUserExist = DatabaseHandler.authorizationUser(clientMessage.getLogin(), clientMessage.getPassword());

        MessageDTO serverMessage = new MessageDTO();
        if (isUserExist) {
            Path ClientPath = serverHandler.getServerPath(clientMessage.getLogin());
            showServerCatalog(serverMessage, ClientPath);
        } else {
            errorAuth(serverMessage);
        }
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private void showServerCatalog(MessageDTO serverMessage, Path ClientPath) {
        serverMessage.setMessageType(MessageType.SERVER_CATALOG);
        serverMessage.setFileDirectorySelectTo(ClientPath.getFileName().toString());
        serverMessage.setServerCatalogList(serverHandler.showMyCatalog(ClientPath));
    }

    private void errorAuth(MessageDTO serverMessage) {
        serverMessage.setMessageType(MessageType.AUTH_ERROR);
    }

    @Override
    public MessageType getCommand() {
        return MessageType.AUTHORIZATION;
    }
}
