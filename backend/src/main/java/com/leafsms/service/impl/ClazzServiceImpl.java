package com.leafsms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.leafsms.entity.Clazz;
import com.leafsms.mapper.ClazzMapper;
import com.leafsms.service.ClazzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {
    
    @Autowired
    private ClazzMapper clazzMapper;

    @Override
    public Page<Clazz> pageClazzs(int page, int size, String grade, String className) {
        Page<Clazz> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(grade)) {
            wrapper.eq(Clazz::getGrade, grade);
        }
        if (StringUtils.hasText(className)) {
            wrapper.like(Clazz::getClassName, className);
        }
        wrapper.eq(Clazz::getStatus, 1);
        wrapper.orderByAsc(Clazz::getGrade, Clazz::getClassName);
        
        return clazzMapper.selectPage(pageParam, wrapper);
    }

    @Override
    public List<Clazz> listByGrade(String grade) {
        LambdaQueryWrapper<Clazz> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Clazz::getGrade, grade);
        wrapper.eq(Clazz::getStatus, 1);
        wrapper.orderByAsc(Clazz::getClassName);
        return clazzMapper.selectList(wrapper);
    }

    @Override
    public boolean addClazz(Clazz clazz) {
        clazz.setStatus(1);
        if (clazz.getStudentCount() == null) {
            clazz.setStudentCount(0);
        }
        return clazzMapper.insert(clazz) > 0;
    }

    @Override
    public boolean updateClazz(Clazz clazz) {
        return clazzMapper.updateById(clazz) > 0;
    }

    @Override
    public boolean deleteClazz(String classId) {
        Clazz clazz = clazzMapper.selectById(classId);
        if (clazz == null) {
            return false;
        }
        clazz.setStatus(0);
        return clazzMapper.updateById(clazz) > 0;
    }
}
