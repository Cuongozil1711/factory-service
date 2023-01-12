package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ReceiptExportWareHouseResponseDTO {
    private Long id;
    private String name;
    private String state;
    private String code;
    private Date dateExport;
    private Double totalPrice;
    private Long idWareHouse;
    private String nameWareHouse;
    private String stateName;
    private Long companyIdTo;
    private Long idWareHouseTo;
}
