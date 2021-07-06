package ru.gb.server.flyway;

import org.flywaydb.core.Flyway;
import ru.gb.server.flyway.flywayinterface.FlywayStarter;
import ru.gb.server.util.PropertyUtils;

public class FlywayMigration implements FlywayStarter {

    @Override
    public void start() {
        Flyway flyway = Flyway.configure().dataSource(PropertyUtils.getProperties("URL"),
                PropertyUtils.getProperties("USER"), PropertyUtils.getProperties("PASSWORD")).load();
        flyway.migrate();
    }

}
