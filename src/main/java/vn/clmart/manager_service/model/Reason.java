package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.ReasonDto;
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
@Description("Table ly do huy hang")
public class Reason extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;

    public static Reason of(ReasonDto reasonDTO, Long cid, String uid){
        Reason reason = Reason.builder()
                .code(reasonDTO.getCode())
                .name(reasonDTO.getName()).build();
        reason.setCreateBy(uid);
        reason.setCompanyId(cid);
        return reason;
    }
}
