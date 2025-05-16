package com.desmin.repositories;

import com.desmin.pojo.MinhChung;
import java.util.List;
import java.util.Map;

public interface MinhChungRepository {
    void saveMinhChung(MinhChung minhChung);
    MinhChung findById(Long id);
    List<MinhChung> findByTrangThai(MinhChung.TrangThai trangThai, Map<String, String> params);
    List<MinhChung> findByTrangThaiAndKhoa(MinhChung.TrangThai trangThai, Long khoaId, Map<String, String> params);
}