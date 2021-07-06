package ru.gb.client.core.command.uicommand;

import domain.MessageDTO;
import domain.MessageType;
import domain.allertsandexeption.AlertMessage;
import javafx.application.Platform;
import org.apache.logging.log4j.core.tools.picocli.CommandLine;
import ru.gb.client.core.command.commandinterface.CommandService;
import ru.gb.client.core.controller.MainController;

@CommandLine.Command
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

