-- 系统用户表
CREATE TABLE IF NOT EXISTS sys_user (
    id          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    username    VARCHAR(64)  NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT 'BCrypt 密码哈希',
    nickname    VARCHAR(64)  DEFAULT NULL COMMENT '昵称',
    avatar      VARCHAR(255) DEFAULT NULL COMMENT '头像 URL',
    role        VARCHAR(32)  NOT NULL DEFAULT 'common' COMMENT '角色: admin/common',
    email       VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
    phone       VARCHAR(32)  DEFAULT NULL COMMENT '手机号',
    description VARCHAR(255) DEFAULT NULL COMMENT '简介',
    status      TINYINT      NOT NULL DEFAULT 1 COMMENT '状态: 1启用 0停用',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    create_by   BIGINT       DEFAULT NULL COMMENT '创建人',
    update_by   BIGINT       DEFAULT NULL COMMENT '更新人',
    deleted     TINYINT      NOT NULL DEFAULT 0 COMMENT '软删除: 0正常 1删除',
    version     INT          NOT NULL DEFAULT 0 COMMENT '乐观锁版本',
    PRIMARY KEY (id),
    UNIQUE KEY uk_sys_user_username (username)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统用户';
