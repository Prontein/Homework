package ru.gb.server.core.comanddictionary.dictionaryservice;

import domain.MessageDTO;
import io.netty.channel.ChannelHandlerContext;

public interface CommandDictionaryService {
    void processCommand(MessageDTO messageDTO, ChannelHandlerContext ctx);
}
