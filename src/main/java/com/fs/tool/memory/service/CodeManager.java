package com.fs.tool.memory.service;

import com.fs.tool.memory.dao.model.Code;
import com.fs.tool.memory.dao.repository.CodeRepository;
import com.fs.tool.memory.model.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 联想词管理器
 * @author zhaofushan
 * @date 2020/6/30
 *
 */
@Service
public class CodeManager {
    @Autowired
    CodeRepository codeRepository;

    private Map<String, Map<String, Code>> posMap = new LinkedHashMap<>();


    @PostConstruct
    public void init() {
        Iterable<Code> all = codeRepository.findAll();
        for (Code code : all) {
            posMap.putIfAbsent(code.getFirst(), new HashMap<>());
            posMap.get(code.getFirst()).put(code.getSecond(), code);
        }
    }

    /**
     * 是否有联想词数据
     *
     * @return
     */
    public boolean hasCodes() {
        return !posMap.isEmpty();
    }

    /**
     * 清除所有联想词
     */
    public void clearAll() {
        posMap.clear();
        codeRepository.deleteAll();
    }

    /**
     * 同步数据到数据库
     */
    public void sync() {
        List<Code> collect = posMap.values().stream()
                .flatMap(map -> map.values().stream()).collect(Collectors.toList());
        codeRepository.saveAll(collect);
    }

    /**
     * 保存联想词
     *
     * @param code
     */
    public void save(Code code) {
        posMap.putIfAbsent(code.getFirst(), new HashMap<>());
        posMap.get(code.getFirst()).put(code.getSecond(), code);
        codeRepository.save(code);
    }

    /**
     * 条件查询
     *
     * @return
     */
    public List<Code> queryByCondition(Query query) {
        return query(query)
                .collect(Collectors.toList());
    }

    private Stream<Code> query(Query query) {
        return posMap.values().stream()
                .flatMap(m -> m.values().stream())
                //记住条件
                .filter(info -> {
                    Boolean isRemembered = query.getIsRemembered();
                    return isRemembered == null || info.isRemembered() == isRemembered;
                })
                //联想词条件
                .filter(info -> {
                    Boolean hasWord = query.getHasWord();
                    if (hasWord == null) {
                        return true;
                    } else {
                        return hasWord == !StringUtils.isEmpty(info.getWord());
                    }
                })
                //word
                .filter(info -> {
                    String source = query.getCode();
                    String target = info.getCode();
                    if (source == null) {
                        return true;
                    } else {
                        return target.startsWith(source);
                    }
                });
    }

    /**
     * 查询所有
     *
     * @return
     */
    public List<Code> queryAll() {
        return posMap.values().stream()
                .flatMap(m -> m.values().stream())
                .collect(Collectors.toList());
    }

    /**
     * 通过row查询codes
     *
     * @param row row index
     * @return
     */
    public Collection<Code> queryByRowIndex(String row) {
        Map<String, Code> stringCodeMap = posMap.getOrDefault(row, new HashMap<>());
        return stringCodeMap.values();
    }

    /**
     * 通过编码查询
     *
     * @param code
     * @return
     */
    public Code queryByCode(String code) {
        assert code.length() == 2;
        char[] key = code.toCharArray();
        Map<String, Code> columnMap = posMap.get(key[0] + "");
        if (columnMap == null) {
            return null;
        }
        return columnMap.get(key[1] + "");
    }

    public long count(Query query) {
        return query(query).count();
    }
}
