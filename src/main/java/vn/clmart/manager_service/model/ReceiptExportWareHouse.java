package vn.clmart.manager_service.model;


import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ReceiptExportWareHouseDto;
import vn.clmart.manager_service.dto.ReceiptImportWareHouseDto;
import vn.clmart.manager_service.model.config.PersistableEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table phieu xuat kho")
public class ReceiptExportWareHouse extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private Date dateExport;
    private String name;
    private String state;
    private String code;
    private Double totalPrice;
    private Long idWareHouse;
    private Long companyIdTo;
    private Long idWareHouseTo;

    public static ReceiptExportWareHouse of(ReceiptExportWareHouseDto receiptExportWareHouseDto, Long cid, String uid){
        ReceiptExportWareHouse receiptExportWareHouse = ReceiptExportWareHouse.builder()
                .code(receiptExportWareHouseDto.getCode())
                .name(receiptExportWareHouseDto.getName())
                .state(receiptExportWareHouseDto.getState())
                .dateExport(receiptExportWareHouseDto.getDateExport())
                .totalPrice(receiptExportWareHouseDto.getTotalPrice())
                .idWareHouse(receiptExportWareHouseDto.getIdWareHouse())
                .companyIdTo(receiptExportWareHouseDto.getCompanyIdTo())
                .idWareHouseTo(receiptExportWareHouseDto.getIdWareHouseTo())
                .build();
        receiptExportWareHouse.setCreateBy(uid);
        receiptExportWareHouse.setCompanyId(cid);
        return receiptExportWareHouse;
    }
}
