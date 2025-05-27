package org.yjl.config;


public class JenkinsConfig {

    public static String xml ="<flow-definition plugin=\"workflow-job@1239.v71b_b_a_124a_725\">\n" +
            "  <actions>\n" +
            "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction plugin=\"pipeline-model-definition@2.2114.v2654ca_721309\"/>\n" +
            "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction plugin=\"pipeline-model-definition@2.2114.v2654ca_721309\">\n" +
            "      <jobProperties/>\n" +
            "      <triggers/>\n" +
            "      <parameters/>\n" +
            "      <options/>\n" +
            "    </org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction>\n" +
            "  </actions>\n" +
            "  <description></description>\n" +
            "  <keepDependencies>false</keepDependencies>\n" +
            "  <properties>\n" +
            "    <com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty plugin=\"gitlab-plugin@1.5.35\">\n" +
            "      <gitLabConnection></gitLabConnection>\n" +
            "      <jobCredentialId></jobCredentialId>\n" +
            "      <useAlternativeCredential>false</useAlternativeCredential>\n" +
            "    </com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty>\n" +
            "    <hudson.model.ParametersDefinitionProperty>\n" +
            "      <parameterDefinitions>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>container_port</name>\n" +
            "          <description>容器端口</description>\n" +
            "          <defaultValue>$container_port</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>active_env</name>\n" +
            "          <description>应用环境，如 prod ,dev ,uat 。 --spring.profiles.active=prod </description>\n" +
            "          <defaultValue>dev</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>branch</name>\n" +
            "          <description>分支名称</description>\n" +
            "          <description>通常master则运行到正式rancherweb环境，其他运行到测试rancherweb环境 </description>\n" +
            "          <defaultValue>dev</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "      </parameterDefinitions>\n" +
            "    </hudson.model.ParametersDefinitionProperty>\n" +
            "  </properties>\n" +
            "  <definition class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\" plugin=\"workflow-cps@2802.v5ea_628154b_c2\">\n" +
            "    <script>node {\n" +
            "    //定义镜像名称\n" +
            "    //拉取代码\n" +
            "    stage(&apos;拉取代码&apos;) {\n" +
            "        checkout([$class: &apos;GitSCM&apos;, branches: [[name: &apos;${branch}&apos;]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: &quot;${git_auth_prod}&quot;, url: &quot;${git_url_prod}&quot;]]])\n" +
            "    }\n" +
            "    echo &quot;进行${branch}环境打包&quot;\n" +
            "    //进行打包\n" +
            "    stage(&apos;进行生产环境项目打包&apos;) {\n" +
            "        ${xml_path_build}\n" +
            "        sh &quot;cd ${module_path}&amp;&amp;docker build -t ${module_path} --build-arg JAR_FILE=${module_path} --no-cache .&quot;\n" +
            "    }\n" +
            "    //进行docker标记\n" +
            "    stage(&apos;进行生产环境docker标记&apos;) {\n" +
            "        echo &quot;进行docker tag 生产 标记&quot;\n" +
            "        sh &quot;docker tag ${imageNameAndLatestTag} ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    }\n" +
            "    //登录Harbor镜像仓库，进行上传\n" +
            "    stage(&apos;登录Harbor生产环境镜像仓库，进行上传&apos;) {\n" +
            "        sh &quot;docker login -u 'jenkins' -p 'Qwer1234' ${harbor_url_prod}&quot;\n" +
            "        sh &quot;curl -u &apos;jenkins:Qwer1234&apos; -X POST -H &apos;Content-Type: application/json&apos; &apos;http://192.168.1.82/api/projects&apos; -d &apos;{\\&quot;project_name\\&quot;: \\&quot;${project_name}\\&quot;, \\&quot;metadata\\&quot;: {\\&quot;public\\&quot;: \\&quot;true\\&quot;}, \\&quot;storage_limit\\&quot;: -1}&apos;&quot;\n" +
            "        sh &quot;docker push ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    }\n" +
            "    //删除本地镜像\n" +
            "    sh &quot;docker rmi -f ${imageNameAndLatestTag}&quot;\n" +
            "    sh &quot;docker rmi -f ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    stage(&apos;开始部署&apos;) {\n" +
            "        echo &quot;开始进行docker生产环境拉取以及部署&quot;\n" +
            "    // 使用curl发送请求，并捕获HTTP状态码和响应内容\n"+
            "        def response = sh(script: \"\"\"\n"+
            "        curl -s -o /tmp/response.txt -w \"%{http_code}\" "+
            "     ${api_server_url}/createRancherV1/${id}%23${branch}%23${active_env} \"\"\", returnStdout: true).trim()\n"+
            "    if (response != \"200\") {\n" +
            "        // 读取并打印出响应内容作为错误信息\n" +
            "        def errorMsg = readFile('/tmp/response.txt')\n" +
            "        error \"调用http接口部署失败，接口返回: ${errorMsg}\"\n" +
            "      } else {\n" +
            "        // 清理临时文件\n" +
            "        sh 'rm -f /tmp/response.txt'\n" +
            "       }"+
            "    }\n" +
            "${code_analysis}"
            +
            "\n" +
            "}\n" +
            "\n" +
            "</script>\n" +
            "    <sandbox>true</sandbox>\n" +
            "  </definition>\n" +
            "  <triggers/>\n" +
            "  <disabled>false</disabled>\n" +
            "</flow-definition>";

