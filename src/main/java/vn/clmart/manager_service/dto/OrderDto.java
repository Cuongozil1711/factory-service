package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderDto {
    private String name;
    private String code;
    private Long idCustomer;
    private String namePayment;
    private Long totalPriceCustomer;
    private List<DetailsItemOrderDto> detailsItemOrders;
}
