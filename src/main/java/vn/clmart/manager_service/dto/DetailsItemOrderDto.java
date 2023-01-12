package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class DetailsItemOrderDto {
    private String dvtCode;
    private Integer quality;
    private Integer numberBox;
    private Double totalSale; // gia tien theo don vi tinh * quality thuc te
    private Double totalPrice; // gia tien theo don vi tinh * quality (gia goc)
    private Long idItems;
    private Long idReceiptImport;
    private Long idImportWareHouse;
    private Long idOrder;
    private Long idPromotion;
    private Date createDate;
    private Long id;
    private ItemsResponseDTO itemsResponseDTO;
}
