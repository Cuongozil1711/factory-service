package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReceiptExportWareHouseDto {
    private Long id;
    private Date dateExport;
    private String state;
    private String name;
    private String code;
    private Double totalPrice;
    private Long idWareHouse;
    private Long companyIdTo;
    private Long idWareHouseTo;
    private String fullName;
    private String companyName;
    private String companyNameTo;
    private String wareHouseName;
    private String wareHouseNameTo;
}
