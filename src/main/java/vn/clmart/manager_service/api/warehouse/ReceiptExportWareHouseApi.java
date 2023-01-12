package vn.clmart.manager_service.api.warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ReceiptExportWareHouseDto;
import vn.clmart.manager_service.service.ReceiptExportWareHouseService;

@RestController
@RequestMapping("/receipt-export")
public class ReceiptExportWareHouseApi {

    private final vn.clmart.manager_service.service.ReceiptExportWareHouseService ReceiptExportWareHouseService;

    public ReceiptExportWareHouseApi(ReceiptExportWareHouseService ReceiptExportWareHouseService) {
        this.ReceiptExportWareHouseService = ReceiptExportWareHouseService;
    }


    @PostMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid
            , Pageable pageable) {
        try {
            return new ResponseEntity<>(ReceiptExportWareHouseService.search(cid, pageable), HttpStatus.OK);
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
            return new ResponseEntity<>(ReceiptExportWareHouseService.search(cid, pageable), HttpStatus.OK);
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
            @RequestBody ReceiptExportWareHouseDto ReceiptExportWareHouseDto
    ) {
        try {
            return new ResponseEntity<>(ReceiptExportWareHouseService.update(ReceiptExportWareHouseDto, cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ReceiptExportWareHouseDto ReceiptExportWareHouseDto
    ) {
        try {
            return new ResponseEntity<>(ReceiptExportWareHouseService.create(ReceiptExportWareHouseDto, cid, uid), HttpStatus.OK);
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
            return new ResponseEntity<>(ReceiptExportWareHouseService.delete(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping("/update/{id}/{state}")
    protected @ResponseBody
    ResponseEntity<Object> updateState(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @PathVariable("state") String state
    ) {
        try {
            return new ResponseEntity<>(ReceiptExportWareHouseService.updateState(state,cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
