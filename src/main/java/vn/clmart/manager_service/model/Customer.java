package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.AddressDto;
import vn.clmart.manager_service.dto.CustomerDto;
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
@Description("Table khach hang")
public class Customer extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String name;
    private String address;
    private String tel;

    public static Customer of(CustomerDto customerDto, Long cid, String uid){
        Customer customer = Customer.builder()
                .code(customerDto.getCode())
                        .name(customerDto.getName())
                                .address(customerDto.getAddress())
                                        .tel(customerDto.getTel()).build();

        customer.setCreateBy(uid);
        customer.setCompanyId(cid);
        return customer;
    }
}
