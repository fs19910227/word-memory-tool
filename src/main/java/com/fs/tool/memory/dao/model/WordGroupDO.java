package com.fs.tool.memory.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 单词分组
 *
 * @author zhaofushan
 * @date 2020/7/29 0029 0:35
 */
@Entity
@Data
@NoArgsConstructor
@Table(name = "word_group")
@Accessors(chain = true)
public class WordGroupDO {
    @Id
    private String id;
    /**
     * name
     */
    private String name;
    /**
     * word group
     */
    private String description;
    /**
     * 是否default
     */
    private Boolean isDefault;

    @Override
    public String toString() {
        return name + ":\"" + (description == null ? "" : description) + "\"";
    }
}
