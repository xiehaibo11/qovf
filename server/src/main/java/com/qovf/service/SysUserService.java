package com.qovf.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qovf.entity.SysUser;
import com.qovf.mapper.SysUserMapper;
import org.springframework.stereotype.Service;

/**
 * 系统用户服务。
 */
@Service
public class SysUserService extends ServiceImpl<SysUserMapper, SysUser> {

    /** 按用户名查询（唯一） */
    public SysUser getByUsername(String username) {
        return lambdaQuery().eq(SysUser::getUsername, username).one();
    }
}
