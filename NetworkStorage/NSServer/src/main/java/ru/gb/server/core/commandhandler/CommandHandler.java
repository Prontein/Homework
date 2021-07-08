package ru.gb.server.core.commandhandler;

import domain.*;
import io.netty.channel.*;
import ru.gb.server.core.comanddictionary.dictionaryservice.CommandDictionaryService;


public class CommandHandler extends SimpleChannelInboundHandler<String> {

    private final CommandDictionaryService dictionaryService;

    public CommandHandler(CommandDictionaryService dictionaryService) {
        this.dictionaryService = dictionaryService;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String messageDTO) {
        MessageDTO messageFromClient = MessageDTO.convertFromJson(messageDTO);
        dictionaryService.processCommand(messageFromClient, ctx);
    }
}
