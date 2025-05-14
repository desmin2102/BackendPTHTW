/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.Khoa;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface KhoaRepository {
    List<Khoa> getKhoas(Map<String, String> params);
    Khoa getKhoaById(long id);

}
