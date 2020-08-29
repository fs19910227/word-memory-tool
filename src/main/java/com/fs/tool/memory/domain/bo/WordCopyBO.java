package com.fs.tool.memory.domain.bo;

import com.fs.tool.memory.dao.model.CommonWordDO;
import com.fs.tool.memory.dao.model.WordGroupDO;
import com.fs.tool.memory.dao.query.Query;
import com.fs.tool.memory.dao.repository.ICodeRepository;
import com.fs.tool.memory.dao.repository.IGroupRepository;
import com.fs.tool.memory.domain.service.IOService;
import lombok.extern.slf4j.Slf4j;
import org.jline.reader.LineReader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 单词copy bo
 *
 * @author zhaofushan
 * @date 2020/8/29 0029 22:15
 */
@Component
@Slf4j
public class WordCopyBO implements InitializingBean {
    @Autowired
    private ICodeRepository codeRepository;
    @Autowired
    private IGroupRepository groupRepository;
    @Autowired
    private IOService consoleService;

    private LineReader reader;

    public void copyAll(String fromGroup, String toGroup, Boolean overwrite) {
        check(fromGroup, toGroup);

        Query build = Query.builder()
                .group(fromGroup)
                .build();
        List<CommonWordDO> allFrom = codeRepository.queryByCondition(build);
        doCopy(allFrom, toGroup, overwrite);
    }

    public void copy(String fromGroup, String toGroup, boolean overwrite) {
        check(fromGroup, toGroup);
        List<CommonWordDO> words = new ArrayList<>();
        while (true) {
            consoleService.outputLn("=========================================================================");
            consoleService.outputLn(String.format("please input codes at group %s(Quit:q,Confirm:y)", fromGroup));
            String input = consoleService.readLine(reader).toLowerCase();
            switch (input) {
                case "y":
                    doCopy(words, toGroup, overwrite);
                    consoleService.outputLn("copy success");
                    return;
                case "q":
                    consoleService.outputLn("exit copy mode");
                    return;
                default:
                    String code = input.toUpperCase();
                    Query build = Query.builder()
                            .group(fromGroup)
                            .code(code)
                            .build();
                    Optional<CommonWordDO> commonWordDO = codeRepository.queryOne(build);
                    if (commonWordDO.isPresent()) {
                        words.add(commonWordDO.get());
                        consoleService.outputLn("add word " + code + " to copy list");
                    } else {
                        consoleService.outputLn("word not exit!");
                    }
            }
        }
    }

    private void check(String fromGroup, String toGroup) {
        //check
        if (!groupRepository.exist(new WordGroupDO().setName(fromGroup))) {
            throw new RuntimeException("group " + fromGroup + " not exist");
        }
        if (!groupRepository.exist(new WordGroupDO().setName(toGroup))) {
            throw new RuntimeException("group " + toGroup + " not exist");
        }
    }

    private void doCopy(List<CommonWordDO> words, String toGroup, boolean overWrite) {
        for (CommonWordDO word : words) {
            word.setWordGroup(toGroup);
            Query build = Query.builder()
                    .group(toGroup)
                    .code(word.getKey())
                    .build();
            Optional<CommonWordDO> target = codeRepository.queryOne(build);
            if (target.isPresent()) {
                if (overWrite) {
                    word.setId(target.get().getId());
                    codeRepository.save(word);
                }
            } else {
                word.setId(UUID.randomUUID().toString());
                codeRepository.save(word);
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.reader = consoleService.createReader();
    }

}
