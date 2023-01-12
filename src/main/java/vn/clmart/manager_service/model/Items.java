package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ItemsDto;
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
@Description("Table mat hang")
public class Items extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private Double priceItem;
    private Long idCategory;
    private Long idPubliser;
    private Long idStall;
    private String image;
    private String state;
    private Double weight;

    public static Items of(ItemsDto itemsDto, Long cid, String uid){
        Items items = Items.builder()
                .code(itemsDto.getCode())
                .name(itemsDto.getName())
                .priceItem(itemsDto.getPriceItem())
                .idCategory(itemsDto.getIdCategory())
                .idPubliser(itemsDto.getIdPubliser())
                .weight(itemsDto.getWeight())
                .idStall(itemsDto.getIdStall()).build();
        items.setCreateBy(uid);
        items.setCompanyId(cid);
        return items;
    }
}
