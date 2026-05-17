package yoyo.inventory.dto.response;

import lombok.Builder;
import lombok.Data;
import yoyo.inventory.enums.CustomerStatus;
import yoyo.inventory.enums.CustomerType;

import java.math.BigDecimal;

@Data
@Builder
public class CustomerResponse {

    private Long id;

    private String customerCode;

    private String fullName;

    private String phone;

    private String email;

    private String address;

    private CustomerType customerType;

    private CustomerStatus status;

    private BigDecimal creditLimit;

    private BigDecimal currentDebt;

    private Integer rewardPoint;
}