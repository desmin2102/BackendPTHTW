/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.Dieu;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ADMIN
 */
public interface DieuService {
    List<Dieu> getDieus(Map<String, String> params);
    Dieu getDieuById(long id);

}
