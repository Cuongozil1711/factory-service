package vn.clmart.manager_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.log4j.Logger;
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
import vn.clmart.manager_service.dto.request.ImportWareHouseResponseDTO;
import vn.clmart.manager_service.dto.request.ItemScannerExport;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.dto.request.MailSendExport;
import vn.clmart.manager_service.generator.FakeId;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.DateUntils;
import vn.clmart.manager_service.untils.MapUntils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.*;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional
public class ImportWareHouseService {


    private static final Logger logger = Logger.getLogger(ImportWareHouseService.class);

    @Autowired
    ImportWareHouseRepository importWareHouseRepository;

    @Autowired
    ReceiptImportWareHouseService receiptImportWareHouseService;

    @Autowired
    ReceiptImportWareHouseRepository receiptImportWareHouseRepository;

    @Autowired
    ReceiptExportWareHouseRepository receiptExportWareHouseService;

    @Autowired
    ItemsRepository itemsRepository;

    @Autowired
    UserService userService;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;

    @Autowired
    @Lazy
    ItemsService itemsService;

    @Autowired
    ExportWareHouseService exportWareHouseService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    TokenFireBaseRepository tokenFireBaseRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    CompanyService companyService;

    @Autowired
    PDFGeneratorService pdfGeneratorService;

    @Autowired
    MailService mailService;

    public void validateDto(ImportWareHouseDto importWareHouseDto, Long cid, String uid){
        if(importWareHouseDto.getIdItems() == null){
            throw new BusinessException("Mặt hàng không được để trống");
        }

        if(importWareHouseDto.getIdReceiptImport() == null){
            throw new BusinessException("Phiếu nhập kho không được để trống");
        }
        else{
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importWareHouseDto.getIdReceiptImport());
            if(receiptImportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
        }
    }

