package vn.clmart.manager_service.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.config.exceptions.BusinessException;
import vn.clmart.manager_service.dto.*;
import vn.clmart.manager_service.dto.request.ExportWareHouseResponseDTO;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.dto.request.MailSendExport;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.DateUntils;
import vn.clmart.manager_service.untils.MapUntils;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Transactional
public class ExportWareHouseService {

    @Autowired
    ReceiptExportWareHouseService receiptExportWareHouseService;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;

    @Autowired
    ItemsRepository itemsRepository;

    @Autowired
    ReceiptExportWareHouseRepository receiptExportWareHouseRepository;

    @Autowired
    UserService userService;

    @Autowired
    PriceItemsRepository priceItemsRepository;

    @Autowired
    OrderRepositorry orderRepositorry;

    @Autowired
    PromotionService promotionService;

    @Lazy
    @Autowired
    ItemsService itemsService;

    @Lazy
    @Autowired
    ImportWareHouseService importWareHouseService;

    @Autowired
    CompanyService companyService;

    @Autowired
    WareHouseService wareHouseService;

    @Autowired
    MailService mailService;

    @Lazy
    @Autowired
    PDFGeneratorService pdfGeneratorService;


    public void validateDto(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        if(exportWareHouseDto.getIdItems() == null){
            throw new BusinessException("Mặt hàng không được để trống");
        }

        if(exportWareHouseDto.getIdReceiptExport() == null){
            throw new BusinessException("Phiếu xuất kho không được để trống");
        }
        else{
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, exportWareHouseDto.getIdReceiptExport());
            if(receiptExportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
        }
    }

    public ExportWareHouseListDto getByIdReceiptExportByOcr(Long cid, String uid, String code){
        ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseRepository.findByCodeAndDeleteFlg(code, Constants.DELETE_FLG.NON_DELETE).orElse(null);
        if(receiptExportWareHouse != null) return this.getByIdReceiptExport(receiptExportWareHouse.getCompanyId(), receiptExportWareHouse.getCreateBy(), receiptExportWareHouse.getId());
        return null;
    }

