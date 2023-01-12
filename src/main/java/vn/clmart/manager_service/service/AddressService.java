package vn.clmart.manager_service.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.model.District;
import vn.clmart.manager_service.model.Province;
import vn.clmart.manager_service.model.Wards;
import vn.clmart.manager_service.repository.DistrictRepository;
import vn.clmart.manager_service.repository.ProvinceRepository;
import vn.clmart.manager_service.repository.WardsRepository;

import java.util.*;

@Service
@Transactional
public class AddressService {

    private static final Logger logger = Logger.getLogger(AddressService.class);

    @Autowired
    ProvinceRepository provinceRepository;

    @Autowired
    DistrictRepository districtRepository;

    @Autowired
    WardsRepository wardsRepository;


    public List<Province> findAllProvince(){
        try {
            return provinceRepository.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public List<District> findAllDistrict(){
        try {
            return districtRepository.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }
    public List<District> findAllDistrictByProvince(Integer proviceId){
        try {
            return districtRepository.findAllByProvinceId(proviceId);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public List<Wards> findAllWards(){
        try {
            return wardsRepository.findAll();
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }
    public List<Wards> findAllWardsByDistrictId(Integer districtId){
        try {
            return wardsRepository.findAllByDistrictId(districtId);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

}
