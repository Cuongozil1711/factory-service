package vn.clmart.manager_service.api.empoyee;

import org.hibernate.annotations.Parameter;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.service.PositionService;
import vn.clmart.manager_service.untils.ResponseAPI;

@RestController
@RequestMapping("/position")
public class PositionApi {

    private final PositionService positionService;

    public PositionApi(PositionService positionService) {
        this.positionService = positionService;
    }

    @GetMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
           @RequestHeader Long cid,
           @RequestHeader String uid
            ) {
        try {
            return new ResponseEntity<>(positionService.search(cid), HttpStatus.OK);
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
            ) {
        try {
            return new ResponseEntity<>(positionService.getById(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("{id}")
    protected @ResponseBody
    ResponseEntity<Object> update(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @RequestBody PositionDto positionDto
            ) {
        try {
            return new ResponseEntity<>(positionService.update(positionDto, cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody PositionDto positionDto
            ) {
        try {
            return new ResponseEntity<>(positionService.create(positionDto, cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/delete/{id}")
    protected @ResponseBody
    ResponseEntity<Object> delete(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id
    ) {
        try {
            return new ResponseEntity<>(positionService.delete(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