    public boolean importWareHouse(ImportWareHouseDto importWareHouseDto, Long cid, String uid){
        try {
            validateDto(importWareHouseDto, cid, uid);
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importWareHouseDto.getIdReceiptImport());
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
            ImportWareHouse importWareHouse = ImportWareHouse.of(importWareHouseDto, cid, uid);
            String codeExport = "IM" + new Date().getTime();
            importWareHouse.setCode(codeExport);
            importWareHouse.setIdReceiptImport(importWareHouseDto.getIdReceiptImport());
            importWareHouseRepository.save(importWareHouse);
            return true;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public boolean checkListImportWareHouse(Long cid, String uid, Long[] ids){
        try {
            boolean checked = false;
            for(Long id : ids){
                ImportWareHouse importWareHouse = importWareHouseRepository.findAllByCompanyIdWorkAndId(cid, id).orElse(null);
                if(importWareHouse != null){
                    checked = true;
                    break;
                }
            }
            return checked;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public boolean importQuickLy(Long cid, String uid, Long idReceiptExport) throws IOException {
        try {
            ReceiptExportWareHouse receiptExportWareHouse = receiptExportWareHouseService.findByIdAndDeleteFlg(idReceiptExport, Constants.DELETE_FLG.NON_DELETE).orElse(null);
            if(receiptExportWareHouse != null){
                if(!receiptExportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name()) && receiptExportWareHouse.getCompanyIdTo().equals(cid)){
                    ReceiptImportWareHouse receiptImportWareHouse = new ReceiptImportWareHouse();
                    receiptImportWareHouse.setCompanyId(cid);
                    receiptImportWareHouse.setCreateBy(uid);
                    receiptImportWareHouse.setCode(receiptExportWareHouse.getCode());
                    receiptImportWareHouse.setName("Nhập kho nhanh");
                    receiptImportWareHouse.setType(Constants.RECEIPT_TYPE_WARE_HOUSE.QUICKLY.name());
                    receiptImportWareHouse.setIdWareHouse(receiptExportWareHouse.getIdWareHouseTo());
                    receiptImportWareHouse.setName(receiptExportWareHouse.getName());
                    receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                    receiptExportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                    receiptExportWareHouseService.save(receiptExportWareHouse);
                    receiptImportWareHouse =receiptImportWareHouseRepository.save(receiptImportWareHouse);
                    List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByIdReceiptExportAndCompanyId(idReceiptExport, receiptExportWareHouse.getCompanyId());
                    List<ImportWareHouse> importWareHouses = new ArrayList<>();
                    for(ExportWareHouse exportWareHouse: wareHouseList){
                        ImportWareHouse importWareHouse = new ImportWareHouse();
                        if(exportWareHouse.getIdImportWareHouse() != null)
                        importWareHouse.setId(exportWareHouse.getIdImportWareHouse());
                        else importWareHouse.setId(FakeId.getInstance().nextId());
                        importWareHouse.setIdReceiptImport(receiptImportWareHouse.getId());
                        importWareHouse.setQuantity(exportWareHouse.getQuantity());
                        importWareHouse.setTotalPrice(exportWareHouse.getTotalPrice());
                        importWareHouse.setNumberBox(exportWareHouse.getNumberBox());
                        importWareHouse.setIdItems(exportWareHouse.getIdItems());
                        importWareHouse.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                        importWareHouse.setCompanyId(cid);
                        importWareHouse.setCompanyIdWork(cid);
                        importWareHouse.setCreateBy(uid);
                        importWareHouse.setCreateDate(new Date());
                        importWareHouses.add(importWareHouse);
                    }
                    importWareHouseRepository.saveAll(importWareHouses);

                    Set<Employee> employees = userService.getUserMappingLeader(cid).stream().collect(Collectors.toSet());
                    employees.addAll(userService.getUserMappingLeader(receiptExportWareHouse.getCompanyId()).stream().collect(Collectors.toSet()));
                    String[] token = new String[employees.size()];
                    // gui thong bao cho quan ly chi nhanh
                    TokenFirseBaseDTO tokenFirseBaseDTO = new TokenFirseBaseDTO();
                    tokenFirseBaseDTO.setPriority("high");
                    Map<String, String> notification = new HashMap<>();
                    notification.put("body", "Hoàn tất phiếu: " + receiptExportWareHouse.getName());
                    notification.put("title", "Nhập kho");
                    tokenFirseBaseDTO.setNotification(notification);
                    for(int i = 0; i < employees.stream().collect(Collectors.toList()).size(); i++){
                        TokenFireBase tokenFireBase = tokenFireBaseRepository.findByDeleteFlgAndUserId(Constants.DELETE_FLG.NON_DELETE, employees.stream().collect(Collectors.toList()).get(i).getIdUser()).orElse(null);
                        if(tokenFireBase != null){
                            token[i] = tokenFireBase.getToken();
                        }
                    }
                    tokenFirseBaseDTO.setRegistration_ids(token);

                    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
                    String json = ow.writeValueAsString(tokenFirseBaseDTO);

                    notificationService.sendNotificationToUser(json);

                    ExportWareHouseListDto exportWareHouseListDto = exportWareHouseService.getByIdReceiptExport(receiptExportWareHouse.getCompanyId(), "", idReceiptExport);
                    MailSendExport mailSendExport = new MailSendExport();
                    mailSendExport.setCodeExport(receiptExportWareHouse.getCode());
                    FullName fullName = userService.getFullName(receiptExportWareHouse.getCompanyId(), receiptExportWareHouse.getCreateBy());
                    mailSendExport.setEmployeeExport(fullName.getFirstName() + " " + fullName.getLastName());
                    mailSendExport.setCompanyName(companyService.getById(cid, uid, receiptExportWareHouse.getCompanyId()).getName());
                    mailSendExport.setCompanyNameTo(companyService.getById(cid, uid, receiptExportWareHouse.getCompanyIdTo()).getName());
                    mailSendExport.setDateExport(new Date());
                    mailSendExport.setStatus("Hoàn thành");
                    exportWareHouseListDto.setCreateByName(fullName.getFirstName() + " " + fullName.getLastName());
                    ReceiptExportWareHouseDto receiptExportWareHouseDto = new ReceiptExportWareHouseDto();
                    MapUntils.copyWithoutAudit(receiptExportWareHouse, receiptExportWareHouseDto);
                    exportWareHouseListDto.setReceiptExportWareHouseDto(receiptExportWareHouseDto);
                    File file = pdfGeneratorService.exportWareHouse(exportWareHouseListDto, receiptExportWareHouse.getId(), receiptExportWareHouse.getCompanyId());
                    mailService.sendEmailStatusExport(mailSendExport, file);
                    return true;
                }
                else return  false;
            }
            else return false;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public boolean importListWareHouse(ImportListDataWareHouseDto importListDataWareHouseDto, Long cid, String uid){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importListDataWareHouseDto.getIdReceiptImport());
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
            for(ImportWareHouseDto item : importListDataWareHouseDto.getData()){
                ImportWareHouse importWareHouse = ImportWareHouse.of(item, cid, uid);
                if(item.getId() != null) {
                    importWareHouse.setId(item.getId());
                }
                else{
                    importWareHouse.setId(FakeId.getInstance().nextId());
                }
                importWareHouse.setCreateDate(new Date());
                importWareHouse.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                importWareHouse.setCode(importListDataWareHouseDto.getCode());
                importWareHouse.setIdReceiptImport(importListDataWareHouseDto.getIdReceiptImport());
                importWareHouseRepository.save(importWareHouse);
            }
            return true;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public Boolean restoreImportWareHouse(Long cid, String uid, Long[] idReceiptImports){
        try {
            for(Long idReceiptImport : idReceiptImports){
                List<ImportWareHouse> importWareHouses = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyIdWork(Constants.DELETE_FLG.DELETE, idReceiptImport, cid);

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date dateNow = cal.getTime();
                if(dateNow.after(importWareHouses.get(0).getCreateDate())){
                    return false;
                }
                else{
                    importWareHouses.forEach(importWareHouse -> {
                        importWareHouse.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                        importWareHouse.setUpdateBy(uid);
                    });
                    importWareHouseRepository.saveAll(importWareHouses);

                    // chỉnh sửa lại trạng thái phiếu
                    ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(idReceiptImport, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
                    if(receiptImportWareHouse != null){
                        receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                        receiptImportWareHouse.setUpdateBy(uid);
                        receiptImportWareHouseRepository.save(receiptImportWareHouse);
                    }
                }
            }
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
        return true;
    }

    public boolean deleteExport(Long cid, String uid, Long idReceiptImport){
        try {
            List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndCompanyIdAndIdReceiptImport(Constants.DELETE_FLG.NON_DELETE, cid, idReceiptImport);
            if(wareHouseList.isEmpty()){
                // chưa được dùng order hoặc xuất kho
                List<ImportWareHouse> importWareHouses = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyIdWork(Constants.DELETE_FLG.NON_DELETE, idReceiptImport, cid);

                    importWareHouses.forEach(importWareHouse -> {
                        importWareHouse.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                        importWareHouse.setUpdateBy(uid);
                        importWareHouseRepository.save(importWareHouse);
                    });
                    // chỉnh sửa lại trạng thái phiếu
                    ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(idReceiptImport, cid, Constants.DELETE_FLG.NON_DELETE).orElse(null);
                    if(receiptImportWareHouse != null){
                        receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name());
                        receiptImportWareHouse.setUpdateBy(uid);
                        receiptImportWareHouseRepository.save(receiptImportWareHouse);
                    }
                    return true;
            }
            else{
                // không được xóa
                return false;
            }

        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public boolean checkRemoveListImport(Long cid, String uid, Long idImport){
        try {
            ImportWareHouse importWareHouse = importWareHouseRepository.findByCompanyIdWorkAndIdAndDeleteFlg(cid, idImport, Constants.DELETE_FLG.NON_DELETE).orElse(null);
            if(importWareHouse != null){
                List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(Constants.DELETE_FLG.NON_DELETE, importWareHouse.getIdItems(), cid, importWareHouse.getIdReceiptImport());
                if(wareHouseList != null){
                    return false;
                }
            }
            return true;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public boolean checkRemoveQuatity(Long cid, String uid, Long idImport, Integer quatity){
        try {
            ImportWareHouse importWareHouse = importWareHouseRepository.findByCompanyIdWorkAndIdAndDeleteFlg(cid, idImport, Constants.DELETE_FLG.NON_DELETE).orElse(null);
            if(importWareHouse != null){
                List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(Constants.DELETE_FLG.NON_DELETE, importWareHouse.getIdItems(), cid, importWareHouse.getIdReceiptImport());
                AtomicReference<Integer> total = new AtomicReference<>(0);
                wareHouseList.forEach(exportWareHouse -> {
                    total.updateAndGet(v -> v + exportWareHouse.getNumberBox() * exportWareHouse.getNumberBox());
                });
                if(total.get() > quatity) return true;
            }
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
        return false;
    }

    public boolean prepareEditImportListWareHouse(ImportListDataWareHouseDto importListDataWareHouseDto, Long cid, String uid){
        AtomicBoolean check = new AtomicBoolean(false);
        importListDataWareHouseDto.getData().forEach(importWareHouseDto -> {
            if(importWareHouseDto.getId() != null){
                if(checkRemoveQuatity(cid, uid, importWareHouseDto.getId(), importWareHouseDto.getNumberBox() * importWareHouseDto.getQuantity())){
                    check.set(true);
                }
            }
        });
        return check.get();
    }

    @Transactional
    public boolean editImportListWareHouse(ImportListDataWareHouseDto importListDataWareHouseDto, Long cid, String uid){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, importListDataWareHouseDto.getIdReceiptImport());
            receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
            if(importListDataWareHouseDto.getIdReceiptImport() != null){
                List<ImportWareHouseDto> listDataEdit = importListDataWareHouseDto.getData();
                for(ImportWareHouseDto importWareHouseEdit : listDataEdit){
                    if(importWareHouseEdit.getId() != null){
                        // check số lượng đã được dùng trong kho hay chưa
                        List<ExportWareHouse> wareHouseList = exportWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdAndIdReceiptImport(Constants.DELETE_FLG.NON_DELETE, importWareHouseEdit.getIdItems(), cid, importWareHouseEdit.getIdReceiptImport());
                        AtomicReference<Integer> quality = new AtomicReference<>(0);
                        wareHouseList.forEach(exportWareHouse -> {
                            quality.updateAndGet(v -> v + exportWareHouse.getNumberBox() * exportWareHouse.getQuantity());
                        });
                        if(importWareHouseEdit.getQuantity() * importWareHouseEdit.getNumberBox() < quality.get()){
                           throw new BusinessException(quality.get() + " sản phẩm " + itemsService.getById(cid, uid, importWareHouseEdit.getId()).getName() + " đã được sử dụng");
                        }
                        else{
                            ImportWareHouse importWareHouse = importWareHouseRepository.findByCompanyIdWorkAndIdAndDeleteFlg(cid, importWareHouseEdit.getId(), Constants.DELETE_FLG.NON_DELETE).orElse(null);
                            if(importWareHouse != null){
                                importWareHouse.setQuantity(importWareHouseEdit.getQuantity());
                                importWareHouse.setNumberBox(importWareHouseEdit.getNumberBox());
                                importWareHouse.setTotalPrice(importWareHouseEdit.getTotalPrice());
                                importWareHouse.setIdItems(importWareHouseEdit.getIdItems());
                                importWareHouse.setDateExpired(importWareHouseEdit.getDateExpired());
                                importWareHouseRepository.save(importWareHouse);
                            }
                        }
                    }
                    else{
                        ImportWareHouse importWareHouse = ImportWareHouse.of(importWareHouseEdit, cid, uid);
                        if(importWareHouseEdit.getId() != null){
                            importWareHouse.setId(importWareHouseEdit.getId());
                        }
                        else{
                            importWareHouse.setId(FakeId.getInstance().nextId());
                        }
                        String codeExport = "I" + itemsRepository.findById(importWareHouseEdit.getIdItems()).get().getName().trim() + new Date().getTime();
                        importWareHouse.setCode(codeExport);
                        importWareHouse.setIdReceiptImport(importWareHouseEdit.getIdReceiptImport());
                        importWareHouseRepository.save(importWareHouse);
                    }
                }
            }
            return true;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public boolean checkUseItemsOfReceiptImport(List<ImportWareHouseDto> listDataEdit, ImportWareHouse importWareHouse){
        for(ImportWareHouseDto importWareHouse1 : listDataEdit){
            if(Objects.equals(importWareHouse1.getId(), importWareHouse.getId())){
                return true;
            }
        }
        return false;
    }

    public ImportListDataWareHouseDto getImportByReceiptId(Long cid, String uid, Long idReceiptImport){
        try {
            ImportListDataWareHouseDto importListDataWareHouseDto = new ImportListDataWareHouseDto();
            if(idReceiptImport != null){
                ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, idReceiptImport);
                ReceiptImportWareHouseDto receiptImportWareHouseDto = new ReceiptImportWareHouseDto();
                BeanUtils.copyProperties(receiptImportWareHouse, receiptImportWareHouseDto);
                importListDataWareHouseDto.setReceiptImportWareHouseDto(receiptImportWareHouseDto);
                importListDataWareHouseDto.setCode(receiptImportWareHouse.getCode());
                importListDataWareHouseDto.setIdReceiptImport(idReceiptImport);
                importListDataWareHouseDto.setImageReceipt(receiptImportWareHouse.getImageReceipt());
                List<ImportWareHouseDto> data = new ArrayList<>();
                List<ImportWareHouse> list = importWareHouseRepository.findAllByIdReceiptImportAndCompanyIdWork(idReceiptImport, cid);
                data = list.stream().map(importWareHouse -> of(importWareHouse,  cid, uid)).collect(Collectors.toList());
                importListDataWareHouseDto.setData(data);
            }
            FullName fullName = userService.getFullName(cid, importListDataWareHouseDto.getData().get(0).getCreateBy());
            importListDataWareHouseDto.setCreateByName(fullName.getFirstName() + " " + fullName.getLastName());
            return importListDataWareHouseDto;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public Double priceImportByReceiptIdAndItemIds(Long cid, Long idReceiptImport, Long idItems) {
        try {
            List<ImportWareHouse> list = importWareHouseRepository.findAllByIdReceiptImportAndCompanyIdWorkAndIdItems(idReceiptImport, cid, idItems);
            Double resultItem = 0d;
            if(list.isEmpty()) return  resultItem;
            else{
                resultItem = list.get(0).getTotalPrice() / (list.get(0).getNumberBox() * list.get(0).getQuantity());
            }
            return resultItem;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public ImportWareHouseDto of(ImportWareHouse importWareHouse, Long cid, String uid){
        ImportWareHouseDto importWareHouseDto = new ImportWareHouseDto();
        importWareHouseDto = MapUntils.copyWithoutAudit(importWareHouse, importWareHouseDto);
        importWareHouseDto.setId(importWareHouse.getId());
        importWareHouseDto.setNameItems(itemsService.getById(cid, uid, importWareHouseDto.getIdItems()).getName());
        importWareHouseDto.setCreateDate(importWareHouse.getCreateDate());
        importWareHouseDto.setCreateBy(importWareHouse.getCreateBy());
        return importWareHouseDto;
    }

    public List<ImportWareHouse> findAll(Long cid, String uid, Long idReceiptImport){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, idReceiptImport);
            if(receiptImportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
            else{
                if(receiptImportWareHouse.getState().equals(Constants.RECEIPT_WARE_HOUSE.PROCESSING.name())){
                    throw new BusinessException("Phiếu nhập kho chưa hoàn thành");
                }
            }
            return importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyIdWork(Constants.DELETE_FLG.NON_DELETE, idReceiptImport, cid);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public List<ImportWareHouse> findAllByItems(Long cid, String uid, Long idItems){
        try {
            return importWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdWork(Constants.DELETE_FLG.NON_DELETE, idItems, cid);
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
    }

    public boolean completeWareHouse(Long cid, String uid, Long idReceiptImport){
        try {
            ReceiptImportWareHouse receiptImportWareHouse = receiptImportWareHouseService.getById(cid, uid, idReceiptImport);
            if(receiptImportWareHouse == null){
                throw new BusinessException("Không tìm thấy phiếu nhập kho");
            }
            else{
                List<ImportWareHouse> list = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyIdWork(Constants.DELETE_FLG.NON_DELETE, idReceiptImport, cid);
                if(!list.isEmpty()){
                    receiptImportWareHouse.setState(Constants.RECEIPT_WARE_HOUSE.COMPLETE.name());
                    receiptImportWareHouseRepository.save(receiptImportWareHouse);
                }
            }
            return true;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public Long totalItemsInImport(Long idItem, Long cid){
        try {
            List<ImportWareHouse> importWareHouses =importWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdWork(Constants.DELETE_FLG.NON_DELETE, idItem, cid);
            if(importWareHouses.size() == 0) return 0l;
            Long total = 0l;
            for(ImportWareHouse importWareHouse : importWareHouses){
                if(importWareHouse.getNumberBox() == null) importWareHouse.setNumberBox(1);
                total += importWareHouse.getQuantity() * importWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public Long totalItemsInImportWareHouse(Long idItem, Long cid, Long idWareHouse){
        try {
            List<ImportWareHouse> importWareHouses =importWareHouseRepository.getImportWareHouse(Constants.DELETE_FLG.NON_DELETE, idItem, cid, idWareHouse);
            if(importWareHouses.size() == 0) return 0l;
            Long total = 0l;
            for(ImportWareHouse importWareHouse : importWareHouses){
                if(importWareHouse.getNumberBox() == null) importWareHouse.setNumberBox(1);
                total += importWareHouse.getQuantity() * importWareHouse.getNumberBox();
            }
            return total;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ImportWareHouseResponseDTO> search(Long cid, String uid, Integer status, String search, ItemsSearchDto itemsSearchDto, Pageable pageable){
        try {
            if(itemsSearchDto.getStartDate() == null) itemsSearchDto.setStartDate(DateUntils.minDate());
            else itemsSearchDto.setStartDate(DateUntils.getStartOfDate(itemsSearchDto.getStartDate()));
            if(itemsSearchDto.getEndDate() == null) itemsSearchDto.setEndDate(DateUntils.maxDate());
            else itemsSearchDto.setEndDate(DateUntils.getEndOfDate(itemsSearchDto.getEndDate()));
            Page<ImportWareHouse> pageSearch = null;
            if(status == 0 || status == 1){
                pageSearch = importWareHouseRepository.findAllByCompanyIdAndDeleteFlg(cid, status, search, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(),  pageable);
            }
            else{
                pageSearch = importWareHouseRepository.findAllByCompanyId(cid, search, null, null,  pageable);
            }
            List<ImportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ImportWareHouse item : pageSearch.getContent()){
                ImportWareHouseResponseDTO importWareHouseResponseDTO = new ImportWareHouseResponseDTO();
                importWareHouseResponseDTO.setDeleteFlg(item.getDeleteFlg());
                importWareHouseResponseDTO.setIdReceiptImport(item.getIdReceiptImport());
                if(importWareHouseResponseDTO.getIdReceiptImport() != null){
                    importWareHouseResponseDTO.setReceiptImportWareHouse(receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(importWareHouseResponseDTO.getIdReceiptImport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptImportWareHouse()));
                    AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
                    List<ImportWareHouse> wareHouseList = importWareHouseRepository.findAllByDeleteFlgAndIdReceiptImportAndCompanyIdWork(item.getDeleteFlg(), importWareHouseResponseDTO.getIdReceiptImport(), cid);
                    wareHouseList.forEach(
                             importWareHouse -> {
                                 if(importWareHouse.getTotalPrice() != null)
                                 totalPrice.updateAndGet(v -> v + importWareHouse.getTotalPrice());
                             }
                    );
                    importWareHouseResponseDTO.setQuantityItems(wareHouseList.size());
                    importWareHouseResponseDTO.setTotalPrice(totalPrice.get());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        importWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        importWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    importWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    importWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                }
                responseDTOS.add(importWareHouseResponseDTO);
            }

            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception e){
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public ItemScannerExport getByIdScanner(Long cid, Long id){
        try {
            ItemScannerExport result = new ItemScannerExport();
            ImportWareHouse importWareHouse = importWareHouseRepository.findByCompanyIdWorkAndIdAndDeleteFlg(cid, id, Constants.DELETE_FLG.NON_DELETE).orElse(new ImportWareHouse());
            ItemsResponseDTO itemsResponseDTO = itemsService.getById(cid, "", importWareHouse.getIdItems());
            BeanUtils.copyProperties(itemsResponseDTO, result);
            result.setIdImportWareHouse(importWareHouse.getId());
            result.setNumberBox(importWareHouse.getNumberBox());
            result.setTotalPrice(importWareHouse.getTotalPrice());
            result.setQuantity(importWareHouse.getQuantity());
            result.setQualityExport(exportWareHouseService.qualityExport(cid, importWareHouse.getIdReceiptImport(), result.getId(), 1));
            return  result;
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public ImportWareHouse getById(Long cid, Long id){
        return importWareHouseRepository.findByCompanyIdWorkAndIdAndDeleteFlg(cid, id, Constants.DELETE_FLG.NON_DELETE).orElse(new ImportWareHouse());
    }

    public List<ImportWareHouse> getByIdtems(Long cid, Long idItems){
        try {
            return importWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdWorkOrderByDateExpiredAsc(Constants.DELETE_FLG.NON_DELETE, idItems, cid);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public List<ImportWareHouse> getByIdtemsInWareHouse(Long cid, Long idItems, Long idWareHouse){
        try {
            return importWareHouseRepository.findAllByDeleteFlgAndIdItemsAndCompanyIdWorkOrderByDateExpiredAscInWareHouse(Constants.DELETE_FLG.NON_DELETE, idItems, cid, idWareHouse);
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<ImportWareHouseResponseDTO> listImport(Long cid, Integer status, String search, ItemsSearchDto itemsSearchDto, Pageable pageable){
        try {
            Page<ImportWareHouse> pageSearch = importWareHouseRepository.listImportWareHouse(cid, status, search, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(),  pageable);
            List<ImportWareHouseResponseDTO> responseDTOS = new ArrayList<>();
            for(ImportWareHouse item : pageSearch.getContent()){
                ImportWareHouseResponseDTO importWareHouseResponseDTO = new ImportWareHouseResponseDTO();
                BeanUtils.copyProperties(item, importWareHouseResponseDTO);
                importWareHouseResponseDTO.setIdReceiptImport(item.getIdReceiptImport());
                if(item.getIdItems() != null){
                    ItemsResponseDTO items = itemsService.getById(cid, "", item.getIdItems());
                    importWareHouseResponseDTO.setItemName(items.getName());
                }
                if(importWareHouseResponseDTO.getIdReceiptImport() != null){
                    importWareHouseResponseDTO.setReceiptImportWareHouse(receiptImportWareHouseRepository.findByIdAndCompanyIdAndDeleteFlg(importWareHouseResponseDTO.getIdReceiptImport(), cid, Constants.DELETE_FLG.NON_DELETE).orElse(new ReceiptImportWareHouse()));
                    importWareHouseResponseDTO.setQuantityItems(item.getQuantity() * item.getNumberBox());
                    importWareHouseResponseDTO.setTotalPrice(item.getTotalPrice());
                    FullName fullName = userService.getFullName(cid, item.getCreateBy());
                    FullName fullNameUpDate = userService.getFullName(cid,item.getUpdateBy());
                    if(status == 0){
                        importWareHouseResponseDTO.setUpdateByName(fullNameUpDate.getFirstName() + " "  + fullNameUpDate.getLastName());
                        importWareHouseResponseDTO.setUpdateDate(item.getModifiedDate());
                    }
                    importWareHouseResponseDTO.setCreatByName(fullName.getFirstName() + " " + fullName.getLastName());
                    importWareHouseResponseDTO.setCreateDate(item.getCreateDate());
                }
                responseDTOS.add(importWareHouseResponseDTO);
            }
            return new PageImpl(responseDTOS, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            logger.error(ex);
            throw new RuntimeException(ex);
        }
    }

    public ImportWareHouse findByCompanyIdAndIdReceiptImportAndIdItems(Long cid, Long idReceiptImport, Long idItems){
        return importWareHouseRepository.findByCompanyIdWorkAndIdReceiptImportAndIdItemsAndDeleteFlg(cid, idReceiptImport, idItems, Constants.DELETE_FLG.NON_DELETE).orElse(new ImportWareHouse());
    }
}
