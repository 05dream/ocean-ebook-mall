package com.edu.wikipro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.DocCollection;

public interface DocCollectionService extends IService<DocCollection> {
    Result<?> addCollection(Integer userId, Integer docId);
    Result<?> removeCollection(Integer userId, Integer docId);
    Result<?> checkCollection(Integer userId, Integer docId);
    Result<?> getCollectionsByUserId(Integer userId);
}