package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.ReceiptImportWareHouseDto;
import vn.clmart.manager_service.dto.StallsDto;
import vn.clmart.manager_service.dto.request.ReceiptImportWareHouseResponseDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.untils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReceiptImportWareHouseService {

    @Autowired
    ReceiptImportWareHouseRepository receiptImportWareHouseRepository;

    @Autowired
    WareHouseService wareHouseService;

    @Autowired
    FullNameRepository fullNameRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    ImportWareHouseRepository importWareHouseRepository;

    public ReceiptImportWareHouse create(ReceiptImportWareHouseDto receiptImportWareHouseDto, Long cid, String uid){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = ReceiptImportWareHouse.of(receiptImportWareHouseDto, cid, uid);
            receiptImportWareHouse.setCode("RI" + new Date().getTime());
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name());
            return receiptImportWareHouseRepository.save(receiptImportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse update(ReceiptImportWareHouseDto receiptImportWareHouseDto, Long cid, String uid, Long id){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            if(receiptImportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.COMPLETE)) return null;
            receiptImportWareHouse.setCompanyId(cid);
            receiptImportWareHouse.setUpdateBy(uid);
            receiptImportWareHouse.setIdWareHouse(receiptImportWareHouseDto.getIdWareHouse());
            receiptImportWareHouse.setName(receiptImportWareHouseDto.getName());
            receiptImportWareHouse.setCode(receiptImportWareHouseDto.getCode());
            receiptImportWareHouse.setDateImport(receiptImportWareHouseDto.getDateImport());
            receiptImportWareHouse.setTotalPrice(receiptImportWareHouseDto.getTotalPrice());
            receiptImportWareHouse.setIdSupplier(receiptImportWareHouseDto.getIdSupplier());
            return receiptImportWareHouseRepository.save(receiptImportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse updateState(String state, Long cid, String uid, Long id){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            receiptImportWareHouse.setUpdateBy(uid);
            receiptImportWareHouse.setState(state);
            return receiptImportWareHouseRepository.save(receiptImportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse getById(Long cid, String uid, Long id){
        try {
            return receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ReceiptImportWareHouseResponseDTO> search(Long cid, Pageable pageable){
        try {
            Page<ReceiptImportWareHouse> pageSearch = receiptImportWareHouseRepository.findAllByCompanyIdAndDeleteFlgOrderByCreateDateDesc(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            List<ReceiptImportWareHouseResponseDTO> list = new ArrayList<>();
            for(ReceiptImportWareHouse receiptImportWareHouse : pageSearch.getContent()){
                ReceiptImportWareHouseResponseDTO responseDTO = new ReceiptImportWareHouseResponseDTO();
                BeanUtils.copyProperties(receiptImportWareHouse, responseDTO);
                if(responseDTO.getIdWareHouse() != null){
                    responseDTO.setNameWareHouse(wareHouseService.getById(cid, responseDTO.getIdWareHouse()).getName());
                }
                if(receiptImportWareHouse.getCreateBy() != null){
                    User user = userRepository.findUserByUidAndDeleteFlg(receiptImportWareHouse.getCreateBy(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
                    Employee employee = employeeRepository.findAllByIdUserAndDeleteFlg(user.getId(), Constants.DELETE_FLG.NON_DELETE).stream().findFirst().orElse(null);
                    if(employee != null){
                        FullName fullName = fullNameRepository.findById(employee.getIdFullName()).orElse(null);
                        responseDTO.setNameCreate(fullName.getFirstName() + " " + fullName.getLastName());
                    }
                }
                list.add(responseDTO);
            }
            return new PageImpl(list, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptImportWareHouse delete(Long cid, String uid, Long id){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            List<ImportWareHouse> importWareHouses = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyIdWork(Constants.DELETE_FLG.NON_DELETE, receiptImportWareHouse.getId(), cid);
            if(receiptImportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name()) && importWareHouses.isEmpty()){
                receiptImportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                receiptImportWareHouse.setCompanyId(cid);
                receiptImportWareHouse.setUpdateBy(uid);
                return receiptImportWareHouseRepository.save(receiptImportWareHouse);
            }
            return null;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
