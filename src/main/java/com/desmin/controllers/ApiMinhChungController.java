package com.desmin.controllers;

import com.desmin.pojo.MinhChung;
import com.desmin.pojo.User;
import com.desmin.services.MinhChungService;
import com.desmin.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiMinhChungController {

    @Autowired
    private MinhChungService minhChungService;

    @Autowired
    private UserService userService;

    @PostMapping("/secure/bao-thieu/{thamGiaId}")
    public ResponseEntity<Void> baoThieu(
            @PathVariable("thamGiaId") Long thamGiaId,
            @RequestParam("description") String description,
            @RequestParam("anhMinhChung") MultipartFile anhMinhChung,
            @RequestParam("userId") long userId
    ) {

        MinhChung minhChung = new MinhChung();
        minhChung.setDescription(description);
        minhChung.setTrangThai(MinhChung.TrangThai.CHO_DUYET);

        minhChungService.addMinhChung(minhChung, anhMinhChung, userId, thamGiaId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/secure/cho-duyet")
    public ResponseEntity<List<MinhChung>> getMinhChungByTrangThaiAndKhoa(
            @RequestParam("trangThai") MinhChung.TrangThai trangThai,
            @RequestParam("khoaId") Long khoaId,
            @RequestParam Map<String, String> params // để lấy thêm param page
    ) {
        List<MinhChung> results = minhChungService.getMinhChungByTrangThaiAndKhoa(trangThai, khoaId, params);
        return ResponseEntity.ok(results);
    }
    
      

 @PutMapping("/secure/duyet/{minhChungId}")
    public ResponseEntity<Void> duyetMinhChung(@PathVariable("minhChungId") Long minhChungId) {
        try {
            minhChungService.approveMinhChung(minhChungId);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(null);
        }
    }

 
    @PutMapping("/secure/tu-choi/{minhChungId}")
    public ResponseEntity<?> tuChoiMinhChung(
            @PathVariable("minhChungId") Long minhChungId,
            @RequestBody String lyDoTuChoi) {
        try {
            minhChungService.rejectMinhChung(minhChungId, lyDoTuChoi);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", "Lỗi hệ thống: " + ex.getMessage()));
        }
    }
    
    @GetMapping("/secure/minh-chung/{id}")
    public ResponseEntity<?> getMinhChungById(@PathVariable("id") Long id) {
        MinhChung minhChung = minhChungService.getMinhChungById(id);
        if (minhChung == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(minhChung);
    }
}
