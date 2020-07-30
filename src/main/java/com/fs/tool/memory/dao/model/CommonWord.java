package com.fs.tool.memory.dao.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

/**
 * 通用单词
 *
 * @author zhaofushan
 * @date 2020/7/29 0029 0:35
 */
@Entity
@Data
@NoArgsConstructor
public class CommonWord {
    @Id
    private String id;
    /**
     * key
     */
    private String key;
    /**
     * word group
     */
    private String wordGroup;
    /**
     * 联想词定义
     */
    private String definition;
    /**
     * 联想词定义
     */
    private String description;
    /**
     * 记住了么
     */
    private Boolean remembered;
    /**
     * 通过测试的次数
     */
    private Integer passTime;
    /**
     * 总测试的次数
     */
    private Integer testTime;

    public CommonWord(String key, String group, String wordDefinition, String wordDescription, boolean remembered, Integer passTime, Integer testTime) {
        this.id = UUID.randomUUID().toString();
        this.key = key;
        this.wordGroup = group;
        this.definition = wordDefinition;
        this.description = wordDescription;
        this.remembered = remembered;
        this.passTime = passTime;
        this.testTime = testTime;
    }

    @Override
    public String toString() {
        return "[" +
                "编码:'" + key + '\'' +
                ", 联想词:'" + definition + '\'' +
                ", 是否记住:" + (remembered ? "Y" : "N") +
                ", 通过测试次数:" + passTime +
                ", 总测试测试:" + testTime +
                ']';
    }
}
