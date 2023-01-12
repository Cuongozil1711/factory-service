package vn.clmart.manager_service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Description("Table don vi tinh")
public class DvtCode {
    @Id
    private String dvtCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String quality;
    private String name;
}
