package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.dto.PromotionResponseDto;
import vn.clmart.manager_service.model.Order;
import vn.clmart.manager_service.model.ReceiptExportWareHouse;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExportWareHouseResponseDTO {
    private Long id;
    private String code;
    private Integer numberBox;
    private Integer quantity;
    private Double totalPrice; // gia tien 1 item
    private Long idReceiptExport;
    private Integer quantityItems;
    private ReceiptExportWareHouse receiptExportWareHouse;
    private List<PromotionResponseDto> promotionResponseDtoList;
    private Order order;
    private Long idItems;
    private Date createDate;
    private String dvtCode;
    private Date updateDate;
    private String updateByName;
    private String itemsName = "";
    private String receiptExportName;
    private String creatByName = "";
    private String image;
    private Double totalPriceImport;
}
