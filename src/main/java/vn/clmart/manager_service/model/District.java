package vn.clmart.manager_service.model;

import lombok.*;
import org.springframework.context.annotation.Description;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Description("Table quan/huyen")
public class District {
    @Id
    private Integer districtId;
    private Integer provinceId;
    private String name;
}
