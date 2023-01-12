package vn.clmart.manager_service.model;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.context.annotation.Description;
import vn.clmart.manager_service.dto.PublisherDto;
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
@Description("Table nha san xuat")
public class Publisher extends PersistableEntity<Long> {
    @Id
    @GenericGenerator(name = "id",strategy = "vn.clmart.manager_service.generator.SnowflakeId")
    @GeneratedValue(generator = "id")
    private Long id;
    private String code;
    private String address;
    private String name;

    public static Publisher of(PublisherDto publisherDto, Long cid, String uid){
        Publisher publisher = Publisher.builder()
                .code(publisherDto.getCode())
                .name(publisherDto.getName())
                .address(publisherDto.getAddress()).build();
        publisher.setCreateBy(uid);
        publisher.setCompanyId(cid);
        return publisher;
    }
}