    public ExportWareHouseListDto getByIdReceiptExport(Long cid, String uid, Long idReceiptExport){
        try {
            ExportWareHouseListDto exportWareHouseListDto = new ExportWareHouseListDto();
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByIdReceiptExportAndCompanyId(idReceiptExport, cid);
            List<DetailsItemOrderDto> detailsItemOrderDtoList = new ArrayList<>();
            String code = "";
            if(exportWareHouses != null) code = exportWareHouses.get(0).getCode();
            else return exportWareHouseListDto;
            exportWareHouses.forEach(exportWareHouse -> {
                DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                detailsItemOrderDto.setIdItems(exportWareHouse.getIdItems());
                detailsItemOrderDto.setQuality(exportWareHouse.getQuantity());
                detailsItemOrderDto.setNumberBox(exportWareHouse.getNumberBox());
                detailsItemOrderDto.setDvtCode(exportWareHouse.getDvtCode());
                detailsItemOrderDto.setTotalPrice(exportWareHouse.getTotalPrice());
                ImportWareHouse importWareHouse = importWareHouseService.findByCompanyIdAndIdReceiptImportAndIdItems(exportWareHouse.getCompanyId(), exportWareHouse.getIdReceiptImport(), exportWareHouse.getIdItems());
                detailsItemOrderDto.setIdReceiptImport(exportWareHouse.getIdReceiptImport());
                detailsItemOrderDto.setIdImportWareHouse(importWareHouse.getId());
                ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(exportWareHouse.getCompanyId(), "", exportWareHouse.getIdItems());
                detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                detailsItemOrderDto.setCreateDate(exportWareHouse.getCreateDate());
                detailsItemOrderDto.setId(exportWareHouse.getId());
                detailsItemOrderDtoList.add(detailsItemOrderDto);
            });
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(exportWareHouses.get(0).getCompanyId(), uid, idReceiptExport);
            ReceiptExportWareHouseDto receiptExportWareHouseDto = new ReceiptExportWareHouseDto();
            BeanUtils.copyProperties(receiptExportWareHouse, receiptExportWareHouseDto);
            FullName fullName = userService.getFullName(receiptExportWareHouse.getCompanyId(), receiptExportWareHouse.getCreateBy());
            receiptExportWareHouseDto.setFullName(fullName.getFirstName() + " " + fullName.getLastName());
            receiptExportWareHouseDto.setCompanyName(companyService.getById(receiptExportWareHouse.getCompanyId(), uid, receiptExportWareHouse.getCompanyId()).getName());
            receiptExportWareHouseDto.setCompanyNameTo(companyService.getById(receiptExportWareHouse.getCompanyIdTo(), uid, receiptExportWareHouse.getCompanyIdTo()).getName());
            receiptExportWareHouseDto.setWareHouseName(wareHouseService.getById(receiptExportWareHouse.getCompanyId(),receiptExportWareHouse.getIdWareHouse()).getName());
            receiptExportWareHouseDto.setWareHouseNameTo(wareHouseService.getById(receiptExportWareHouse.getCompanyIdTo(),receiptExportWareHouse.getIdWareHouseTo()).getName());
            fullName = userService.getFullName(cid, exportWareHouses.get(0).getCreateBy());
            exportWareHouseListDto.setReceiptExportWareHouseDto(receiptExportWareHouseDto);
            exportWareHouseListDto.setCode(code);
            exportWareHouseListDto.setIdReceiptExport(idReceiptExport);
            exportWareHouseListDto.setCreateByName(fullName.getFirstName() + " " + fullName.getLastName());
            exportWareHouseListDto.setData(detailsItemOrderDtoList);
            return exportWareHouseListDto;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
    public boolean deleteExport(Long cid, String uid, Long idReceiptExport){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(idReceiptExport, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
            if(receiptExportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name())){
                return false;
            }
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptExport, cid);

            exportWareHouses.forEach(exportWareHouse -> {
                exportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                exportWareHouse.setUpdateBy(uid);
                exportWareHouseRepository.save(exportWareHouse);
            });
            // chỉnh sửa lại trạng thái phiếu
            if(receiptExportWareHouse != null){
                receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name());
                receiptExportWareHouse.setUpdateBy(uid);
                receiptExportWareHouseRepository.save(receiptExportWareHouse);
            }
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return true;
    }

