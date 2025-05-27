package org.yjl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.surenpi.jenkins.client.Jenkins;
import com.surenpi.jenkins.client.JenkinsHttpClient;
import com.surenpi.jenkins.client.job.Jobs;
import com.surenpi.jenkins.client.util.UrlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.yjl.config.JenkinsConfig;
import org.yjl.entity.ProjectManage;
import org.yjl.mapper.ProjectManageMapper;
import org.yjl.service.ProjectManageService;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Service
@Slf4j
public class ProjectManageServiceImpl extends ServiceImpl<ProjectManageMapper, ProjectManage> implements ProjectManageService {
    @Value("${project.jenkins.url}")
    private String jenkinsUrl;
    @Value("${project.jenkins.user-name}")
    private String jenkinsUserName;

    @Value("${project.jenkins.pass-word}")
    private String jenkinsPassWord;

    @Override
    public void createRancher(String id) {

    }

    @Override
    public void createJenkins(String id) {
        //ProjectManage projectManage = getById(id);
        URI serverURI = null;
        Jobs jobMgr;
        String config = JenkinsConfig.noPathXml;
        try {
            serverURI = new URI(jenkinsUrl);
            Jenkins jenkins = new Jenkins(serverURI, jenkinsUserName, jenkinsPassWord);
//            Credentials credentialMgr = jenkins.getJobs();
//            Map<String, Credential> credentialMap = credentialMgr.list();
            jobMgr = jenkins.getJobs();
            config = config.replace("${imageName}", "test" + ":${active_env}");
            config = config.replace("${xml_path_build}", "sh &quot;mvn clean package -Dmaven.test.skip=true&quot;");
            config = config.replace("${xml_no_path_build}", "sh &quot;mvn clean package -Dmaven.test.skip=true dockerfile:build &quot;");
            config = config.replace("$container_port", "8201");
            config = config.replace("${server_port}", "8201");
            config = config.replace("${project_name}", "test");
            config = config.replace("${git_url_prod}", "https://github.com/yjl");
            config = config.replace("${id}", "1");
            config = config.replace("${api_server_url}", "https://api.github.com");
            log.info("config:{}", config);
            jobMgr.create("test", config);
        } catch (URISyntaxException | IOException e) {
            //创建异常则进行一次更新
            try {
                //进行更行
                new JenkinsHttpClient(serverURI, jenkinsUserName, jenkinsPassWord).postXml(UrlUtils.toJobBaseUrl(null, "test") + "/config.xml", config);
            } catch (IOException ex) {
                log.info("更新xml数据失败:{}", ex.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        }

    }
}
