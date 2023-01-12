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
@Description("Table tinh thanh")
public class Province {
    @Id
    private Integer provinceId;
    private String name;
}
