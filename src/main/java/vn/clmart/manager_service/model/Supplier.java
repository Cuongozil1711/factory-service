package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.SupplierDto;
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
@Description("Table nha cung cap")
public class Supplier extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private String address;
    private String phone;

    public static Supplier of(SupplierDto supplierDto, Long cid, String uid){
        Supplier supplier = Supplier.builder()
                .name(supplierDto.getName())
                .address(supplierDto.getAddress())
                .phone(supplierDto.getName())
                .build();
        supplier.setCompanyId(cid);
        supplier.setCreateBy(uid);
        return supplier;
    }
}
