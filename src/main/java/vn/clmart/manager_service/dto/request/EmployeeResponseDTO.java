package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import vn.clmart.manager_service.dto.FullNameDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmployeeResponseDTO {
    private Long id;
    private String code;
    private String tel;
    private FullNameDto fullNameDto;
    private String namePosition;
    private String state;
}
