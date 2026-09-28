package com.edu.wikipro.controller;

import com.edu.wikipro.common.Result;
import com.edu.wikipro.service.BannerService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/banner")
@CrossOrigin(origins = "*", maxAge = 3600)
public class BannerController {
    @Resource
    private BannerService bannerService;

    @GetMapping("/findBanners")
    public Result<?> findBanners() {
        return bannerService.findBanners();
    }
}
