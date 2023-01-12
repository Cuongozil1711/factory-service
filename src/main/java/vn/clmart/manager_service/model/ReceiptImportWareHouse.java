package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ReceiptImportWareHouseDto;
import vn.clmart.manager_service.dto.StallsDto;
import vn.clmart.manager_service.model.config.PersistableEntity;
import vn.clmart.manager_service.untils.Constants;

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
@Description("Table phieu nhap kho")
public class ReceiptImportWareHouse extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private String state;
    private String code;
    private Date dateImport;
    private Double totalPrice;
    private Long idWareHouse;
    private String imageReceipt;
    private Long idSupplier;
    private String type;

    public static ReceiptImportWareHouse of(ReceiptImportWareHouseDto receiptImportWareHouseDto, Long cid, String uid){
        ReceiptImportWareHouse receiptImportWareHouse = ReceiptImportWareHouse.builder()
                .code(receiptImportWareHouseDto.getCode())
                .name(receiptImportWareHouseDto.getName())
                .state(receiptImportWareHouseDto.getState())
                .dateImport(receiptImportWareHouseDto.getDateImport())
                .totalPrice(receiptImportWareHouseDto.getTotalPrice())
                .idWareHouse(receiptImportWareHouseDto.getIdWareHouse())
                .idSupplier(receiptImportWareHouseDto.getIdSupplier())
                .type(Constants.RECEIPT_TYPE_WARE_HOUSE.BASIC.name())
                .build();
        receiptImportWareHouse.setCreateBy(uid);
        receiptImportWareHouse.setCompanyId(cid);
        return receiptImportWareHouse;
    }
}