    public static String noPathXml ="<flow-definition plugin=\"workflow-job@1239.v71b_b_a_124a_725\">\n" +
            "  <actions>\n" +
            "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction plugin=\"pipeline-model-definition@2.2114.v2654ca_721309\"/>\n" +
            "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction plugin=\"pipeline-model-definition@2.2114.v2654ca_721309\">\n" +
            "      <jobProperties/>\n" +
            "      <triggers/>\n" +
            "      <parameters/>\n" +
            "      <options/>\n" +
            "    </org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction>\n" +
            "  </actions>\n" +
            "  <description></description>\n" +
            "  <keepDependencies>false</keepDependencies>\n" +
            "  <properties>\n" +
            "    <com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty plugin=\"gitlab-plugin@1.5.35\">\n" +
            "      <gitLabConnection></gitLabConnection>\n" +
            "      <jobCredentialId></jobCredentialId>\n" +
            "      <useAlternativeCredential>false</useAlternativeCredential>\n" +
            "    </com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty>\n" +
            "    <hudson.model.ParametersDefinitionProperty>\n" +
            "      <parameterDefinitions>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>container_port</name>\n" +
            "          <description>容器端口</description>\n" +
            "          <defaultValue>$container_port</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>active_env</name>\n" +
            "          <description>应用环境，如 prod ,dev  。 --spring.profiles.active=prod </description>\n" +
            "          <defaultValue>prod</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>branch</name>\n" +
            "          <description>分支名称</description>\n" +
            "          <description>通常master则运行到正式rancherweb环境，其他运行到测试rancherweb环境 </description>\n" +
            "          <defaultValue>master</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "      </parameterDefinitions>\n" +
            "    </hudson.model.ParametersDefinitionProperty>\n" +
            "  </properties>\n" +
            "  <definition class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\" plugin=\"workflow-cps@2802.v5ea_628154b_c2\">\n" +
            "    <script>node {\n" +
            "    //定义镜像名称\n" +
            "    //拉取代码\n" +
            "    stage(&apos;拉取代码&apos;) {\n" +
            "        checkout([$class: &apos;GitSCM&apos;, branches: [[name: &apos;${branch}&apos;]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: &quot;${git_auth_prod}&quot;, url: &quot;${git_url_prod}&quot;]]])\n" +
            "    }\n" +
            "    echo &quot;进行${branch}环境打包&quot;\n" +
            "    //进行打包\n" +
            "    stage(&apos;进行生产环境项目打包&apos;) {\n" +
            "        ${xml_no_path_build}\n" +
            "    }\n" +
            "    //进行docker标记\n" +
            "    stage(&apos;进行生产环境docker标记&apos;) {\n" +
            "        echo &quot;进行docker tag 生产 标记&quot;\n" +
            "        sh &quot;docker tag ${imageNameAndLatestTag} ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    }\n" +
            "    //登录Harbor镜像仓库，进行上传\n" +
            "    stage(&apos;登录Harbor生产环境镜像仓库，进行上传&apos;) {\n" +
            "        sh &quot;docker login -u 'jenkins' -p 'Qwer1234' ${harbor_url_prod}&quot;\n" +
            "        sh &quot;curl -u &apos;jenkins:Qwer1234&apos; -X POST -H &apos;Content-Type: application/json&apos; &apos;http://192.168.1.82/api/projects&apos; -d &apos;{\\&quot;project_name\\&quot;: \\&quot;${project_name}\\&quot;, \\&quot;metadata\\&quot;: {\\&quot;public\\&quot;: \\&quot;true\\&quot;}, \\&quot;storage_limit\\&quot;: -1}&apos;&quot;\n" +
            "        sh &quot;docker push ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    }\n" +
            "    //删除本地镜像\n" +
            "    sh &quot;docker rmi -f ${imageNameAndLatestTag}&quot;\n" +
            "    sh &quot;docker rmi -f ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    stage(&apos;开始部署&apos;) {\n" +
            "        echo &quot;开始进行docker生产环境拉取以及部署&quot;\n" +
            "    // 使用curl发送请求，并捕获HTTP状态码和响应内容\n"+
            "    def response = sh(script: \"\"\"\n"+
            "        curl -s -o /tmp/response.txt -w \"%{http_code}\" "+
            "     ${api_server_url}/createRancherV1/${id}%23${branch}%23${active_env} \"\"\", returnStdout: true).trim()\n"+
            "    if (response != \"200\") {\n" +
            "        // 读取并打印出响应内容作为错误信息\n" +
            "        def errorMsg = readFile('/tmp/response.txt')\n" +
            "        error \"调用http接口部署失败，接口返回: ${errorMsg}\"\n" +
            "      } else {\n" +
            "        // 清理临时文件\n" +
            "        sh 'rm -f /tmp/response.txt'\n" +
            "       }"+
            "    }\n" +
            "    //进行代码质量分析\n" +
            "    stage('进行代码质量分析'){\n" +
            "        sh \"mvn org.sonarsource.scanner.maven:sonar-maven-plugin:3.3.0.603:sonar  -Dsonar.projectKey=${project_name} -Dsonar.host.url=http://192.168.0.114:19000  -Dsonar.login=ba27ec8551c34d81f971ea088e49b94ebd9eb097\"\n" +
            "    }"+
            "\n" +
            "}\n" +
            "\n" +
            "</script>\n" +
            "    <sandbox>true</sandbox>\n" +
            "  </definition>\n" +
            "  <triggers/>\n" +
            "  <disabled>false</disabled>\n" +
            "</flow-definition>";

