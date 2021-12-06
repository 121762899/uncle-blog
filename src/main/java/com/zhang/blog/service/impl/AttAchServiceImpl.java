package com.zhang.blog.service.impl;

import com.zhang.blog.dao.AttachMapper;
import com.zhang.blog.dao.BlogLinkMapper;
import com.zhang.blog.entity.Attach;
import com.zhang.blog.service.AttAchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AttAchServiceImpl implements AttAchService {

    @Resource
    private AttachMapper attachMapper;

    @Override
    public void addAttAch(Attach attAchDomain) {
        attachMapper.insert(attAchDomain);
    }
}
