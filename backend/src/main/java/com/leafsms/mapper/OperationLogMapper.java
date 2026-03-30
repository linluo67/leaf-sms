package com.leafsms.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.leafsms.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}