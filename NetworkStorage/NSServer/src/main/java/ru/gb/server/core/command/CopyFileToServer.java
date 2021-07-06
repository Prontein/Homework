package ru.gb.server.core.command;

import domain.BigFilesWriteHandler;
import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import ru.gb.server.core.command.commandinterface.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.util.PropertyUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command
public class CopyFileToServer implements CommandService {

    private final String IN_OUT_OBJECT_HANDLER = "ChunkedWriteHandler";
    private final String BIF_FILE_HANDLER = "BigFilesWriteHandler";

    private final Path serverCloud = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY")).toAbsolutePath();
    private final ServerHandler serverHandler;

    public CopyFileToServer(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {
        MessageDTO serverMessage = new MessageDTO();

        if (checkout(clientMessage)) {
            serverMessage.setMessageType(MessageType.FILE_COPY_ACCEPT);
            ctx.pipeline().addFirst(BIF_FILE_HANDLER, new BigFilesWriteHandler(createFilename(clientMessage), clientMessage.getFileSize()));
            ctx.pipeline().addFirst(IN_OUT_OBJECT_HANDLER, new ChunkedWriteHandler());
        } else {
            serverMessage.setMessageType(MessageType.FILE_COPY_REJECT);
        }
        ctx.writeAndFlush(serverMessage.convertToJson());
    }

    private boolean checkout (MessageDTO clientMessage) {
        return serverHandler.fileIsExists(clientMessage.getFileName(), clientMessage.getFileDirectorySelectTo());
    }

    private String createFilename (MessageDTO clientMessage) {
        return  serverCloud.resolve(Paths.get(clientMessage.getFileDirectorySelectTo(),clientMessage.getFileName())).toString();
    }

    @Override
    public MessageType getCommand() {
        return MessageType.FILE_COPY;
    }

}
