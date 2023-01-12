package vn.clmart.manager_service.model;

import lombok.*;
import org.springframework.context.annotation.Description;

import javax.persistence.Column;
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
@Description("Table xa/phuong")
public class Wards {
    @Id
    private Integer wardId;
    private Integer districtId;
    private String name;
}
