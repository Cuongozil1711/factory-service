package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.dto.ImportWareHouseDto;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table xuat kho")
public class ExportWareHouse extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String note;
    private Integer numberBox;
    private Integer quantity;
    private Double totalPrice;
    private Long idOrder;
    private Long idReceiptExport;
    private Long idReceiptImport;
    private Long idImportWareHouse;
    private Long idItems;
    private String dvtCode;

    public static ExportWareHouse of(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        ExportWareHouse exportWareHouse = ExportWareHouse.builder()
                .quantity(exportWareHouseDto.getQuantity())
                .totalPrice(exportWareHouseDto.getTotalPrice())
                .idImportWareHouse(exportWareHouseDto.getIdImportWareHouse())
                .id(exportWareHouseDto.getIdReceiptExport())
                .idItems(exportWareHouseDto.getIdItems())
                .idOrder(exportWareHouseDto.getIdOrder())
                .note(exportWareHouseDto.getNote())
                .dvtCode(exportWareHouseDto.getDvtCode())
                .idReceiptImport(exportWareHouseDto.getIdReceiptImport())
                .idReceiptExport(exportWareHouseDto.getIdReceiptExport())
                .build();
        exportWareHouse.setCreateBy(uid);
        exportWareHouse.setCompanyId(cid);
        return exportWareHouse;
    }
}
