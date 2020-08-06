package com.fs.tool.memory.dao.query;

import lombok.Data;

/**
 * @author zhaofushan
 * @date 2020/8/6 0006 22:32
 */
@Data
public class PageQuery {
    final static int DEFAULT_PAGE_NUM = 1;
    final static int DEFAULT_PAGE_SIZE = 10;

    private int pageNum;
    private int pageSize;
}
