package ru.gb.server.core.command;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import ru.gb.server.core.command.commandinterface.CommandService;
import ru.gb.server.core.commandhandler.ServerHandler;
import ru.gb.server.util.PropertyUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@CommandLine.Command
public class CopyFileFromServer implements CommandService {

    private final String IN_OUT_OBJECT_HANDLER = "ChunkedWriteHandler";
    private final Path serverCloud = Paths.get(PropertyUtils.getProperties("SERVER_DIRECTORY")).toAbsolutePath();
    private final ServerHandler serverHandler;

    public CopyFileFromServer(ServerHandler serverHandler) {
        this.serverHandler = serverHandler;
    }

    @Override
    public void processCommand(MessageDTO clientMessage, ChannelHandlerContext ctx) {

        try {
            ctx.pipeline().addFirst(IN_OUT_OBJECT_HANDLER , new ChunkedWriteHandler());
            ChunkedFile sendFileFromServer = new ChunkedFile(new File(createFilename(clientMessage)));

            ChannelFuture future = ctx.writeAndFlush(sendFileFromServer);
            future.addListener((ChannelFutureListener) channelFuture -> {
                ctx.pipeline().remove(IN_OUT_OBJECT_HANDLER);
                });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String createFilename (MessageDTO clientMessage) {
        return serverCloud.resolve(Paths.get(clientMessage.getFileDirectorySelectFrom(), clientMessage.getFileName())).toString();
    }

    @Override
    public MessageType getCommand() {
        return MessageType.FILE_COPY_FROM_SERVER;
    }

}
