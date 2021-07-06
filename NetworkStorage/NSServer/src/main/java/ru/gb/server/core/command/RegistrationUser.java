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
public class RegistrationUser implements CommandService {

    private final ServerHandler serverHandler;
    public RegistrationUser(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {
        boolean isUserExist = DatabaseHandler.createNewUser(clientMessage.getLogin(), clientMessage.getPassword());

        MessageDTO serverMessage = new MessageDTO();
        if (isUserExist) {
            Path newClientPath = createCatalog(clientMessage);
            showServerCatalog(serverMessage, newClientPath);
        } else {
            errorRegistration(serverMessage);
        }
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private Path createCatalog (MessageDTO clientMessage) {
        return serverHandler.createUserCatalog(clientMessage.getLogin());
    }

    private void showServerCatalog (MessageDTO serverMessage, Path newClientPath) {
        serverMessage.setMessageType(MessageType.SERVER_CATALOG);
        serverMessage.setFileDirectorySelectTo(newClientPath.getFileName().toString());
        serverMessage.setServerCatalogList(serverHandler.showMyCatalog(newClientPath));
    }

    private void errorRegistration (MessageDTO serverMessage) {
        serverMessage.setMessageType(MessageType.REGISTRATION_ERROR);
    }

    @Override
    public MessageType getCommand() {
        return MessageType.REGISTRATION;
    }
}
