package vn.clmart.manager_service.service;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import vn.clmart.manager_service.dto.DetailsItemOrderDto;
import vn.clmart.manager_service.dto.ExportWareHouseListDto;
import vn.clmart.manager_service.dto.ImportListDataWareHouseDto;
import vn.clmart.manager_service.dto.ImportWareHouseDto;
import vn.clmart.manager_service.dto.request.ItemsResponseDTO;
import vn.clmart.manager_service.dto.request.OrderItemResponseDTO;
import vn.clmart.manager_service.model.ImportWareHouse;
import vn.clmart.manager_service.untils.ReadNumber;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class PDFGeneratorService {

    public static File fontFile = new File("./font/vuArial.ttf");
    public static File imageFile = new File("./image/logo.png");

    @Autowired
    ExportWareHouseService exportWareHouseService;

    @Autowired
    OrderService orderService;

    @Autowired
    WareHouseService wareHouseService;

    @Autowired
    CompanyService companyService;

    @Autowired
    ItemsService itemsService;

    @Autowired
    @Lazy
    ImportWareHouseService importWareHouseService;

    @Autowired
    SupplierService supplierService;


    public void exportWareHouse(HttpServletResponse response, Long idReceiptExport, Long cid) throws Exception {
        ExportWareHouseListDto exportWareHouseListDto = exportWareHouseService.getByIdReceiptExport(cid, "", idReceiptExport);
        Document document = new Document(PageSize.A4);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        OutputStream outputStream = response.getOutputStream();
        PdfWriter pdf  = PdfWriter.getInstance(document, outputStream);

        document.open();
        BaseFont courier = BaseFont.createFont(fontFile.getPath(),BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font myfont = new Font(courier, 18, Font.NORMAL);
        Paragraph paragraph = new Paragraph(new
                String(("Phiếu xuất kho").getBytes(StandardCharsets.UTF_8)), myfont);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontTitleName = new Font(courier, 12, Font.NORMAL);
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate= DateFor.format(exportWareHouseListDto.getData().get(0).getCreateDate());
        Paragraph paragraphDate = new Paragraph("Ngày xuất: " + stringDate, fontTitleName);
        paragraphDate.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraphCreateBy = new Paragraph("Người xuất: " + exportWareHouseListDto.getCreateByName(), fontTitleName);
        paragraphCreateBy.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraphName = new Paragraph("Địa chỉ: " + companyService.getById(cid, "", cid).getName() + " - " + wareHouseService.getById(cid,exportWareHouseListDto.getReceiptExportWareHouseDto().getIdWareHouse()).getName(), fontTitleName);
        paragraphName.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph3 = new Paragraph("Mã phiếu xuất: " + exportWareHouseListDto.getReceiptExportWareHouseDto().getCode(), fontTitleName);
        paragraph3.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph4 = new Paragraph("Tên phiếu xuất: " + exportWareHouseListDto.getReceiptExportWareHouseDto().getName(), fontTitleName);
        paragraph4.setAlignment(Paragraph.ALIGN_LEFT);


        Paragraph paragraph5 = new Paragraph( "Chi nhánh xuất tới: " + companyService.getById(exportWareHouseListDto.getReceiptExportWareHouseDto().getCompanyIdTo(), "", exportWareHouseListDto.getReceiptExportWareHouseDto().getCompanyIdTo()).getName() + " - " +  wareHouseService.getById(exportWareHouseListDto.getReceiptExportWareHouseDto().getCompanyIdTo(),exportWareHouseListDto.getReceiptExportWareHouseDto().getIdWareHouseTo()).getName(), fontTitleName);
        paragraph5.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph6 = new Paragraph();
        paragraph6.setSpacingBefore(10f);
        paragraph6.setSpacingAfter(10f);
        paragraph6.add (new Phrase("Chữ ký người xuất kho", fontTitleName));
        paragraph6.add (new Phrase("        Chữ ký người tiếp nhận", fontTitleName));
        paragraph6.add (new Phrase("        Chữ ký quản lý", fontTitleName));
        paragraph6.setFont(fontTitleName);
        paragraph6.setMultipliedLeading(2.0f);
        paragraph6.setSpacingAfter(10);
        paragraph6.setSpacingBefore(10);
        paragraph6.setAlignment(Paragraph.ALIGN_RIGHT);

        Paragraph paragraphSign = new Paragraph();
        paragraphSign.setSpacingBefore(10f);
        paragraphSign.setSpacingAfter(10f);
        paragraphSign.add (new Phrase(exportWareHouseListDto.getCreateByName(), fontTitleName));
        paragraphSign.add (new Phrase("                                        ", fontTitleName));
        paragraphSign.add (new Phrase("                                        ", fontTitleName));
        paragraphSign.setFont(fontTitleName);
        paragraphSign.setMultipliedLeading(8.0f);
        paragraphSign.setSpacingAfter(10);
        paragraphSign.setSpacingBefore(10);
        paragraphSign.setAlignment(Paragraph.ALIGN_RIGHT);

        PdfContentByte contB = pdf.getDirectContent();
        Barcode128 barCode = new Barcode128();
        barCode.setCode(idReceiptExport.toString());
        barCode.setCodeType(Barcode128.CODE128);

        Image image = barCode.createImageWithBarcode(contB, BaseColor.BLACK, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("ATCADO DOS PISOS\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 5));
        titulo.setPaddingTop(0);
        titulo.setAlignment(Element.ALIGN_CENTER);

        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 20;

        image.scalePercent(scaler);
        image.setPaddingTop(0);
        image.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph7 = new Paragraph();
        paragraph7.setSpacingBefore(10f);
        paragraph7.setSpacingAfter(10f);
        paragraph7.add (new Chunk(image, 0, 0, true));;
        paragraph7.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(paragraph7);
        document.add(paragraph);
        document.add(paragraphDate);
        document.add(paragraphCreateBy);
        document.add(paragraphName);
        document.add(paragraph3);
        document.add(paragraph4);
        document.add(paragraph5);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {0.5f, 2f, 1.5f, 1f, 1f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table, fontTitleName);
        writeTableData(table, exportWareHouseListDto.getData(), fontTitleName);

        AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        exportWareHouseListDto.getData().forEach(detailsItemOrderDto -> {
            Double priceItem = itemsService.getPriceItems(detailsItemOrderDto.getIdItems());
            totalPrice.updateAndGet(v -> v + priceItem*detailsItemOrderDto.getQuality()*detailsItemOrderDto.getNumberBox());
        });

        Paragraph paragraphTotal = new Paragraph( "Tổng tiền: " + formatter.format(totalPrice.get()) + " VND", fontTitleName);
        paragraphTotal.setAlignment(Paragraph.ALIGN_LEFT);
        paragraphTotal.setMultipliedLeading(2.0f);

        Locale locale = new Locale("vi");
        NumberFormat format =  NumberFormat.getCurrencyInstance(locale);
        Paragraph paragraphTotalRead = new Paragraph( "Tổng tiền viết bằng chữ: " + ReadNumber.numberToString(totalPrice.get()), fontTitleName);
        paragraphTotalRead.setAlignment(Paragraph.ALIGN_LEFT);

        document.add(table);
        document.add(paragraphTotal);
        document.add(paragraphTotalRead);
        document.add(paragraph6);
        document.add(paragraphSign);
        document.close();
    }

    public File exportWareHouse(ExportWareHouseListDto exportWareHouseListDto, Long idReceiptExport, Long cid) throws Exception {
        Document document = new Document(PageSize.A4);
        File f = new File("/var/ExportWareHouse.pdf");
        FileOutputStream outputStream = new FileOutputStream(f);
        PdfWriter pdf  = PdfWriter.getInstance(document, outputStream);

        document.open();
        BaseFont courier = BaseFont.createFont(fontFile.getPath(),BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font myfont = new Font(courier, 18, Font.NORMAL);
        Paragraph paragraph = new Paragraph(new
                String(("Phiếu xuất kho").getBytes(StandardCharsets.UTF_8)), myfont);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontTitleName = new Font(courier, 12, Font.NORMAL);
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate= DateFor.format(new Date());
        Paragraph paragraphDate = new Paragraph("Ngày xuất: " + stringDate, fontTitleName);
        paragraphDate.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraphCreateBy = new Paragraph("Người xuất: " + exportWareHouseListDto.getCreateByName(), fontTitleName);
        paragraphCreateBy.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraphName = new Paragraph("Địa chỉ: " + companyService.getById(cid, "", cid).getName() + " - " + wareHouseService.getById(cid,exportWareHouseListDto.getReceiptExportWareHouseDto().getIdWareHouse()).getName(), fontTitleName);
        paragraphName.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph3 = new Paragraph("Mã phiếu xuất: " + exportWareHouseListDto.getReceiptExportWareHouseDto().getCode(), fontTitleName);
        paragraph3.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph4 = new Paragraph("Tên phiếu xuất: " + exportWareHouseListDto.getReceiptExportWareHouseDto().getName(), fontTitleName);
        paragraph4.setAlignment(Paragraph.ALIGN_LEFT);


        Paragraph paragraph5 = new Paragraph( "Chi nhánh xuất tới: " + companyService.getById(exportWareHouseListDto.getReceiptExportWareHouseDto().getCompanyIdTo(), "", exportWareHouseListDto.getReceiptExportWareHouseDto().getCompanyIdTo()).getName() + " - " +  wareHouseService.getById(exportWareHouseListDto.getReceiptExportWareHouseDto().getCompanyIdTo(),exportWareHouseListDto.getReceiptExportWareHouseDto().getIdWareHouseTo()).getName(), fontTitleName);
        paragraph5.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph6 = new Paragraph();
        paragraph6.setSpacingBefore(10f);
        paragraph6.setSpacingAfter(10f);
        paragraph6.add (new Phrase("Chữ ký người xuất kho", fontTitleName));
        paragraph6.add (new Phrase("        Chữ ký người tiếp nhận", fontTitleName));
        paragraph6.add (new Phrase("        Chữ ký quản lý", fontTitleName));
        paragraph6.setFont(fontTitleName);
        paragraph6.setMultipliedLeading(2.0f);
        paragraph6.setSpacingAfter(10);
        paragraph6.setSpacingBefore(10);
        paragraph6.setAlignment(Paragraph.ALIGN_RIGHT);

        Paragraph paragraphSign = new Paragraph();
        paragraphSign.setSpacingBefore(10f);
        paragraphSign.setSpacingAfter(10f);
        paragraphSign.add (new Phrase(exportWareHouseListDto.getCreateByName(), fontTitleName));
        paragraphSign.add (new Phrase("                                        ", fontTitleName));
        paragraphSign.add (new Phrase("                                        ", fontTitleName));
        paragraphSign.setFont(fontTitleName);
        paragraphSign.setMultipliedLeading(8.0f);
        paragraphSign.setSpacingAfter(10);
        paragraphSign.setSpacingBefore(10);
        paragraphSign.setAlignment(Paragraph.ALIGN_RIGHT);

        PdfContentByte contB = pdf.getDirectContent();
        Barcode128 barCode = new Barcode128();
        barCode.setCode(idReceiptExport.toString());
        barCode.setCodeType(Barcode128.CODE128);

        Image image = barCode.createImageWithBarcode(contB, BaseColor.BLACK, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("ATCADO DOS PISOS\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 5));
        titulo.setPaddingTop(0);
        titulo.setAlignment(Element.ALIGN_CENTER);

        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 20;

        image.scalePercent(scaler);
        image.setPaddingTop(0);
        image.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph7 = new Paragraph();
        paragraph7.setSpacingBefore(10f);
        paragraph7.setSpacingAfter(10f);
        paragraph7.add (new Chunk(image, 0, 0, true));;
        paragraph7.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(paragraph7);
        document.add(paragraph);
        document.add(paragraphDate);
        document.add(paragraphCreateBy);
        document.add(paragraphName);
        document.add(paragraph3);
        document.add(paragraph4);
        document.add(paragraph5);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {0.5f, 2f, 1.5f, 1f, 1f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table, fontTitleName);
        writeTableData(table, exportWareHouseListDto.getData(), fontTitleName);

        AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        exportWareHouseListDto.getData().forEach(detailsItemOrderDto -> {
            Double priceItem = itemsService.getPriceItems(detailsItemOrderDto.getIdItems());
            totalPrice.updateAndGet(v -> v + priceItem*detailsItemOrderDto.getQuality()*detailsItemOrderDto.getNumberBox());
        });

        Paragraph paragraphTotal = new Paragraph( "Tổng tiền: " + formatter.format(totalPrice.get()) + " VND", fontTitleName);
        paragraphTotal.setAlignment(Paragraph.ALIGN_LEFT);
        paragraphTotal.setMultipliedLeading(2.0f);

        Locale locale = new Locale("vi");
        NumberFormat format =  NumberFormat.getCurrencyInstance(locale);
        Paragraph paragraphTotalRead = new Paragraph( "Tổng tiền viết bằng chữ: " + ReadNumber.numberToString(totalPrice.get()), fontTitleName);
        paragraphTotalRead.setAlignment(Paragraph.ALIGN_LEFT);

        document.add(table);
        document.add(paragraphTotal);
        document.add(paragraphTotalRead);
        document.add(paragraph6);
        document.add(paragraphSign);
        document.close();
        return f;
    }

    private void writeTableData(PdfPTable table, List<DetailsItemOrderDto> dtoList, Font font) {
        font.setColor(BaseColor.BLACK);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        int i=1;
        for (DetailsItemOrderDto itemOrderDto : dtoList) {
            cell.setPhrase(new Phrase(String.valueOf(i), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            i++;
            cell.setPhrase(new Phrase(itemsService.getById(137l, "", itemOrderDto.getIdItems()).getName(), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(itemOrderDto.getQuality()), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(itemOrderDto.getNumberBox()), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            Double priceItem = itemsService.getPriceItems(itemOrderDto.getIdItems());
            cell.setPhrase(new Phrase(formatter.format(priceItem), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell.setPhrase(new Phrase(formatter.format(priceItem*itemOrderDto.getQuality()*itemOrderDto.getNumberBox()), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }

    private void writeTableHeaderOrder(PdfPTable table, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(0);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setPhrase(new Phrase("Mặt hàng", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Đơn giá", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Số lượng", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Thành tiền", font));
        table.addCell(cell);
    }

    private void writeTableFooterOrder(PdfPTable table, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(1);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        table.addCell(cell);
    }

    private void writeTableDataOrder(PdfPTable table, List<DetailsItemOrderDto> dtoList, Font font) {
        font.setColor(BaseColor.BLACK);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        table.setHorizontalAlignment(Element.ALIGN_LEFT);
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(0);
        int i=1;
        for (DetailsItemOrderDto itemOrderDto : dtoList) {
            cell.setPhrase(new Phrase(itemsService.getById(137l, "", itemOrderDto.getIdItems()).getName(), font));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            Double priceItem = itemOrderDto.getTotalPrice() / itemOrderDto.getQuality();
            cell.setPhrase(new Phrase(formatter.format(priceItem), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell.setPhrase(new Phrase(itemOrderDto.getQuality() + "", font));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
            cell.setPhrase(new Phrase(formatter.format(itemOrderDto.getTotalPrice()), font));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);
        }
    }

    private void writeTableHeader(PdfPTable table, Font font) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        cell.setPhrase(new Phrase("STT", font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        table.addCell(cell);

        cell.setPhrase(new Phrase("Tên sản phẩm", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Số SP/thùng", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Số thùng", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Đơn giá", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Thành tiền", font));
        table.addCell(cell);
    }


    public void orderExport(HttpServletResponse response, Long id, Long cid) throws Exception {
        OrderItemResponseDTO orderItemResponseDTO = orderService.getOrderById(cid, id);
        Document document = new Document(PageSize.A7);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        OutputStream outputStream = response.getOutputStream();
        PdfWriter pdf  = PdfWriter.getInstance(document, outputStream);
        BaseFont courier = BaseFont.createFont(fontFile.getPath(),BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font myfont = new Font(courier, 5, Font.NORMAL);
        Font fontTitle = new Font(courier, 4, Font.NORMAL);

        document.open();
        URL url = new URL("https://i.pinimg.com/originals/7f/69/3e/7f693e0563f7334a1683db3deeeb89f3.png");
        Image image = Image.getInstance(url);
        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 30;
        image.scalePercent(scaler);
        image.setAlignment(Element.ALIGN_CENTER);


        Paragraph paragraph2 = new Paragraph();
        paragraph2.add (new Phrase("Địa chỉ: " + "Nam Giang - Nam Trực - Nam Định", myfont));
        paragraph2.setMultipliedLeading(-0.1f);
        paragraph2.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraph3 = new Paragraph();
        paragraph3.add (new Phrase("Chi nhánh: " + companyService.getById(cid, "", cid).getName() , myfont));;
        paragraph3.setAlignment(Paragraph.ALIGN_CENTER);


        Paragraph paragraph4 = new Paragraph();
        paragraph4.add (new Phrase("Sđt: " + "0528129662" , myfont));;
        paragraph4.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraphTitle = new Paragraph();
        paragraphTitle.add (new Chunk(image, 0, 0, true));
        paragraphTitle.setAlignment(Paragraph.ALIGN_CENTER);
        paragraphTitle.setMultipliedLeading(0.0f);



        Paragraph paragraph = new Paragraph(new
                String(("Hóa đơn bán hàng").getBytes(StandardCharsets.UTF_8)), myfont);
        paragraph.setSpacingBefore(10f);
        paragraph.setSpacingAfter(10f);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraph6 = new Paragraph();
        DottedLineSeparator dottedline = new DottedLineSeparator();
        dottedline.setOffset(-2);
        dottedline.setGap(2f);
        paragraph6.setLeading(10f);
        paragraph6.add(dottedline);

        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy  hh:mm");
        String stringDate= DateFor.format(orderItemResponseDTO.getCreateDate());

        Paragraph paragraphCode = new Paragraph("Mã đơn: " + orderItemResponseDTO.getCode(), fontTitle);
        paragraphCode.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraphDate = new Paragraph("Ngày bán: " + stringDate, fontTitle);
        paragraphDate.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraphCreateBy = new Paragraph("Người bán: " + orderItemResponseDTO.getCreateBy(), fontTitle);
        paragraphCreateBy.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraphCus = new Paragraph("Tên khách hàng: " + orderItemResponseDTO.getCustomerDto().getName(), fontTitle);
        paragraphCus.setAlignment(Paragraph.ALIGN_LEFT);

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {3f, 1.5f, 1.5f, 1.5f});
        table.setSpacingBefore(25);
        table.setSpacingAfter(10);

        writeTableHeaderOrder(table, fontTitle);
        writeTableDataOrder(table, orderItemResponseDTO.getDetailsItemOrders(), fontTitle);
        writeTableFooterOrder(table, fontTitle);

        AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        orderItemResponseDTO.getDetailsItemOrders().forEach(detailsItemOrderDto -> {
            totalPrice.updateAndGet(v -> v + detailsItemOrderDto.getTotalPrice());
        });

        Paragraph paragraphTotal = new Paragraph( "Tổng tiền: " + formatter.format(totalPrice.get()) + " VND", fontTitle);
        paragraphTotal.setSpacingBefore(10);
        paragraphTotal.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraphTotalRead = new Paragraph( "Tổng tiền viết bằng chữ: " + ReadNumber.numberToString(totalPrice.get()), fontTitle);
        paragraphTotalRead.setAlignment(Paragraph.ALIGN_LEFT);

        Font fontNote = new Font(courier, 5, Font.NORMAL);
        Paragraph paragraphNote = new Paragraph( "Lưu ý: " + " cửa hàng không hỗ trợ đổi trả trong vòng 24h", fontNote);
        paragraphNote.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraphThanks = new Paragraph( "Cảm ơn quý khách !", fontTitle);
        paragraphThanks.setSpacingBefore(5);
        paragraphThanks.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph p = new Paragraph();
        DottedLineSeparator dottedline1 = new DottedLineSeparator();
        p.setSpacingBefore(-8);
        dottedline1.setOffset(-2);
        dottedline1.setGap(2f);
        p.add(dottedline1);

        document.add(paragraphTitle);
        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph6);
        document.add(paragraph);
        document.add(paragraphCode);
        document.add(paragraphDate);
        document.add(paragraphCreateBy);
        document.add(paragraphCus);
        document.add(p);
        document.add(table);


        DottedLineSeparator dottedline2 = new DottedLineSeparator();
        dottedline2.setGap(2f);
        dottedline2.setOffset(-2f);

        document.add(dottedline2);
        document.add(paragraphTotal);
        document.add(paragraphTotalRead);
        document.add(paragraphThanks);
        document.add(paragraphNote);


        PdfContentByte contB = pdf.getDirectContent();
//        Barcode128 barCode = new Barcode128();
//        barCode.setCode();
//        barCode.setCodeType(Barcode128.CODE128);

        BarcodeQRCode qrcode = new BarcodeQRCode(id.toString(), 80, 80, null);

        Image image1 = qrcode.getImage();
        Paragraph titulo = new Paragraph("ATCADO DOS PISOS\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 5));
        titulo.setPaddingTop(0);
        titulo.setAlignment(Element.ALIGN_CENTER);

        float scaler1 = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 100;

        image1.scalePercent(scaler1);
        image1.setPaddingTop(0);
        image1.setAlignment(Element.ALIGN_CENTER);
        document.add(image1);
        document.close();
    }

    public void orderExportIem(HttpServletResponse response, Long id, Long cid) throws Exception {
        Document document = new Document(PageSize.A7);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        OutputStream outputStream = response.getOutputStream();
        PdfWriter pdf  = PdfWriter.getInstance(document, outputStream);
        BaseFont courier = BaseFont.createFont(fontFile.getPath(),BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font myfont = new Font(courier, 10, Font.NORMAL);

        document.open();
        URL url = new URL("https://i.pinimg.com/originals/7f/69/3e/7f693e0563f7334a1683db3deeeb89f3.png");
        Image image = Image.getInstance(url);
        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 30;
        image.scalePercent(scaler);
        image.setAlignment(Element.ALIGN_LEFT);

        Paragraph paragraph2 = new Paragraph();
        paragraph2.add (new Phrase("Name: " + "Mỳ tôm hảo hảo", myfont));
        paragraph2.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph3 = new Paragraph();
        paragraph3.add (new Phrase("Sum: " + "15X30", myfont));
        paragraph3.setAlignment(Paragraph.ALIGN_LEFT);


        Paragraph paragraph4 = new Paragraph();
        paragraph4.add (new Phrase("Price: " + "3.333" , myfont));;
        paragraph4.setAlignment(Paragraph.ALIGN_LEFT);


        Paragraph paragraph5 = new Paragraph();
        paragraph5.add (new Phrase("DateExpected: " + "17-11-2000" , myfont));;
        paragraph5.setAlignment(Paragraph.ALIGN_LEFT);

        document.add(paragraph2);
        document.add(paragraph3);
        document.add(paragraph4);
        document.add(paragraph5);

        PdfContentByte contB = pdf.getDirectContent();
        Barcode128 barCode = new Barcode128();
        barCode.setCode(id.toString());
        barCode.setCodeType(Barcode128.CODE128);

        Image image1 = barCode.createImageWithBarcode(contB, BaseColor.BLACK, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("ATCADO DOS PISOS\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 5));
        titulo.setPaddingTop(0);
        titulo.setAlignment(Element.ALIGN_CENTER);

        float scaler1 = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 100;

        image1.scalePercent(scaler1);
        image1.setPaddingTop(0);
        image1.setAlignment(Element.ALIGN_CENTER);

        document.close();
    }

    private Paragraph generateBarcode(Document document, PdfWriter pdf , Long code){
        PdfContentByte contB = pdf.getDirectContent();
        Barcode128 barCode = new Barcode128();
        barCode.setCode(code.toString());
        barCode.setCodeType(Barcode128.CODE128);

        BarcodeEAN barcodeEAN = new BarcodeEAN();
        barcodeEAN.setCode(code.toString());
        barcodeEAN.setCodeType(Barcode.EAN13);

        Image image = barCode.createImageWithBarcode(contB, BaseColor.BLACK, BaseColor.BLACK);
        barCode.setCodeType(Barcode.EAN13);
        Image image1 = barcodeEAN.createImageWithBarcode(contB, BaseColor.BLACK, BaseColor.BLACK);
        barCode.setCodeType(Barcode.EAN8);
        Image image2 = barcodeEAN.createImageWithBarcode(contB, BaseColor.BLACK, BaseColor.BLACK);


        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 25;

        image.scalePercent(scaler);
        image.setPaddingTop(0);
        image.setAlignment(Element.ALIGN_CENTER);

        image1.scalePercent(scaler);
        image1.setPaddingTop(0);
        image1.setAlignment(Element.ALIGN_CENTER);


        image2.scalePercent(scaler);
        image2.setPaddingTop(0);
        image2.setAlignment(Element.ALIGN_CENTER);

        Paragraph paragraph7 = new Paragraph();
        paragraph7.setSpacingBefore(10f);
        paragraph7.setSpacingAfter(10f);
        paragraph7.add (new Chunk(image, 0, 10, true));;
        paragraph7.setAlignment(Paragraph.ALIGN_CENTER);

        return paragraph7;
    }

    public void exportBarcodeItems(HttpServletResponse response, Long idItems, Long cid) throws Exception {
        ItemsResponseDTO items = itemsService.getById(cid, "", idItems);
        Document document = new Document(PageSize.A4);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        OutputStream outputStream = response.getOutputStream();
        PdfWriter pdf  = PdfWriter.getInstance(document, outputStream);
        BaseFont courier = BaseFont.createFont(fontFile.getPath(),BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font myfont = new Font(courier, 10, Font.NORMAL);

        document.open();

        Paragraph paragraph = new Paragraph(new
                String(("Danh sách mã vạch " + items.getName()).getBytes(StandardCharsets.UTF_8)), myfont);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(paragraph);
        List<ImportWareHouse> importWareHouses = importWareHouseService.getByIdtems(cid, idItems);
        importWareHouses.forEach(importWareHouse -> {
            try {
                document.add(generateBarcode(document, pdf, importWareHouse.getId()));
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        });

        document.close();
    }

    public void importWareHouse(HttpServletResponse response, Long idReceiptImport, Long cid) throws Exception {
        ImportListDataWareHouseDto importListDataWareHouseDto = importWareHouseService.getImportByReceiptId(cid, "", idReceiptImport);
        Document document = new Document(PageSize.A4);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        OutputStream outputStream = response.getOutputStream();
        PdfWriter pdf  = PdfWriter.getInstance(document, outputStream);

        document.open();
        BaseFont courier = BaseFont.createFont(fontFile.getPath(),BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font myfont = new Font(courier, 18, Font.NORMAL);
        Paragraph paragraph = new Paragraph(new
                String(("Phiếu nhập kho").getBytes(StandardCharsets.UTF_8)), myfont);
        paragraph.setAlignment(Paragraph.ALIGN_CENTER);

        Font fontTitleName = new Font(courier, 12, Font.NORMAL);
        SimpleDateFormat DateFor = new SimpleDateFormat("dd/MM/yyyy");
        String stringDate= DateFor.format(importListDataWareHouseDto.getData().get(0).getCreateDate());
        Paragraph paragraphDate = new Paragraph("Ngày nhập: " + stringDate, fontTitleName);
        paragraphDate.setAlignment(Paragraph.ALIGN_CENTER);

        Paragraph paragraphCreateBy = new Paragraph("Người nhập: " + importListDataWareHouseDto.getCreateByName(), fontTitleName);
        paragraphCreateBy.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraphName = new Paragraph("Kho nhập hàng: " + companyService.getById(cid, "", cid).getName() + " - " + wareHouseService.getById(cid,importListDataWareHouseDto.getReceiptImportWareHouseDto().getIdWareHouse()).getName(), fontTitleName);
        paragraphName.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph3 = new Paragraph("Mã phiếu nhập: " + importListDataWareHouseDto.getReceiptImportWareHouseDto().getCode(), fontTitleName);
        paragraph3.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph4 = new Paragraph("Tên phiếu nhập: " + importListDataWareHouseDto.getReceiptImportWareHouseDto().getName(), fontTitleName);
        paragraph4.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph5 = new Paragraph( "Nhà cung cấp: " + supplierService.getById(cid, "", importListDataWareHouseDto.getReceiptImportWareHouseDto().getIdSupplier()).getName(), fontTitleName);
        paragraph5.setAlignment(Paragraph.ALIGN_LEFT);

        Paragraph paragraph6 = new Paragraph();
        paragraph6.setSpacingBefore(10f);
        paragraph6.setSpacingAfter(10f);
        paragraph6.add (new Phrase("Chữ ký người nhập kho", fontTitleName));
        paragraph6.add (new Phrase("                                        ", fontTitleName));
        paragraph6.add (new Phrase("        Chữ ký quản lý", fontTitleName));
        paragraph6.setFont(fontTitleName);
        paragraph6.setMultipliedLeading(2.0f);
        paragraph6.setSpacingAfter(10);
        paragraph6.setSpacingBefore(10);
        paragraph6.setAlignment(Paragraph.ALIGN_RIGHT);

        Paragraph paragraphSign = new Paragraph();
        paragraphSign.setSpacingBefore(10f);
        paragraphSign.setSpacingAfter(10f);
        paragraphSign.add (new Phrase(importListDataWareHouseDto.getCreateByName(), fontTitleName));
        paragraphSign.add (new Phrase("                                        ", fontTitleName));
        paragraphSign.add (new Phrase("                                        ", fontTitleName));
        paragraphSign.setFont(fontTitleName);
        paragraphSign.setMultipliedLeading(8.0f);
        paragraphSign.setSpacingAfter(10);
        paragraphSign.setSpacingBefore(10);
        paragraphSign.setAlignment(Paragraph.ALIGN_RIGHT);

        PdfContentByte contB = pdf.getDirectContent();
        Barcode128 barCode = new Barcode128();
        barCode.setCode(idReceiptImport.toString());
        barCode.setCodeType(Barcode128.CODE128);

        Image image = barCode.createImageWithBarcode(contB, BaseColor.BLACK, BaseColor.BLACK);
        Paragraph titulo = new Paragraph("ATCADO DOS PISOS\n",
                new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 5));
        titulo.setPaddingTop(0);
        titulo.setAlignment(Element.ALIGN_CENTER);

        float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()
                - 0) / image.getWidth()) * 20;

        image.scalePercent(scaler);
        image.setPaddingTop(0);
        image.setAlignment(Element.ALIGN_CENTER);
        Paragraph paragraph7 = new Paragraph();
        paragraph7.setSpacingBefore(10f);
        paragraph7.setSpacingAfter(10f);
        paragraph7.add (new Chunk(image, 0, 0, true));;
        paragraph7.setAlignment(Paragraph.ALIGN_LEFT);
        document.add(paragraph7);
        document.add(paragraph);
        document.add(paragraphDate);
        document.add(paragraphCreateBy);
        document.add(paragraphName);
        document.add(paragraph3);
        document.add(paragraph4);
        document.add(paragraph5);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {0.5f, 2f, 1.5f, 1f, 1f, 1.5f});
        table.setSpacingBefore(10);

        writeTableHeader(table, fontTitleName);
        writeTableDataImport(table, importListDataWareHouseDto.getData(), fontTitleName);

        AtomicReference<Double> totalPrice = new AtomicReference<>(0d);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        importListDataWareHouseDto.getData().forEach(detailsItemOrderDto -> {
            Double priceItem = itemsService.getPriceItems(detailsItemOrderDto.getIdItems());
            totalPrice.updateAndGet(v -> v + priceItem*detailsItemOrderDto.getQuantity()*detailsItemOrderDto.getNumberBox());
        });

        Paragraph paragraphTotal = new Paragraph( "Tổng tiền nhập: " + formatter.format(totalPrice.get()) + " VND", fontTitleName);
        paragraphTotal.setAlignment(Paragraph.ALIGN_LEFT);
        paragraphTotal.setMultipliedLeading(2.0f);

        Locale locale = new Locale("vi");
        NumberFormat format =  NumberFormat.getCurrencyInstance(locale);
        Paragraph paragraphTotalRead = new Paragraph( "Tổng tiền viết bằng chữ: " + ReadNumber.numberToString(totalPrice.get()), fontTitleName);
        paragraphTotalRead.setAlignment(Paragraph.ALIGN_LEFT);

        document.add(table);
        document.add(paragraphTotal);
        document.add(paragraphTotalRead);
        document.add(paragraph6);
        document.add(paragraphSign);
        document.close();
    }

    private void writeTableDataImport(PdfPTable table, List<ImportWareHouseDto> dtoList, Font font) {
        font.setColor(BaseColor.BLACK);
        DecimalFormat formatter = new DecimalFormat("###,###,###");
        table.setHorizontalAlignment(Element.ALIGN_CENTER);
        PdfPCell cell = new PdfPCell();
        cell.setPadding(5);
        int i=1;
        for (ImportWareHouseDto itemOrderDto : dtoList) {
            cell.setPhrase(new Phrase(String.valueOf(i), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            i++;
            cell.setPhrase(new Phrase(itemsService.getById(137l, "", itemOrderDto.getIdItems()).getName(), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(itemOrderDto.getQuantity()), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(itemOrderDto.getNumberBox()), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            Double priceItem = itemsService.getPriceItems(itemOrderDto.getIdItems());
            cell.setPhrase(new Phrase(formatter.format(priceItem), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            cell.setPhrase(new Phrase(formatter.format(priceItem*itemOrderDto.getQuantity()*itemOrderDto.getNumberBox()), font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
    }
}
