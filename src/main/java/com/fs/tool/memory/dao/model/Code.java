package com.fs.tool.memory.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Code {
    /**
     * 联想code
     */
    @Id
    private String code;
    /**
     * first
     */
    private String first;
    /**
     * second
     */
    private String second;
    /**
     * 联想词
     */
    private String word;
    /**
     * 记住了么
     */
    private boolean remembered;
    /**
     * 通过测试的次数
     */
    @Column()
    private Integer passTime = 0;
    /**
     * 总测试的次数
     */
    @Column()
    private Integer testTime = 0;

    @Override
    public String toString() {
        return "Code{" +
                "编码:'" + code + '\'' +
                ", 联想词:'" + word + '\'' +
                ", 是否记住:" + (remembered ? "Y" : "N") +
                ", 通过测试次数:" + passTime +
                ", 总测试测试:" + testTime +
                '}';
    }
}
