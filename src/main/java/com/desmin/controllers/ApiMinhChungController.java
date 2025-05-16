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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiMinhChungController {

    @Autowired
    private MinhChungService minhChungService;

    @Autowired
    private UserService userService;

    @PostMapping("/bao-thieu/{thamGiaId}")
    public ResponseEntity<Void> baoThieu(
            @PathVariable("thamGiaId") Long thamGiaId,
            @RequestParam("description") String description,
            @RequestParam("anhMinhChung") MultipartFile anhMinhChung,
            Authentication auth) {
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        MinhChung minhChung = new MinhChung();
        minhChung.setDescription(description);
        minhChung.setTrangThai(MinhChung.TrangThai.CHO_DUYET);

        minhChungService.addMinhChung(minhChung, anhMinhChung, user, thamGiaId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/cho-duyet")
    public ResponseEntity<List<MinhChung>> getMinhChungChoDuyet(
            @RequestParam(defaultValue = "1") String page,
            Authentication auth) {
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        Map<String, String> params = new HashMap<>();
        params.put("page", page);

        List<MinhChung> minhChungs = minhChungService.getMinhChungByTrangThaiAndKhoa(
                MinhChung.TrangThai.CHO_DUYET,
                user.getKhoaPhuTrach().getId(),
                params
        );
        return ResponseEntity.ok(minhChungs);
    }

    @PutMapping("/{minhChungId}/duyet")
    public ResponseEntity<Void> duyetMinhChung(
            @PathVariable("minhChungId") Long minhChungId,
            Authentication auth) {
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        minhChungService.approveMinhChung(minhChungId, user);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{minhChungId}/tu-choi")
    public ResponseEntity<Void> tuChoiMinhChung(
            @PathVariable("minhChungId") Long minhChungId,
            Authentication auth) {
        String username = auth.getName();
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("Người dùng không tồn tại");
        }

        minhChungService.rejectMinhChung(minhChungId, user);
        return ResponseEntity.ok().build();
    }
}