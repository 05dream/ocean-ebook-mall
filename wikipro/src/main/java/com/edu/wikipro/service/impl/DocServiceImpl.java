package com.edu.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Doc;
import com.edu.wikipro.mapper.DocMapper;
import com.edu.wikipro.service.DocService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DocServiceImpl extends ServiceImpl<DocMapper, Doc> implements DocService {

    @Resource
    private DocMapper docMapper;

    @Override
    public Result<?> getPageByDoc(Integer currentPage, Integer pageSize) {
        Page<Doc> page = new Page<>(currentPage, pageSize);
        QueryWrapper<Doc> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("doc_id");
        Page<Doc> result = docMapper.selectPage(page, wrapper);
        return Result.success("获取成功", result);
    }

    @Override
    public Result<?> getAllDocs() {
        QueryWrapper<Doc> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("doc_id");
        List<Doc> docs = docMapper.selectList(wrapper);
        return Result.success("获取成功", docs);
    }

    @Override
    public Result<?> getPageQueryByDoc(String searchKey, Integer currentPage, Integer pageSize) {
        Page<Doc> page = new Page<>(currentPage, pageSize);
        LambdaQueryWrapper<Doc> wrapper = new LambdaQueryWrapper<>();
        if (org.springframework.util.StringUtils.hasText(searchKey)) {
            wrapper.like(Doc::getDocTitle, searchKey)
                   .or()
                   .like(Doc::getDocDesc, searchKey)
                   .or()
                   .like(Doc::getAuthor, searchKey)
                   .or()
                   .like(Doc::getCategory, searchKey);
        }
        wrapper.orderByDesc(Doc::getDocId);
        Page<Doc> result = docMapper.selectPage(page, wrapper);
        return Result.success("获取成功", result);
    }

    @Override
    public Result<?> findCategoryList() {
        List<String> categories = docMapper.selectObjs(new QueryWrapper<Doc>()
                .select("DISTINCT category")
                .isNotNull("category"))
                .stream()
                .map(Object::toString)
                .collect(Collectors.toList());
        return Result.success("获取成功", categories);
    }

    @Override
    public Result<?> findByCategory(String category) {
        LambdaQueryWrapper<Doc> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Doc::getCategory, category)
               .orderByDesc(Doc::getDocId);
        List<Doc> docs = docMapper.selectList(wrapper);
        return Result.success("获取成功", docs);
    }
}
