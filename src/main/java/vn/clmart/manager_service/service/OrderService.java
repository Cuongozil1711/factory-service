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
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.dto.request.OrderItemResponseDTO;
import vn.clmart.manager_service.dto.request.OrderResponseDTO;
import vn.clmart.manager_service.model.*;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.untils.Constants;
import vn.clmart.manager_service.untils.DateUntils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {

    @Autowired
    ExportWareHouseService exportWareHouseService;

    @Autowired
    @Lazy
    ImportWareHouseService importWareHouseService;

    @Autowired
    OrderRepositorry orderRepositorry;

    @Autowired
    ItemsService itemsService;

    @Autowired
    CustomerService customerService;

    @Autowired
    PriceItemsRepository priceItemsRepository;

    @Autowired
    DetailsItemOrderRepository detailsItemOrderRepository;

    @Autowired
    PromotionService promotionService;

    @Autowired
    ItemsDonateRepository itemsDonateRepository;

    @Autowired
    ConditionRepository conditionRepository;

    @Autowired
    UserService userService;

    @Autowired
    ReasonService reasonService;

    @Autowired
    BillRepositorry billRepositorry;

    @Autowired
    CompanyRepository companyRepository;

    public Order createOrder(OrderDto orderDto, Long cid, String uid){
        try {
            Order order = Order.of(orderDto, cid, uid);
            List<Order> order1 = orderRepositorry.findAll();
            Company company = companyRepository.findByIdAndDeleteFlg(order.getCompanyId(), Constants.DELETE_FLG.NON_DELETE).orElse(new Company());
            order.setCode("OR" + company.getCode() + "" + (order1.size() + 1));
            if(!orderRepositorry.findAllByCompanyIdAndDeleteFlgAndCode(cid, Constants.DELETE_FLG.NON_DELETE, orderDto.getCode()).isEmpty()){
                throw new BusinessException("Mã đã tồn tại");
            }
            // dat hang
            order = orderRepositorry.save(order);
            // check so luong dat hang
            List<DetailsItemOrder> detailsItem = new ArrayList<>();
            Double priceTotal = 0d;
            for(DetailsItemOrderDto items : orderDto.getDetailsItemOrders()){
                items.setIdOrder(order.getId());
                DetailsItemOrder dItems = DetailsItemOrder.of(items, cid, uid);
                Long idItems = items.getIdItems();
                Integer qualityItems = checkQualityItem(idItems, cid, uid); // check trong kho xem còn bao nhiêu
                PriceItems priceItems = priceItemsRepository.findByIdItemsAndDeleteFlgAndDvtCode(idItems, Constants.DELETE_FLG.NON_DELETE, items.getDvtCode()).orElse(null);
                Double priceSale = priceItems.getPriceItems() *items.getQuality().doubleValue();
                Double totalPrice = priceItems.getPriceItems() *items.getQuality().doubleValue();
                // kiem tra khuyen mai
                if(items.getIdPromotion() != null){
                    Promotion promotion = promotionService.getById(cid, uid, items.getIdPromotion());
                    if(promotion.getTypePromotion().equals("donate")){
                        // tặng kèm sản phẩm
                        ItemsDonate itemsDonate = itemsDonateRepository.findByIdAndCompanyId(promotion.getIdItemsDonate(), cid).orElse(null);
                        if(itemsDonate != null){
                            PriceItems priceItemsDonate = priceItemsRepository.findByIdItemsAndDeleteFlgAndDvtCode(itemsDonate.getIdItems(), Constants.DELETE_FLG.NON_DELETE, items.getDvtCode()).orElse(null);
                            ItemsResponseDto itemsResponseDto = itemsService.getByIdtemsAndQuality(cid ,uid, itemsDonate.getIdItems());
                            ExportWareHouseDto exportWareHouseDto = new ExportWareHouseDto();
                            exportWareHouseDto.setNumberBox(1);
                            exportWareHouseDto.setQuantity(itemsDonate.getQuanlity());
                            exportWareHouseDto.setIdReceiptExport(null);
                            exportWareHouseDto.setIdItems(itemsDonate.getIdItems());
                            exportWareHouseDto.setIdReceiptImport(itemsResponseDto.getReceiptImportWareHouse().getId());
                            exportWareHouseDto.setDvtCode("000");
                            exportWareHouseDto.setTotalPrice(priceItemsDonate.getPriceItems() *itemsDonate.getQuanlity().doubleValue());
                            exportWareHouseService.saleExport(exportWareHouseDto, cid, uid);

                            DetailsItemOrder dItemNew = new DetailsItemOrder();
                            dItemNew.setDvtCode("000");
                            dItemNew.setQuality(itemsDonate.getQuanlity());
                            dItemNew.setTotalPrice(priceItemsDonate.getPriceItems() *itemsDonate.getQuanlity().doubleValue());
                            dItemNew.setIdOrder(items.getIdOrder());
                            dItemNew.setIdItems(itemsDonate.getIdItems());
                            dItemNew.setType("SALE");
                            dItemNew.setCreateBy(uid);
                            dItemNew.setCompanyId(cid);
                            detailsItem.add(dItemNew);
                        }
                    }
                    else if(promotion.getTypePromotion().equals("product")){
                        if(promotion.getType().equals("percent")){
                            priceSale = totalPrice - totalPrice * promotion.getPercent() / 100;
                        }
                        else if(promotion.getType().equals("vnd")){
                            priceSale = totalPrice - promotion.getPrice();
                        }
                    }
                }

                Integer qualitySold = 0;
                if(priceItems == null){
                    throw new BusinessException("Không tìm " + itemsService.getById(cid, uid, idItems).getName() + " đơn vị tính");
                }
                else{
                    qualitySold = items.getQuality();
                }
                if(qualitySold * priceItems.getQuality() > qualityItems){
                    throw new BusinessException("Mặt hàng " + itemsService.getById(cid, uid, idItems).getName() + " không còn trong kho");
                }
                dItems.setTotalPrice(priceSale); // tổng tiền theo số lượng, giá sản phảm
                dItems.setQuality(qualitySold);
                dItems.setType("SOLD");
                detailsItem.add(dItems);

                // Lưu vào xuất kho
                ExportWareHouseDto exportWareHouseDto = new ExportWareHouseDto();
                exportWareHouseDto.setNumberBox(items.getQuality());
                exportWareHouseDto.setQuantity(priceItems.getQuality());
                exportWareHouseDto.setIdReceiptExport(null);
                exportWareHouseDto.setIdItems(idItems);
                ImportWareHouse importWareHouse = importWareHouseService.getById(cid, items.getIdImportWareHouse());
                exportWareHouseDto.setIdReceiptImport(importWareHouse.getIdReceiptImport());
                exportWareHouseDto.setDvtCode(items.getDvtCode());
                exportWareHouseDto.setIdOrder(order.getId());
                exportWareHouseDto.setTotalPrice(priceSale);
                priceTotal+=totalPrice;
                exportWareHouseService.orderToExport(exportWareHouseDto, cid, uid);
            }
            detailsItemOrderRepository.saveAll(detailsItem);
            // tao hoa don
            Bill bill = new Bill();
            bill.setIdCustomer(orderDto.getIdCustomer());
            bill.setTotalPrice(priceTotal.longValue());
            bill.setTotalPriceCustomer(orderDto.getTotalPriceCustomer());
            bill.setIdOrder(order.getId());
            bill.setNamePayment(orderDto.getNamePayment());
            if(priceTotal > orderDto.getTotalPriceCustomer() || orderDto.getTotalPriceCustomer() == 0){
                bill.setState(Constants.BILL_EMPLOYEE.PENDING.name());
            }
            else bill.setState(Constants.BILL_EMPLOYEE.COMPLETE.name());
            bill.setCompanyId(cid);
            bill.setCreateBy(uid);
            billRepositorry.save(bill);
            return order;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Boolean restoreOrder(Long cid, String uid, Long id){
        try {
            Order order = orderRepositorry.findByCompanyIdAndIdAndDeleteFlg(cid, id, Constants.DELETE_FLG.DELETE).orElse(null);
            if(order != null){
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                Date dateNow = cal.getTime();
                if(dateNow.after(order.getCreateDate())){
                    return false;
                }
                else{
                    // update Order
                    order.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                    order.setUpdateBy(uid);
                    orderRepositorry.save(order);

                    // update price item don hang
                    List<DetailsItemOrder> detailsItemOrders = detailsItemOrderRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.DELETE, id);
                    detailsItemOrders.forEach(detailsItemOrder -> {
                        detailsItemOrder.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                        detailsItemOrder.setUpdateBy(uid);
                    });
                    detailsItemOrderRepository.saveAll(detailsItemOrders);

                    Bill bill = billRepositorry.findByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.DELETE, order.getId()).orElse(null);
                    if(bill != null){
                        bill.setDeleteFlg(Constants.DELETE_FLG.NON_DELETE);
                        billRepositorry.save(bill);
                    }

                    // check xuất kho để khôi phục
                    exportWareHouseService.restoreExportByOrderId(cid, uid, order.getId());
                }
            }
        }
        catch (Exception ex){
            throw new BusinessException(ex.getMessage());
        }
        return true;
    }

    public boolean deleteOrder(Long cid, String uid, Long id, Long reasonId){
        Order order = orderRepositorry.findByCompanyIdAndIdAndDeleteFlg(cid, id, Constants.DELETE_FLG.NON_DELETE).orElse(null);
        if(order != null){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            Date dateNow = cal.getTime();
            if(dateNow.after(order.getCreateDate())){
                return false;
            }
            else{
                // update Order
                order.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                order.setReasonId(reasonId);
                order.setUpdateBy(uid);
                orderRepositorry.save(order);

                // update price item don hang
                List<DetailsItemOrder> detailsItemOrders = detailsItemOrderRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, id);
                detailsItemOrders.forEach(detailsItemOrder -> {
                    detailsItemOrder.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                    detailsItemOrder.setUpdateBy(uid);
                });
                detailsItemOrderRepository.saveAll(detailsItemOrders);

                Bill bill = billRepositorry.findByCompanyIdAndDeleteFlgAndIdOrder(cid, Constants.DELETE_FLG.NON_DELETE, order.getId()).orElse(null);
                if(bill != null){
                    bill.setDeleteFlg(Constants.DELETE_FLG.DELETE);
                    billRepositorry.save(bill);
                }

                // check xuất kho để xóa
                exportWareHouseService.deleteExportByOrderId(cid, uid, order.getId());
            }
        }
        return true;
    }

    public Integer checkQualityItem(Long id, Long cid, String uid){
        Integer qualityImport = 0;
        List<ImportWareHouse> importWareHouseList = importWareHouseService.findAllByItems(cid, uid, id);
        for(ImportWareHouse importWareHouse : importWareHouseList){
            if(importWareHouse.getNumberBox() == 0) {
                qualityImport += importWareHouse.getQuantity();
            }
            else{
                qualityImport += importWareHouse.getQuantity() * importWareHouse.getNumberBox();
            }
        }
        Integer qualityExport = 0;
        List<ExportWareHouse> exportWareHouseList = exportWareHouseService.findAllByItems(cid, uid, id);
        for(ExportWareHouse exportWareHouse : exportWareHouseList){
            if(exportWareHouse.getNumberBox() == 0) {
                qualityExport += exportWareHouse.getQuantity();
            }
            else{
                qualityExport += exportWareHouse.getQuantity() * exportWareHouse.getNumberBox();
            }
        }
        return qualityImport - qualityExport;
    }

    public OrderItemResponseDTO getOrderById(Long cid, Long id){
        try {
            Order order = orderRepositorry.findByCompanyIdAndId(cid, id).orElse(null);
            if(order != null){
                OrderItemResponseDTO itemsResponseDTO = new OrderItemResponseDTO();
                BeanUtils.copyProperties(order, itemsResponseDTO);
                if(order.getIdCustomer() != null){
                    CustomerDto customerDto = new CustomerDto();
                    BeanUtils.copyProperties(customerService.getById(cid , "", order.getIdCustomer()), customerDto);
                    itemsResponseDTO.setCustomerDto(customerDto);
                }
                if(order.getCreateBy() != null){
                    FullName fullName = userService.getFullName(cid, order.getCreateBy());
                    itemsResponseDTO.setCreateBy(fullName.getFirstName() + " " + fullName.getLastName());
                }
                List<DetailsItemOrder> orders = detailsItemOrderRepository.findAllByCompanyIdAndIdOrder(cid, id);
                Double totalPrice = 0d;
                Double totalSale = 0d;
                Integer size = 0;
                List<DetailsItemOrderDto> list = new ArrayList<>();
                List<DetailsItemOrderDto> listSale = new ArrayList<>();
                for(int i=0; i < orders.size(); i++){
                    DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                    DetailsItemOrder item = orders.get(i);
                    BeanUtils.copyProperties(item, detailsItemOrderDto);
                    detailsItemOrderDto.setTotalSale(item.getTotalPrice());
                    if(item.getIdItems() != null){
                        ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(cid, "", item.getIdItems());
                        List<ExportWareHouse> exportWareHouses = exportWareHouseService.findAllByCompanyIdAndIdOrderAndIdItemsAndDvtCode(cid, item.getIdItems(), item.getIdOrder(), item.getDvtCode());
                        if(exportWareHouses.size() != 0){
                            itemsResponseDTO1.setTotalSold(item.getQuality().longValue());
                            size += item.getQuality();
                            detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                            if(item.getType().equals("SOLD")){
                                totalSale += item.getTotalPrice();
                                totalPrice += exportWareHouses.get(0).getTotalPrice();
                                detailsItemOrderDto.setTotalPrice(exportWareHouses.get(0).getTotalPrice());
                            }
                            else{
                                detailsItemOrderDto.setTotalPrice(item.getTotalPrice());
                            }
                        }
                    }
                    if(item.getType().equals("SALE")) listSale.add(detailsItemOrderDto);
                    else if(item.getType().equals("SOLD")) list.add(detailsItemOrderDto);
                }
                Bill bill = billRepositorry.findByCompanyIdAndIdOrder(cid, order.getId()).orElse(new Bill());
                BillDto billDto = new BillDto();
                BeanUtils.copyProperties(bill, billDto);
                itemsResponseDTO.setBillDto(billDto);
                itemsResponseDTO.setDetailsItemOrdersSale(listSale);
                itemsResponseDTO.setDetailsItemOrders(list);
                itemsResponseDTO.setTotalSale(totalSale);
                itemsResponseDTO.setTotalPrice(totalPrice);
                itemsResponseDTO.setQuantity(size);
                if(order.getDeleteFlg() == 0 && order.getReasonId() != null){
                    Reason reason = reasonService.getById(cid, "", order.getReasonId());
                    itemsResponseDTO.setReasonName(reason.getName());
                }
                return itemsResponseDTO;
            }
            return null;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Order> search(Long cid, Pageable pageable, String search, ItemsSearchDto itemsSearchDto,Integer status){
        try {
            if(itemsSearchDto.getStartDate() == null) itemsSearchDto.setStartDate(DateUntils.minDate());
            else itemsSearchDto.setStartDate(DateUntils.getStartOfDate(itemsSearchDto.getStartDate()));
            if(itemsSearchDto.getEndDate() == null) itemsSearchDto.setEndDate(DateUntils.maxDate());
            else itemsSearchDto.setEndDate(DateUntils.getEndOfDate(itemsSearchDto.getEndDate()));
            Page<Order> pageSearch = orderRepositorry.findAllByCompanyId(cid, search, status, itemsSearchDto.getStartDate(), itemsSearchDto.getEndDate(), pageable);
            List<Order> list = pageSearch.getContent();
            List<OrderResponseDTO> responseDTOList = new ArrayList<>();
            for(Order items : list){
                OrderResponseDTO itemsResponseDTO = new OrderResponseDTO();
                BeanUtils.copyProperties(items, itemsResponseDTO);
                if(items.getIdCustomer() != null){
                    CustomerDto customerDto = new CustomerDto();
                    BeanUtils.copyProperties(customerService.getById(cid , "", items.getIdCustomer()), customerDto);
                    itemsResponseDTO.setCustomerDto(customerDto);
                }
                if(status == 0 && items.getReasonId() != null){
                    itemsResponseDTO.setReasonName(reasonService.getById(cid, "", items.getReasonId()).getName());
                }
                Double totalPrice = 0d;
                Integer size = 0;
                List<DetailsItemOrder> orders = detailsItemOrderRepository.findAllByCompanyIdAndDeleteFlgAndIdOrder(cid, status, items.getId());
                for(int i=0; i < orders.size(); i++){
                    DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                    DetailsItemOrder item = orders.get(i);
                    BeanUtils.copyProperties(item, detailsItemOrderDto);
                    if(item.getIdItems() != null && item.getType().equals("SOLD")){
                        ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(cid, "", item.getIdItems());
                        itemsResponseDTO1.setTotalSold(item.getQuality().longValue());
                        size += item.getQuality();
                        detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                        totalPrice += item.getTotalPrice();
                    }
                }
                itemsResponseDTO.setTotalPrice(totalPrice);
                itemsResponseDTO.setQuantity(size);

                responseDTOList.add(itemsResponseDTO);
            }
            return new PageImpl(responseDTOList, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<OrderResponseDTO>  getListByEmpId(Long cid, Long empId){
        try {
            List<Order> list = orderRepositorry.findAllByCompanyIdAndIdCustomer(cid, empId);
            List<OrderResponseDTO> responseDTOList = new ArrayList<>();
            for(Order items : list){
                OrderResponseDTO itemsResponseDTO = new OrderResponseDTO();
                BeanUtils.copyProperties(items, itemsResponseDTO);
                if(items.getIdCustomer() != null){
                    CustomerDto customerDto = new CustomerDto();
                    BeanUtils.copyProperties(customerService.getById(cid , "", items.getIdCustomer()), customerDto);
                    itemsResponseDTO.setCustomerDto(customerDto);
                }
                Double totalPrice = 0d;
                Integer size = 0;
                List<DetailsItemOrder> orders = detailsItemOrderRepository.findAllByCompanyIdAndIdOrder(cid, items.getId());
                for(int i=0; i < orders.size(); i++){
                    DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                    DetailsItemOrder item = orders.get(i);
                    BeanUtils.copyProperties(item, detailsItemOrderDto);
                    if(item.getIdItems() != null){
                        ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(cid, "", item.getIdItems());
                        itemsResponseDTO1.setTotalSold(item.getQuality().longValue());
                        size += item.getQuality();
                        detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                        totalPrice += item.getTotalPrice();
                    }
                }
                if(items.getReasonId() != null){
                    itemsResponseDTO.setReasonName(reasonService.getById(cid, "", items.getReasonId()).getName());
                }
                itemsResponseDTO.setTotalPrice(totalPrice);
                itemsResponseDTO.setQuantity(size);

                responseDTOList.add(itemsResponseDTO);
            }
            return responseDTOList;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<OrderResponseDTO>  getListByCreateBy(Long cid, String createBy){
        try {
            List<Order> list = orderRepositorry.findAllByCompanyIdAndCreateBy(cid, createBy);
            List<OrderResponseDTO> responseDTOList = new ArrayList<>();
            for(Order items : list){
                OrderResponseDTO itemsResponseDTO = new OrderResponseDTO();
                BeanUtils.copyProperties(items, itemsResponseDTO);
                if(items.getIdCustomer() != null){
                    CustomerDto customerDto = new CustomerDto();
                    BeanUtils.copyProperties(customerService.getById(cid , "", items.getIdCustomer()), customerDto);
                    itemsResponseDTO.setCustomerDto(customerDto);
                }
                Double totalPrice = 0d;
                Integer size = 0;
                List<DetailsItemOrder> orders = detailsItemOrderRepository.findAllByCompanyIdAndIdOrder(cid, items.getId());
                for(int i=0; i < orders.size(); i++){
                    DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                    DetailsItemOrder item = orders.get(i);
                    BeanUtils.copyProperties(item, detailsItemOrderDto);
                    if(item.getIdItems() != null){
                        ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(cid, "", item.getIdItems());
                        itemsResponseDTO1.setTotalSold(item.getQuality().longValue());
                        size += item.getQuality();
                        detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                        totalPrice += item.getTotalPrice();
                    }
                }
                if(items.getReasonId() != null){
                    itemsResponseDTO.setReasonName(reasonService.getById(cid, "", items.getReasonId()).getName());
                }
                itemsResponseDTO.setTotalPrice(totalPrice);
                itemsResponseDTO.setQuantity(size);

                responseDTOList.add(itemsResponseDTO);
            }
            return responseDTOList;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public PageImpl<Order> searchByDate(Long cid, Pageable pageable, String search, String state){
        try {
            Date date = null;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.MINUTE,0);
            if(state.equals("now")){
                date = calendar.getTime();
            }
            else if(state.equals("week")){
                calendar.add(Calendar.DATE, -7);
                date = calendar.getTime();
            }
            else if(state.equals("month")){
                calendar.add(Calendar.MONTH, -1);
                date = calendar.getTime();
            }
            else{
                date = null;
            }
            Page<Order> pageSearch = orderRepositorry.getAllByCompanyId(cid, search, date, pageable);
            List<Order> list = pageSearch.getContent();
            List<OrderResponseDTO> responseDTOList = new ArrayList<>();
            for(Order items : list){
                OrderResponseDTO itemsResponseDTO = new OrderResponseDTO();
                BeanUtils.copyProperties(items, itemsResponseDTO);
                if(items.getIdCustomer() != null){
                    CustomerDto customerDto = new CustomerDto();
                    BeanUtils.copyProperties(customerService.getById(cid , "", items.getIdCustomer()), customerDto);
                    itemsResponseDTO.setCustomerDto(customerDto);
                }
                Double totalPrice = 0d;
                Integer size = 0;
                List<DetailsItemOrder> orders = detailsItemOrderRepository.findAllByCompanyIdAndIdOrder(cid, items.getId());
                for(int i=0; i < orders.size(); i++){
                    DetailsItemOrderDto detailsItemOrderDto = new DetailsItemOrderDto();
                    DetailsItemOrder item = orders.get(i);
                    BeanUtils.copyProperties(item, detailsItemOrderDto);
                    if(item.getIdItems() != null){
                        ItemsResponseDTO itemsResponseDTO1 = itemsService.getById(cid, "", item.getIdItems());
                        itemsResponseDTO1.setTotalSold(item.getQuality().longValue());
                        size += item.getQuality();
                        detailsItemOrderDto.setItemsResponseDTO(itemsResponseDTO1);
                        totalPrice += item.getTotalPrice();
                    }
                }
                if(items.getReasonId() != null){
                    itemsResponseDTO.setReasonName(reasonService.getById(cid, "", items.getReasonId()).getName());
                }
                itemsResponseDTO.setTotalPrice(totalPrice);
                itemsResponseDTO.setQuantity(size);

                responseDTOList.add(itemsResponseDTO);
            }
            return new PageImpl(responseDTOList, pageable, pageSearch.getTotalElements());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<Map<String, Object>> getEmpoyeeOrder(Long cid){
        try {
            List<Map<String, Object>> listOrder = orderRepositorry.getOrdersByEmployee(cid, Constants.DELETE_FLG.NON_DELETE);
            return listOrder;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public List<OrderItemResponseDTO> getItemOrder(Long cid, String uid){
        try {
            List<Order> listOrder = orderRepositorry.getItemOrder(cid, uid, Constants.DELETE_FLG.NON_DELETE);
            return listOrder.stream().map(order -> getOrderById(cid, order.getId())).collect(Collectors.toList());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Long getRevenueNow(Long cid, String uid){
        try {
            Calendar calendar = new GregorianCalendar();
            List<Order> listOrderNow = orderRepositorry.getItemOrderByDateOrder(cid, calendar.getTime(), Constants.DELETE_FLG.NON_DELETE);
            return this.getRevenuePrice(listOrderNow, cid);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Long getRevenueAfter(Long cid, String uid){
        try {
            Calendar calendar = new GregorianCalendar();
            calendar.add(Calendar.MONTH, -1);
            List<Order> listOrderAfter = orderRepositorry.getItemOrderByDateOrder(cid, calendar.getTime(), Constants.DELETE_FLG.NON_DELETE);
            return this.getRevenuePrice(listOrderAfter, cid);
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private Long getRevenuePrice(List<Order> orderList, Long cid){
        List<OrderItemResponseDTO> list = orderList.stream().map(order -> getOrderById(cid, order.getId())).collect(Collectors.toList());
        Long sumPrice = 0l;
        for(OrderItemResponseDTO orderItemResponseDTO : list){
            sumPrice += orderItemResponseDTO.getTotalPrice().longValue();
        }
        return sumPrice;
    }

    public Integer[] getCountByDate(Long cid, String uid){
        try {
            Integer[] result = new Integer[7];
            for(int i = 0; i < 7; i++){
                Calendar calendar = new GregorianCalendar();
                calendar.set(Calendar.HOUR, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.MINUTE,0);
                calendar.add(Calendar.DATE, -i);
                result[7-i-1] = orderRepositorry.getCountByDate(cid, Constants.DELETE_FLG.NON_DELETE, calendar.getTime());
            }
            return result;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

}
