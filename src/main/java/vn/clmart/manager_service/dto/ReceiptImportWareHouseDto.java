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
public class ReceiptImportWareHouseDto {
    private String name;
    private String state;
    private String code;
    private Date dateImport;
    private Double totalPrice;
    private Long idWareHouse;
    private String imageReceipt;
    private Long idSupplier;
    private String type;
}
