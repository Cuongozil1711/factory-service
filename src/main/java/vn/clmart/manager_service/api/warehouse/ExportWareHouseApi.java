package vn.clmart.manager_service.api.warehouse;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ExportWareHouseDto;
import vn.clmart.manager_service.dto.ExportWareHouseListDto;
import vn.clmart.manager_service.dto.ItemsSearchDto;
import vn.clmart.manager_service.service.ExportWareHouseService;

import java.util.List;

@RestController
@RequestMapping("/export-ware-house")
public class ExportWareHouseApi {

    private final ExportWareHouseService exportWareHouseService;


    public ExportWareHouseApi(ExportWareHouseService exportWareHouseService) {
        this.exportWareHouseService = exportWareHouseService;
    }

    @PostMapping("/list")
    protected @ResponseBody
    ResponseEntity<Object> createList(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ExportWareHouseListDto exportWareHouseDto
    ) {
        try {
            exportWareHouseService.exportWareHouse(exportWareHouseDto, cid, uid);
            return new ResponseEntity<>(true, HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/update")
    protected @ResponseBody
    ResponseEntity<Object> updateList(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ExportWareHouseListDto exportWareHouseDto
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.editWareHouse(exportWareHouseDto, cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PutMapping({"idReceiptExport"})
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptExport") Long idReceiptExport
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.completeWareHouse(cid, uid, idReceiptExport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
    @GetMapping("{idReceiptExport}")
    protected @ResponseBody
    ResponseEntity<Object> findAllByIdReceiptExport(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptExport") Long idReceiptExport
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.getByIdReceiptExport(cid, uid, idReceiptExport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @DeleteMapping("{idReceiptExport}")
    protected @ResponseBody
    ResponseEntity<Object> deleteExport(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idReceiptExport") Long idReceiptExport
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.deleteExport(cid, uid, idReceiptExport),HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/list/{status}")
    protected @ResponseBody
    ResponseEntity<Object> findAllByImportWareHouse(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "status") Integer status,
            @RequestParam(value = "", required = false) String search,
            @RequestBody(required = false) ItemsSearchDto itemsSearchDto,
            Pageable pageable
    ) {
        try {
            if(itemsSearchDto == null)
            itemsSearchDto = new ItemsSearchDto();
            return new ResponseEntity<>(exportWareHouseService.search(cid, status, search, itemsSearchDto, pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/list/{status}")
    protected @ResponseBody
    ResponseEntity<Object> findAllByExportWareHouse(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "status") Integer status,
            @RequestParam(value = "", required = false) String search,
            @RequestBody(required = false) ItemsSearchDto itemsSearchDto,
            Pageable pageable
    ) {
        try {
            if(itemsSearchDto == null)
                itemsSearchDto = new ItemsSearchDto();
            return new ResponseEntity<>(exportWareHouseService.search(cid, status, search, itemsSearchDto, pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/restore")
    protected @ResponseBody
    ResponseEntity<Object> restoreExportWareHouse(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody Long[] idReceiptExport
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.restoreExportWareHouse(cid, uid, idReceiptExport), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/statistical/export/{status}")
    protected @ResponseBody
    ResponseEntity<Object> findAllByImportWareHouseAndOrder(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "status") Integer status,
            @RequestParam(value = "", required = false) String search,
            @RequestBody ItemsSearchDto itemsSearchDto,
            Pageable pageable
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.statistical(cid, status, search, itemsSearchDto, pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping("/list-export/{status}")
    protected @ResponseBody
    ResponseEntity<Object> listExport(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable(value = "status") Integer status,
            @RequestParam(value = "", required = false) String search,
            @RequestBody ItemsSearchDto itemsSearchDto,
            Pageable pageable
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.listExport(cid, status, search, itemsSearchDto, pageable), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getListOrder")
    protected @ResponseBody
    ResponseEntity<Object> getListOrder(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.getListOrder(cid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/ocr/{code}")
    protected @ResponseBody
    ResponseEntity<Object> findAllByIdReceiptExport(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("code") String code
    ) {
        try {
            return new ResponseEntity<>(exportWareHouseService.getByIdReceiptExportByOcr(cid, uid, code), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
