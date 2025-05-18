package com.desmin.services.impl;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.DiemRenLuyenChiTiet;
import com.desmin.pojo.Dieu;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.pojo.User;
import com.desmin.repositories.DiemRenLuyenChiTietRepository;
import com.desmin.repositories.DiemRenLuyenRepository;
import com.desmin.repositories.DieuRepository;
import com.desmin.repositories.HoatDongNgoaiKhoaRepository;
import com.desmin.repositories.HocKyNamHocRepository;
import com.desmin.repositories.LopRepository;
import com.desmin.services.DiemRenLuyenService;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.constants.StandardFonts;


@Service
@Transactional
public class DiemRenLuyenServiceImpl implements DiemRenLuyenService {

    private static final Logger logger = LoggerFactory.getLogger(DiemRenLuyenServiceImpl.class);

    @Autowired
    private DiemRenLuyenRepository diemRenLuyenRepository;

    @Autowired
    private DiemRenLuyenChiTietRepository diemChiTietRepository;
    @Autowired
    private DieuRepository dieuRepository;

    @Autowired
    private LopRepository lopRepository;
    @Autowired
    private HoatDongNgoaiKhoaRepository hdnkRepository;
    @Autowired
    private HocKyNamHocRepository hkNhRepository;

    @Override

    public List<DiemRenLuyen> getDiemRenLuyens(Map<String, String> params) {
        return diemRenLuyenRepository.getDiemRenLuyens(params);
    }

    @Override
    public List<DiemRenLuyen> getDiemRenLuyenBySinhVienId(long userId, Map<String, String> params) {
        return diemRenLuyenRepository.getDiemRenLuyenBySinhVienId(userId, params);
    }

