package yoyo.inventory.common;

import org.springframework.stereotype.Service;



@Service
public  class InvoiceService {
    public String generate(String prefix , Long Id ) {
        return prefix + "-" +  Id;
    }
}
