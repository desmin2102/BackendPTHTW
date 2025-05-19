package com.desmin.repositories;

import com.desmin.pojo.Like;

public interface LikeRepository {
    void addLike(Like like);

    void removeLike(Like like);
    
    boolean existsLike(long userId,long baiVietId);
}