package com.edu.wikipro.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Banner;
import com.edu.wikipro.mapper.BannerMapper;
import com.edu.wikipro.service.BannerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements BannerService {

    @Resource
    private BannerMapper bannerMapper;

    @Override
    public Result<?> findBanners() {
        List<Banner> banners = bannerMapper.selectList(null);
        return Result.success("获取成功", banners);
    }
}
