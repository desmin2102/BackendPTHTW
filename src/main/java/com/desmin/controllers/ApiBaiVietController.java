/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.controllers;

import com.desmin.pojo.BaiViet;
import com.desmin.pojo.Comment;
import com.desmin.pojo.Like;
import com.desmin.services.BaiVietService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author ADMIN
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class ApiBaiVietController {

    @Autowired
    private BaiVietService baiVietService;

    @GetMapping("/baiviets")
    public ResponseEntity<List<BaiViet>> list(@RequestParam Map<String, String> params) {
        return new ResponseEntity<>(this.baiVietService.getAllBaiViet(params), HttpStatus.OK);
    }

    @PostMapping(path = "secure/baiviets", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@RequestParam Map<String, String> params,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        try {
            // Kiểm tra các tham số
            System.out.println("Creating BaiViet with params: " + params);
            if (params == null || params.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Thiếu thông tin bài viết");
            }

            // Gọi service để tạo bài viết
            BaiViet createdBaiViet = this.baiVietService.addBaiViet(params, imageFile);

            return new ResponseEntity<>(createdBaiViet, HttpStatus.CREATED);

        } catch (Exception e) {
            System.err.println("Error creating BaiViet: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi khi tạo bài viết: " + e.getMessage());
        }
    }

    @GetMapping("/baiviets/{id}")
    public ResponseEntity<BaiViet> getById(@PathVariable("id") long id) {
        BaiViet baiViet = baiVietService.getBaiVietById(id);
        if (baiViet != null) {
            return new ResponseEntity<>(baiViet, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("secure/delete/{id}")
    public ResponseEntity<?> deleteBaiViet(@PathVariable("id") long id) {
        try {
            baiVietService.deleteBaiViet(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa bài viết thành công");
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa bài viết: " + e.getMessage());
        }
    }

    @GetMapping("/baiviets/{id}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable("id") long id) {
        return new ResponseEntity<>(this.baiVietService.getComments(id), HttpStatus.OK);
    }

  @GetMapping("/baiviets/{id}/likes")
public ResponseEntity<List<Like>> getLikes(@PathVariable("id") long id) {
    return new ResponseEntity<>(this.baiVietService.getLikes(id), HttpStatus.OK);
}

}
