package yoyo.inventory.dto.request;

import lombok.Data;
import yoyo.inventory.enums.CustomerType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerRequest {

    private String fullName;

    private String gender;

    private LocalDate birthDate;

    private String phone;

    private String email;

    private String address;

    private String city;

    private String country;

    private CustomerType customerType;

    private BigDecimal creditLimit;

    private String note;
}