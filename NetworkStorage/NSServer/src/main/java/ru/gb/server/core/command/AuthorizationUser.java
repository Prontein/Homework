package ru.gb.server.core.command;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import ru.gb.server.core.command.commandinterface.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.database.DatabaseHandler;

import java.nio.file.Path;

@CommandLine.Command
public class AuthorizationUser implements CommandService {

    private final ServerHandler serverHandler;
    public AuthorizationUser(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {
        boolean isUserExist = DatabaseHandler.authorizationUser(clientMessage.getLogin(), clientMessage.getPassword());

        MessageDTO serverMessage = new MessageDTO();
            if (isUserExist) {
                Path ClientPath = serverHandler.getServerPath(clientMessage.getLogin());
                showServerCatalog(serverMessage, ClientPath);
            } else {
                errorAuth (serverMessage);
            }
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private void showServerCatalog (MessageDTO serverMessage, Path ClientPath) {
        serverMessage.setMessageType(MessageType.SERVER_CATALOG);
        serverMessage.setFileDirectorySelectTo(ClientPath.getFileName().toString());
        serverMessage.setServerCatalogList(serverHandler.showMyCatalog(ClientPath));
    }

    private void errorAuth (MessageDTO serverMessage) {
        serverMessage.setMessageType(MessageType.AUTH_ERROR);
    }

    @Override
    public MessageType getCommand() {
        return MessageType.AUTHORIZATION;
    }
}
