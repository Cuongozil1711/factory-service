package vn.clmart.manager_service.config.schedule;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import vn.clmart.manager_service.dto.TokenFirseBaseDTO;
import vn.clmart.manager_service.model.Company;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.TokenFireBase;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.service.NotificationService;
import vn.clmart.manager_service.service.UserService;
import vn.clmart.manager_service.untils.Constants;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
@EnableScheduling
public class SpringConfig {

    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenFireBaseRepository tokenFireBaseRepository;

    @Autowired
    OrderRepositorry orderRepositorry;

    @Autowired
    ImportWareHouseRepository importWareHouseRepository;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserService userService;

    @Scheduled(cron = "00 15 22 ? * *")
    public void scheduleFixedDelayTask() throws IOException {
        System.out.println(
                "Fixed delay task - " + System.currentTimeMillis() / 1000);

        // gui thong bao cho quan ly chi nhanh
        TokenFirseBaseDTO tokenFirseBaseDTO = new TokenFirseBaseDTO();
        tokenFirseBaseDTO.setPriority("high");
        String[] token = new String[10];

        for(Company company: companyRepository.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE)){
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new Date());
            Integer sumOrder = orderRepositorry.getCountByDate(company.getId(), Constants.DELETE_FLG.NON_DELETE, calendar.getTime());
            Integer sumImport = importWareHouseRepository.getCountByDate(company.getId(), Constants.DELETE_FLG.NON_DELETE, calendar.getTime());
            Integer sumExport = exportWareHouseRepository.getCountByDate(company.getId(), Constants.DELETE_FLG.NON_DELETE, calendar.getTime());

            Set<Employee> employees = userService.getUserMappingLeader(company.getId()).stream().collect(Collectors.toSet());

            employees.forEach(e -> {
                Map<String, String> notification = new HashMap<>();
                notification.put("body",  calendar.get(Calendar.DATE) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR) + ": " + sumOrder + " đơn hàng " +
                        sumImport + " nhập " + sumExport + " xuất");
                notification.put("title", "Thông báo");
                tokenFirseBaseDTO.setNotification(notification);
                TokenFireBase tokenFireBase = tokenFireBaseRepository.findByDeleteFlgAndUserId(Constants.DELETE_FLG.NON_DELETE, e.getIdUser()).orElse(null);
                if(tokenFireBase != null){
                    token[0] = tokenFireBase.getToken();
                }
                tokenFirseBaseDTO.setRegistration_ids(token);

                ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                String json = null;
                try {
                    json = ow.writeValueAsString(tokenFirseBaseDTO);
                } catch (JsonProcessingException ex) {
                    ex.printStackTrace();
                }

                try {
                    notificationService.sendNotificationToUser(json);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

}
