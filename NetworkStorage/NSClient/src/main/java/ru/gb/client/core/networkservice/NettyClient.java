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
import ru.gb.client.core.controller.MainController;
import ru.gb.client.core.networkservice.networkInterface.ClientNetworkService;

import java.io.File;
import java.io.IOException;


public class NettyClient implements ClientNetworkService {

    private static final String HOST = "localhost";
    private static final int PORT = 8189;

    private MainController mainController;
    private SocketChannel channel;

    public NettyClient (MainController mainController) {
        this.mainController = mainController;
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
                                        .addLast(new CommandHandler(mainController));
                            }
                        });
                ChannelFuture future = bootstrap.connect(HOST, PORT).sync();
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

        channel.pipeline().addFirst("1", new BigFilesWriteHandler(clientPathDirectory + "/" + selectedFilename, fileSize));
        channel.pipeline().addFirst("2", new ChunkedWriteHandler());
        channel.writeAndFlush(messageDTO.convertToJson());
    }

    @Override
    public void startCopyFile(String path) {
        try {
            channel.pipeline().addLast("2", new ChunkedWriteHandler());
            ChunkedFile sendFile = new ChunkedFile(new File(path));
            ChannelFuture future = channel.writeAndFlush(sendFile);

            future.addListener((ChannelFutureListener) channelFuture -> {
                channel.pipeline().remove("2"); System.out.println("Finish write");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectWithServer() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageType(MessageType.SERVER_CATALOG);
        channel.writeAndFlush(messageDTO.convertToJson());
    }

    @Override
    public void upServerCatalog(String pathServerCatalog) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageType(MessageType.SERVER_CATALOG_UP);
        messageDTO.setServerCatalogDirectory(pathServerCatalog);
        channel.writeAndFlush(messageDTO.convertToJson());
    }

    @Override
    public void showSelectedCatalog(String path) {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessageType(MessageType.SERVER_CATALOG_SHOW_SELECTED);
        messageDTO.setFileDirectorySelectTo(path);
        channel.writeAndFlush(messageDTO.convertToJson());
    }

    @Override
    public void transferFile() {

    }

    @Override
    public void deleteFile() {

    }

    @Override
    public void readCommandResult() {

    }

    @Override
    public void closeConnection() {

    }
}
