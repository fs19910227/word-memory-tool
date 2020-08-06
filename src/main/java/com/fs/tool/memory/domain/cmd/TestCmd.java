package com.fs.tool.memory.domain.cmd;

import com.fs.tool.memory.dao.model.CommonWordDO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 测试cmd
 *
 * @author zhaofushan
 * @date 2020/8/6 0006 20:25
 */
@Data
@Accessors(chain = true)
public class TestCmd {
    /**
     * 是否随机
     */
    private boolean isRandom;
    /**
     * 重复模式
     */
    private boolean repeatMode;
    /**
     * 数据源
     */
    private List<CommonWordDO> wordDOList;
}
