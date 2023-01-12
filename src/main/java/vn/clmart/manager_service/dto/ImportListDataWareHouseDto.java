package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ImportListDataWareHouseDto {
    private String code;
    private Long idReceiptImport;
    private String imageReceipt;
    private ReceiptImportWareHouseDto receiptImportWareHouseDto;
    private List<ImportWareHouseDto> data;
    private String createByName;
}
