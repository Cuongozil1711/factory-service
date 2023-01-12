package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.PositionDto;
import vn.clmart.manager_service.dto.SupplierDto;
import vn.clmart.manager_service.model.Position;
import vn.clmart.manager_service.model.Supplier;
import vn.clmart.manager_service.repository.SupplierRepository;
import vn.clmart.manager_service.untils.Constants;

@Transactional
@Service
public class SupplierService {

    @Autowired
    SupplierRepository supplierRepository;

    public void validateDto(SupplierDto supplierDto){
        if(supplierDto.getName().isEmpty()){
            throw new BusinessException("Tên không được để trống");
        }
    }

    public Supplier create(SupplierDto supplierDto, Long cid, String uid){
        try {
            validateDto(supplierDto);
            Supplier supplier = Supplier.of(supplierDto, cid, uid);
            return  supplierRepository.save(supplier);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Supplier update(SupplierDto supplierDto, Long cid, String uid, Long id){
        try {
            Supplier supplier = supplierRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            supplier.setCompanyId(cid);
            supplier.setUpdateBy(uid);
            supplier.setAddress(supplierDto.getAddress());
            supplier.setName(supplierDto.getName());
            supplier.setPhone(supplierDto.getPhone());
            return supplierRepository.save(supplier);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Supplier getById(Long cid, String uid, Long id){
        try {
            if(id != null)
            return supplierRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElse(new Supplier());
            else return new Supplier();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Supplier> search(Long cid, Pageable pageable){
        try {
            Page<Supplier> pageSearch = supplierRepository.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE, pageable);
            return new PageImpl(pageSearch.getContent(), pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Supplier delete(Long cid, String uid, Long id){
        try {
            Supplier supplier = supplierRepository.findByIdAndDeleteFlg(id, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            supplier.setDeleteFlg(Constants.DELETE_FLG.DELETE);
            supplier.setCompanyId(cid);
            supplier.setUpdateBy(uid);
            return supplierRepository.save(supplier);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
