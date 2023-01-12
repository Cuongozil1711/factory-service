package vn.clmart.manager_service.api;


import lombok.RequiredArgsConstructor;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import vn.clmart.manager_service.service.AddressService;
import java.util.HashMap;

@Controller
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressApi {

    @Autowired
    AddressService addressService;

    @GetMapping()
    protected @ResponseBody
    ResponseEntity<Object> all(
            ) {
        try {
            Map<String, Object> address = new HashMap<>();
            address.put("province", addressService.findAllProvince());
            address.put("district", addressService.findAllDistrict());
            address.put("wards", addressService.findAllWards());
            return new ResponseEntity<>(address, HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/province")
    protected @ResponseBody
    ResponseEntity<Object> province(
    ) {
        try {
            Map<String, Object> address = new HashMap<>();
            address.put("province", addressService.findAllProvince());
            return new ResponseEntity<>(address, HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/district/{provinceId}")
    protected @ResponseBody
    ResponseEntity<Object> district(
            @PathVariable("provinceId") Integer provinceId
    ) {
        try {
            Map<String, Object> address = new HashMap<>();
            address.put("district", addressService.findAllDistrictByProvince(provinceId));
            return new ResponseEntity<>(address, HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

    @GetMapping("/wards/{districtId}")
    protected @ResponseBody
    ResponseEntity<Object> wards(
            @PathVariable("districtId") Integer districtId
    ) {
        try {
            Map<String, Object> address = new HashMap<>();
            address.put("wards", addressService.findAllWardsByDistrictId(districtId));
            return new ResponseEntity<>(address, HttpStatus.OK);
        }
        catch (Exception ex) {
            return new ResponseEntity<>(ex, HttpStatus.EXPECTATION_FAILED);
        }
    }

}
