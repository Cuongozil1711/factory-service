package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.dto.CustomerDto;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class OrderResponseDTO {
    private Long id;
    private String name;
    private String code;
    private CustomerDto customerDto;
    private Integer quantity;
    private Double totalPrice;
    private Integer deleteFlg;
    private Date createDate;
    private String reasonName;
}
