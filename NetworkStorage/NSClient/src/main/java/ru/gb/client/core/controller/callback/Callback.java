package ru.gb.client.core.controller.callback;

import domain.MessageDTO;

public interface Callback {
    void callback (MessageDTO serverMessage);
}
