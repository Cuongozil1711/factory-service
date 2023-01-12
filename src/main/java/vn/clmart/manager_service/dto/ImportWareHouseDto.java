package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImportWareHouseDto {
    private String code;
    private Long id;
    private Integer numberBox;
    private Integer quantity;
    private Double totalPrice;
    private Long idReceiptImport;
    private Long idItems;
    private String nameItems;
    private Date dateExpired;
    private Date createDate;
    private String createBy;
}
