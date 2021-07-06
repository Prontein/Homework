package ru.gb.server.core.command;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import ru.gb.server.core.command.commandinterface.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.util.PropertyUtils;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command
public class ShowDirectoryUp implements CommandService {

    private final Path serverCloud = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY")).toAbsolutePath();
    private final ServerHandler serverHandler;

    public ShowDirectoryUp(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {
        MessageDTO serverMessage = new MessageDTO();
        collectMessage(serverMessage, clientMessage);
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private void collectMessage (MessageDTO serverMessage, MessageDTO clientMessage) {
        serverMessage.setMessageType(MessageType.SERVER_CATALOG);
        serverMessage.setFileDirectorySelectTo(convertCatalogPath(serverHandler.getServerCatalogPathUp(collectServerPath(clientMessage))));
        serverMessage.setServerCatalogList(serverHandler.serverCatalogUp(collectServerPath(clientMessage)));
    }

    private Path collectServerPath (MessageDTO clientMessage) {
        return serverCloud.resolve(clientMessage.getServerCatalog());
    }

    private String convertCatalogPath (Path parentPath) {
        return serverCloud.relativize(parentPath).normalize().toString();
    }

    @Override
    public MessageType getCommand() {
        return MessageType.SERVER_CATALOG_UP;
    }
}
