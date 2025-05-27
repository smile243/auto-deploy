package org.yjl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.yjl.entity.ProjectManage;

public interface ProjectManageService extends IService<ProjectManage> {
    void createRancher(String id);

    void createJenkins(String id);
}
