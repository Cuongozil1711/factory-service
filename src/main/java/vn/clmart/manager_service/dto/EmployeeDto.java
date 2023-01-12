package vn.clmart.manager_service.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class EmployeeDto {
    private Long id;
    private UserLoginDto userLoginDto;
    private AddressDto addressDto;
    private FullNameDto fullNameDto;
    private String idUser;
    private String tel;
    private String cmt;
    private Long idPosition;
    private String namePosition;
    private Integer status;
    private Date birthDay;
    private String image;
    private Long cid;
    private String companyName;
}
