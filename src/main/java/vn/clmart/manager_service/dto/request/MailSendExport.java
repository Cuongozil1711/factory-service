package vn.clmart.manager_service.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MailSendExport {
    private String companyName;
    private String employeeExport;
    private Date dateExport;
    private String codeExport;
    private String companyNameTo;
    private String status;
}
