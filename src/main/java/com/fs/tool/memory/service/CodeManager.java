package com.fs.tool.memory.service;

import com.fs.tool.memory.dao.model.Code;
import com.fs.tool.memory.dao.repository.CodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 联想词管理器
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

    public void saveAll() {
        List<Code> collect = posMap.values().stream()
                .flatMap(map -> map.values().stream()).collect(Collectors.toList());
        codeRepository.saveAll(collect);
    }

    public void save(Code code) {
        codeRepository.save(code);
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
}
