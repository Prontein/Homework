package ru.gb.client.core.commandhandler.CommandDictionary.dictionaryinterface;

import domain.MessageDTO;
import domain.MessageType;
import ru.gb.client.core.controller.MainController;

public interface CommandDictionaryService {
    void processCommand (MessageDTO messageDTO, MainController controller);
}
