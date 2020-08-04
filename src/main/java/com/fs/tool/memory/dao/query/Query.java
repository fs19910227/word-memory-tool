package com.fs.tool.memory.dao.query;

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
     * 是否存在定义
     */
    private Boolean existDefinition;
    /**
     * code search mode
     */
    private Mode codeMode = Mode.EXACT;
    /**
     * code搜索词
     */
    private String code;
    /**
     * 分组信息
     */
    private String group;
    /**
     * 通过测试的次数区间
     */
    private Region passTimes;

    public static class Region {
        public int start = 0;
        public int end = Integer.MAX_VALUE;
    }
}
