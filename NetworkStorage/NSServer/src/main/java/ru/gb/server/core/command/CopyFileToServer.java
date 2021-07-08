package ru.gb.server.core.command;

import domain.BigFilesWriteHandler;
import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.server.core.command.commandservice.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.factory.Factory;
import ru.gb.server.util.PropertyUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@CommandServer
public class CopyFileToServer implements CommandService {

    private final Path serverCloud = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY")).toAbsolutePath();
    private final ServerHandler serverHandler = Factory.getServerHandler();

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {
        MessageDTO serverMessage = new MessageDTO();

        if (checkout(clientMessage)) {
            serverMessage.setMessageType(MessageType.FILE_COPY_ACCEPT);
            ctx.pipeline().addFirst(new BigFilesWriteHandler(createFilename(clientMessage), clientMessage.getFileSize()));
            ctx.pipeline().addFirst(new ChunkedWriteHandler());
        } else {
            serverMessage.setMessageType(MessageType.FILE_COPY_REJECT);
        }
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private boolean checkout(MessageDTO clientMessage) {
        return serverHandler.fileIsExists(clientMessage.getFileName(), clientMessage.getFileDirectorySelectTo());
    }

    private String createFilename(MessageDTO clientMessage) {
        return serverCloud.resolve(Paths.get(clientMessage.getFileDirectorySelectTo(), clientMessage.getFileName())).toString();
    }

    @Override
    public MessageType getCommand() {
        return MessageType.FILE_COPY;
    }
}
