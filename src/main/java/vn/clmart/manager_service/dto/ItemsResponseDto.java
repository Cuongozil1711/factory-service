package vn.clmart.manager_service.dto;

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
public class ItemsResponseDto {
    Long id;
    ReceiptImportWareHouse receiptImportWareHouse;
    Integer qualityImport;
    Integer qualityExport;
    Integer qualityCanceled;
    Date dateExpired;
}
