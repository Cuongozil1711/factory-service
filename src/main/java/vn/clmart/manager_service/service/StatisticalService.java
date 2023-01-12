package vn.clmart.manager_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.clmart.manager_service.dto.request.EmployeesOrderResponse;
import vn.clmart.manager_service.dto.request.StatisticalResponseDTO;
import vn.clmart.manager_service.model.Employee;
import vn.clmart.manager_service.model.FullName;
import vn.clmart.manager_service.repository.*;
import vn.clmart.manager_service.untils.Constants;

import java.util.*;
@Service
@Transactional
public class StatisticalService {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    OrderRepositorry orderRepositorry;

    @Autowired
    OrderService orderService;

    @Autowired
    ReceiptImportWareHouseRepository receiptImportWareHouseRepository;

    @Autowired
    ReceiptExportWareHouseRepository receiptExportWareHouseRepository;

    @Autowired
    EmployeeRepository employeeRepository;

    @Autowired
    UserService userService;

    @Autowired
    ExportWareHouseRepository exportWareHouseRepository;

    @Autowired
    ImportWareHouseRepository importWareHouseRepository;

    public Map<String, StatisticalResponseDTO> statistical(Long cid, String uid){
        try {
            Map<String, StatisticalResponseDTO> statisticaList = new HashMap<>();
            // thống kê khách hàng
            StatisticalResponseDTO statisticalCustomer = new StatisticalResponseDTO();
            statisticalCustomer.setSumQuality(customerRepository.findAllByDeleteFlg(Constants.DELETE_FLG.NON_DELETE).size());
            statisticalCustomer.setSumNow(customerRepository.getCount());
            statisticaList.put("statisticalCustomer", statisticalCustomer);
            // thống kê đơn hàng
            StatisticalResponseDTO statisticalOrder = new StatisticalResponseDTO();
            statisticalOrder.setSumQuality(orderRepositorry.findAllByCompanyIdAndDeleteFlg(cid, Constants.DELETE_FLG.NON_DELETE).size());
            statisticalOrder.setSumNow(orderRepositorry.getCount(cid, Constants.DELETE_FLG.NON_DELETE));
            statisticaList.put("statisticalOrder", statisticalOrder);
            // thống kê doanh thu Revenue
            StatisticalResponseDTO statisticalRevenue = new StatisticalResponseDTO();
            statisticalRevenue.setSumPriceNow(orderService.getRevenueNow(cid, uid));
             statisticalRevenue.setSumPriceAfter(orderService.getRevenueAfter(cid, uid));
            statisticaList.put("statisticalRevenue", statisticalRevenue);
            // nhập kho
            StatisticalResponseDTO statisticalImport = new StatisticalResponseDTO();
            statisticalImport.setTotalImport(receiptImportWareHouseRepository.getCountImport(cid, Constants.DELETE_FLG.NON_DELETE));
            statisticaList.put("totalImport", statisticalImport);
            // xuất kho
            StatisticalResponseDTO statisticalExport = new StatisticalResponseDTO();
            statisticalExport.setTotalExport(receiptExportWareHouseRepository.getCountExport(cid, Constants.DELETE_FLG.NON_DELETE) + orderRepositorry.getSumOrder(cid, Constants.DELETE_FLG.NON_DELETE));
            statisticaList.put("totalExport", statisticalExport);
            return statisticaList;
        }
        catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public Map<String, Object> getCountOrderAndImport(Long cid, String uid, Integer year){
        try {
            Integer[] exportItems = new Integer[12];
            Integer[] importItems = new Integer[12];
            for(int i=1; i <= 12; i++){
                exportItems[i-1] = receiptExportWareHouseRepository.getImportForMonth(cid, Constants.DELETE_FLG.NON_DELETE, i, year) + orderRepositorry.getOrderForMonth(cid, Constants.DELETE_FLG.NON_DELETE, i, year);
                importItems[i-1] = receiptImportWareHouseRepository.getImportForMonth(cid, Constants.DELETE_FLG.NON_DELETE, i, year);
            }
            HashMap<String, Object> response = new HashMap<>();
            response.put("exportItems", exportItems);
            response.put("importItems", importItems);
            return response;
        }
        catch (Exception ex){
            throw new RuntimeException();
        }
    }

    public Integer[] getCountOrder(Long cid, String uid, Integer year){
        try {
            Integer[] exportItems = new Integer[12];
            for(int i=1; i <= 12; i++){
                exportItems[i-1] = orderRepositorry.getOrderForMonth(cid, Constants.DELETE_FLG.NON_DELETE, i, year);
            }
            return exportItems;
        }
        catch (Exception ex){
            throw new RuntimeException();
        }
    }

    public List<EmployeesOrderResponse> getOrderByEmployee(Long cid, String uid){
        try {
            List<Employee> employees = employeeRepository.findAllByDeleteFlgAndCompanyId(Constants.DELETE_FLG.NON_DELETE, cid);
            List<EmployeesOrderResponse> employeesOrderResponses = new ArrayList<>();
            employees.forEach(employee -> {
                EmployeesOrderResponse employeesOrderResponse = new EmployeesOrderResponse();
                if(employee.getIdUser() != null){
                    Integer countOrder = orderRepositorry.getOrderByEmployee(cid, Constants.DELETE_FLG.NON_DELETE, employee.getIdUser());
                    if(countOrder > 0){
                        employeesOrderResponse.setCountOrder(countOrder);
                        employeesOrderResponse.setImage(employee.getImage());
                        FullName fullName = userService.getFullName(cid, employee.getIdUser());
                        if(fullName != null)
                        employeesOrderResponse.setName(fullName.getFirstName() + " " + fullName.getLastName());
                        employeesOrderResponses.add(employeesOrderResponse);
                    }
                }
            });
            Collections.sort(employeesOrderResponses, (o1, o2) -> {
                return o1.getCountOrder() > o2.getCountOrder() ? -1 : 1;
            });
            return employeesOrderResponses;
        }
        catch (Exception ex){
            throw new RuntimeException();
        }
    }
}
