package ru.gb.server.core.commandhandler;

import domain.*;
import io.netty.channel.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.handler.stream.ChunkedWriteHandler;
import ru.gb.server.database.DatabaseHandler;
import ru.gb.server.factory.Factory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;


public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private ServerHandler serverOperator = Factory.getServerHandler();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String messageDTO) {
        MessageDTO messageFromClient = MessageDTO.convertFromJson(messageDTO);

        switch (messageFromClient.getMessageType()) {

            case SERVER_CATALOG ->  {
                MessageDTO serverMessage = new MessageDTO();
                showServerCatalog(serverMessage);
                ctx.writeAndFlush(serverMessage.convertToJson());
            }

            case FILE_COPY -> {
//                if (DatabaseHandler.fileIsExists(messageFromClient.getFileName(), messageFromClient.getFileDirectorySelectTo())) {
                if (serverOperator.fileIsExists(messageFromClient.getFileName(), messageFromClient.getFileDirectorySelectTo())) {
                    MessageDTO copyMessage = new MessageDTO();
                    copyMessage.setMessageType(MessageType.FILE_COPY_ACCEPT);
                    ctx.writeAndFlush(copyMessage.convertToJson());
                    ctx.pipeline().addFirst("1", new BigFilesWriteHandler(messageFromClient.getFileDirectorySelectTo() + "/" +
                            messageFromClient.getFileName(), messageFromClient.getFileSize()));
                    ctx.pipeline().addFirst("2", new ChunkedWriteHandler());
                } else System.out.println("Такой файл уже есть"); // TODO : Отправить ответ на клиента
            }

            case FILE_COPY_FROM_SERVER -> {
                startCopyFile(ctx, messageFromClient);
            }

            case SERVER_CATALOG_UP -> {
                MessageDTO serverMessage = new MessageDTO();
                serverMessage.setMessageType(MessageType.SERVER_CATALOG);
                serverMessage.setFileDirectorySelectTo(serverOperator.getServerCatalogPath(messageFromClient.getServerCatalogDirectory()).toString());
                serverMessage.setServerCatalogList(serverOperator.serverCatalogUp(messageFromClient.getServerCatalogDirectory()));
                ctx.writeAndFlush(serverMessage.convertToJson());
            }

            case SERVER_CATALOG_SHOW_SELECTED -> {
                MessageDTO serverMessage = new MessageDTO();
                serverMessage.setMessageType(MessageType.SERVER_CATALOG);
                serverMessage.setFileDirectorySelectTo(messageFromClient.getFileDirectorySelectTo());
                serverMessage.setServerCatalogList(serverOperator.showMyCatalog(Paths.get(messageFromClient.getFileDirectorySelectTo())));
                ctx.writeAndFlush(serverMessage.convertToJson());
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");
    }

    public void startCopyFile(ChannelHandlerContext ctx, MessageDTO messageFromClient) {
        try {
            ctx.pipeline().addFirst("2" , new ChunkedWriteHandler());
            ChunkedFile sendFileFromServer = new ChunkedFile(new File(messageFromClient.getFileDirectorySelectFrom() + "/" + messageFromClient.getFileName()));
            ChannelFuture future = ctx.writeAndFlush(sendFileFromServer);
            future.addListener((ChannelFutureListener) channelFuture -> {
                ctx.pipeline().remove("2");
                System.out.println("Finish write");});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showServerCatalog (MessageDTO serverMessage) {
        serverMessage.setMessageType(MessageType.SERVER_CATALOG);
        serverMessage.setFileDirectorySelectTo(serverOperator.getServerCatalogPath().toString());
        serverMessage.setServerCatalogList(serverOperator.showMyCatalog());
    }
}
