/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.HocKyNamHoc;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface HocKyNamHocService {

    List<HocKyNamHoc> getHocKyNamHocs(Map<String, String> params);

    HocKyNamHoc getHocKyNamHocById(long id);
}