    @Override
    public void congDiemRenLuyen(User sinhVien, HoatDongNgoaiKhoa hoatDong) {
        HocKyNamHoc hkNh = hoatDong.getHkNh();
        Integer diem = hoatDong.getDiemRenLuyen();
        Dieu dieu = hoatDong.getDieu();

        // Kiểm tra dieu hợp lệ
        if (dieu == null) {
            throw new IllegalArgumentException("Hoạt động không có Điều liên kết");
        }

        // Tạo hoặc lấy DiemRenLuyen
        DiemRenLuyen diemRenLuyen = diemRenLuyenRepository.createDiemRenLuyen(sinhVien, hkNh);

        // Tạo DiemRenLuyenChiTiet cho tất cả Dieu nếu chưa có
        diemChiTietRepository.createDiemRenLuyenChiTietForAllDieu(diemRenLuyen);

        // Cộng điểm vào DiemRenLuyenChiTiet
        List<DiemRenLuyenChiTiet> chiTiets = diemChiTietRepository.getDiemRenLuyenChiTietByDiemRenLuyenId(
                diemRenLuyen.getId(), new HashMap<>()
        );
        DiemRenLuyenChiTiet chiTiet = chiTiets.stream()
                .filter(ct -> ct.getDieu().getId().equals(dieu.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy DiemRenLuyenChiTiet cho Dieu: " + dieu.getId()));

        // Tính điểm còn lại
        int tongDiemHienTai = chiTiet.getDiem();
        int diemConLai = dieu.getDiemToiDa() - tongDiemHienTai;
        if (diemConLai <= 0) {
            return;
        }

        int diemThucTe = Math.min(diem, diemConLai);

        // Cập nhật DiemRenLuyenChiTiet
        chiTiet.setDiem(tongDiemHienTai + diemThucTe);
        diemChiTietRepository.saveDiemRenLuyenChiTiet(chiTiet);

        // Cập nhật DiemRenLuyen
        diemRenLuyen.setDiemTong(diemRenLuyen.getDiemTong() + diemThucTe);
        diemRenLuyen.setUpdatedDate(LocalDateTime.now());
        diemRenLuyenRepository.saveDiemRenLuyen(diemRenLuyen);
    }

    @Override
    public List<DiemRenLuyen> getDiemRenLuyenTongHop(Long khoaId, Long lopId, String xepLoai, Long hkNhId, int page, int size) {
        return diemRenLuyenRepository.getDiemRenLuyenTongHop(khoaId, lopId, xepLoai, hkNhId, page, size);
    }
    
    
 @Override
    public byte[] exportDiemRenLuyenToCsv(Long khoaId, Long lopId, String xepLoai, Long hkNhId) {
        try {
            List<DiemRenLuyen> danhSach = diemRenLuyenRepository.getAllDiemRenLuyenTongHop(khoaId, lopId, xepLoai, hkNhId);
            if (danhSach.isEmpty()) {
                logger.warn("No data found for CSV export with parameters: khoaId={}, lopId={}, xepLoai={}, hkNhId={}",
                        khoaId, lopId, xepLoai, hkNhId);
                throw new IllegalStateException("Không có dữ liệu điểm rèn luyện.");
            }

            String lopName = lopId != null && lopRepository.getLopById(lopId) != null
                    ? lopRepository.getLopById(lopId).getTenLop()
                    : "Tất cả lớp";
            String xepLoaiFilter = xepLoai != null && !xepLoai.isEmpty() ? xepLoai : "Tất cả xếp loại";
            String hkNhFilter = hkNhId != null && hkNhRepository.getHocKyNamHocById(hkNhId) != null
                    ? (hkNhRepository.getHocKyNamHocById(hkNhId).getHocKy() == HocKyNamHoc.HocKy.ONE ? "Học kỳ 1"
                    : hkNhRepository.getHocKyNamHocById(hkNhId).getHocKy() == HocKyNamHoc.HocKy.TWO ? "Học kỳ 2" : "Học kỳ 3")
                    + " - " + hkNhRepository.getHocKyNamHocById(hkNhId).getNamHoc()
                    : "Tất cả học kỳ";
            List<Dieu> dieuList = dieuRepository.getDieus(null);

            StringBuilder csvContent = new StringBuilder();
            csvContent.append("\uFEFF");
            csvContent.append(String.format("\"%s\"\n", lopName));
            csvContent.append(String.format("\"%s\"\n", xepLoaiFilter));
            csvContent.append(String.format("\"%s\"\n", hkNhFilter));
            csvContent.append("\n");

            csvContent.append("\"#\",\"Họ tên\",\"MSSV\",\"Lớp\",\"Khoa\",\"Học kỳ - Năm học\",\"Điểm tổng\",\"Xếp loại\"");
            for (Dieu dieu : dieuList) {
                csvContent.append(",\"").append(dieu.getTenDieu()).append("\"");
            }
            csvContent.append("\n");

            for (int i = 0; i < danhSach.size(); i++) {
                DiemRenLuyen drl = danhSach.get(i);
                User sv = drl.getSinhVien();
                HocKyNamHoc hkRow = drl.getHkNh();
                String hocKy = hkRow.getHocKy() == HocKyNamHoc.HocKy.ONE ? "Học kỳ 1"
                        : hkRow.getHocKy() == HocKyNamHoc.HocKy.TWO ? "Học kỳ 2" : "Học kỳ 3";
                String hoTen = (sv.getHo() + " " + sv.getTen()).replace("\"", "\"\"");
                String tenLop = sv.getLop().getTenLop().replace("\"", "\"\"");
                String tenKhoa = sv.getLop().getKhoa() != null ? sv.getLop().getKhoa().getTenKhoa().replace("\"", "\"\"") : "";
                Map<Long, Integer> diemMap = drl.getChiTiet().stream()
                        .filter(ct -> ct.getDieu() != null)
                        .collect(Collectors.toMap(
                                ct -> ct.getDieu().getId(),
                                DiemRenLuyenChiTiet::getDiem,
                                (d1, d2) -> d1
                        ));
                csvContent.append(String.format(
                        "\"%d\",\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",\"%d\",\"%s\"",
                        i + 1,
                        hoTen,
                        sv.getMssv(),
                        tenLop,
                        tenKhoa,
                        hocKy + " - " + hkRow.getNamHoc(),
                        drl.getDiemTong(),
                        drl.getXepLoai()
                ));
                for (Dieu dieu : dieuList) {
                    csvContent.append(",\"").append(diemMap.getOrDefault(dieu.getId(), 0)).append("\"");
                }
                csvContent.append("\n");
            }

            return csvContent.toString().getBytes("UTF-8");
        } catch (Exception e) {
            logger.error("Error creating CSV: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo file CSV: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] exportDiemRenLuyenToPdf(Long khoaId, Long lopId, String xepLoai, Long hkNhId) {
        try {
            List<DiemRenLuyen> danhSach = diemRenLuyenRepository.getAllDiemRenLuyenTongHop(khoaId, lopId, xepLoai, hkNhId);
            if (danhSach.isEmpty()) {
                logger.warn("No data found for PDF export with parameters: khoaId={}, lopId={}, xepLoai={}, hkNhId={}",
                        khoaId, lopId, xepLoai, hkNhId);
                throw new IllegalStateException("Không có dữ liệu điểm rèn luyện.");
            }

            String lopName = lopId != null && lopRepository.getLopById(lopId) != null
                    ? lopRepository.getLopById(lopId).getTenLop()
                    : "Tất cả lớp";
            String xepLoaiFilter = xepLoai != null && !xepLoai.isEmpty() ? xepLoai : "Tất cả xếp loại";
            String hkNhFilter = hkNhId != null && hkNhRepository.getHocKyNamHocById(hkNhId) != null
                    ? (hkNhRepository.getHocKyNamHocById(hkNhId).getHocKy() == HocKyNamHoc.HocKy.ONE ? "Học kỳ 1"
                    : hkNhRepository.getHocKyNamHocById(hkNhId).getHocKy() == HocKyNamHoc.HocKy.TWO ? "Học kỳ 2" : "Học kỳ 3")
                    + " - " + hkNhRepository.getHocKyNamHocById(hkNhId).getNamHoc()
                    : "Tất cả học kỳ";
            List<Dieu> dieuList = dieuRepository.getDieus(null);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            String fontPath = "fonts/DejaVuSans.ttf";
            PdfFont font;
            try {
                font = PdfFontFactory.createFont(fontPath, PdfEncodings.IDENTITY_H, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            } catch (Exception e) {
                logger.error("Failed to load DejaVuSans font: {}", e.getMessage(), e);
                font = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);
            }

            document.add(new Paragraph("DANH SÁCH ĐIỂM RÈN LUYỆN")
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16)
                    .setBold());
            document.add(new Paragraph(lopName)
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));
            document.add(new Paragraph(xepLoaiFilter)
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));
            document.add(new Paragraph(hkNhFilter)
                    .setFont(font)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(12));

            float[] columnWidths = new float[8 + dieuList.size()];
            columnWidths[0] = 1f;
            columnWidths[1] = 3f;
            columnWidths[2] = 2f;
            columnWidths[3] = 2f;
            columnWidths[4] = 2f;
            columnWidths[5] = 3f;
            columnWidths[6] = 1.5f;
            columnWidths[7] = 2f;
            for (int i = 8; i < columnWidths.length; i++) {
                columnWidths[i] = 1.5f;
            }
            Table table = new Table(columnWidths);

            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("#").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Họ tên").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("MSSV").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Lớp").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Khoa").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Học kỳ - Năm học").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Điểm tổng").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                    .add(new Paragraph("Xếp loại").setFont(font).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            for (Dieu dieu : dieuList) {
                table.addHeaderCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(dieu.getTenDieu()).setFont(font).setBold())
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            for (int i = 0; i < danhSach.size(); i++) {
                DiemRenLuyen drl = danhSach.get(i);
                User sv = drl.getSinhVien();
                HocKyNamHoc hkRow = drl.getHkNh();
                String hocKy = hkRow.getHocKy() == HocKyNamHoc.HocKy.ONE ? "Học kỳ 1"
                        : hkRow.getHocKy() == HocKyNamHoc.HocKy.TWO ? "Học kỳ 2" : "Học kỳ 3";
                Map<Long, Integer> diemMap = drl.getChiTiet().stream()
                        .filter(ct -> ct.getDieu() != null)
                        .collect(Collectors.toMap(
                                ct -> ct.getDieu().getId(),
                                DiemRenLuyenChiTiet::getDiem,
                                (d1, d2) -> d1
                        ));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(String.valueOf(i + 1)).setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(sv.getHo() + " " + sv.getTen()).setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(sv.getMssv()).setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(sv.getLop().getTenLop()).setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(sv.getLop().getKhoa() != null ? sv.getLop().getKhoa().getTenKhoa() : "").setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(hocKy + " - " + hkRow.getNamHoc()).setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(String.valueOf(drl.getDiemTong())).setFont(font)));
                table.addCell(new com.itextpdf.layout.element.Cell()
                        .add(new Paragraph(drl.getXepLoai().toString()).setFont(font)));
                for (Dieu dieu : dieuList) {
                    table.addCell(new com.itextpdf.layout.element.Cell()
                            .add(new Paragraph(String.valueOf(diemMap.getOrDefault(dieu.getId(), 0))).setFont(font)));
                }
            }

            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error creating PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Lỗi khi tạo file PDF: " + e.getMessage(), e);
        }
    }
}
