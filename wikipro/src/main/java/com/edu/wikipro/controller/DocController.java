package com.edu.wikipro.controller;

import com.edu.wikipro.common.Result;
import com.edu.wikipro.service.DocService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/doc")
@CrossOrigin(origins = "*", maxAge = 3600)
public class DocController {
    @Resource
    private DocService docService;

    @GetMapping("/getPageByDoc")
    public Result<?> getPageByDoc(Integer currentPage, Integer pageSize) {
        return docService.getPageByDoc(currentPage, pageSize);
    }

    @GetMapping("/list")
    public Result<?> list() {
        return docService.getAllDocs();
    }

    @GetMapping("/getPageQueryByDoc")
    public Result<?> getPageQueryByDoc(String keyword, String searchKey, Integer current, Integer currentPage, Integer pageSize) {
        String queryKeyword = org.springframework.util.StringUtils.hasText(keyword) ? keyword : searchKey;
        Integer queryCurrent = current != null ? current : (currentPage != null ? currentPage : 1);
        return docService.getPageQueryByDoc(queryKeyword, queryCurrent, pageSize);
    }

    @GetMapping("/findCategoryList")
    public Result<?> findCategoryList() {
        return docService.findCategoryList();
    }

    @GetMapping("/findByCategory")
    public Result<?> findByCategory(String category) {
        return docService.findByCategory(category);
    }

    @GetMapping("/getById")
    public Result<?> getById(Integer id) {
        return Result.success("获取成功", docService.getById(id));
    }
}
