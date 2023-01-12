package vn.clmart.manager_service.api.shophouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.BillService;

@RestController
@RequestMapping("/bill")
public class BillApi {

    @Autowired
    BillService billService;


    @GetMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid) {
        try {
            return new ResponseEntity<>(billService.search(cid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/update/{id}")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @PathVariable(value = "id") Long id) {
        try {
            return new ResponseEntity<>(billService.updateBill(id, cid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
