package com.beeshop.sd44.service;

import com.beeshop.sd44.entity.Order;
import com.beeshop.sd44.entity.OrderDetail;
import com.beeshop.sd44.repository.OrderDetailRepo;
import com.beeshop.sd44.repository.OrderRepo;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;

@Service
public class InvoicePdfService {

    private final OrderRepo orderRepo;
    private final OrderDetailRepo orderDetailRepo;

    // Màu chủ đạo
    private static final DeviceRgb PRIMARY_COLOR = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb HEADER_BG     = new DeviceRgb(41, 128, 185);
    private static final DeviceRgb ROW_ALT_BG    = new DeviceRgb(235, 245, 251);
    private static final DeviceRgb BORDER_COLOR  = new DeviceRgb(189, 195, 199);

    public InvoicePdfService(OrderRepo orderRepo, OrderDetailRepo orderDetailRepo) {
        this.orderRepo = orderRepo;
        this.orderDetailRepo = orderDetailRepo;
    }

    /**
     * Tạo file PDF hóa đơn cho đơn hàng có id = orderId.
     * @return byte[] nội dung file PDF, hoặc null nếu không tìm thấy đơn hàng.
     */
    public byte[] generateInvoicePdf(UUID orderId) throws IOException {
        Order order = orderRepo.findById(orderId).orElse(null);
        if (order == null) return null;

        List<OrderDetail> details = orderDetailRepo.getOrderDetailByOrder(order);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);
        document.setMargins(36, 36, 36, 36);

        // Font Arial TTF hỗ trợ tiếng Việt đầy đủ dấu
        PdfFont font;
        PdfFont fontBold;
        try {
            byte[] fontBytes = getClass().getResourceAsStream("/fonts/Arial.ttf").readAllBytes();
            byte[] fontBoldBytes = getClass().getResourceAsStream("/fonts/Arial-Bold.ttf").readAllBytes();
            font = PdfFontFactory.createFont(fontBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
            fontBold = PdfFontFactory.createFont(fontBoldBytes, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.FORCE_EMBEDDED);
        } catch (Exception e) {
            // Fallback nếu không tìm thấy font
            font = PdfFontFactory.createFont();
            fontBold = PdfFontFactory.createFont();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat df = new DecimalFormat("#,###");

        // ===================== HEADER CỬA HÀNG =====================
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER);

        // Bên trái: tên cửa hàng, địa chỉ
        Cell shopCell = new Cell().setBorder(Border.NO_BORDER);
        shopCell.add(new Paragraph("BEE SHOP")
                .setFont(fontBold).setFontSize(22).setFontColor(PRIMARY_COLOR));
        shopCell.add(new Paragraph("Địa chỉ: 123 Đường ABC, Hà Nội")
                .setFont(font).setFontSize(9).setFontColor(ColorConstants.GRAY));
        shopCell.add(new Paragraph("ĐT: 0901 234 567 | Email: beeshop@gmail.com")
                .setFont(font).setFontSize(9).setFontColor(ColorConstants.GRAY));
        headerTable.addCell(shopCell);

        // Bên phải: tiêu đề HÓA ĐƠN + mã đơn
        Cell titleCell = new Cell().setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT);
        titleCell.add(new Paragraph("HÓA ĐƠN BÁN HÀNG")
                .setFont(fontBold).setFontSize(16).setFontColor(PRIMARY_COLOR));
        titleCell.add(new Paragraph("Mã: " + safeStr(order.getCode()))
                .setFont(font).setFontSize(10));
        titleCell.add(new Paragraph("Ngày: " + (order.getCreatedAt() != null ? sdf.format(order.getCreatedAt()) : "--"))
                .setFont(font).setFontSize(10));
        headerTable.addCell(titleCell);

        document.add(headerTable);