    public Boolean restoreExportWareHouse(Long cid, String uid, Long[] idReceiptExports){
        try {
            for(Long idReceiptExport : idReceiptExports){
                List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.DELETE, idReceiptExport, cid);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date dateNow = cal.getTime();
                if(dateNow.after(exportWareHouses.get(0).getCreateDate())){
                    return false;
                }
                else{

                    exportWareHouses.forEach(exportWareHouse -> {
                        exportWareHouse.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                        exportWareHouse.setUpdateBy(uid);
                    });
                    exportWareHouseRepository.saveAll(exportWareHouses);

                    // chỉnh sửa lại trạng thái phiếu
                    ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(idReceiptExport, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
                    if(receiptExportWareHouse != null){
                        receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                        receiptExportWareHouse.setUpdateBy(uid);
                        receiptExportWareHouseRepository.save(receiptExportWareHouse);
                    }
                }
            }
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
        return true;
    }
    public void deleteExportByOrderId(Long cid, String uid, Long orgId){
        try {
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, orgId);
            exportWareHouses.forEach(exportWareHouse -> {
                exportWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                exportWareHouse.setUpdateBy(uid);
            });
            exportWareHouseRepository.saveAll(exportWareHouses);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public void restoreExportByOrderId(Long cid, String uid, Long orgId){
        try {
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.DELETE, orgId);
            exportWareHouses.forEach(exportWareHouse -> {
                exportWareHouse.setUpdateBy(uid);
            });
            exportWareHouseRepository.saveAll(exportWareHouses);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public boolean editWareHouse(ExportWareHouseListDto exportWareHouseListDto, Long cid, String uid){
        ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, exportWareHouseListDto.getIdReceiptExport());
        if(receiptExportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.COMPLETE)) return true;
        for(DetailsItemOrderDto itemExport : exportWareHouseListDto.getData()){
            ImportWareHouse importWareHouse = importWareHouseService.getById(cid, itemExport.getIdImportWareHouse());
            Integer totalInWareHouse = importWareHouse.getNumberBox() * importWareHouse.getQuantity();
            Integer totalExportWareHouse = this.qualityExport(cid, importWareHouse.getIdReceiptImport(), itemExport.getIdItems(), Constants.DELETE_FLG.NON_DELETE);
            ExportWareHouse exportWareHouse = exportWareHouseRepository.findById(itemExport.getId()).orElse(null);
            if(exportWareHouse == null) return false;
            if(totalInWareHouse - totalExportWareHouse + exportWareHouse.getNumberBox() * exportWareHouse.getQuantity()  < itemExport.getNumberBox() * itemExport.getQuality()) return false;
            exportWareHouse.setQuantity(itemExport.getQuality());
            exportWareHouse.setTotalPrice(itemExport.getTotalPrice());
            exportWareHouse.setNumberBox(itemExport.getNumberBox());
            exportWareHouseRepository.save(exportWareHouse);
        }
        return true;
    }
    public boolean exportWareHouse(ExportWareHouseListDto exportWareHouseListDto, Long cid, String uid){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, exportWareHouseListDto.getIdReceiptExport());
            for(DetailsItemOrderDto itemExport : exportWareHouseListDto.getData()){
                ExportWareHouseDto exportWareHouseDto = new ExportWareHouseDto();
                exportWareHouseDto.setNumberBox(itemExport.getNumberBox());
                Long idItems = itemExport.getIdItems();
                exportWareHouseDto.setQuantity(itemExport.getQuality());
                exportWareHouseDto.setIdReceiptExport(exportWareHouseListDto.getIdReceiptExport());
                ImportWareHouse importWareHouse = importWareHouseService.getById(cid, itemExport.getIdImportWareHouse());
                exportWareHouseDto.setIdReceiptImport(importWareHouse.getIdReceiptImport());
                exportWareHouseDto.setIdImportWareHouse(importWareHouse.getId());
                exportWareHouseDto.setIdItems(idItems);
                exportWareHouseDto.setTotalPrice(itemExport.getTotalPrice());
                exportWareHouseDto.setDvtCode(itemExport.getDvtCode());
                ExportWareHouse exportWareHouse = ExportWareHouse.of(exportWareHouseDto, cid, uid);
                exportWareHouse.setNumberBox(exportWareHouseDto.getNumberBox());
                exportWareHouse.setCode(exportWareHouseListDto.getCode());
                exportWareHouseRepository.save(exportWareHouse);
            }
            MailSendExport mailSendExport = new MailSendExport();
            mailSendExport.setCodeExport(receiptExportWareHouse.getCode());
            FullName fullName = userService.getFullName(receiptExportWareHouse.getCompanyId(), receiptExportWareHouse.getCreateBy());
            mailSendExport.setEmployeeExport(fullName.getFirstName() + " " + fullName.getLastName());
            mailSendExport.setCompanyName(companyService.getById(cid, uid, receiptExportWareHouse.getCompanyId()).getName());
            mailSendExport.setCompanyNameTo(companyService.getById(cid, uid, receiptExportWareHouse.getCompanyIdTo()).getName());
            mailSendExport.setDateExport(new Date());
            mailSendExport.setStatus("Đang thực hiện");
            exportWareHouseListDto.setCreateByName(fullName.getFirstName() + " " + fullName.getLastName());
            ReceiptExportWareHouseDto receiptExportWareHouseDto = new ReceiptExportWareHouseDto();
            MapUntils.copyWithoutAudit(receiptExportWareHouse, receiptExportWareHouseDto);
            exportWareHouseListDto.setReceiptExportWareHouseDto(receiptExportWareHouseDto);
            File file = pdfGeneratorService.exportWareHouse(exportWareHouseListDto, receiptExportWareHouse.getId(), cid);
            mailService.sendEmailStatusExport(mailSendExport, file);
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean orderToExport(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        try {
            ExportWareHouse exportWareHouse = ExportWareHouse.of(exportWareHouseDto, cid, uid);
            exportWareHouse.setNumberBox(exportWareHouseDto.getNumberBox());
            String codeExport = "EX"  + new Date().getTime();
            exportWareHouse.setCode(codeExport);
            exportWareHouseRepository.save(exportWareHouse);
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean saleExport(ExportWareHouseDto exportWareHouseDto, Long cid, String uid){
        try {
            ExportWareHouse exportWareHouse = ExportWareHouse.of(exportWareHouseDto, cid, uid);
            exportWareHouse.setNumberBox(exportWareHouseDto.getNumberBox());
            String codeExport = "S" + itemsRepository.findById(exportWareHouseDto.getIdItems()).get().getName().trim() + new Date();
            exportWareHouse.setCode(codeExport);
            exportWareHouseRepository.save(exportWareHouse);
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }


    public boolean completeWareHouse(Long cid, String uid, Long idReceiptExport){
        try {
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.getById(cid, uid, idReceiptExport);
            if(receiptExportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
            else{
                List<ExportWareHouse> list = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idReceiptExport, cid);
                if(!list.isEmpty()){
                    receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                    receiptExportWareHouseRepository.save(receiptExportWareHouse);
                }
            }
            return true;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }
    public List<ExportWareHouse> findAllByItems(Long cid, String uid, Long idItems){
        try {
            return exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idItems, cid);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public List<ExportWareHouse> findAllByCompanyIdAndIdOrderAndIdItemsAndDvtCode(Long cid, Long idItems, Long idOrder, String dvtCode) {
        try {
            return exportWareHouseRepository.findAllByCompanyIdAndIdOrderAndIdItemsAndDvtCode(cid, idOrder, idItems, dvtCode);
        } catch (Exception ex) {
            throw new BusinessException(ex.getMessage());
        }
    }

    public Long totalItemsInExport(Long idItem, Long cid){
        try {
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyId(Constants.DELETE_FLG.NON_DELETE, idItem, cid);
            if(exportWareHouses.size() == 0) return 0l;
            Long total = 0l;
            for(ExportWareHouse exportWareHouse : exportWareHouses){
                if(exportWareHouse.getNumberBox() == null) exportWareHouse.setNumberBox(1);
                total += exportWareHouse.getQuantity() * exportWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public Long totalItemsInExportIdWareHouse(Long idItem, Long cid, Long idWareHouse){
        try {
            List<ExportWareHouse> exportWareHouses = exportWareHouseRepository.getAllByIdWareHouse(Constants.DELETE_FLG.NON_DELETE, idItem, cid, idWareHouse);
            if(exportWareHouses.size() == 0) return 0l;
            Long total = 0l;
            for(ExportWareHouse exportWareHouse : exportWareHouses){
                if(exportWareHouse.getNumberBox() == null) exportWareHouse.setNumberBox(1);
                total += exportWareHouse.getQuantity() * exportWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public PageImpl<ExportWareHouseResponseDTO> search(Long cid, Integer status, String search, ItemsSearchDto itemsSearchDto, Pageable pageable){
        try {
            if(itemsSearchDto.getStartDate() == null) itemsSearchDto.setStartDate(DateUntils.minDate());
            else itemsSearchDto.setStartDate(DateUntils.getStartOfDate(itemsSearchDto.getStartDate()));
            if(itemsSearchDto.getEndDate() == null) itemsSearchDto.setEndDate(DateUntils.maxDate());
            else itemsSearchDto.setEndDate(DateUntils.getEndOfDate(itemsSearchDto.getEndDate()));
            Page<ExportWareHouse> pageSearch = exportWareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, status, search, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(), pageable);
            List<ExportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ExportWareHouse item : pageSearch.getContent()){
                ExportWareHouseResponseDTO exportWareHouseResponseDTO = new ExportWareHouseResponseDTO();
                exportWareHouseResponseDTO.setIdReceiptExport(item.getIdReceiptExport());
                if(item.getIdReceiptExport() != null){
                    exportWareHouseResponseDTO.setReceiptExportWareHouse(receiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(item.getIdReceiptExport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptExportWareHouse()));
                    List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(status, item.getIdReceiptExport(), cid);
                    AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
                    wareHouseList.forEach(
                            importWareHouse -> {
                                if(importWareHouse.getTotalPrice() != null)
                                    totalPrice.updateAndGet(v -> v + importWareHouse.getTotalPrice());
                            }
                    );
                    exportWareHouseResponseDTO.setQuantityItems(wareHouseList.size());
                    exportWareHouseResponseDTO.setTotalPrice(totalPrice.get());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        exportWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        exportWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    exportWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    exportWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                }
                responseDTOS.add(exportWareHouseResponseDTO);
            }
            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Integer qualityExport(Long cid, Long idReceiptImport, Long idItems, Integer status){
        try {
            AtomicReference<Integer> qualityExport = new AtomicReference<>(0);
            exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(status, idItems, cid, idReceiptImport).forEach(exportWareHouse -> {
                qualityExport.updateAndGet(v -> v + exportWareHouse.getQuantity() * exportWareHouse.getNumberBox());
            });
            return qualityExport.get();
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ExportWareHouseResponseDTO> statistical(Long cid, Integer status, String search, ItemsSearchDto itemsSearchDto, Pageable pageable){
        try {
            Page<ExportWareHouse> pageSearch = exportWareHouseRepository.statisticalByCompanyIdAndDeleteFlgAndOrder(cid, status, search, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(), pageable);
            List<ExportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ExportWareHouse item : pageSearch.getContent()){
                ExportWareHouseResponseDTO exportWareHouseResponseDTO = new ExportWareHouseResponseDTO();
                exportWareHouseResponseDTO.setIdReceiptExport(item.getIdReceiptExport());
                if(item.getIdReceiptExport() != null){
                    exportWareHouseResponseDTO.setReceiptExportWareHouse(receiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(item.getIdReceiptExport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptExportWareHouse()));
                    List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdReceiptExportAndCompanyId(status, item.getIdReceiptExport(), cid);
                    AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
                    wareHouseList.forEach(
                            importWareHouse -> {
                                if(importWareHouse.getTotalPrice() != null)
                                    totalPrice.updateAndGet(v -> v + importWareHouse.getTotalPrice());
                            }
                    );
                    exportWareHouseResponseDTO.setQuantityItems(wareHouseList.size());
                    exportWareHouseResponseDTO.setTotalPrice(totalPrice.get());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        exportWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        exportWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    exportWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    exportWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                }
                else if(item.getIdOrder() != null){
                    exportWareHouseResponseDTO.setOrder(orderRepositorry.findByCompanyIdAndIdAndDeleteFlg(cid, item.getIdOrder(), Constants.DELETE_FLG.NON_DELETE).orElse(new Order()));
                    List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, item.getIdOrder());
                    AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
                    wareHouseList.forEach(
                            importWareHouse -> {
                                if(importWareHouse.getTotalPrice() != null)
                                    totalPrice.updateAndGet(v -> v + importWareHouse.getTotalPrice());
                            }
                    );
                    exportWareHouseResponseDTO.setQuantityItems(wareHouseList.size());
                    exportWareHouseResponseDTO.setTotalPrice(totalPrice.get());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        exportWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        exportWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    exportWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    exportWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                }
                responseDTOS.add(exportWareHouseResponseDTO);
            }
            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ExportWareHouseResponseDTO> listExport(Long cid, Integer status, String search, ItemsSearchDto itemsSearchDto, Pageable pageable){
        try {
            Page<ExportWareHouse> pageSearch = exportWareHouseRepository.listExport(cid, status, search, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(), pageable);
            List<ExportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ExportWareHouse item : pageSearch.getContent()){
                ExportWareHouseResponseDTO exportWareHouseResponseDTO = new ExportWareHouseResponseDTO();
                BeanUtils.copyProperties(item, exportWareHouseResponseDTO);
                Double priceItemImport = importWareHouseService.priceImportByReceiptIdAndItemIds(cid, item.getIdReceiptImport(), item.getIdItems());
                exportWareHouseResponseDTO.setIdReceiptExport(item.getIdReceiptExport());
                if(item.getIdItems() != null){
                    ItemsResponseDTO items = itemsService.getById(cid, "", item.getIdItems());
                    exportWareHouseResponseDTO.setItemsName(items.getName());
                }
                if(item.getIdReceiptExport() != null){
                    exportWareHouseResponseDTO.setReceiptExportWareHouse(receiptExportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(item.getIdReceiptExport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptExportWareHouse()));
                    exportWareHouseResponseDTO.setQuantityItems(item.getQuantity() * item.getNumberBox());
                    exportWareHouseResponseDTO.setTotalPrice(item.getTotalPrice() * item.getNumberBox());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        exportWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        exportWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    exportWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    exportWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                    exportWareHouseResponseDTO.setTotalPriceImport(item.getTotalPrice() * item.getNumberBox());
                }
                else if(item.getIdOrder() != null){
                    exportWareHouseResponseDTO.setOrder(orderRepositorry.findByCompanyIdAndIdAndDeleteFlg(cid, item.getIdOrder(), Constants.DELETE_FLG.NON_DELETE).orElse(new Order()));
                    exportWareHouseResponseDTO.setQuantityItems(item.getQuantity() * item.getNumberBox());
                    exportWareHouseResponseDTO.setTotalPrice(item.getTotalPrice() * item.getNumberBox());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        exportWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        exportWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    exportWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    exportWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                    exportWareHouseResponseDTO.setTotalPriceImport(priceItemImport * item.getQuantity() * item.getNumberBox());
                }
                else{
                    List<PromotionResponseDto> promotion = promotionService.getByItemsId(cid, item.getIdItems());
                    exportWareHouseResponseDTO.setQuantityItems(item.getQuantity() * item.getNumberBox());
                    exportWareHouseResponseDTO.setPromotionResponseDtoList(promotion);
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        exportWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        exportWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    exportWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    exportWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                    exportWareHouseResponseDTO.setTotalPriceImport(priceItemImport * item.getQuantity() * item.getNumberBox());
                }
                responseDTOS.add(exportWareHouseResponseDTO);
            }
            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<ExportWareHouseResponseDTO> getListOrder(Long cid){
        try {
            List<ExportWareHouse> pageSearch = exportWareHouseRepository.getListOrder(cid, Constants.DELETE_FLG.NON_DELETE);
            List<ExportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ExportWareHouse item : pageSearch){
                ExportWareHouseResponseDTO exportWareHouseResponseDTO = new ExportWareHouseResponseDTO();
                BeanUtils.copyProperties(item, exportWareHouseResponseDTO);
                exportWareHouseResponseDTO.setIdReceiptExport(item.getIdReceiptExport());
                if(item.getIdItems() != null){
                    ItemsResponseDTO items = itemsService.getById(cid, "", item.getIdItems());
                    exportWareHouseResponseDTO.setItemsName(items.getName());
                    exportWareHouseResponseDTO.setImage(items.getImage());
                    exportWareHouseResponseDTO.setQuantityItems(this.totalItemsInExport(items.getId(), cid).intValue());
                }
                responseDTOS.add(exportWareHouseResponseDTO);
            }
            Collections.sort(responseDTOS, (o1, o2) -> {
                return  o2.getQuantityItems() - o1.getQuantityItems();
            });
            return responseDTOS;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
