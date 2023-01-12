package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.clmart.manager_service.dto.PositionDto;
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
public class Position extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String authority;
    private String name;

    public static Position of(Long cid, String uid, PositionDto positionDto){
        Position position = Position.builder()
                .code(positionDto.getCode())
                .name(positionDto.getName())
                .authority(positionDto.getAuthority())
                .build();
        position.setCreateBy(uid);
        position.setCompanyId(cid);
        return position;
    }
}
