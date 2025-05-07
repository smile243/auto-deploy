import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {
    private static final String DB_URL = "jdbc:postgresql://locahost:6543/test";
    private static final String DB_USERNAME = "username";
    private static final String DB_PASSWORD = "password";

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        FastAutoGenerator.create(DB_URL, DB_USERNAME, DB_PASSWORD)
                // 全局配置
                .globalConfig((scanner, builder) -> builder
                        .author("yjl")
                        .outputDir(projectPath+"/src/main/java")
                        .disableOpenDir()
                        .dateType(DateType.ONLY_DATE))
                // 包配置
                .packageConfig((scanner, builder) -> builder
                        .parent("org.yjl"))
                // 策略配置
                .strategyConfig((scanner, builder) -> builder.addInclude(getTables("test"))
                        .entityBuilder()
                        .enableLombok()
                        .addTableFills(
                                new Column("create_time", FieldFill.INSERT)
                        )
                        .formatFileName("%sPo")
                        .serviceBuilder()
                        .formatServiceFileName("%sManager").formatServiceImplFileName("%sManagerImpl")
                        .mapperBuilder()
                        .formatMapperFileName("%sMapper")
                        .build())
                // 使用Freemarker引擎模板，默认的是Velocity引擎模板
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
    }

    // 处理 all 情况
    protected static List<String> getTables(String tables) {
        return "all".equals(tables) ? Collections.emptyList() : Arrays.asList(tables.split(","));
    }
}
