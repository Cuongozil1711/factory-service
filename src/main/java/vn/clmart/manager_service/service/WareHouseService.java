package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.WareHouseDto;
import vn.clmart.manager_service.model.WareHouse;
import vn.clmart.manager_service.repository.WareHouseRepository;
import vn.clmart.manager_service.untils.Constants;

@Service
@Transactional
public class WareHouseService {
    @Autowired
    WareHouseRepository WareHouseRepository;

    public WareHouse create(WareHouseDto WareHouseDto, Long cid, String uid){
        try {
            WareHouse wareHouse = WareHouse.of(WareHouseDto, cid, uid);
            return WareHouseRepository.save(wareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public WareHouse update(WareHouseDto WareHouseDto, Long cid, String uid, Long id){
        try {
            WareHouse WareHouse = WareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            WareHouse.setCompanyId(cid);
            WareHouse.setUpdateBy(uid);
            WareHouse.setAddress(WareHouseDto.getAddress());
            WareHouse.setName(WareHouseDto.getName());
            WareHouse.setCode(WareHouseDto.getCode());
            return WareHouseRepository.save(WareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public WareHouse getById(Long cid,Long id){
        try {
            return WareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElse(new WareHouse());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<WareHouse> search(Long cid, Pageable pageable){
        try {
            Page<WareHouse> pageSearch = WareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<WareHouse> searchAll(Pageable pageable){
        try {
            Page<WareHouse> pageSearch = WareHouseRepository.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public WareHouse delete(Long cid, String uid, Long id){
        try {
            WareHouse WareHouse = WareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            WareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            WareHouse.setCompanyId(cid);
            WareHouse.setUpdateBy(uid);
            return WareHouseRepository.save(WareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
