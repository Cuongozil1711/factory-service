package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.request.BillResponseDTO;
import vn.clmart.manager_service.model.Bill;
import vn.clmart.manager_service.repository.BillRepositorry;
import vn.clmart.manager_service.untils.Constants;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class BillService {

    @Autowired
    BillRepositorry billRepositorry;

    @Autowired
    OrderService orderService;

    public List<BillResponseDTO> search(Long cid){
        try {
            return billRepositorry.findAllByCompanyIdAndStateAndDeleteFlg(cid, Constants.BILL_EMPLOYEE.PENDING.name() ,Constants.DELETE_FLG.NON_DELETE).stream().map(bill -> {
                BillResponseDTO billResponseDTO = new BillResponseDTO();
                billResponseDTO.setBill(bill);
                if(bill.getIdOrder() != null){
                    billResponseDTO.setOrderItemResponseDTO(orderService.getOrderById(cid, bill.getIdOrder()));
                }
                return billResponseDTO;
            }).collect(Collectors.toList());
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public boolean updateBill(Long id, Long cid){
        try {
            Bill bill = billRepositorry.findByCompanyIdAndId(cid, id).orElse(null);
            if(bill != null){
                bill.setTotalPriceCustomer(bill.getTotalPrice());
                bill.setState(Constants.BILL_EMPLOYEE.COMPLETE.name());
                billRepositorry.save(bill);
            }
            else return false;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return true;
    }
}
