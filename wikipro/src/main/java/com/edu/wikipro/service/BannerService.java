package com.edu.wikipro.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Banner;

public interface BannerService extends IService<Banner> {
    Result<?> findBanners();
}