    public static String webXml ="<flow-definition plugin=\"workflow-job@1239.v71b_b_a_124a_725\">\n" +
            "  <actions>\n" +
            "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobAction plugin=\"pipeline-model-definition@2.2114.v2654ca_721309\"/>\n" +
            "    <org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction plugin=\"pipeline-model-definition@2.2114.v2654ca_721309\">\n" +
            "      <jobProperties/>\n" +
            "      <triggers/>\n" +
            "      <parameters/>\n" +
            "      <options/>\n" +
            "    </org.jenkinsci.plugins.pipeline.modeldefinition.actions.DeclarativeJobPropertyTrackerAction>\n" +
            "  </actions>\n" +
            "  <description></description>\n" +
            "  <keepDependencies>false</keepDependencies>\n" +
            "  <properties>\n" +
            "    <com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty plugin=\"gitlab-plugin@1.5.35\">\n" +
            "      <gitLabConnection></gitLabConnection>\n" +
            "      <jobCredentialId></jobCredentialId>\n" +
            "      <useAlternativeCredential>false</useAlternativeCredential>\n" +
            "    </com.dabsquared.gitlabjenkins.connection.GitLabConnectionProperty>\n" +
            "    <hudson.model.ParametersDefinitionProperty>\n" +
            "      <parameterDefinitions>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>container_port</name>\n" +
            "          <description>容器端口</description>\n" +
            "          <defaultValue>$container_port</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>active_env</name>\n" +
            "          <description>应用环境，如 prod ,dev  。 --spring.profiles.active=prod </description>\n" +
            "          <defaultValue>prod</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "        <hudson.model.StringParameterDefinition>\n" +
            "          <name>branch</name>\n" +
            "          <description>分支名称</description>\n" +
            "          <description>通常master则运行到正式rancherweb环境，其他运行到测试rancherweb环境 </description>\n" +
            "          <defaultValue>master</defaultValue>\n" +
            "          <trim>false</trim>\n" +
            "        </hudson.model.StringParameterDefinition>\n" +
            "      </parameterDefinitions>\n" +
            "    </hudson.model.ParametersDefinitionProperty>\n" +
            "  </properties>\n" +
            "  <definition class=\"org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition\" plugin=\"workflow-cps@2802.v5ea_628154b_c2\">\n" +
            "    <script>node {\n" +
            "    //定义镜像名称\n" +
            "    //拉取代码\n" +
            "    stage(&apos;拉取代码&apos;) {\n" +
            "        checkout([$class: &apos;GitSCM&apos;, branches: [[name: &apos;${branch}&apos;]], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: &quot;${git_auth_prod}&quot;, url: &quot;${git_url_prod}&quot;]]])\n" +
            "    }\n" +
            "    echo &quot;进行${branch}环境打包&quot;\n" +
            "    //进行打包\n" +
            "    stage(&apos;进行生产环境项目打包&apos;) {\n" +
            "        sh &quot;docker build --build-arg BUILD_ENV=${active_env} -t ${project_name} .&quot;\n" +
            "    }\n" +
            "    //进行docker标记\n" +
            "    stage(&apos;进行生产环境docker标记&apos;) {\n" +
            "        echo &quot;进行docker tag 生产 标记&quot;\n" +
            "        sh &quot;docker tag ${imageNameAndLatestTag} ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    }\n" +
            "    //登录Harbor镜像仓库，进行上传\n" +
            "    stage(&apos;登录Harbor生产环境镜像仓库，进行上传&apos;) {\n" +
            "        sh &quot;docker login -u 'jenkins' -p 'Qwer1234' ${harbor_url_prod}&quot;\n" +
            "        sh &quot;curl -u &apos;jenkins:Qwer1234&apos; -X POST -H &apos;Content-Type: application/json&apos; &apos;http://192.168.1.82/api/projects&apos; -d &apos;{\\&quot;project_name\\&quot;: \\&quot;${project_name}\\&quot;, \\&quot;metadata\\&quot;: {\\&quot;public\\&quot;: \\&quot;true\\&quot;}, \\&quot;storage_limit\\&quot;: -1}&apos;&quot;\n" +
            "        sh &quot;docker push ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    }\n" +
            "    //删除本地镜像\n" +
            "    sh &quot;docker rmi -f ${imageNameAndLatestTag}&quot;\n" +
            "    sh &quot;docker rmi -f ${harbor_url_prod}/${project_name}/${imageName}&quot;\n" +
            "    stage(&apos;开始部署&apos;) {\n" +
            "        echo &quot;开始进行docker生产环境拉取以及部署&quot;\n" +
            "        sh &quot;curl ${api_server_url}/createRancherV1/${id}%23${branch}%23${active_env}&quot; \n" +
            "    }\n" +
            "\n" +
            "}\n" +
            "\n" +
            "</script>\n" +
            "    <sandbox>true</sandbox>\n" +
            "  </definition>\n" +
            "  <triggers/>\n" +
            "  <disabled>false</disabled>\n" +
            "</flow-definition>";
}
