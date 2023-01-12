package vn.clmart.manager_service.api.export;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.clmart.manager_service.service.PDFGeneratorService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/pdf")
public class PDFExportController {
    private final PDFGeneratorService pdfGeneratorService;

    public PDFExportController(PDFGeneratorService pdfGeneratorService) {
        this.pdfGeneratorService = pdfGeneratorService;
    }

    @GetMapping("/export/{idReceiptExport}/{cid}")
    public void generatePDFExport(HttpServletResponse response, @PathVariable("idReceiptExport") Long idReceiptExport, @PathVariable("cid") Long cid) throws Exception {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=phieuxuat_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.pdfGeneratorService.exportWareHouse(response, idReceiptExport, cid);
    }

    @GetMapping("/import/{idReceiptImport}/{cid}")
    public void generatePDFImport(HttpServletResponse response, @PathVariable("idReceiptImport") Long idReceiptExport, @PathVariable("cid") Long cid) throws Exception {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=phieuxuat_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.pdfGeneratorService.importWareHouse(response, idReceiptExport, cid);
    }

    @GetMapping("/order/{id}/{cid}")
    public void generatePDFOrder(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("cid") Long cid) throws Exception {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=donhang_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);

        this.pdfGeneratorService.orderExport(response, id, cid);
    }

    @GetMapping("/orderItem/{id}/{cid}")
    public void generatePDFOrderItem(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("cid") Long cid) throws Exception {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=xuathang_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        this.pdfGeneratorService.orderExportIem(response, id, cid);
    }

    @GetMapping("/barcode/{id}/{cid}")
    public void generateBarcodeItem(HttpServletResponse response, @PathVariable("id") Long id, @PathVariable("cid") Long cid) throws Exception {
        response.setContentType("application/pdf");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd:hh:mm:ss");
        String currentDateTime = dateFormatter.format(new Date());
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=barocde_" + currentDateTime + ".pdf";
        response.setHeader(headerKey, headerValue);
        this.pdfGeneratorService.exportBarcodeItems(response, id, cid);
    }


}
