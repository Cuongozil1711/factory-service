package vn.clmart.manager_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.model.Condition;
import vn.clmart.manager_service.model.ItemsDonate;
import vn.clmart.manager_service.model.Promotion;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class PromotionResponseDto {
    private Promotion promotion;
    private Condition condition;
    private ItemsDonate itemsDonate;
}
