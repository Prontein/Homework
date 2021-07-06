package ru.gb.server.core.command.commandinterface;

import domain.MessageDTO;
import domain.MessageType;
import io.netty.channel.ChannelHandlerContext;

public interface CommandService {
    void processCommand (MessageDTO messageDTO, ChannelHandlerContext ctx);
    MessageType getCommand();
}
