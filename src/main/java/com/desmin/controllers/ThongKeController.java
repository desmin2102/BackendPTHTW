package com.desmin.controllers;

import com.desmin.pojo.DiemRenLuyen;
import com.desmin.pojo.Dieu;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.services.DiemRenLuyenService;
import com.desmin.services.DieuService;
import com.desmin.services.HocKyNamHocService;
import com.desmin.services.KhoaService;
import com.desmin.services.LopService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller xử lý thống kê điểm rèn luyện
 */
@Controller
@RequestMapping("/thong-ke")
public class ThongKeController {

    @Autowired
    private DiemRenLuyenService diemRenLuyenService;

    @Autowired
    private KhoaService khoaService;

    @Autowired
    private LopService lopService;

    @Autowired
    private HocKyNamHocService hocKyNamHocService;

    @Autowired
    private DieuService dieuService;

    private static final Logger logger = LoggerFactory.getLogger(ThongKeController.class);

    @GetMapping
    @PreAuthorize("hasAnyRole('CVCTSV')")
    public String thongKeDiemRenLuyen(
            Model model,
            @RequestParam(name = "khoaId", required = false) Long khoaId,
            @RequestParam(name = "lopId", required = false) Long lopId,
            @RequestParam(name = "xepLoai", required = false) String xepLoai,
            @RequestParam(name = "hkNhId", required = false) Long hkNhId) {

        try {
            // Khởi tạo tham số
            Map<String, String> params = new HashMap<>();
            if (khoaId != null) {
                params.put("khoaId", khoaId.toString());
            }
            if (lopId != null) {
                params.put("lopId", lopId.toString());
            }
            if (xepLoai != null && !xepLoai.trim().isEmpty()) {
                if (!isValidXepLoai(xepLoai)) {
                    logger.warn("Xếp loại không hợp lệ: {}", xepLoai);
                    model.addAttribute("error", "Xếp loại không hợp lệ.");
                    return "thong-ke";
                }
                params.put("xepLoai", xepLoai);
            }
            if (hkNhId != null) {
                params.put("hkNhId", hkNhId.toString());
            }

            // Lấy danh sách khoa, lớp, học kỳ, và điều
            List<?> khoaList = khoaService.getKhoas(new HashMap<>());
            List<?> lopList = lopService.getLops(khoaId != null ? Map.of("khoaId", khoaId.toString()) : new HashMap<>());
            List<HocKyNamHoc> hocKyList = hocKyNamHocService.getHocKyNamHocs(new HashMap<>());
            List<Dieu> dieuList = dieuService.getDieus(new HashMap<>());

            // Log danh sách học kỳ trước khi sắp xếp
            logger.info("Danh sách học kỳ trước khi sắp xếp: {}", hocKyList);

            // Nếu không có hkNhId được truyền vào, chọn học kỳ có ID cao nhất
            Long defaultHkNhId = hkNhId;
            if (defaultHkNhId == null && hocKyList != null && !hocKyList.isEmpty()) {
                HocKyNamHoc latestHocKy = hocKyList.stream()
                        .max(Comparator.comparingLong(HocKyNamHoc::getId))
                        .orElse(null);
                if (latestHocKy != null) {
                    defaultHkNhId = latestHocKy.getId();
                    logger.info("Chọn học kỳ mặc định (ID cao nhất): ID={}, HocKy={}, NamHoc={}", 
                                defaultHkNhId, latestHocKy.getHocKy(), latestHocKy.getNamHoc());
                    params.put("hkNhId", defaultHkNhId.toString());
                }
            }

            // Log tham số được sử dụng cho thống kê
            logger.info("Tham số thống kê: {}", params);

            // Lấy dữ liệu thống kê từ service
            List<Map<String, Object>> thongKeData = diemRenLuyenService.thongKeDiemRenLuyen(
                    khoaId, lopId, xepLoai, defaultHkNhId != null ? defaultHkNhId : hkNhId);

            // Tính toán dữ liệu cho biểu đồ tròn (xếp loại)
            Map<String, Long> xepLoaiCounts = new HashMap<>();
            xepLoaiCounts.put("XUAT_SAC", 0L);
            xepLoaiCounts.put("GIOI", 0L);
            xepLoaiCounts.put("KHA", 0L);
            xepLoaiCounts.put("TRUNG_BINH", 0L);
            xepLoaiCounts.put("YEU", 0L);
            xepLoaiCounts.put("KEM", 0L);

            for (Map<String, Object> item : thongKeData) {
                Object xepLoaiObj = item.get("xepLoai");
                String xepLoaiItem = null;
                if (xepLoaiObj instanceof String) {
                    xepLoaiItem = (String) xepLoaiObj;
                } else if (xepLoaiObj instanceof DiemRenLuyen.XepLoai) {
                    xepLoaiItem = xepLoaiObj.toString();
                }
                if (xepLoaiItem != null && xepLoaiCounts.containsKey(xepLoaiItem)) {
                    xepLoaiCounts.put(xepLoaiItem, xepLoaiCounts.get(xepLoaiItem) + 1);
                }
            }

            // Dữ liệu cho Pie Chart
            List<Long> pieData = List.of(
                    xepLoaiCounts.get("XUAT_SAC"),
                    xepLoaiCounts.get("GIOI"),
                    xepLoaiCounts.get("KHA"),
                    xepLoaiCounts.get("TRUNG_BINH"),
                    xepLoaiCounts.get("YEU"),
                    xepLoaiCounts.get("KEM")
            );

            // Tính toán dữ liệu cho biểu đồ cột (điểm tổng trung bình theo lớp)
            Map<String, Double> lopTotals = new HashMap<>();
            Map<String, Integer> lopCounts = new HashMap<>();
            for (Map<String, Object> item : thongKeData) {
                String lop = item.get("lop") != null ? (String) item.get("lop") : "Không xác định";
                Double diemTong = item.get("diemTong") != null ? ((Number) item.get("diemTong")).doubleValue() : 0.0;
                lopTotals.merge(lop, diemTong, Double::sum);
                lopCounts.merge(lop, 1, Integer::sum);
            }

            List<String> lopLabels = lopTotals.keySet().stream().sorted().collect(Collectors.toList());
            List<Double> lopAverages = lopLabels.stream()
                    .map(lop -> lopCounts.get(lop) > 0 ? lopTotals.get(lop) / lopCounts.get(lop) : 0.0)
                    .collect(Collectors.toList());

            // Log nội dung thongKeData để kiểm tra
            if (thongKeData != null && !thongKeData.isEmpty()) {
                logger.info("Nội dung thongKeData (mẫu 5 bản ghi đầu):");
                for (int i = 0; i < Math.min(5, thongKeData.size()); i++) {
                    Map<String, Object> item = thongKeData.get(i);
                    logger.info("Bản ghi {}: hocKyNamHoc={}, hocKy={}, namHoc={}, diemChiTiet={}", 
                                i + 1, item.get("hocKyNamHoc"), item.get("hocKy"), item.get("namHoc"), item.get("diemChiTiet"));
                }
            } else {
                logger.info("Không có dữ liệu thống kê: thongKeData is {}", thongKeData);
            }

            // Nếu không có dữ liệu thống kê
            if (thongKeData == null || thongKeData.isEmpty()) {
                model.addAttribute("message", "Không có dữ liệu thống kê phù hợp.");
                model.addAttribute("thongKeData", Collections.emptyList());
            } else {
                model.addAttribute("thongKeData", thongKeData);
            }

            // Thêm các danh sách và bộ lọc vào model
            model.addAttribute("khoaList", khoaList != null ? khoaList : Collections.emptyList());
            model.addAttribute("lopList", lopList != null ? lopList : Collections.emptyList());
            model.addAttribute("hocKyList", hocKyList != null ? hocKyList : Collections.emptyList());
            model.addAttribute("dieuList", dieuList != null ? dieuList : Collections.emptyList());
            model.addAttribute("pieData", pieData);
            model.addAttribute("lopLabels", lopLabels);
            model.addAttribute("lopAverages", lopAverages);
            model.addAttribute("filters", Map.of(
                    "khoaId", khoaId != null ? khoaId : "",
                    "lopId", lopId != null ? lopId : "",
                    "xepLoai", xepLoai != null ? xepLoai : "",
                    "hkNhId", defaultHkNhId != null ? defaultHkNhId : (hkNhId != null ? hkNhId : "")
            ));

        } catch (Exception e) {
            logger.error("Lỗi khi lấy dữ liệu thống kê: {}", e.getMessage(), e);
            model.addAttribute("error", "Lỗi khi tải dữ liệu thống kê: " + e.getMessage());
            model.addAttribute("thongKeData", Collections.emptyList());
            model.addAttribute("khoaList", Collections.emptyList());
            model.addAttribute("lopList", Collections.emptyList());
            model.addAttribute("hocKyList", Collections.emptyList());
            model.addAttribute("dieuList", Collections.emptyList());
            model.addAttribute("pieData", Collections.emptyList());
            model.addAttribute("lopLabels", Collections.emptyList());
            model.addAttribute("lopAverages", Collections.emptyList());
            model.addAttribute("filters", Map.of("khoaId", "", "lopId", "", "xepLoai", "", "hkNhId", ""));
        }

        return "thong-ke";
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasAnyRole('CVCTSV')")
    public ResponseEntity<Resource> exportDiemRenLuyenToCsv(
            @RequestParam(name = "khoaId", required = false) Long khoaId,
            @RequestParam(name = "lopId", required = false) Long lopId,
            @RequestParam(name = "xepLoai", required = false) String xepLoai,
            @RequestParam(name = "hkNhId", required = false) Long hkNhId) {
        try {
            logger.info("Export CSV request: khoaId={}, lopId={}, xepLoai={}, hkNhId={}",
                    khoaId, lopId, xepLoai, hkNhId);

            if (xepLoai != null && !xepLoai.trim().isEmpty()) {
                if (!isValidXepLoai(xepLoai)) {
                    logger.warn("Invalid xepLoai: {}", xepLoai);
                    return ResponseEntity.badRequest()
                            .body(new ByteArrayResource("Lỗi: Xếp loại không hợp lệ.".getBytes()));
                }
            }

            byte[] csvData = diemRenLuyenService.exportDiemRenLuyenToCsv(khoaId, lopId, xepLoai, hkNhId);

            if (csvData == null || csvData.length == 0) {
                logger.warn("No data found for CSV export with given parameters");
                return ResponseEntity.badRequest()
                        .body(new ByteArrayResource("Lỗi: Không tìm thấy dữ liệu để xuất.".getBytes()));
            }

            ByteArrayResource resource = new ByteArrayResource(csvData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diem_ren_luyen.csv")
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .contentLength(csvData.length)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error exporting CSV: {}", e.getMessage(), e);
            String errorMessage = "Lỗi server khi xuất CSV: " + e.getMessage();
            return ResponseEntity.badRequest()
                    .body(new ByteArrayResource(errorMessage.getBytes()));
        }
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('CVCTSV')")
    public ResponseEntity<Resource> exportDiemRenLuyenToPdf(
            @RequestParam(name = "khoaId", required = false) Long khoaId,
            @RequestParam(name = "lopId", required = false) Long lopId,
            @RequestParam(name = "xepLoai", required = false) String xepLoai,
            @RequestParam(name = "hkNhId", required = false) Long hkNhId) {
        try {
            logger.info("Export PDF request: khoaId={}, lopId={}, xepLoai={}, hkNhId={}",
                    khoaId, lopId, xepLoai, hkNhId);

            if (xepLoai != null && !xepLoai.trim().isEmpty()) {
                if (!isValidXepLoai(xepLoai)) {
                    logger.warn("Invalid xepLoai: {}", xepLoai);
                    return ResponseEntity.badRequest()
                            .body(new ByteArrayResource("Lỗi: Xếp loại không hợp lệ.".getBytes()));
                }
            }

            byte[] pdfData = diemRenLuyenService.exportDiemRenLuyenToPdf(khoaId, lopId, xepLoai, hkNhId);

            if (pdfData == null || pdfData.length == 0) {
                logger.warn("No data found for PDF export with given parameters");
                return ResponseEntity.badRequest()
                        .body(new ByteArrayResource("Lỗi: Không tìm thấy dữ liệu để xuất.".getBytes()));
            }

            ByteArrayResource resource = new ByteArrayResource(pdfData);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=diem_ren_luyen.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfData.length)
                    .body(resource);
        } catch (Exception e) {
            logger.error("Error exporting PDF: {}", e.getMessage(), e);
            String errorMessage = "Lỗi server khi xuất PDF: " + e.getMessage();
            return ResponseEntity.badRequest()
                    .body(new ByteArrayResource(errorMessage.getBytes()));
        }
    }

    private boolean isValidXepLoai(String xepLoai) {
        String[] validXepLoai = {"XUAT_SAC", "GIOI", "KHA", "TRUNG_BINH", "YEU", "KEM"};
        for (String valid : validXepLoai) {
            if (valid.equals(xepLoai)) {
                return true;
            }
        }
        return false;
    }
}