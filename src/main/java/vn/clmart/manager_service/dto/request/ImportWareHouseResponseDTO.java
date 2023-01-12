package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.model.ReceiptImportWareHouse;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImportWareHouseResponseDTO {
    private Long id;
    private String code;
    private Integer quantityItems;
    private Double totalPrice; // gia tien 1 item
    private Long idReceiptImport;
    private ReceiptImportWareHouse receiptImportWareHouse;
    private Date createDate;
    private Date updateDate;
    private String updateByName = "";
    private String receiptImportName = "";
    private String creatByName = "";
    private String itemName = "";
    private Integer deleteFlg;
}
