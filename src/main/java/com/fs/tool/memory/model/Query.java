package com.fs.tool.memory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询条件
 *
 * @author zhaofushan
 * @date 2020/6/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
