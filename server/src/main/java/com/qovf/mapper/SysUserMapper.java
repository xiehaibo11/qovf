package com.qovf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qovf.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户 Mapper。继承 MyBatis-Plus BaseMapper，提供基础 CRUD。
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}
