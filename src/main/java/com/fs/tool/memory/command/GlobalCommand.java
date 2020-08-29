package com.fs.tool.memory.command;

/**
 * 全局命令
 *
 * @author zhaofushan
 * @date 2020/6/30
 */
public interface GlobalCommand {
    /**
     * 跨组copy所有数据，
     *
     * @param fromGroup from group
     * @param toGroup   to group
     * @param overwrite overwrite or not ,default true
     * @return
     */
    String copyAll(String fromGroup,
                   String toGroup,
                   Boolean overwrite);

    /**
     * 跨组copy数据，
     * 指定的key会被copy
     *
     * @param fromGroup from group
     * @param toGroup   to group
     * @param overwrite overwrite or not ,default true
     * @return
     */
    String copy(String fromGroup,
                String toGroup,
                Boolean overwrite);
}
