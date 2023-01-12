package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.AddressDto;
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
@Description("Table dia chi")
public class Address extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String name;
    private Integer provinceId;
    private Integer districtId;
    private Integer wardId;

    public static Address of(AddressDto addressDto, Long cid, String uid){
        Address address = Address.builder()
                .districtId(addressDto.getDistrictId())
                .provinceId(addressDto.getProvinceId())
                .wardId(addressDto.getWardId())
                .name(addressDto.getName())
                .build();
        address.setCreateBy(uid);
        address.setCompanyId(cid);
        return address;
    }
}
