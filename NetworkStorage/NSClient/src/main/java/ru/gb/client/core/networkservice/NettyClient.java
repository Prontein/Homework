package ru.gb.client.core.networkservice;

import domain.BigFilesWriteHandler;
import domain.MessageDTO;
import domain.MessageType;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.client.core.commandhandler.CommandHandler;
import ru.gb.client.core.controller.callback.Callback;
import ru.gb.client.core.networkservice.networkInterface.ClientNetworkService;
import ru.gb.client.core.util.PropertyUtils;
import java.io.File;
import java.io.IOException;


public class NettyClient implements ClientNetworkService {

    private final String IN_OUT_OBJECT_HANDLER = "ChunkedWriteHandler";
    private final String BIF_FILE_HANDLER = "BigFilesWriteHandler";

    private SocketChannel channel;
    private final Callback messageFromServer;

    public NettyClient (Callback callback) {
        this.messageFromServer = callback;
    }

    @Override
    public void start() {
        Thread t = new Thread(() -> {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();
            try {
                Bootstrap bootstrap = new Bootstrap();
                bootstrap.group(workerGroup)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) {
                                channel = socketChannel;
                                channel.config().setRecvByteBufAllocator(new FixedRecvByteBufAllocator(9048));
                                socketChannel.pipeline()
                                        .addLast(new StringEncoder())
                                        .addLast(new StringDecoder())
                                        .addLast(new CommandHandler(messageFromServer));
                            }
                        });
                ChannelFuture future = bootstrap.connect(PropertyUtils.getProperties("HOST"), Integer.parseInt(PropertyUtils.getProperties("PORT"))).sync();
                future.channel().closeFuture().sync();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                workerGroup.shutdownGracefully();
            }
        });
        t.start();
    }

    @Override
    public void copyFile(String filename, String path, Long fileSize) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageType(MessageType.FILE_COPY);
        messageDTO.setFileName(filename);
        messageDTO.setFileSize(fileSize);
        messageDTO.setFileDirectorySelectTo(path);

        channel.writeAndFlush(messageDTO.convertToJson());
    }

    @Override
    public void copyFileFromServer(String selectedFilename, String clientPathDirectory, String serverPathDirectory, Long fileSize) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageType(MessageType.FILE_COPY_FROM_SERVER);
        messageDTO.setFileDirectorySelectTo(clientPathDirectory);
        messageDTO.setFileName(selectedFilename);
        messageDTO.setFileDirectorySelectFrom(serverPathDirectory);

        channel.pipeline().addFirst(BIF_FILE_HANDLER, new BigFilesWriteHandler(clientPathDirectory + "\\" + selectedFilename, fileSize));
        channel.pipeline().addFirst(IN_OUT_OBJECT_HANDLER, new ChunkedWriteHandler());
        channel.writeAndFlush(messageDTO.convertToJson());
    }

    @Override
    public void startCopyFile(String path) {
        try {
            channel.pipeline().addLast(IN_OUT_OBJECT_HANDLER, new ChunkedWriteHandler());
            ChunkedFile sendFile = new ChunkedFile(new File(path));
            ChannelFuture future = channel.writeAndFlush(sendFile);

            future.addListener((ChannelFutureListener) channelFuture -> {
                channel.pipeline().remove(IN_OUT_OBJECT_HANDLER);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectWithServer(String login, String password) {
        MessageDTO clientMessage = new MessageDTO();
        clientMessage.setLogin(login);
        clientMessage.setPassword(password);
        clientMessage.setMessageType(MessageType.AUTHORIZATION);

        channel.writeAndFlush(clientMessage.convertToJson());
    }

    @Override
    public void registrationOnServer(String login, String password) {
        MessageDTO clientMessage = new MessageDTO();
        clientMessage.setLogin(login);
        clientMessage.setPassword(password);
        clientMessage.setMessageType(MessageType.REGISTRATION);

        channel.writeAndFlush(clientMessage.convertToJson());
    }

    @Override
    public void upServerCatalog(String pathServerCatalog) {
        MessageDTO clientMessage = new MessageDTO();
        clientMessage.setMessageType(MessageType.SERVER_CATALOG_UP);
        clientMessage.setServerCatalog(pathServerCatalog);

        channel.writeAndFlush(clientMessage.convertToJson());
    }

    @Override
    public void showSelectedCatalog(String path, String catalogName) {
        MessageDTO clientMessage = new MessageDTO();
        clientMessage.setMessageType(MessageType.SERVER_CATALOG_SHOW_SELECTED);
        clientMessage.setFileDirectorySelectTo(path);
        clientMessage.setCatalogName(catalogName);

        channel.writeAndFlush(clientMessage.convertToJson());
    }

    @Override
    public void closeConnection() {

    }
}
