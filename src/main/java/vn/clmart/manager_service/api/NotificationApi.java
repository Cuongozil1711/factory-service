package vn.clmart.manager_service.api;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.NotificationService;
import java.util.*;

@RestController
@RequestMapping("/fcm")
public class NotificationApi {

    @Autowired
    NotificationService notificationService;

    @PostMapping("/send")
    protected @ResponseBody
    ResponseEntity<Object> testNotificaion(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody Map<String, Object> body) {
        try {
            JSONObject jsonObject = new JSONObject(body);
            return new ResponseEntity<>(notificationService.sendNotification(jsonObject.toString(), ""), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
