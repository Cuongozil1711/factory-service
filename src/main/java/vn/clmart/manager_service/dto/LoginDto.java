package vn.clmart.manager_service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class LoginDto {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long companyId;
    private String uId;
    private String role;

    private String fullName;

    public LoginDto(String accessToken, Long companyId, String uId, String role, String fullName) {
        this.setAccessToken(accessToken);
        this.companyId = companyId;
        this.uId = uId;
        this.role = role;
        this.fullName = fullName;
    }
}
