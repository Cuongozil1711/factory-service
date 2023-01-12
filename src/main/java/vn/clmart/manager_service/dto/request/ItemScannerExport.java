package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.dto.PriceItemsDto;
import vn.clmart.manager_service.dto.PromotionResponseDto;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ItemScannerExport {
    private Long id;
    private String code;
    private String name;
    private Double priceItem;
    private Long idCategory;
    private Long idPubliser;
    private Long idStall;
    private String image;
    private Long totalSold;
    private Long totalInWareHouse;
    private Long idImportWareHouse;
    private Integer numberBox;
    private Integer quantity;
    private Double totalPrice; // gia tien 1 item
    private Integer qualityExport;
    private List<PriceItemsDto> priceItemsDtos;
    private List<PromotionResponseDto> promotionResponseDto;
}
