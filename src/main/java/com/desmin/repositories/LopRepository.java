/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.repositories;

import com.desmin.pojo.Khoa;
import com.desmin.pojo.Lop;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface LopRepository {

    List<Lop> getLops(Map<String, String> params);

    Lop getLopById(int id);
}
