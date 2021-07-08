package ru.gb.client.core.command.commandservice;

import domain.MessageDTO;
import domain.MessageType;
import ru.gb.client.core.controller.MainController;

public interface CommandService {

    void processCommand (MessageDTO messageDTO, MainController controller);
    MessageType getCommand();
}
