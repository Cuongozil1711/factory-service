package vn.clmart.manager_service.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ExportWareHouseDto {
    private Integer numberBox = 1;
    private Integer quantity;
    private Double totalPrice; // số lượng trên 1 đơn vị
    private Long idReceiptExport;
    private Long idReceiptImport;
    private Long idImportWareHouse;
    private Long idOrder;
    private Long idItems;
    private String note;
    private String dvtCode;
}
