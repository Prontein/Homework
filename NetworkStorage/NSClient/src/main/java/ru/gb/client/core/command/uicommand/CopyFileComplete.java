package ru.gb.client.core.command.uicommand;

import domain.MessageDTO;
import domain.MessageType;
import ru.gb.client.core.command.commandservice.CommandService;
import ru.gb.client.core.controller.MainController;

@CommandClient
public class CopyFileComplete implements CommandService {
    @Override
    public void processCommand(MessageDTO serverMessage, MainController controller) {
        controller.refreshClientCatalog();
    }

    @Override
    public MessageType getCommand() {
        return MessageType.FILE_COPY_COMPLETE;
    }
}

