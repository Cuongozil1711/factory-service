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
public class ItemsDto {
    private String code;
    private String name;
    private Double priceItem;
    private Long idCategory;
    private Long idPubliser;
    private Long idWareHouse;
    private Long idStall;
    private String state;
    private List<PriceItemsDto> priceItemsDtos;
    private Double weight;
}
