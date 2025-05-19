/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.desmin.services;

import com.desmin.pojo.Like;


public interface LikeService {
boolean addLike(Like like);
       boolean existsLike(long userId,long baiVietId);

}