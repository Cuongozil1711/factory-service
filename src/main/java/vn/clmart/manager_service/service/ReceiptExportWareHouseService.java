package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.ReceiptExportWareHouseDto;
import vn.clmart.manager_service.dto.request.ReceiptExportWareHouseResponseDTO;
import vn.clmart.manager_service.dto.request.ReceiptImportWareHouseResponseDTO;
import vn.clmart.manager_service.model.ExportWareHouse;
import vn.clmart.manager_service.model.ReceiptExportWareHouse;
import vn.clmart.manager_service.model.ReceiptImportWareHouse;
import vn.clmart.manager_service.repository.ExportWareHouseRepository;
import vn.clmart.manager_service.repository.ReceiptExportWareHouseRepository;
import vn.clmart.manager_service.untils.Constants;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReceiptExportWareHouseService {

    @Autowired
    ReceiptExportWareHouseRepository ReceiptExportWareHouseRepository;

    @Autowired
    WareHouseService wareHouseService;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;


    public ReceiptExportWareHouse create(ReceiptExportWareHouseDto ReceiptExportWareHouseDto, Long cid, String uid){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = ReceiptExportWareHouse.of(ReceiptExportWareHouseDto, cid, uid);
            receiptExportWareHouse.setCode("RX" + new Date().getTime());
            receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name());
            return ReceiptExportWareHouseRepository.save(receiptExportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse update(ReceiptExportWareHouseDto ReceiptExportWareHouseDto, Long cid, String uid, Long id){
        try {
            ReceiptExportWareHouse ReceiptExportWareHouse = ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            if(ReceiptExportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.COMPLETE)) return null;
            ReceiptExportWareHouse.setCompanyId(cid);
            ReceiptExportWareHouse.setUpdateBy(uid);
            ReceiptExportWareHouse.setIdWareHouse(ReceiptExportWareHouseDto.getIdWareHouse());
            ReceiptExportWareHouse.setIdWareHouseTo(ReceiptExportWareHouseDto.getIdWareHouseTo());
            ReceiptExportWareHouse.setName(ReceiptExportWareHouseDto.getName());
            ReceiptExportWareHouse.setCode(ReceiptExportWareHouseDto.getCode());
            ReceiptExportWareHouse.setDateExport(ReceiptExportWareHouseDto.getDateExport());
            ReceiptExportWareHouse.setTotalPrice(ReceiptExportWareHouseDto.getTotalPrice());
            return ReceiptExportWareHouseRepository.save(ReceiptExportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse updateState(String state, Long cid, String uid, Long id){
        try {
            ReceiptExportWareHouse ReceiptExportWareHouse = ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            ReceiptExportWareHouse.setUpdateBy(uid);
            ReceiptExportWareHouse.setState(state);
            return ReceiptExportWareHouseRepository.save(ReceiptExportWareHouse);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse getById(Long cid, String uid, Long id){
        try {
            return ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ReceiptExportWareHouseResponseDTO> search(Long cid, Pageable pageable){
        try {
            Page<ReceiptExportWareHouse> pageSearch = ReceiptExportWareHouseRepository.findAllByCompanyIdAndDeleteFlgOrderByCreateDateDesc(cid, Constants.DELETE_FLG.NON_DELETE, pageable);
            List<ReceiptExportWareHouseResponseDTO> list = new ArrayList<>();
            for(ReceiptExportWareHouse receiptImportWareHouse : pageSearch.getContent()){
                ReceiptExportWareHouseResponseDTO responseDTO = new ReceiptExportWareHouseResponseDTO();
                BeanUtils.copyProperties(receiptImportWareHouse, responseDTO);
                if(responseDTO.getIdWareHouse() != null){
                    responseDTO.setNameWareHouse(wareHouseService.getById(cid, responseDTO.getIdWareHouse()).getName());
                }
                list.add(responseDTO);
            }
            return new PageImpl(list, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public ReceiptExportWareHouse delete(Long cid, String uid, Long id){
        try {
            ReceiptExportWareHouse ReceiptExportWareHouse = ReceiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(id, cid, Constants.DELETE_FLG.NON_DELETE).orElseThrow();
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, ReceiptExportWareHouse.getId(), cid);
            if(ReceiptExportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name()) && exportWareHouses.isEmpty()){
                ReceiptExportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                ReceiptExportWareHouse.setCompanyId(cid);
                ReceiptExportWareHouse.setUpdateBy(uid);
                return ReceiptExportWareHouseRepository.save(ReceiptExportWareHouse);
            }
            else return null;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }


}
