package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.dto.BillDto;
import vn.clmart.manager_service.dto.CustomerDto;
import vn.clmart.manager_service.dto.DetailsItemOrderDto;
import vn.clmart.manager_service.model.DetailsItemOrder;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderItemResponseDTO {
    private Long id;
    private String name;
    private String code;
    private CustomerDto customerDto;
    private Integer quantity;
    private Double totalPrice;
    private Double totalSale;
    private Integer deleteFlg;
    private List<DetailsItemOrderDto> detailsItemOrders;
    private List<DetailsItemOrderDto> detailsItemOrdersSale;
    private Date createDate;
    private String createBy;
    private BillDto billDto;
    private String reasonName;
}
