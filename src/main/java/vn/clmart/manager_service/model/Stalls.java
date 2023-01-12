package vn.clmart.manager_service.model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.CustomerDto;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.dto.StallsDto;
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
@Description("Table quay hang")
public class Stalls extends PersistableEntity<Long>{
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private String address;
    private String image;

    public static Stalls of(StallsDto stallsDto, Long cid, String uid){
        Stalls stalls = Stalls.builder()
                .code(stallsDto.getCode())
                .name(stallsDto.getName())
                .address(stallsDto.getAddress())
                .image(stallsDto.getImage()).build();
        stalls.setCreateBy(uid);
        stalls.setCompanyId(cid);
        return stalls;
    }
}
