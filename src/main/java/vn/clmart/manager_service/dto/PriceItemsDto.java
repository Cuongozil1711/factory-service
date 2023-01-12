package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PriceItemsDto {
    private Long id;
    private Double priceItems;
    private String dvtCode;
    private Long idItems;
    private Integer quality;
}
