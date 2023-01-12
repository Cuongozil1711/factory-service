package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class BillDto {
    private Long id;
    private String code;
    private String state;
    private Long totalPrice;
    private Long totalPriceCustomer;
    private Long idOrder;
    private Long idCustomer;
    private String namePayment;
}
