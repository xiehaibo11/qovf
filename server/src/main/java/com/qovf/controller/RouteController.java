package com.qovf.controller;

import com.qovf.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 动态路由（菜单）接口。
 * 底座暂不返回任何后端动态路由，菜单全部由前端 src/router/modules 静态生成。
 */
@RestController
public class RouteController {

    @GetMapping("/get-async-routes")
    public Result<List<Map<String, Object>>> getAsyncRoutes() {
        return Result.ok(Collections.emptyList());
    }
}
