package com.fs.tool.memory.dao.repository;

import com.fs.tool.memory.dao.mapper.GroupMapper;
import com.fs.tool.memory.dao.model.WordGroupDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * @author zhaofushan
 * @date 2020/8/6 0006 22:59
 */
@Repository
public class GroupRepository implements IGroupRepository {
    @Autowired
    private GroupMapper groupRepository;

    /**
     * 是否存在分组
     */
    @Override
    public boolean exist(WordGroupDO wordGroup) {
        Example<WordGroupDO> of = Example.of(wordGroup);
        return groupRepository.exists(of);
    }

    @Override
    public Optional<WordGroupDO> defaultGroup() {
        WordGroupDO wordGroup = new WordGroupDO();
        Example<WordGroupDO> of = Example.of(wordGroup);
        List<WordGroupDO> all = groupRepository.findAll(of);
        if (all.isEmpty()) {
            return Optional.empty();
        }
        Optional<WordGroupDO> any = all.stream()
                .filter(WordGroupDO::getIsDefault)
                .findAny();
        if (!any.isPresent()) {
            WordGroupDO wordGroupDO = all.get(0);
            wordGroupDO.setIsDefault(true);
            groupRepository.save(wordGroupDO);
            return Optional.of(wordGroupDO);
        } else {
            return any;
        }
    }

    /**
     * 获取分组
     *
     * @return
     */
    @Override
    public Optional<WordGroupDO> queryOne(String name) {
        WordGroupDO wordGroup = new WordGroupDO();
        wordGroup.setName(name);
        Example<WordGroupDO> of = Example.of(wordGroup);
        of.getMatcher().withMatcher("name", matcher -> matcher.exact());
        return groupRepository.findOne(of);
    }


    /**
     * 新增分组
     */
    @Override
    public void add(WordGroupDO wordGroup) {
        wordGroup.setId(UUID.randomUUID().toString());
        groupRepository.save(wordGroup);
    }

    @Override
    public void save(WordGroupDO groupDO) {
        groupRepository.save(groupDO);
    }

    @Override
    public void deleteById(String id) {
        groupRepository.deleteById(id);
    }

    @Override
    public void saveAll(Iterable<WordGroupDO> groups) {
        groupRepository.saveAll(groups);
    }

    /**
     * 所有分组信息
     *
     * @return
     */
    @Override
    public List<WordGroupDO> groups() {
        return groupRepository.findAll();
    }
}
