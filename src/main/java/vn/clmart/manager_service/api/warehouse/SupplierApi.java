package vn.clmart.manager_service.api.warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.dto.SupplierDto;
import vn.clmart.manager_service.service.SupplierService;
import vn.clmart.manager_service.untils.ResponseAPI;

@RestController
@RequestMapping("/supplier")
public class SupplierApi {

    private final SupplierService supplierService;

    public SupplierApi(SupplierService supplierService) {
        this.supplierService = supplierService;
    }


    @PostMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(supplierService.search(cid, pageable), HttpStatus.OK);
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
            return new ResponseEntity<>(supplierService.search(cid, pageable), HttpStatus.OK);
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
            @RequestBody SupplierDto supplierDto
    ) {
        try {
            return new ResponseEntity<>(supplierService.update(supplierDto, cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody SupplierDto supplierDto
    ) {
        try {
            return new ResponseEntity<>(supplierService.create(supplierDto, cid, uid), HttpStatus.OK);
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
            return new ResponseEntity<>(supplierService.delete(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
