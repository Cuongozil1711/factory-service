package vn.clmart.manager_service.dto;

import lombok.*;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class ItemsSearchDto{
    private Long idPubliser;
    private Long idStall;
    private Long idCategory;
    private Date startDate;
    private Date endDate;
}
