package com.leafsms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leafsms.entity.Clazz;

import java.util.List;

public interface ClazzService extends IService<Clazz> {
    
    Page<Clazz> pageClazzs(int page, int size, String grade, String className);
    
    List<Clazz> listByGrade(String grade);
    
    boolean addClazz(Clazz clazz);
    
    boolean updateClazz(Clazz clazz);
    
    boolean deleteClazz(String classId);
}
