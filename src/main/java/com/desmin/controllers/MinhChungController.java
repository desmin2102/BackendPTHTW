package com.desmin.controllers;

import com.desmin.pojo.HoatDongNgoaiKhoa;
import com.desmin.pojo.MinhChung;
import com.desmin.pojo.ThamGia;
import com.desmin.pojo.User;
import com.desmin.services.MinhChungService;
import com.desmin.services.ThamGiaService;
import com.desmin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class MinhChungController {

    @Autowired
    private MinhChungService minhChungService;

    @Autowired
    private UserService userService;

    @Autowired
    private ThamGiaService thamGiaService;

    @GetMapping("/minh-chung")
    @PreAuthorize("hasAnyRole('CVCTSV'")
    public String listMinhChung(Model model,
                                @RequestParam(value = "keyword", required = false) String keyword) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username);

            // Kiểm tra quyền truy cập
            if (currentUser == null || (currentUser.getRole() != User.Role.CVCTSV && currentUser.getRole() != User.Role.TRO_LY_SINH_VIEN)) {
                model.addAttribute("error", "Bạn không có quyền xem danh sách minh chứng chờ duyệt.");
                return "minh-chung-list";
            }

          
            List<MinhChung> minhChungs = minhChungService.getMinhChungByTrangThai(MinhChung.TrangThai.CHO_DUYET,null);

            // Lấy danh sách hoatDongId từ minh chứng CHO_DUYET
            Set<Long> hoatDongIdsChoDuyet = new HashSet<>();
            for (MinhChung mc : minhChungs) {
                if (mc.getThamGia() != null && mc.getThamGia().getHoatDongNgoaiKhoa() != null) {
                    hoatDongIdsChoDuyet.add(mc.getThamGia().getHoatDongNgoaiKhoa().getId());
                }
            }

            // Lấy danh sách tham gia có trạng thái BaoThieu (không lọc theo khoa)
            Map<String, String> paramsBaoThieu = new HashMap<>();
            paramsBaoThieu.put("state", "BaoThieu");
            List<ThamGia> thamGiasBaoThieu = thamGiaService.getThamGias(paramsBaoThieu);

            // Lọc hoạt động BaoThieu chỉ giữ những hoạt động có minh chứng CHO_DUYET
            Map<Long, HoatDongNgoaiKhoa> hoatDongMap = new HashMap<>();
            for (ThamGia thamGia : thamGiasBaoThieu) {
                if (thamGia.getHoatDongNgoaiKhoa() != null) {
                    Long hoatDongId = thamGia.getHoatDongNgoaiKhoa().getId();
                    if (hoatDongIdsChoDuyet.contains(hoatDongId) && !hoatDongMap.containsKey(hoatDongId)) {
                        hoatDongMap.put(hoatDongId, thamGia.getHoatDongNgoaiKhoa());
                    }
                }
            }
            List<HoatDongNgoaiKhoa> hoatDongs = new ArrayList<>(hoatDongMap.values());

            // Tạo cấu trúc dữ liệu cho mỗi hoạt động: danh sách minh chứng CHO_DUYET
            List<Map<String, Object>> hoatDongDataList = new ArrayList<>();
            for (HoatDongNgoaiKhoa hoatDong : hoatDongs) {
                Map<String, Object> hoatDongData = new HashMap<>();
                hoatDongData.put("hoatDong", hoatDong);

                // Lọc danh sách minh chứng CHO_DUYET cho hoạt động này
                List<MinhChung> minhChungsCuaHoatDong = minhChungs.stream()
                        .filter(mc -> mc.getThamGia() != null && 
                                      mc.getThamGia().getHoatDongNgoaiKhoa() != null && 
                                      mc.getThamGia().getHoatDongNgoaiKhoa().getId().equals(hoatDong.getId()))
                        .collect(Collectors.toList());
                hoatDongData.put("minhChungs", minhChungsCuaHoatDong);

                hoatDongDataList.add(hoatDongData);
            }

            // Truyền dữ liệu vào model
            model.addAttribute("hoatDongDataList", hoatDongDataList);
            model.addAttribute("isTroLySinhVien", currentUser.getRole() == User.Role.TRO_LY_SINH_VIEN);
            model.addAttribute("selectedKeyword", keyword);
            return "minh-chung-list";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy danh sách minh chứng: " + e.getMessage());
            return "minh-chung-list";
        }
    }
    @GetMapping("/minh-chung/{id}")
    @PreAuthorize("hasAnyRole('CVCTSV')")
    public String showMinhChungDetail(@PathVariable("id") Long id, Model model) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            User currentUser = userService.getUserByUsername(username);

            // Kiểm tra quyền truy cập
            if (currentUser == null || (currentUser.getRole() != User.Role.CVCTSV && currentUser.getRole() != User.Role.TRO_LY_SINH_VIEN)) {
                model.addAttribute("error", "Bạn không có quyền xem chi tiết minh chứng.");
                return "minh-chung-detail";
            }

            // Lấy chi tiết minh chứng
            MinhChung minhChung = minhChungService.getMinhChungById(id);
            if (minhChung == null) {
                model.addAttribute("error", "Không tìm thấy minh chứng.");
                return "minh-chung-detail";
            }

            // Truyền dữ liệu vào model
            model.addAttribute("minhChung", minhChung);
            model.addAttribute("isTroLySinhVien", currentUser.getRole() == User.Role.TRO_LY_SINH_VIEN);
            return "minh-chung-detail";
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi lấy chi tiết minh chứng: " + e.getMessage());
            return "minh-chung-detail";
        }
    }

    @PostMapping("/minh-chung/duyet/{id}")
    @PreAuthorize("hasAnyRole('CVCTSV')")
    public String approveMinhChung(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            minhChungService.approveMinhChung(id);
            redirectAttributes.addFlashAttribute("actionSuccess", "Đã duyệt minh chứng thành công.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("actionError", "Không thể duyệt minh chứng: " + e.getMessage());
        }
        return "redirect:/minh-chung/" + id;
    }

    @PostMapping("/minh-chung/tu-choi/{id}")
    @PreAuthorize("hasAnyRole('CVCTSV')")
    public String rejectMinhChung(@PathVariable("id") Long id,
                                  @RequestParam(value = "lyDoTuChoi", required = false) String lyDoTuChoi,
                                  RedirectAttributes redirectAttributes) {
        try {
            if (lyDoTuChoi == null || lyDoTuChoi.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng nhập lý do từ chối.");
            }
            minhChungService.rejectMinhChung(id, lyDoTuChoi);
            redirectAttributes.addFlashAttribute("actionSuccess", "Đã từ chối minh chứng thành công.");
            return "redirect:/minh-chung";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("actionError", "Không thể từ chối minh chứng: " + e.getMessage());
            return "redirect:/minh-chung/" + id;
        }
    }
}