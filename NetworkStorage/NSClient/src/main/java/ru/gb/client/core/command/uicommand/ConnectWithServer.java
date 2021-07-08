package ru.gb.client.core.command.uicommand;

import domain.MessageDTO;
import domain.MessageType;
import ru.gb.client.core.command.commandservice.CommandService;
import ru.gb.client.core.controller.MainController;
import ru.gb.client.core.controller.PanelController;

@CommandClient
public class ConnectWithServer implements CommandService {
    @Override
    public void processCommand(MessageDTO serverMessage, MainController controller) {
        PanelController serverPanelController = controller.getServerPanelController();
        serverPanelController.updateListServerCatalog(serverMessage.getServerCatalogList());
        serverPanelController.setServerDirectoryPath(serverMessage.getFileDirectorySelectTo());
    }

    @Override
    public MessageType getCommand() {
        return MessageType.SERVER_CATALOG;
    }
}
