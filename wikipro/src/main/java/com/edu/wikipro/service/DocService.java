package com.edu.wikipro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Doc;

public interface DocService extends IService<Doc> {
    Result<?> getPageByDoc(Integer currentPage, Integer pageSize);
    Result<?> getAllDocs();
    Result<?> getPageQueryByDoc(String searchKey, Integer currentPage, Integer pageSize);
    Result<?> findCategoryList();
    Result<?> findByCategory(String category);
}
