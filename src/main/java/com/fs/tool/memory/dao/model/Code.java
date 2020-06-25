package com.fs.tool.memory.dao.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Override
    public String toString() {
        return code.toUpperCase() + ":" + word;
    }
}
