package com.fs.tool.memory.model;

import lombok.Data;

/**
 * 查询条件
 */
@Data
public class Query {
    /**
     * 是否记住
     */
    private Boolean isRemembered;
    /**
     * 是否存在联想词
     */
    private Boolean hasWord;
    /**
     * 编码搜索 前缀模糊
     */
    private String code;
}
