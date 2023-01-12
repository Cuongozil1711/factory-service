package vn.clmart.manager_service.api.shophouse;


import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.ItemsDto;
import vn.clmart.manager_service.service.ItemsService;

@RestController
@RequestMapping("/items")
public class ItemsApi {

    private final vn.clmart.manager_service.service.ItemsService ItemsService;

    public ItemsApi(ItemsService ItemsService) {
        this.ItemsService = ItemsService;
    }


    @PostMapping("/search")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody(required = false) ItemsDto itemsDto,
            @RequestParam(value = "", required = false) String search
            , Pageable pageable) {
        try {
            if(itemsDto == null) itemsDto = new ItemsDto();
            return new ResponseEntity<>(ItemsService.search(cid, pageable, itemsDto, search), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/getByImport/{idImportWareHouse}")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("idImportWareHouse") Long idImportWareHouse
            ) {
        try {
            return new ResponseEntity<>(ItemsService.getItemsResponseDto(cid, uid, idImportWareHouse), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }



    @GetMapping("/list")
    protected @ResponseBody
    ResponseEntity<Object> list(
            @RequestHeader Long cid,
            @RequestHeader String uid) {
        try {
            return new ResponseEntity<>(ItemsService.list(cid), HttpStatus.OK);
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
            return new ResponseEntity<>(ItemsService.getById(cid, uid, id), HttpStatus.OK);
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
            @RequestBody ItemsDto ItemsDto
    ) {
        try {
            return new ResponseEntity<>(ItemsService.update(ItemsDto, cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody ItemsDto ItemsDto
    ) {
        try {
            return new ResponseEntity<>(ItemsService.create(ItemsDto, cid, uid), HttpStatus.OK);
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
            return new ResponseEntity<>(ItemsService.delete(cid, uid, id), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("details/{id}/{idWareHouse}")
    protected @ResponseBody
    ResponseEntity<Object> getByIdDetails(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @PathVariable("id") Long id,
            @PathVariable(value = "idWareHouse", required = false) Long idWareHousse
            ) {
        try {
            return new ResponseEntity<>(ItemsService.getByIdtems(cid, uid, id, idWareHousse), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/findItems-dateExpired")
    protected @ResponseBody
    ResponseEntity<Object> getByItemImport(
            @RequestHeader Long cid,
            @RequestHeader String uid
    ) {
        try {
            return new ResponseEntity<>(ItemsService.getByItemImport(cid, uid), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
