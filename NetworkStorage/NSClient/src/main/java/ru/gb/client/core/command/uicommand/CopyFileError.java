package ru.gb.client.core.command.uicommand;

import domain.MessageDTO;
import domain.MessageType;
import domain.allertsandexeption.AlertMessage;
import javafx.application.Platform;
import ru.gb.client.core.command.commandservice.CommandService;
import ru.gb.client.core.controller.MainController;

@CommandClient
public class CopyFileError implements CommandService {
    @Override
    public void processCommand(MessageDTO serverMessage, MainController controller) {
        Platform.runLater(() -> controller.typeAlert(AlertMessage.COPY_ERROR));
    }

    @Override
    public MessageType getCommand() {
        return MessageType.FILE_COPY_REJECT;
    }
}

