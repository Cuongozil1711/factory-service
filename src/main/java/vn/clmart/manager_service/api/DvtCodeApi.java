package vn.clmart.manager_service.api;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.dto.CompanyDto;
import vn.clmart.manager_service.dto.DvtCodeDto;
import vn.clmart.manager_service.model.DvtCode;
import vn.clmart.manager_service.repository.DvtCodeRepository;
import vn.clmart.manager_service.service.CompanyService;

@RestController
@RequestMapping("/dvtCode")
public class DvtCodeApi {

    private final DvtCodeRepository dvtCodeRepository;

    public DvtCodeApi(DvtCodeRepository dvtCodeRepository) {
        this.dvtCodeRepository = dvtCodeRepository;
    }


    @GetMapping("/getAll")
    protected @ResponseBody
    ResponseEntity<Object> search(
            @RequestHeader Long cid,
            @RequestHeader String uid){
        try {
            return new ResponseEntity<>(dvtCodeRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @PostMapping()
    protected @ResponseBody
    ResponseEntity<Object> create(
            @RequestHeader Long cid,
            @RequestHeader String uid,
            @RequestBody DvtCodeDto dvtCodeDto
    ) {
        try {
            DvtCode dvtCode = new DvtCode();
            dvtCode.setDvtCode(dvtCodeDto.getCode());
            dvtCode.setQuality(dvtCodeDto.getQuality());
            dvtCode.setName(dvtCodeDto.getName());
            dvtCodeRepository.save(dvtCode);
            return new ResponseEntity<>(dvtCodeRepository.findAll(), HttpStatus.OK);
        } catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }
}
