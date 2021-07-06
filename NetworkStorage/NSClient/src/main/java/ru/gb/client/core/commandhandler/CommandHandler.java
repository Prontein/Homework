package ru.gb.client.core.commandhandler;

import domain.MessageDTO;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import ru.gb.client.core.controller.callback.Callback;


public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private final Callback messageFromServer;

    public CommandHandler(Callback messageFromServer) {
        this.messageFromServer = messageFromServer;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx,String messageDTO) {
        MessageDTO serverMessage = MessageDTO.convertFromJson(messageDTO);

        if (messageFromServer != null) {
            messageFromServer.callback(serverMessage);
        }
    }
}
