package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import vn.clmart.manager_service.dto.FullNameDto;
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
public class FullName extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String firstName;
    private String lastName;

    public static FullName of(FullNameDto dto, Long companyId, String uId){
        FullName fullName = FullName.builder()
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .build();
        fullName.setCompanyId(companyId);
        fullName.setCreateBy(uId);
        return fullName;
    }
}
