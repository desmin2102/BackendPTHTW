package com.desmin.services;

import com.desmin.pojo.MinhChung;
import com.desmin.pojo.User;
import java.util.List;
import java.util.Map;
import org.springframework.web.multipart.MultipartFile;

public interface MinhChungService {
    void addMinhChung(MinhChung minhChung, MultipartFile anhMinhChung, User user, Long thamGiaId);
    MinhChung getMinhChungById(Long id);
    List<MinhChung> getMinhChungByTrangThai(MinhChung.TrangThai trangThai, Map<String, String> params);
    List<MinhChung> getMinhChungByTrangThaiAndKhoa(MinhChung.TrangThai trangThai, Long khoaId, Map<String, String> params);
    void approveMinhChung(Long minhChungId, User user);
    void rejectMinhChung(Long minhChungId, User user);
}