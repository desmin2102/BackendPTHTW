package com.desmin.controllers;

import com.desmin.pojo.Dieu;
import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.HocKyNamHoc;
import com.desmin.services.DieuService;
import com.desmin.services.HoatDongNgoaiKhoaService;
import com.desmin.services.HocKyNamHocService;
import com.desmin.services.ThamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/hoat-dong")
public class HoatDongController {

    @Autowired
    private HoatDongNgoaiKhoaService hoatDongNgoaiKhoaService;

    @Autowired
    private DieuService dieuService;

    @Autowired
    private HocKyNamHocService hocKyNamHocService;

    @Autowired
    private ThamGiaService thamGiaService;

    @GetMapping
    @PreAuthorize("hasRole('CVCTSV')")
    public String listHoatDong(Model model, @RequestParam(name = "page", defaultValue = "1") String page) {
        try {
            Map<String, String> params = new HashMap<>();
            params.put("page", page);
            List<HoatDongNgoaiKhoa> hoatDongs = hoatDongNgoaiKhoaService.getHoatDongNgoaiKhoas(params);
            if (hoatDongs == null || hoatDongs.isEmpty()) {
                model.addAttribute("error", "Không có hoạt động ngoại khóa nào.");
                model.addAttribute("hoatDongs", Collections.emptyList());
            } else {
                model.addAttribute("hoatDongs", hoatDongs);
            }
            model.addAttribute("currentPage", Integer.parseInt(page));
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách hoạt động ngoại khóa: " + e.getMessage());
            model.addAttribute("hoatDongs", Collections.emptyList());
            model.addAttribute("currentPage", Integer.parseInt(page));
        }
        return "hoat-dong";
    }

    @GetMapping("/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String showCreateForm(Model model) {
        model.addAttribute("hoatDong", new HoatDongNgoaiKhoa());
        try {
            List<Dieu> dieuList = dieuService.getDieus(new HashMap<>());
            List<HocKyNamHoc> hocKyList = hocKyNamHocService.getHocKyNamHocs(new HashMap<>());
            model.addAttribute("dieuList", dieuList != null ? dieuList : Collections.emptyList());
            model.addAttribute("hocKyList", hocKyList != null ? hocKyList : Collections.emptyList());
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu điều hoặc học kỳ: " + e.getMessage());
            model.addAttribute("dieuList", Collections.emptyList());
            model.addAttribute("hocKyList", Collections.emptyList());
        }
        return "create-hoat-dong";
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('CVCTSV')")
    public String createHoatDong(@ModelAttribute("hoatDong") HoatDongNgoaiKhoa hoatDong, 
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        List<Dieu> dieuList = dieuService.getDieus(new HashMap<>());
        List<HocKyNamHoc> hocKyList = hocKyNamHocService.getHocKyNamHocs(new HashMap<>());

        // Kiểm tra thủ công các trường bắt buộc
        if (hoatDong.getMaHoatDong() == null || hoatDong.getMaHoatDong().trim().isEmpty() ||
            hoatDong.getTenHoatDong() == null || hoatDong.getTenHoatDong().trim().isEmpty() ||
            hoatDong.getNgayDienRa() == null || hoatDong.getHanDangKy() == null ||
            hoatDong.getDiemRenLuyen() == null || hoatDong.getDieu() == null || hoatDong.getHkNh() == null) {
            model.addAttribute("error", "Vui lòng điền đầy đủ các trường bắt buộc.");
            model.addAttribute("dieuList", dieuList != null ? dieuList : Collections.emptyList());
            model.addAttribute("hocKyList", hocKyList != null ? hocKyList : Collections.emptyList());
            return "create-hoat-dong";
        }

        try {
            // Lấy ID của Dieu và HocKyNamHoc từ form
            Long dieuId = hoatDong.getDieu() != null ? hoatDong.getDieu().getId() : null;
            Long hocKyId = hoatDong.getHkNh() != null ? hoatDong.getHkNh().getId() : null;

            // Kiểm tra dieuId và hocKyId
            if (dieuId == null || hocKyId == null) {
                model.addAttribute("error", "Vui lòng chọn điều và học kỳ hợp lệ.");
                model.addAttribute("dieuList", dieuList != null ? dieuList : Collections.emptyList());
                model.addAttribute("hocKyList", hocKyList != null ? hocKyList : Collections.emptyList());
                return "create-hoat-dong";
            }

            // Lấy đối tượng Dieu và HocKyNamHoc từ service
            Dieu selectedDieu = dieuService.getDieuById(dieuId);
            HocKyNamHoc selectedHocKy = hocKyNamHocService.getHocKyNamHocById(hocKyId);

            // Kiểm tra nếu không tìm thấy Dieu hoặc HocKyNamHoc
            if (selectedDieu == null || selectedHocKy == null) {
                model.addAttribute("error", "Điều hoặc học kỳ không tồn tại.");
                model.addAttribute("dieuList", dieuList != null ? dieuList : Collections.emptyList());
                model.addAttribute("hocKyList", hocKyList != null ? hocKyList : Collections.emptyList());
                return "create-hoat-dong";
            }

            // Gán lại đối tượng vào hoatDong
            hoatDong.setDieu(selectedDieu);
            hoatDong.setHkNh(selectedHocKy);

            // Tự động điền các giá trị mặc định
            hoatDong.setActive(true);
            hoatDong.setCreatedDate(LocalDateTime.now());
            hoatDong.setUpdatedDate(LocalDateTime.now());

            // Lưu hoạt động vào cơ sở dữ liệu
            hoatDongNgoaiKhoaService.addHoatDongNgoaiKhoa(hoatDong);
            redirectAttributes.addFlashAttribute("message", "Tạo hoạt động thành công!");
            return "redirect:/hoat-dong";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi tạo hoạt động: " + e.getMessage());
            model.addAttribute("dieuList", dieuList != null ? dieuList : Collections.emptyList());
            model.addAttribute("hocKyList", hocKyList != null ? hocKyList : Collections.emptyList());
            return "create-hoat-dong";
        }
    }

    @GetMapping("/export-csv/{id}")
    @PreAuthorize("hasRole('CVCTSV')")
    public ResponseEntity<Resource> exportThamGiaToCsv(@PathVariable("id") Long hoatDongId) {
        try {
            byte[] csvBytes = thamGiaService.exportThamGiaToCsv(hoatDongId);

            ByteArrayResource resource = new ByteArrayResource(csvBytes);

            HttpHeaders headers = new HttpHeaders();
            String fileName = "tham_gia_hoat_dong_" + hoatDongId + ".csv";
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(csvBytes.length)
                    .body(resource);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(new ByteArrayResource(("Lỗi: " + e.getMessage()).getBytes()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ByteArrayResource(("Lỗi khi xuất file CSV: " + e.getMessage()).getBytes()));
        }
    }

    @PostMapping("/diem-danh-csv/{id}")
    @PreAuthorize("hasRole('CVCTSV')")
    public String diemDanhByCsv(@PathVariable("id") Long hoatDongId,
                                @RequestParam("file") MultipartFile file,
                                RedirectAttributes redirectAttributes) {
        try {
            thamGiaService.diemDanhByCsv(hoatDongId, file);
            redirectAttributes.addFlashAttribute("message", "Điểm danh và cộng điểm thành công!");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi điểm danh: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xử lý file CSV: " + e.getMessage());
        }
        return "redirect:/hoat-dong";
    }
}