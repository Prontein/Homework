package ru.gb.client.core.commandhandler;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.client.core.controller.MainController;


public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private MainController mainController;

    public CommandHandler(MainController mainController) {
        this.mainController = mainController;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,String messageDTO) {
        MessageDTO messageFromServer = MessageDTO.convertFromJson(messageDTO);

        switch (messageFromServer.getMessageType()) {

            case SERVER_CATALOG -> {
                mainController.getServerPanelController().updateListServerCatalog(messageFromServer.getServerCatalogList());
                mainController.getServerPanelController().setServerDirectoryPath(messageFromServer.getFileDirectorySelectTo());
            }

            case FILE_COPY_ACCEPT -> {
                mainController.startCopy();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился");
    }
}
