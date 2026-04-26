package yoyo.inventory.common;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class InvoiceGeneratorService {
    public String generate(String prefix) {
        return prefix + "-" +
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) +
                "-" + System.currentTimeMillis();
    }
}
