package vn.clmart.manager_service.api.shophouse;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ItemsDto;
import vn.clmart.manager_service.dto.ItemsSearchDto;
import vn.clmart.manager_service.dto.OrderDto;
import vn.clmart.manager_service.service.ItemsService;

@RestController
@RequestMapping("/order")
public class OrderApi {

    private final vn.clmart.manager_service.service.OrderService orderService;

    public OrderApi(vn.clmart.manager_service.service.OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody OrderDto orderDto
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return new ResponseEntity<>(objectMapper.writeValueAsString(orderService.createOrder(orderDto, cid, uid)), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/search/{status}")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestParam(value = "", required = false) String search,
            @RequestBody(required = false) ItemsSearchDto itemsSearchDto
            ,@PathVariable("status") Integer status
            , Pageable pageable) {
        try {
            if(itemsSearchDto == null)
                itemsSearchDto = new ItemsSearchDto();
            return new ResponseEntity<>(orderService.search(cid, pageable, search, itemsSearchDto, status), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/searchState/{state}")
    protected @ResponseBody
    ResponseEntity<Object> searchState(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestParam(value = "", required = false) String search
            ,@PathVariable("state") String state
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(orderService.searchByDate(cid, pageable, search, state), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/customer/{empId}")
    protected @ResponseBody
    ResponseEntity<Object> searchState(
            @RequestHeader Long cid
            ,@PathVariable("empId") Long empId
            ) {
        try {
            return new ResponseEntity<>(orderService.getListByEmpId(cid, empId), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/employee/{createBy}")
    protected @ResponseBody
    ResponseEntity<Object> getListByEmployee(
            @RequestHeader Long cid
            ,@PathVariable("createBy") String createBy
    ) {
        try {
            return new ResponseEntity<>(orderService.getListByCreateBy(cid, createBy), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> getById(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(orderService.getOrderById(cid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("{id}/{reasonId}")
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "id") Long id,
            @PathVariable(value = "reasonId") Long reasonId
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return new ResponseEntity<>(objectMapper.writeValueAsString(orderService.deleteOrder(cid, uid, id, reasonId)), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/restore/{id}")
    protected @ResponseBody
    ResponseEntity<Object> restoreExportWareHouse(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
    ) {
        try {
            return new ResponseEntity<>(orderService.restoreOrder(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getCountOrder")
    protected @ResponseBody
    ResponseEntity<Object> getById(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(orderService.getCountByDate(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
