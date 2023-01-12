package vn.clmart.manager_service.api.statistical;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.OrderService;
import vn.clmart.manager_service.service.StatisticalService;

import java.util.Calendar;

@Controller
@RequestMapping("/statisticalApi")
@RequiredArgsConstructor
public class StatisticalApi {

    @Autowired
    OrderService orderService;

    @Autowired
    StatisticalService statisticalService;

    @GetMapping("/getCountOrder")
    protected @ResponseBody
    ResponseEntity<Object> getCount(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(orderService.getEmpoyeeOrder(cid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getItemOrder")
    protected @ResponseBody
    ResponseEntity<Object> getItemOrder(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(orderService.getItemOrder(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @GetMapping()
    protected @ResponseBody
    ResponseEntity<Object> getStatistical(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(statisticalService.statistical(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/chartInfo/{year}")
    protected @ResponseBody
    ResponseEntity<Object> chartInfo(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "year", required = false) Integer year
    ) {
        try {
            year = year == null ? Calendar.getInstance().get(Calendar.YEAR) : year;
            return new ResponseEntity<>(statisticalService.getCountOrderAndImport(cid, uid, year), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/chartInfoOrder/{year}")
    protected @ResponseBody
    ResponseEntity<Object> chartInfoOrder(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "year", required = false) Integer year
    ) {
        try {
            year = year == null ? Calendar.getInstance().get(Calendar.YEAR) : year;
            return new ResponseEntity<>(statisticalService.getCountOrder(cid, uid, year), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }


    @GetMapping("/getOrderByEmployee")
    protected @ResponseBody
    ResponseEntity<Object> getOrderByEmployee(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(statisticalService.getOrderByEmployee(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }

    }
}
