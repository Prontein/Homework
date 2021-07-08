package ru.gb.server;

import org.flywaydb.core.Flyway;
import ru.gb.server.factory.Factory;

public class Main {
    public static void main(String[] args) {
        Factory.getFlyway().start();
        Factory.getServerService().startServer();
    }
}
