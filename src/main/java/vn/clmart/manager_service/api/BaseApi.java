package vn.clmart.manager_service.api;

import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.User;
import vn.clmart.manager_service.repository.EmployeeRepository;
import vn.clmart.manager_service.untils.Constants;

public class BaseApi {
    @Autowired
    EmployeeRepository employeeRepository;

    public final Employee getUser(Long cid) {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                String userCd = auth.getName();
                return employeeRepository.findAllByIdUserAndDeleteFlg(userCd, Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElseThrow();
            }
            return new Employee();
        } catch (Exception ex) {
            return new Employee();
        }
    }
}
