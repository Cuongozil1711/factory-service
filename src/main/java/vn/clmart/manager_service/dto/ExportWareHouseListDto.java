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
public class ExportWareHouseListDto {
    private String name;
    private String code;
    private Long idReceiptExport;
    private ReceiptExportWareHouseDto receiptExportWareHouseDto;
    private String createByName;
    private List<DetailsItemOrderDto> data;
}
