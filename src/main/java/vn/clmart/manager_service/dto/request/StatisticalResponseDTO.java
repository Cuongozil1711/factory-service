package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class StatisticalResponseDTO {
    private Integer sumQuality;
    private Long sumPriceNow;
    private Long sumPriceAfter;
    private Integer sumNow;
    private Integer totalImport;
    private Integer totalExport;
}
