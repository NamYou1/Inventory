package yoyo.inventory.common;

import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.TimeZone;

@Configuration
public class GlobalDateTimeConfig {

    public static final String APP_TIME_ZONE = "Asia/Phnom_Penh";

    @PostConstruct
    public void setUpTimeZone() {
        // Set JVM default timezone for all date/time APIs used in the app.
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of(APP_TIME_ZONE)));
    }
}

