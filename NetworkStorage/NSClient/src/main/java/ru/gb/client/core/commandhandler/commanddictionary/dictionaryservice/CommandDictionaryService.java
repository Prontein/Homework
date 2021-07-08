package ru.gb.client.core.commandhandler.commanddictionary.dictionaryservice;

import domain.MessageDTO;
import ru.gb.client.core.controller.MainController;

public interface CommandDictionaryService {
    void processCommand(MessageDTO messageDTO, MainController controller);
}
