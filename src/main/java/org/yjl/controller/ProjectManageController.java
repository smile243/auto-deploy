package org.yjl.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yjl.service.ProjectManageService;


@RestController
@RequestMapping("/project")
@Slf4j
@RequiredArgsConstructor
public class ProjectManageController {

    private final ProjectManageService projectManageService;

    @PostMapping("/createRancher")
    public void createRancher(String id){
        projectManageService.createRancher(id);
    }

    @GetMapping("/createJenkins")
    public void createJenkins(){
        projectManageService.createJenkins("1");
    }
}
