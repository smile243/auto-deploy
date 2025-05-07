package org.yjl.controller;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yjl.mapper.ProjectManageMapper;

@RequiredArgsConstructor
@RestController
@RequestMapping("/hello")
@Slf4j
public class HelloController {
    private final ProjectManageMapper projectManageMapper;
    @GetMapping
    public void test(){
        projectManageMapper.selectList(Wrappers.emptyWrapper());
        log.info("hello");
    }
}