        // Đường kẻ ngang
        document.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.SolidLine())
                .setMarginTop(8).setMarginBottom(8));

        // ===================== THÔNG TIN KHÁCH HÀNG =====================
        document.add(new Paragraph("Thông tin khách hàng")
                .setFont(fontBold).setFontSize(11).setFontColor(PRIMARY_COLOR)
                .setMarginBottom(4));

        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{50, 50}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER);

        String customerName = "--";
        String customerPhone = "--";
        String customerAddress = "--";
        if (order.getUser() != null) {
            customerName = safeStr(order.getUser().getName());
            customerPhone = safeStr(order.getUser().getPhone());
            customerAddress = safeStr(order.getUser().getAddress());
        }
        if (order.getCustomer() != null) {
            if (order.getCustomer().getTen() != null) customerName = order.getCustomer().getTen();
            if (order.getCustomer().getSdt() != null) customerPhone = order.getCustomer().getSdt();
            if (order.getCustomer().getDiaChi() != null) customerAddress = order.getCustomer().getDiaChi();
        }

        infoTable.addCell(noBorderCell("Khách hàng: " + customerName, font, 10));
        infoTable.addCell(noBorderCell("Phương thức TT: " + formatPaymentMethod(order.getPaymentMethod()), font, 10));
        infoTable.addCell(noBorderCell("SĐT: " + customerPhone, font, 10));
        infoTable.addCell(noBorderCell("Trạng thái TT: " + formatPaymentStatus(order.getPaymentStatus()), font, 10));
        infoTable.addCell(noBorderCell("Địa chỉ: " + customerAddress, font, 10));
        infoTable.addCell(noBorderCell("Ghi chú: " + safeStr(order.getNote()), font, 10));
        document.add(infoTable);

        document.add(new Paragraph(" ").setMarginBottom(4));

        // ===================== BẢNG SẢN PHẨM =====================
        document.add(new Paragraph("Chi tiết sản phẩm")
                .setFont(fontBold).setFontSize(11).setFontColor(PRIMARY_COLOR)
                .setMarginBottom(4));

        Table productTable = new Table(UnitValue.createPercentArray(new float[]{5, 35, 15, 15, 15, 15}))
                .setWidth(UnitValue.createPercentValue(100));

        // Header bảng
        String[] headers = {"STT", "Sản phẩm", "Size", "Màu sắc", "Đơn giá", "Thành tiền"};
        for (String h : headers) {
            productTable.addHeaderCell(new Cell()
                    .setBackgroundColor(HEADER_BG)
                    .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                    .add(new Paragraph(h).setFont(fontBold).setFontSize(9).setFontColor(ColorConstants.WHITE))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setPadding(5));
        }

        // Dữ liệu sản phẩm
        double subTotal = 0;
        for (int i = 0; i < details.size(); i++) {
            OrderDetail od = details.get(i);
            boolean isAlt = (i % 2 == 1);
            DeviceRgb rowBg = isAlt ? ROW_ALT_BG : new DeviceRgb(255, 255, 255);

            String productName = "--";
            String sizeName = "--";
            String colorName = "--";
            int qty = od.getQuantity() != null ? od.getQuantity() : 0;
            double price = od.getPrice() != null ? od.getPrice() : 0;

            if (od.getProductDetail() != null) {
                productName = safeStr(od.getProductDetail().getProduct().getName());
                if (od.getProductDetail().getSize() != null)
                    sizeName = safeStr(od.getProductDetail().getSize().getName());
                if (od.getProductDetail().getColor() != null)
                    colorName = safeStr(od.getProductDetail().getColor().getName());
            }

            double lineTotal = price * qty;
            subTotal += lineTotal;

            productTable.addCell(tableCell(String.valueOf(i + 1), font, 9, rowBg, TextAlignment.CENTER));
            productTable.addCell(tableCell(productName, font, 9, rowBg, TextAlignment.LEFT));
            productTable.addCell(tableCell(sizeName, font, 9, rowBg, TextAlignment.CENTER));
            productTable.addCell(tableCell(colorName, font, 9, rowBg, TextAlignment.CENTER));
            productTable.addCell(tableCell(df.format(price) + " đ", font, 9, rowBg, TextAlignment.RIGHT));
            productTable.addCell(tableCell(df.format(lineTotal) + " đ", font, 9, rowBg, TextAlignment.RIGHT));
        }

        document.add(productTable);

        // ===================== TỔNG TIỀN =====================
        document.add(new Paragraph(" ").setMarginBottom(2));

        Table totalTable = new Table(UnitValue.createPercentArray(new float[]{65, 35}))
                .setWidth(UnitValue.createPercentValue(100))
                .setBorder(Border.NO_BORDER)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT);

        double shippingFee = order.getShippingFee() != null ? order.getShippingFee() : 0;
        double total = order.getTotal() != null ? order.getTotal() : 0;
        double discount = subTotal + shippingFee - total;
        if (discount < 0) discount = 0;

        addTotalRow(totalTable, "Tổng tiền hàng:", df.format(subTotal) + " đ", font, false);
        addTotalRow(totalTable, "Phí vận chuyển:", df.format(shippingFee) + " đ", font, false);
        if (discount > 0) {
            addTotalRow(totalTable, "Giảm giá:", "- " + df.format(discount) + " đ", font, false);
        }

        // Đường kẻ trước tổng cộng
        totalTable.addCell(new Cell(1, 2)
                .add(new Paragraph(" "))
                .setBorder(Border.NO_BORDER)
                .setBorderTop(new SolidBorder(PRIMARY_COLOR, 1.5f))
                .setPaddingTop(4));

        addTotalRow(totalTable, "TỔNG THANH TOÁN:", df.format(total) + " đ", fontBold, true);

        document.add(totalTable);

        // ===================== FOOTER =====================
        document.add(new Paragraph(" ").setMarginBottom(8));
        document.add(new LineSeparator(new com.itextpdf.kernel.pdf.canvas.draw.DashedLine())
                .setMarginBottom(8));
        document.add(new Paragraph("Cảm ơn quý khách đã mua hàng tại BEE SHOP!")
                .setFont(fontBold).setFontSize(10).setFontColor(PRIMARY_COLOR)
                .setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Mọi thắc mắc xin liên hệ: 0901 234 567 | beeshop@gmail.com")
                .setFont(font).setFontSize(9).setFontColor(ColorConstants.GRAY)
                .setTextAlignment(TextAlignment.CENTER));

        document.close();
        return baos.toByteArray();
    }

    // ===================== HELPER METHODS =====================

    private String safeStr(String s) {
        return s != null ? s : "--";
    }

    private String formatPaymentMethod(String method) {
        if (method == null) return "--";
        return switch (method) {
            case "COD"   -> "Thanh toán khi nhận hàng (COD)";
            case "VNPAY" -> "Thanh toán VNPay";
            case "CASH"  -> "Tiền mặt";
            default -> method;
        };
    }

    private String formatPaymentStatus(Integer status) {
        if (status == null) return "--";
        return switch (status) {
            case 0 -> "Chưa thanh toán";
            case 1 -> "Đã thanh toán";
            case 3 -> "Thanh toán thất bại";
            default -> "Không xác định";
        };
    }

    private Cell noBorderCell(String text, PdfFont font, float size) {
        return new Cell().setBorder(Border.NO_BORDER)
                .add(new Paragraph(text).setFont(font).setFontSize(size).setMarginBottom(2));
    }

    private Cell tableCell(String text, PdfFont font, float size, DeviceRgb bg, TextAlignment align) {
        return new Cell()
                .setBackgroundColor(bg)
                .setBorder(new SolidBorder(BORDER_COLOR, 0.5f))
                .add(new Paragraph(text).setFont(font).setFontSize(size))
                .setTextAlignment(align)
                .setPaddingTop(4).setPaddingBottom(4).setPaddingLeft(6).setPaddingRight(6);
    }

    private void addTotalRow(Table table, String label, String value, PdfFont font, boolean isBold) {
        float size = isBold ? 11f : 10f;
        DeviceRgb color = isBold ? PRIMARY_COLOR : new DeviceRgb(50, 50, 50);
        table.addCell(new Cell().setBorder(Border.NO_BORDER)
                .add(new Paragraph(label).setFont(font).setFontSize(size).setFontColor(color))
                .setTextAlignment(TextAlignment.RIGHT));
        table.addCell(new Cell().setBorder(Border.NO_BORDER)
                .add(new Paragraph(value).setFont(font).setFontSize(size).setFontColor(color))
                .setTextAlignment(TextAlignment.RIGHT));
    }
}
