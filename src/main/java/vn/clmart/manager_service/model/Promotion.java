package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.PromotionDto;
import vn.clmart.manager_service.dto.StallsDto;
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
@Description("Table khuyen mai")
public class Promotion extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String type;
    private String name;
    private Long idItems;
    private Date dateFrom;
    private Date dateEnd;
    private Integer percent;
    private Double price;
    private Long idItemsDonate;
    private Long idCondition;
    private String typePromotion; // loại khuyến mại

    public static Promotion of(PromotionDto promotionDto, Long cid, String uid){
        Promotion promotion = Promotion.builder()
                .code(promotionDto.getCode())
                .name(promotionDto.getName())
                .percent(promotionDto.getPercent())
                .idItems(promotionDto.getIdItems())
                .price(promotionDto.getPrice())
                .dateFrom(promotionDto.getStartDate())
                .dateEnd(promotionDto.getEndDate())
                .type(promotionDto.getType())
                .typePromotion(promotionDto.getTypePromotion())
                .build();
        promotion.setCreateBy(uid);
        promotion.setCompanyId(cid);
        return promotion;
    }
}
