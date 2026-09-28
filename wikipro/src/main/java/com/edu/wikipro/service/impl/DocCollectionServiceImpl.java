package com.edu.wikipro.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.edu.wikipro.common.Result;
import com.edu.wikipro.entity.Doc;
import com.edu.wikipro.entity.DocCollection;
import com.edu.wikipro.mapper.DocCollectionMapper;
import com.edu.wikipro.mapper.DocMapper;
import com.edu.wikipro.service.DocCollectionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocCollectionServiceImpl extends ServiceImpl<DocCollectionMapper, DocCollection> implements DocCollectionService {

    @Resource
    private DocMapper docMapper;

    @Override
    public Result<?> addCollection(Integer userId, Integer docId) {
        LambdaQueryWrapper<DocCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocCollection::getUserId, userId).eq(DocCollection::getDocId, docId);
        if (baseMapper.selectCount(wrapper) > 0) {
            return Result.fail("已收藏");
        }
        
        DocCollection collection = new DocCollection();
        collection.setUserId(userId);
        collection.setDocId(docId);
        baseMapper.insert(collection);
        return Result.success("收藏成功");
    }

    @Override
    public Result<?> removeCollection(Integer userId, Integer docId) {
        LambdaQueryWrapper<DocCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocCollection::getUserId, userId).eq(DocCollection::getDocId, docId);
        int deleted = baseMapper.delete(wrapper);
        if (deleted > 0) {
            return Result.success("取消收藏");
        }
        return Result.fail("未收藏");
    }

    @Override
    public Result<?> checkCollection(Integer userId, Integer docId) {
        LambdaQueryWrapper<DocCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocCollection::getUserId, userId).eq(DocCollection::getDocId, docId);
        boolean exists = baseMapper.selectCount(wrapper) > 0;
        return Result.success("", exists);
    }

    @Override
    public Result<?> getCollectionsByUserId(Integer userId) {
        LambdaQueryWrapper<DocCollection> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DocCollection::getUserId, userId);
        List<DocCollection> collections = baseMapper.selectList(wrapper);
        
        List<Doc> docs = new ArrayList<>();
        for (DocCollection c : collections) {
            Doc doc = docMapper.selectById(c.getDocId());
            if (doc != null) {
                docs.add(doc);
            }
        }
        return Result.success("获取成功", docs);
    }
}