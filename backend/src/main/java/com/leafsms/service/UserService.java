package com.leafsms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.leafsms.entity.User;

public interface UserService extends IService<User> {
    
    User findByUsername(String username);
    
    Page<User> pageUsers(int page, int size, String keyword, String roleCode, Integer status);
}
