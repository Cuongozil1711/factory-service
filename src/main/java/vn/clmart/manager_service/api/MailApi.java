package vn.clmart.manager_service.api;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.request.MailSendExport;
import vn.clmart.manager_service.service.MailService;

import java.util.Map;

@RestController
@RequestMapping("/email")
public class MailApi {

    @Autowired
    MailService mailService;

    @PostMapping("/send")
    protected @ResponseBody
    ResponseEntity<Object> sendEmail(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody Map<String, Object> body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            return new ResponseEntity<>(mailService.sendEmailStatusExport(new MailSendExport(), null), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
