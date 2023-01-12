package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.PublisherDto;
import vn.clmart.manager_service.dto.WareHouseDto;
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
@Description("Table kho hang")
public class WareHouse extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private String address;

    public static WareHouse of(WareHouseDto wareHouseDto, Long cid, String uid){
        WareHouse wareHouse = WareHouse.builder()
                .code(wareHouseDto.getCode())
                .name(wareHouseDto.getName())
                .address(wareHouseDto.getAddress()).build();
        wareHouse.setCreateBy(uid);
        wareHouse.setCompanyId(cid);
        return wareHouse;
    }
}
