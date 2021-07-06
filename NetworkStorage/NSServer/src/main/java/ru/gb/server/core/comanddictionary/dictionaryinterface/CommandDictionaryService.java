package ru.gb.server.core.comanddictionary.dictionaryinterface;

import domain.MessageDTO;
import io.netty.channel.ChannelHandlerContext;

public interface CommandDictionaryService {
    void processCommand (MessageDTO messageDTO, ChannelHandlerContext ctx);
}
