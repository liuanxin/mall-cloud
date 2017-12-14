package com.github.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.common.ModuleTest.PACKAGE;

public class ModuleTest {

    /** true 生成断路器 */
    static boolean fallback = true;

    /** 包名 */
    static String PACKAGE = "com.github";
    /** 注册中心的端口 */
    static String REGISTER_CENTER_PORT = "8761";
    // static String PARENT = "/home/tony/project/mall-cloud/";
    private static String PARENT = ModuleTest.class.getClassLoader().getResource("").getFile() + "../../../";
    static String PACKAGE_PATH = PACKAGE.replaceAll("\\.", "/");
    static String AUTHOR = " *\n * @author https://github.com/liuanxin\n";

    static String capitalize(String name) {
        StringBuilder sbd = new StringBuilder();
        for (String str : name.split("[.-]")) {
            sbd.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
        }
        return sbd.toString();
    }
    static void writeFile(File file, String content) {
        try (OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
            write.write(content);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        generate("0-common",  "8090", "公共服务");
        generate("1-user",    "8091", "用户");
        generate("2-product", "8092", "商品");
        generate("3-order",   "8093", "订单");

        soutInfo();
    }

    private static List<List<String>> moduleNameList = new ArrayList<>();
    private static List<List<String>> moduleList = new ArrayList<>();
    private static void generate(String basicModuleName, String port, String comment) throws Exception {
        String moduleName = "module-" + basicModuleName;
        String packageName = basicModuleName;
        if (basicModuleName.contains("-")) {
            packageName = basicModuleName.substring(basicModuleName.indexOf("-") + 1);
        }
        String model = packageName + "-model";
        String client = packageName + "-client";
        String server = packageName + "-server";
        String module = PARENT + moduleName;

        Parent.generateParent(moduleName, model, client, server, module, comment);
        Client.generateClient(moduleName, packageName, client, module, comment);
        Model.generateModel(moduleName, packageName, model, module, comment);
        Server.generateServer(moduleName, packageName, model, server, module, port, comment);

        moduleNameList.add(Arrays.asList(comment, moduleName));
        moduleList.add(Arrays.asList(model, client, server));
    }

    private static void soutInfo() throws Exception {
        System.out.println();
        for (List<String> list : moduleNameList) {
            System.out.println(String.format("<!-- %s模块 -->\n<module>%s</module>", list.get(0), list.get(1)));
        }
        System.out.println();
        for (List<String> list : moduleList) {
            System.out.println(String.format("\n<dependency>\n" +
                    "    <groupId>${project.groupId}</groupId>\n" +
                    "    <artifactId>%s</artifactId>\n" +
                    "    <version>${project.version}</version>\n" +
                    "</dependency>\n" +
                    "<dependency>\n" +
                    "    <groupId>${project.groupId}</groupId>\n" +
                    "    <artifactId>%s</artifactId>\n" +
                    "    <version>${project.version}</version>\n" +
                    "</dependency>\n"+
                    "<dependency>\n" +
                    "    <groupId>${project.groupId}</groupId>\n" +
                    "    <artifactId>%s</artifactId>\n" +
                    "    <version>${project.version}</version>\n" +
                    "</dependency>", list.get(0), list.get(1), list.get(2)));
        }
        System.out.println("\n");
        for (List<String> list : moduleList) {
            System.out.println(String.format("<dependency>\n" +
                    "    <groupId>${project.groupId}</groupId>\n" +
                    "    <artifactId>%s</artifactId>\n" +
                    "</dependency>", list.get(1)));
        }
        System.out.println();

        StringBuilder sbd = new StringBuilder();
        for (List<String> list : moduleList) {
            String module = list.get(0);
            String className = capitalize(module.substring(0, module.indexOf("-model")));
            sbd.append(String.format("%sConst.MODULE_NAME, %sConst.class,\n", className, className));
        }
        sbd.delete(sbd.length() - 2, sbd.length());
        System.out.println(sbd.toString());
        System.out.println();
        Thread.sleep(20);
    }
}


class Parent {
    static void generateParent(String moduleName, String model, String client, String server, String module, String comment) {
        new File(module).mkdirs();
        String PARENT_POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
                "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
                "    <parent>\n" +
                "        <artifactId>mall-cloud</artifactId>\n" +
                "        <groupId>" + PACKAGE + "</groupId>\n" +
                "        <version>1.0-SNAPSHOT</version>\n" +
                "    </parent>\n" +
                "    <modelVersion>4.0.0</modelVersion>\n" +
                "\n" +
                "    <artifactId>%s</artifactId>\n" +
                "    <description>%s模块</description>\n" +
                "    <packaging>pom</packaging>\n" +
                "\n" +
                "    <modules>\n" +
                "        <module>%s</module>\n" +
                "        <module>%s</module>\n" +
                "        <module>%s</module>\n" +
                "    </modules>\n" +
                "</project>\n";
        String pom = String.format(PARENT_POM, moduleName, comment, model, client, server);
        ModuleTest.writeFile(new File(module, "pom.xml"), pom);
    }
}


class Client {
    private static String CLIENT = "package " + PACKAGE + ".%s.client;\n"+
            "\n"+
            "import " + PACKAGE + ".%s.service.%sInterface;\n" +
            "import " + PACKAGE + ".%s.config.%sConst;\n" +
            (ModuleTest.fallback ? "import " + PACKAGE + ".%s.hystrix.%sFallback;\n" : "") +
            "import org.springframework.cloud.netflix.feign.FeignClient;\n" +
            "\n" +
            "/**\n" +
            " * %s相关的调用接口\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "@FeignClient(value = %sConst.MODULE_NAME" + (ModuleTest.fallback ? ", fallback = %sFallback.class" : "") + ")\n" +
            "public interface %sClient extends %sInterface {\n" +
            "}\n";

    private static String FALLBACK = "package " + PACKAGE + ".%s.hystrix;\n" +
            "\n" +
            "import " + PACKAGE + ".common.page.PageInfo;\n" +
            "import " + PACKAGE + ".common.page.Pages;\n" +
            "import " + PACKAGE + ".common.util.LogUtil;\n" +
            "import " + PACKAGE + ".%s.client.%sClient;\n" +
            "import org.springframework.stereotype.Component;\n" +
            "\n" +
            "/**\n" +
            " * %s相关的断路器\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "@Component\n" +
            "public class %sFallback implements %sClient {\n" +
            "\n" +
            "    @Override\n" +
            "    public PageInfo demo(String xx, Integer page, Integer limit) {\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled()) {\n" +
            "            LogUtil.ROOT_LOG.debug(\"调用断路器\");\n" +
            "        }\n" +
            "        return Pages.returnList(null);\n" +
            "    }\n" +
            "}\n";

    private static String POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <parent>\n" +
            "        <artifactId>%s</artifactId>\n" +
            "        <groupId>" + PACKAGE + "</groupId>\n" +
            "        <version>1.0-SNAPSHOT</version>\n" +
            "    </parent>\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <artifactId>%s-client</artifactId>\n" +
            "    <description>%s模块的客户端包(单独分包的原因是: 只在调用的地方才需要引入 feign 包和开启 @EnableFeignClients 注解)</description>\n" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>mall-common</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>%s-model</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.cloud</groupId>\n" +
            "            <artifactId>spring-cloud-starter-feign</artifactId>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "</project>\n";

    static void generateClient(String moduleName, String packageName, String client,
                              String module, String comment) throws IOException {
        String parentPackageName = packageName.replace("-", ".");
        String clazzName = ModuleTest.capitalize(parentPackageName);

        File modelPath = new File(module + "/" + client + "/src/main/java");
        modelPath.mkdirs();
        String modelPom = String.format(POM, moduleName, packageName, comment, packageName);
        ModuleTest.writeFile(new File(module + "/" + client, "pom.xml"), modelPom);

        File modelSourcePath = new File(modelPath, ModuleTest.PACKAGE_PATH + "/" + parentPackageName.replaceAll("\\.", "/"));
        File model_client = new File(modelSourcePath, "client");
        model_client.mkdirs();

        String constModel;
        if (ModuleTest.fallback) {
            constModel = String.format(CLIENT, parentPackageName,
                    parentPackageName, clazzName, parentPackageName, clazzName,
                    parentPackageName, clazzName, comment, clazzName, clazzName,
                    clazzName, clazzName);

            File modelHystrix = new File(modelSourcePath, "hystrix");
            modelHystrix.mkdirs();
            String interfaceModel = String.format(FALLBACK, parentPackageName,
                    parentPackageName, clazzName, comment, clazzName, clazzName);
            ModuleTest.writeFile(new File(modelHystrix, clazzName + "Fallback.java"), interfaceModel);
        } else {
            constModel = String.format(CLIENT, parentPackageName,
                    parentPackageName, clazzName, parentPackageName, clazzName,
                    comment, clazzName, clazzName, clazzName, clazzName);
        }
        ModuleTest.writeFile(new File(model_client, clazzName + "Client.java"), constModel);
    }
}


class Model {
    private static String CONST = "package " + PACKAGE + ".%s.config;\n"+
            "\n"+
            "/**\n" +
            " * %s模块相关的常数设置类\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "public final class %sConst {\n"+
            "\n"+
            "    /** 当前模块名. 要与 application.yml 中的一致 */\n"+
            "    public static final String MODULE_NAME = \"%s\";\n"+
            "\n" +
            "    /** 当前模块说明. 当用在文档中时有用 */\n" +
            "    public static final String MODULE_INFO = MODULE_NAME + \"-%s\";\n" +
            "    /** 当前模块版本. 当用在文档中时有用 */\n" +
            "    public static final String MODULE_VERSION = \"1.0\";\n" +
            "\n\n" +
            "    // ========== url 说明 ==========\n\n" +
            "    /** 测试地址 */\n" +
            "    public static final String %s_DEMO = MODULE_NAME + \"/demo\";\n" +
            "}\n";

    private static String INTERFACE = "package " + PACKAGE + ".%s.service;\n" +
            "\n" +
            "import " + PACKAGE + ".common.page.PageInfo;\n" +
            "import " + PACKAGE + ".%s.config.%sConst;\n" +
            "import org.springframework.web.bind.annotation.GetMapping;\n" +
            "import org.springframework.web.bind.annotation.RequestParam;\n" +
            "\n" +
            "/**\n" +
            " * %s相关的接口\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "public interface %sInterface {\n" +
            "    \n" +
            "    /**\n" +
            "     * 示例接口\n" +
            "     * \n" +
            "     * @param xx 参数\n" +
            "     * @param page 当前页\n" +
            "     * @param limit 每页行数\n" +
            "     * @return 分页信息\n" +
            "     */\n" +
            "    @GetMapping(%sConst.%s_DEMO)\n" +
            "    PageInfo demo(@RequestParam(value = \"xx\", required = false) String xx,\n" +
            "                  @RequestParam(value = \"page\", required = false) Integer page,\n" +
            "                  @RequestParam(value = \"limit\", required = false) Integer limit);\n" +
            "}\n";

    private static String POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <parent>\n" +
            "        <artifactId>%s</artifactId>\n" +
            "        <groupId>" + PACKAGE + "</groupId>\n" +
            "        <version>1.0-SNAPSHOT</version>\n" +
            "    </parent>\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <artifactId>%s</artifactId>\n" +
            "    <description>%s模块相关的实体</description>" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>mall-common</artifactId>\n" +
            "            <scope>provided</scope>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.boot</groupId>\n" +
            "            <artifactId>spring-boot-starter-web</artifactId>\n" +
            "            <scope>provided</scope>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "</project>\n";

    static void generateModel(String moduleName, String packageName, String model,
                              String module, String comment) throws IOException {
        packageName = packageName.replace("-", ".");
        String clazzName = ModuleTest.capitalize(packageName);

        File modelPath = new File(module + "/" + model + "/src/main/java");
        modelPath.mkdirs();
        String modelPom = String.format(POM, moduleName, model, comment);
        ModuleTest.writeFile(new File(module + "/" + model, "pom.xml"), modelPom);

        File modelSourcePath = new File(modelPath, ModuleTest.PACKAGE_PATH + "/" + packageName.replaceAll("\\.", "/"));
        File model_config = new File(modelSourcePath, "config");
        File model_interface = new File(modelSourcePath, "service");
        model_config.mkdirs();
        model_interface.mkdirs();
        new File(modelSourcePath, "enums").mkdirs();
        new File(modelSourcePath, "model").mkdirs();
        String constModel = String.format(CONST, packageName, comment, clazzName,
                packageName, comment, clazzName.toUpperCase());
        ModuleTest.writeFile(new File(model_config, clazzName + "Const.java"), constModel);

        String interfaceModel = String.format(INTERFACE, packageName, packageName, clazzName,
                comment, clazzName, clazzName, clazzName.toUpperCase());
        ModuleTest.writeFile(new File(model_interface, clazzName + "Interface.java"), interfaceModel);
    }
}


class Server {
    private static String APPLICATION = "package " + PACKAGE + ";\n" +
            "\n" +
            "import " + PACKAGE + ".common.util.A;\n" +
            "import " + PACKAGE + ".common.util.LogUtil;\n" +
            "import org.springframework.boot.SpringApplication;\n" +
            "import org.springframework.boot.autoconfigure.SpringBootApplication;\n" +
            "import org.springframework.boot.builder.SpringApplicationBuilder;\n" +
            "import org.springframework.boot.web.support.SpringBootServletInitializer;\n" +
            "import org.springframework.cloud.client.discovery.EnableDiscoveryClient;\n" +
            "import org.springframework.context.ApplicationContext;\n" +
            "\n" +
            "@SpringBootApplication\n" +
            "@EnableDiscoveryClient\n" +
            "public class %sApplication extends SpringBootServletInitializer {\n" +
            "\n" +
            "    @Override\n" +
            "    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {\n" +
            "        return application.sources(%sApplication.class);\n" +
            "    }\n" +
            "\n" +
            "    public static void main(String[] args) {\n" +
            "        ApplicationContext ctx = SpringApplication.run(%sApplication.class, args);\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled()) {\n" +
            "            String[] activeProfiles = ctx.getEnvironment().getActiveProfiles();\n" +
            "            if (A.isNotEmpty(activeProfiles)) {\n" +
            "                LogUtil.ROOT_LOG.debug(\"current profile : ({})\", A.toStr(activeProfiles));\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}\n";

    private static String MODULE_CONFIG = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import " + PACKAGE + ".global.model.Develop;\n" +
            "import com.github.liuanxin.api.annotation.EnableApiInfo;\n" +
            "import com.github.liuanxin.api.model.DocumentCopyright;\n" +
            "import org.springframework.beans.factory.annotation.Value;\n" +
            "import org.springframework.context.annotation.Bean;\n" +
            "import org.springframework.context.annotation.Configuration;\n" +
            "\n" +
            "/**\n" +
            " * %s模块里需要放入 spring 上下文中的 bean\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "@Configuration\n" +
            "@EnableApiInfo\n" +
            "public class %sConfig {\n" +
            "\n" +
            "    @Value(\"${online:false}\")\n" +
            "    private boolean online;\n" +
            "\n" +
            "    @Bean\n" +
            "    public DocumentCopyright urlCopyright() {\n" +
            "        return new DocumentCopyright()\n" +
            "                .setTitle(Develop.TITLE)\n" +
            "                .setContact(Develop.CONTACT)\n" +
            "                .setTeam(Develop.TEAM)\n" +
            "                .setVersion(%sConst.MODULE_VERSION)\n" +
            "                .setOnline(online);\n" +
            "    }\n" +
            "}\n";

    private static String CONFIG_DATA = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import com.google.common.collect.Lists;\n" +
            "import " + PACKAGE + ".common.Const;\n" +
            "import " + PACKAGE + ".common.resource.CollectHandlerUtil;\n" +
            "import " + PACKAGE + ".common.resource.CollectResourceUtil;\n" +
            "import " + PACKAGE + ".common.resource.LoaderHandler;\n" +
            "import " + PACKAGE + ".common.resource.LoaderResource;\n" +
            "import " + PACKAGE + ".global.config.GlobalConst;\n" +
            "import " + PACKAGE + ".%s.config.%sConst;\n" +
            "import org.apache.ibatis.type.TypeHandler;\n" +
            "import org.springframework.core.io.Resource;\n" +
            "\n" +
            "import java.util.List;\n" +
            "\n" +
            "/**\n" +
            " * %s模块的配置数据. 主要是 mybatis 的多配置目录和类型处理器\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "final class %sConfigData {\n" +
            "\n" +
            "    private static final String[] RESOURCE_PATH = new String[] {\n" +
            "            %sConst.MODULE_NAME + \"/*.xml\",\n" +
            "            %sConst.MODULE_NAME + \"-custom/*.xml\"\n" +
            "    };\n" +
            "    private static final List<Resource[]> RESOURCES = Lists.newArrayList();\n" +
            "    static {\n" +
            "        RESOURCES.add(LoaderResource.getResourceArray(%sConfigData.class, RESOURCE_PATH));\n" +
            "    }\n" +
            "\n" +
            "    private static final List<TypeHandler[]> HANDLERS = Lists.newArrayList();\n" +
            "    static {\n" +
            "        HANDLERS.add(LoaderHandler.getHandleArray(GlobalConst.class, Const.handlerPath(GlobalConst.MODULE_NAME)));\n" +
            "        HANDLERS.add(LoaderHandler.getHandleArray(%sConfigData.class, Const.handlerPath(%sConst.MODULE_NAME)));\n" +
            "    }\n" +
            "\n" +
            "    /** 要加载的 mybatis 的配置文件目录 */\n" +
            "    static final Resource[] RESOURCE_ARRAY = CollectResourceUtil.resource(RESOURCES);\n" +
            "    /** 要加载的 mybatis 类型处理器的目录 */\n" +
            "    static final TypeHandler[] HANDLER_ARRAY = CollectHandlerUtil.handler(HANDLERS);\n" +
            "}\n";

    private static String DATA_SOURCE = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import com.github.liuanxin.page.PageInterceptor;\n" +
            "import " + PACKAGE + ".common.Const;\n" +
            "import org.apache.ibatis.plugin.Interceptor;\n" +
            "import org.apache.ibatis.session.SqlSessionFactory;\n" +
            "import org.mybatis.spring.SqlSessionFactoryBean;\n" +
            "import org.mybatis.spring.SqlSessionTemplate;\n" +
            "import org.mybatis.spring.annotation.MapperScan;\n" +
            "import org.springframework.beans.factory.annotation.Autowired;\n" +
            "import org.springframework.context.annotation.Bean;\n" +
            "import org.springframework.context.annotation.Configuration;\n" +
            "\n" +
            "import javax.sql.DataSource;\n" +
            "\n" +
            "/**\n" +
            " * 扫描指定目录. MapperScan 的处理类是 MapperScannerRegistrar, 其基于 ClassPathMapperScanner<br>\n" +
            " *\n" +
            " * @see org.mybatis.spring.annotation.MapperScannerRegistrar#registerBeanDefinitions\n" +
            " * @see org.mybatis.spring.mapper.MapperScannerConfigurer#postProcessBeanDefinitionRegistry\n" +
            " * @see org.mybatis.spring.mapper.ClassPathMapperScanner\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "@Configuration\n" +
            "@MapperScan(basePackages = Const.BASE_PACKAGE)\n" +
            "public class %sDataSourceInit {\n" +
            "\n" +
            "    @Autowired\n" +
            "    private DataSource dataSource;\n" +
            "\n" +
            "    @Bean\n" +
            "    public SqlSessionFactory sqlSessionFactory() throws Exception {\n" +
            "        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();\n" +
            "        sessionFactory.setDataSource(dataSource);\n" +
            "        // 装载 xml 实现\n" +
            "        sessionFactory.setMapperLocations(%sConfigData.RESOURCE_ARRAY);\n" +
            "        // 装载 handler 实现\n" +
            "        sessionFactory.setTypeHandlers(%sConfigData.HANDLER_ARRAY);\n" +
            "        // mybatis 的分页插件\n" +
            "        sessionFactory.setPlugins(new Interceptor[] { new PageInterceptor(\"mysql\") });\n" +
            "        return sessionFactory.getObject();\n" +
            "    }\n" +
            "\n" +
            "    /** 要构建 or 语句, 参考: http://www.mybatis.org/generator/generatedobjects/exampleClassUsage.html */\n" +
            "    @Bean(name = \"sqlSessionTemplate\", destroyMethod = \"clearCache\")\n" +
            "    public SqlSessionTemplate sqlSessionTemplate() throws Exception {\n" +
            "        return new SqlSessionTemplate(sqlSessionFactory());\n" +
            "    }\n" +
            "\n" +
            "    /*\n" +
            "     * 事务控制, 默认已经装载了\n" +
            "     *\n" +
            "     * @see DataSourceTransactionManagerAutoConfiguration\n" +
            "     */\n" +
            "    /*\n" +
            "    @Bean\n" +
            "    public PlatformTransactionManager transactionManager() {\n" +
            "        return new DataSourceTransactionManager(dataSource());\n" +
            "    }\n" +
            "    */\n" +
            "}\n";

    private static String EXCEPTION = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import " + PACKAGE + ".common.exception.NotLoginException;\n" +
            "import " + PACKAGE + ".common.exception.ServiceException;\n" +
            "import " + PACKAGE + ".common.json.JsonResult;\n" +
            "import " + PACKAGE + ".common.util.A;\n" +
            "import " + PACKAGE + ".common.util.LogUtil;\n" +
            "import " + PACKAGE + ".common.util.RequestUtils;\n" +
            "import " + PACKAGE + ".common.util.U;\n" +
            "import org.springframework.beans.factory.annotation.Value;\n" +
            "import org.springframework.web.HttpRequestMethodNotSupportedException;\n" +
            "import org.springframework.web.bind.annotation.ControllerAdvice;\n" +
            "import org.springframework.web.bind.annotation.ExceptionHandler;\n" +
            "import org.springframework.web.multipart.MaxUploadSizeExceededException;\n" +
            "import org.springframework.web.servlet.NoHandlerFoundException;\n" +
            "\n" +
            "import javax.servlet.http.HttpServletResponse;\n" +
            "import java.io.IOException;\n" +
            "\n" +
            "/**\n" +
            " * 处理全局异常的控制类. 如果要自定义错误处理类\n" +
            " *\n" +
            " * @see org.springframework.boot.autoconfigure.web.ErrorController\n" +
            " * @see org.springframework.boot.autoconfigure.web.ErrorProperties\n" +
            " * @see org.springframework.boot.autoconfigure.web.ErrorMvcAutoConfiguration\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "@ControllerAdvice\n" +
            "public class %sGlobalException {\n" +
            "\n" +
            "    @Value(\"${online:false}\")\n" +
            "    private boolean online;\n" +
            "\n" +
            "    @ExceptionHandler(NotLoginException.class)\n" +
            "    public void notFound(NotLoginException e, HttpServletResponse response) throws IOException {\n" +
            "        RequestUtils.toJson(JsonResult.notLogin(), response);\n" +
            "    }\n" +
            "\n" +
            "    @ExceptionHandler(NoHandlerFoundException.class)\n" +
            "    public void forbidden(NoHandlerFoundException e, HttpServletResponse response) throws IOException {\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled())\n" +
            "            LogUtil.ROOT_LOG.debug(e.getMessage(), e);\n" +
            "\n" +
            "        RequestUtils.toJson(JsonResult.fail(\"无对应的请求\"), response);\n" +
            "    }\n" +
            "\n" +
            "    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)\n" +
            "    public void notSupported(HttpRequestMethodNotSupportedException e,\n" +
            "                             HttpServletResponse response) throws IOException {\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled())\n" +
            "            LogUtil.ROOT_LOG.debug(e.getMessage());\n" +
            "\n" +
            "        String msg = U.EMPTY;\n" +
            "        if (!online) {\n" +
            "            msg = \" 当前方式(\" + e.getMethod() + \"), 支持方式(\" + A.toStr(e.getSupportedMethods()) + \")\";\n" +
            "        }\n" +
            "        RequestUtils.toJson(JsonResult.fail(\"不支持此种请求方式!\" + msg), response);\n" +
            "    }\n" +
            "\n" +
            "    /** 业务异常 */\n" +
            "    @ExceptionHandler(ServiceException.class)\n" +
            "    public void serviceException(ServiceException e, HttpServletResponse response) throws IOException {\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled())\n" +
            "            LogUtil.ROOT_LOG.debug(e.getMessage());\n" +
            "        RequestUtils.toJson(JsonResult.fail(e.getMessage()), response);\n" +
            "    }\n" +
            "\n" +
            "    /** 上传文件太大 */\n" +
            "    @ExceptionHandler(MaxUploadSizeExceededException.class)\n" +
            "    public void notFound(MaxUploadSizeExceededException e, HttpServletResponse response) throws IOException {\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled())\n" +
            "            LogUtil.ROOT_LOG.debug(\"文件太大: \" + e.getMessage(), e);\n" +
            "        RequestUtils.toJson(JsonResult.fail(\"上传文件太大! 请保持在 \" + (e.getMaxUploadSize() >> 20) + \"M 以内\"), response);\n" +
            "    }\n" +
            "\n" +
            "    /** 未知的所有其他异常 */\n" +
            "    @ExceptionHandler(Throwable.class)\n" +
            "    public void exception(Throwable e, HttpServletResponse response) throws IOException {\n" +
            "        if (LogUtil.ROOT_LOG.isErrorEnabled())\n" +
            "            LogUtil.ROOT_LOG.error(\"有错误: \" + e.getMessage(), e);\n" +
            "        RequestUtils.toJson(JsonResult.fail(online || U.isBlank(e.getMessage()) ? \"服务异常\" : e.getMessage()), response);\n" +
            "    }\n" +
            "}\n";

    private static String INTERCEPTOR = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import " + PACKAGE + ".common.util.LogUtil;\n" +
            "import " + PACKAGE + ".common.util.RequestUtils;\n" +
            "import org.springframework.beans.factory.annotation.Value;\n" +
            "import org.springframework.web.servlet.HandlerInterceptor;\n" +
            "import org.springframework.web.servlet.ModelAndView;\n" +
            "\n" +
            "import javax.servlet.http.HttpServletRequest;\n" +
            "import javax.servlet.http.HttpServletResponse;\n" +
            "\n" +
            "/**\n" +
            " * %s模块的 web 拦截器\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "public class %sInterceptor implements HandlerInterceptor {\n" +
            "\n" +
            "    @Value(\"${online:false}\")\n" +
            "    private boolean online;\n" +
            "\n" +
            "    @Override\n" +
            "    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,\n" +
            "                             Object handler) throws Exception {\n" +
            "        LogUtil.bind(RequestUtils.logContextInfo(online));\n" +
            "        return true;\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void postHandle(HttpServletRequest request, HttpServletResponse response,\n" +
            "                           Object handler, ModelAndView modelAndView) throws Exception {\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,\n" +
            "                                Object handler, Exception ex) throws Exception {\n" +
            "        if (ex != null) {\n" +
            "            if (LogUtil.ROOT_LOG.isDebugEnabled())\n" +
            "                LogUtil.ROOT_LOG.debug(\"request was over, but have exception: \" + ex.getMessage());\n" +
            "        }\n" +
            "        LogUtil.unbind();\n" +
            "    }\n" +
            "}\n";

    private static String WEB_ADAPTER = "package " + PACKAGE + ".%s.config;\n" +
            "\n" +
            "import " + PACKAGE + ".common.Const;\n" +
            "import " + PACKAGE + ".common.converter.*;\n" +
            "import " + PACKAGE + ".common.mvc.SpringMvc;\n" +
            "import org.springframework.context.annotation.Configuration;\n" +
            "import org.springframework.format.FormatterRegistry;\n" +
            "import org.springframework.http.converter.HttpMessageConverter;\n" +
            "import org.springframework.web.servlet.config.annotation.CorsRegistry;\n" +
            "import org.springframework.web.servlet.config.annotation.InterceptorRegistry;\n" +
            "import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;\n" +
            "\n" +
            "import java.util.List;\n" +
            "\n" +
            "/**\n" +
            " * %s模块的配置数据. 主要是 mybatis 的多配置目录和类型处理器\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "@Configuration\n" +
            "public class %sWebAdapter extends WebMvcConfigurerAdapter {\n" +
            "\n" +
            "    @Override\n" +
            "    public void addFormatters(FormatterRegistry registry) {\n" +
            "        SpringMvc.handlerFormatter(registry);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {\n" +
            "        SpringMvc.handlerConvert(converters);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void addInterceptors(InterceptorRegistry registry) {\n" +
            "        registry.addInterceptor(new %sInterceptor()).addPathPatterns(\"/**\");\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * see : http://www.ruanyifeng.com/blog/2016/04/cors.html\n" +
            "     *\n" +
            "     * {@link org.springframework.web.servlet.config.annotation.CorsRegistration#CorsRegistration(String)}\n" +
            "     */\n" +
            "    @Override\n" +
            "    public void addCorsMappings(CorsRegistry registry) {\n" +
            "        registry.addMapping(\"/**\").allowedMethods(Const.SUPPORT_METHODS);\n" +
            "    }\n" +
            "}\n";

    private static String SERVICE = "package " + PACKAGE + ".%s.service;\n" +
            "\n" +
            "import " + PACKAGE + ".common.page.PageInfo;\n" +
            "import " + PACKAGE + ".common.page.Pages;\n" +
            "import " + PACKAGE + ".common.util.LogUtil;\n" +
            "import " + PACKAGE + ".global.model.Develop;\n" +
            "import com.github.liuanxin.api.annotation.ApiGroup;\n" +
            "import com.github.liuanxin.api.annotation.ApiMethod;\n" +
            "import com.github.liuanxin.api.annotation.ApiParam;\n" +
            "import " + PACKAGE + ".%s.config.%sConst;\n" +
            "import org.springframework.web.bind.annotation.RestController;\n" +
            "\n" +
            "/**\n" +
            " * %s模块的接口实现类\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "@ApiGroup({ %sConst.MODULE_INFO })\n" +
            "@RestController\n" +
            "public class %sService implements %sInterface {\n" +
            "    \n" +
            "    @ApiMethod(title = \"%s测试接口\", develop = Develop.%s)\n" +
            "    @Override\n" +
            "    public PageInfo demo(String xx, \n" +
            "                         @ApiParam(desc = \"当前页数\") Integer page,\n" +
            "                         @ApiParam(desc = \"每页条数\") Integer limit) {\n" +
            "        if (LogUtil.ROOT_LOG.isDebugEnabled()) {\n" +
            "            LogUtil.ROOT_LOG.debug(\"调用实现类\");\n" +
            "        }\n" +
            "        return Pages.returnList(null);\n" +
            "    }\n" +
            "}\n";

    private static String APPLICATION_YML = "\n" +
            "# https://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html\n" +
            "online: false\n" +
            "\n" +
            "server.port: %s\n" +
            "\n" +
            "spring.application.name: %s\n" +
            "\n" +
            "logging.config: classpath:log-dev.xml\n" +
            "\n" +
            "spring.datasource:\n" +
            "  url: jdbc:mysql://127.0.0.1:3306/cloud?useSSL=false&useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&statementInterceptors=" + PACKAGE + ".common.sql.ShowSqlInterceptor\n" +
            "  username: root\n" +
            "  password: root\n" +
            "  hikari:\n" +
            "    minimumIdle: 1\n" +
            "    maximumPoolSize: 1\n" +
            "\n" +
            "register.center: http://127.0.0.1:" + ModuleTest.REGISTER_CENTER_PORT + "/eureka/\n" +
            "eureka:\n" +
            "  client:\n" +
            "    # 开启健康检查(需要 spring-boot-starter-actuator 包)\n" +
            "    healthcheck.enabled: true\n" +
            "    # 客户端间隔多久去拉取服务注册信息, 默认为 30 秒\n" +
            "    registry-fetch-interval-seconds: 20\n" +
            "    serviceUrl.defaultZone: ${register.center}\n" +
            "  instance:\n" +
            "    # 注册到服务器的是 ip 地址, 不要用主机名(只在开发时这样, 测试和线上还是用默认)\n" +
            "    prefer-ip-address: true\n" +
            "    # 客户端发送心跳给注册中心的频率, 默认 30 秒\n" +
            "    lease-renewal-interval-in-seconds: 20\n" +
            "    # 服务端在收到最后一个心跳后的等待时间. 超出将移除该实例, 默认 90 秒, 此值至少要大于 lease-renewal-interval-in-seconds\n" +
            "    lease-expiration-duration-in-seconds: 60\n";

    private static String APPLICATION_TEST_YML = "\n" +
            "online: false\n" +
            "\n" +
            "server.port: %s\n" +
            "\n" +
            "spring.application.name: %s\n" +
            "\n" +
            "logging.config: classpath:log-test.xml\n" +
            "\n" +
            "spring.datasource:\n" +
            "  url: jdbc:mysql://test_%s_db?useSSL=false&useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true&statementInterceptors=" + PACKAGE + ".common.sql.ShowSqlInterceptor\n" +
            "  username: test_%s_user\n" +
            "  password: test_%s_pass\n" +
            "  hikari:\n" +
            "    minimumIdle: 2\n" +
            "    maximumPoolSize: 5\n" +
            "    dataSourceProperties:\n" +
            "      prepStmtCacheSize: 250\n" +
            "      prepStmtCacheSqlLimit: 2048\n" +
            "      cachePrepStmts: true\n" +
            "      useServerPrepStmts: true\n" +
            "\n" +
            "register.center: http://test1:" + ModuleTest.REGISTER_CENTER_PORT + "/eureka/,http://test2:" +
            ModuleTest.REGISTER_CENTER_PORT + "/eureka/,http://test3:" + ModuleTest.REGISTER_CENTER_PORT + "/eureka/\n" +
            "eureka:\n" +
            "  client:\n" +
            "    healthcheck.enabled: true\n" +
            "    registry-fetch-interval-seconds: 10\n" +
            "    serviceUrl.defaultZone: ${register.center}\n" +
            "  instance:\n" +
            "    lease-renewal-interval-in-seconds: 10\n" +
            "    lease-expiration-duration-in-seconds: 30\n";

    private static String APPLICATION_PROD_YML = "\n" +
            "online: true\n" +
            "\n" +
            "server.port: %s\n" +
            "\n" +
            "spring.application.name: %s\n" +
            "\n" +
            "logging.config: classpath:log-prod.xml\n" +
            "\n" +
            "spring.datasource:\n" +
            "  url: jdbc:mysql://prod_%s_db?useSSL=false&useUnicode=true&characterEncoding=utf8&autoReconnect=true&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true\n" +
            "  username: prod_%s_user\n" +
            "  password: prod_%s_pass\n" +
            "  hikari:\n" +
            "    minimumIdle: 10\n" +
            "    maximumPoolSize: 30\n" +
            "    dataSourceProperties:\n" +
            "      prepStmtCacheSize: 250\n" +
            "      prepStmtCacheSqlLimit: 2048\n" +
            "      cachePrepStmts: true\n" +
            "      useServerPrepStmts: true\n" +
            "\n" +
            "register.center: http://prod1:" + ModuleTest.REGISTER_CENTER_PORT + "/eureka/,http://prod2:" +
            ModuleTest.REGISTER_CENTER_PORT + "/eureka/,http://prod3:" + ModuleTest.REGISTER_CENTER_PORT + "/eureka/\n" +
            "eureka:\n" +
            "  client:\n" +
            "    healthcheck.enabled: true\n" +
            "    registry-fetch-interval-seconds: 5\n" +
            "    serviceUrl.defaultZone: ${register.center}\n" +
            "  instance:\n" +
            "    lease-renewal-interval-in-seconds: 5\n" +
            "    lease-expiration-duration-in-seconds: 15\n";

    private static final String CONFIG = "\n"+
            "# 当前文件是主要为了抑制 <No URLs will be polled as dynamic configuration sources> 这个警告. 无其他用处\n"+
            "# see com.netflix.config.sources.URLConfigurationSource.URLConfigurationSource()\n";

    private static String LOG_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <include resource=\"org/springframework/boot/logging/logback/defaults.xml\" />\n" +
            "    <property name=\"CONSOLE_LOG_PATTERN\" value=\"[%X{receiveTime}%d] [${PID:- } %t\\\\(%logger\\\\) : %p]%n%X{requestInfo}%class.%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "    <include resource=\"org/springframework/boot/logging/logback/console-appender.xml\" />\n" +
            "\n" +
            "    <logger name=\"" + PACKAGE + ".~MODULE_NAME~.repository\" level=\"warn\"/>\n" +
            "    <logger name=\"org.springframework\" level=\"warn\"/>\n" +
            "    <logger name=\"org.hibernate\" level=\"warn\"/>\n" +
            "    <logger name=\"com.netflix\" level=\"warn\"/>\n" +
            "    <logger name=\"org.mybatis\" level=\"warn\"/>\n" +
            "    <logger name=\"com.github\" level=\"warn\"/>\n" +
            "    <logger name=\"com.zaxxer\" level=\"warn\"/>\n" +
            "    <logger name=\"org.apache\" level=\"warn\"/>\n" +
            "    <logger name=\"org.jboss\" level=\"warn\"/>\n" +
            "\n" +
            "    <root level=\"debug\">\n" +
            "        <appender-ref ref=\"CONSOLE\"/>\n" +
            "    </root>\n" +
            "</configuration>\n";

    private static String LOG_TEST_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <property name=\"FILE_PATH\" value=\"${user.home}/logs/~MODULE_NAME~-test\"/>\n" +
            "    <property name=\"SQL_PATTERN\" value=\"%d [${PID:- } %t\\\\(%logger\\\\) : %p]%n%class.%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "    <property name=\"LOG_PATTERN\" value=\"[%X{receiveTime}%d] [${PID:- } %t\\\\(%logger\\\\) : %p] %X{requestInfo} %class{30}#%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "\n" +
            "    <appender name=\"PROJECT\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
            "        <file>${FILE_PATH}.log</file>\n" +
            "        <!-- yyyy-MM-dd_HH 每小时建一个, yyyy-MM-dd_HH-mm 每分钟建一个 -->\n" +
            "        <rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
            "            <fileNamePattern>${FILE_PATH}-%d{yyyy-MM-dd}.log</fileNamePattern>\n" +
            "            <maxHistory>7</maxHistory>\n" +
            "        </rollingPolicy>\n" +
            "        <!-- 开启了下面的配置将会在文件达到 10MB 的时候才新建文件, 将会按上面的规则一天建一个  -->\n" +
            "        <!--<triggeringPolicy class=\"ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy\">\n" +
            "            <MaxFileSize>10MB</MaxFileSize>\n" +
            "        </triggeringPolicy>-->\n" +
            "        <encoder>\n" +
            "            <pattern>${LOG_PATTERN}</pattern>\n" +
            "        </encoder>\n" +
            "    </appender>\n" +
            "\n" +
            "    <appender name=\"SQL\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
            "        <file>${FILE_PATH}-sql.log</file>\n" +
            "        <rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
            "            <fileNamePattern>${FILE_PATH}-sql-%d{yyyy-MM-dd}.log</fileNamePattern>\n" +
            "            <maxHistory>7</maxHistory>\n" +
            "        </rollingPolicy>\n" +
            "        <encoder>\n" +
            "            <pattern>${SQL_PATTERN}</pattern>\n" +
            "        </encoder>\n" +
            "    </appender>\n" +
            "    <logger name=\"sqlLog\" level=\"debug\" additivity=\"false\">\n" +
            "        <appender-ref ref=\"SQL\" />\n" +
            "    </logger>\n" +
            "\n" +
            "    <logger name=\"" + PACKAGE + ".~MODULE_NAME~.repository\" level=\"warn\"/>\n" +
            "    <logger name=\"org.springframework\" level=\"warn\"/>\n" +
            "    <logger name=\"org.hibernate\" level=\"warn\"/>\n" +
            "    <logger name=\"com.netflix\" level=\"warn\"/>\n" +
            "    <logger name=\"org.mybatis\" level=\"warn\"/>\n" +
            "    <logger name=\"com.github\" level=\"warn\"/>\n" +
            "    <logger name=\"com.zaxxer\" level=\"warn\"/>\n" +
            "    <logger name=\"org.apache\" level=\"warn\"/>\n" +
            "    <logger name=\"org.jboss\" level=\"warn\"/>\n" +
            "\n" +
            "    <root level=\"debug\">\n" +
            "        <appender-ref ref=\"PROJECT\"/>\n" +
            "    </root>\n" +
            "</configuration>\n";

    private static String LOG_PROD_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<configuration>\n" +
            "    <property name=\"FILE_PATH\" value=\"${user.home}/logs/~MODULE_NAME~-prod\"/>\n" +
            "    <property name=\"LOG_PATTERN\" value=\"[%X{receiveTime}%d] [${PID:- } %t\\\\(%logger\\\\) : %p] %X{requestInfo} %class{30}#%method\\\\(%file:%line\\\\)%n%m%n%n\"/>\n" +
            "\n" +
            "    <appender name=\"PROJECT\" class=\"ch.qos.logback.core.rolling.RollingFileAppender\">\n" +
            "        <file>${FILE_PATH}.log</file>\n" +
            "        <rollingPolicy class=\"ch.qos.logback.core.rolling.TimeBasedRollingPolicy\">\n" +
            "            <fileNamePattern>${FILE_PATH}-%d{yyyy-MM-dd}.log</fileNamePattern>\n" +
            "            <maxHistory>60</maxHistory>\n" +
            "        </rollingPolicy>\n" +
            "        <encoder>\n" +
            "            <pattern>${LOG_PATTERN}</pattern>\n" +
            "        </encoder>\n" +
            "    </appender>\n" +
            "\n" +
            "    <appender name=\"ASYNC\" class=\"ch.qos.logback.classic.AsyncAppender\">\n" +
            "        <discardingThreshold>0</discardingThreshold>\n" +
            "        <includeCallerData>true</includeCallerData>\n" +
            "        <appender-ref ref =\"PROJECT\"/>\n" +
            "    </appender>\n" +
            "    \n" +
            "    <logger name=\"" + PACKAGE + ".~MODULE_NAME~.repository\" level=\"warn\"/>\n" +
            "    <logger name=\"org.springframework\" level=\"warn\"/>\n" +
            "    <logger name=\"org.hibernate\" level=\"warn\"/>\n" +
            "    <logger name=\"com.netflix\" level=\"warn\"/>\n" +
            "    <logger name=\"org.mybatis\" level=\"warn\"/>\n" +
            "    <logger name=\"com.github\" level=\"warn\"/>\n" +
            "    <logger name=\"com.zaxxer\" level=\"warn\"/>\n" +
            "    <logger name=\"org.apache\" level=\"warn\"/>\n" +
            "    <logger name=\"org.jboss\" level=\"warn\"/>\n" +
            "\n" +
            "    <root level=\"info\">\n" +
            "        <appender-ref ref=\"ASYNC\"/>\n" +
            "    </root>\n" +
            "</configuration>\n";


    private static String SERVER_POM = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<project xmlns=\"http://maven.apache.org/POM/4.0.0\"\n" +
            "         xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
            "         xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\">\n" +
            "    <parent>\n" +
            "        <artifactId>%s</artifactId>\n" +
            "        <groupId>" + PACKAGE + "</groupId>\n" +
            "        <version>1.0-SNAPSHOT</version>\n" +
            "    </parent>\n" +
            "    <modelVersion>4.0.0</modelVersion>\n" +
            "\n" +
            "    <artifactId>%s</artifactId>\n" +
            "    <description>%s模块的服务端</description>\n" +
            "\n" +
            "    <dependencies>\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>mall-common</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>mall-global</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>${project.groupId}</groupId>\n" +
            "            <artifactId>%s</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.cloud</groupId>\n" +
            "            <artifactId>spring-cloud-starter-eureka</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.boot</groupId>\n" +
            "            <artifactId>spring-boot-starter-actuator</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>org.springframework.boot</groupId>\n" +
            "            <artifactId>spring-boot-starter-jdbc</artifactId>\n" +
            "            <exclusions>\n" +
            "                <exclusion>\n" +
            "                    <groupId>org.apache.tomcat</groupId>\n" +
            "                    <artifactId>tomcat-jdbc</artifactId>\n" +
            "                </exclusion>\n" +
            "            </exclusions>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.zaxxer</groupId>\n" +
            "            <artifactId>HikariCP</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>mysql</groupId>\n" +
            "            <artifactId>mysql-connector-java</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>org.mybatis</groupId>\n" +
            "            <artifactId>mybatis</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>org.mybatis</groupId>\n" +
            "            <artifactId>mybatis-spring</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.github.liuanxin</groupId>\n" +
            "            <artifactId>mybatis-page</artifactId>\n" +
            "        </dependency>\n" +
            "        <dependency>\n" +
            "            <groupId>com.github.liuanxin</groupId>\n" +
            "            <artifactId>mybatis-redis-cache</artifactId>\n" +
            "        </dependency>\n" +
            "\n" +
            "        <dependency>\n" +
            "            <groupId>com.github.liuanxin</groupId>\n" +
            "            <artifactId>api-info</artifactId>\n" +
            "        </dependency>\n" +
            "    </dependencies>\n" +
            "\n" +
            "    <build>\n" +
            "        <finalName>%s</finalName>\n" +
            "        <plugins>\n" +
            "            <plugin>\n" +
            "                <groupId>org.springframework.boot</groupId>\n" +
            "                <artifactId>spring-boot-maven-plugin</artifactId>\n" +
            "            </plugin>\n" +
            "        </plugins>\n" +
            "    </build>\n" +
            "</project>\n";

    
    private static String TEST_ENUM_HANDLE = "package " + PACKAGE + ".%s;\n" +
            "\n" +
            "import " + PACKAGE + ".common.Const;\n" +
            "import " + PACKAGE + ".common.util.GenerateEnumHandler;\n" +
            "import " + PACKAGE + ".%s.config.%sConst;\n" +
            "import org.junit.Test;\n" +
            "\n" +
            "/**\n" +
            " * %s模块生成 enumHandle 的工具类\n" +
            ModuleTest.AUTHOR +
            " */\n" +
            "public class %sGenerateEnumHandler {\n" +
            "\n" +
            "    @Test\n" +
            "    public void generate() {\n" +
            "        GenerateEnumHandler.generateEnum(getClass(), Const.BASE_PACKAGE, %sConst.MODULE_NAME);\n" +
            "    }\n" +
            "}\n";

    static void generateServer(String moduleName, String packageName, String model,
                               String server, String module, String port, String comment) throws IOException {
        String parentPackageName = packageName.replace("-", ".");
        String clazzName = ModuleTest.capitalize(parentPackageName);

        File servmallath = new File(module + "/" + server + "/src/main/java");
        servmallath.mkdirs();

        String servmallom = String.format(SERVER_POM, moduleName, server, comment, model, server + "-" + port);
        ModuleTest.writeFile(new File(module + "/" + server, "pom.xml"), servmallom);

        File packagePath = new File(servmallath + "/" + ModuleTest.PACKAGE_PATH);
        File sourcePath = new File(packagePath + "/" + parentPackageName.replaceAll("\\.", "/"));
        File configPath = new File(sourcePath, "config");
        File servicePath = new File(sourcePath, "service");
        configPath.mkdirs();
        servicePath.mkdirs();
        new File(sourcePath, "handler").mkdirs();
        new File(sourcePath, "repository").mkdirs();

        String application = String.format(APPLICATION, clazzName, clazzName, clazzName);
        ModuleTest.writeFile(new File(packagePath, clazzName + "Application.java"), application);

        String moduleConfig = String.format(MODULE_CONFIG, parentPackageName, comment, clazzName, clazzName);
        ModuleTest.writeFile(new File(configPath, clazzName + "Config.java"), moduleConfig);

        String configData = String.format(CONFIG_DATA, parentPackageName, parentPackageName, clazzName, comment,
                clazzName, clazzName, clazzName, clazzName, clazzName, clazzName);
        ModuleTest.writeFile(new File(configPath, clazzName + "ConfigData.java"), configData);

        String dataSource = String.format(DATA_SOURCE, parentPackageName, clazzName, clazzName, clazzName);
        ModuleTest.writeFile(new File(configPath, clazzName + "DataSourceInit.java"), dataSource);

        String exception = String.format(EXCEPTION, parentPackageName, clazzName);
        ModuleTest.writeFile(new File(configPath, clazzName + "GlobalException.java"), exception);

        String interceptor = String.format(INTERCEPTOR, parentPackageName, comment, clazzName);
        ModuleTest.writeFile(new File(configPath, clazzName + "Interceptor.java"), interceptor);

        String war = String.format(WEB_ADAPTER, parentPackageName, comment, clazzName, clazzName);
        ModuleTest.writeFile(new File(configPath, clazzName + "WebAdapter.java"), war);

        String service = String.format(SERVICE, parentPackageName, parentPackageName, clazzName,
                comment, clazzName, clazzName, clazzName, comment, clazzName.toUpperCase());
        ModuleTest.writeFile(new File(servicePath, clazzName + "Service.java"), service);


        File resourcePath = new File(module + "/" + server + "/src/main/resources");
        resourcePath.mkdirs();

        String applicationYml = String.format(APPLICATION_YML, port, packageName);
        ModuleTest.writeFile(new File(resourcePath, "application.yml"), applicationYml);
        String applicationTestYml = String.format(APPLICATION_TEST_YML, port,
                packageName, packageName, packageName, packageName);
        ModuleTest.writeFile(new File(resourcePath, "application-test.yml"), applicationTestYml);
        String applicationProdYml = String.format(APPLICATION_PROD_YML, port,
                packageName, packageName, packageName, packageName);
        ModuleTest.writeFile(new File(resourcePath, "application-prod.yml"), applicationProdYml);

        ModuleTest.writeFile(new File(resourcePath, "config.properties"), CONFIG);
        String logXml = LOG_XML.replaceAll("~MODULE_NAME~", parentPackageName);
        ModuleTest.writeFile(new File(resourcePath, "log-dev.xml"), logXml);
        String testXml = LOG_TEST_XML.replaceAll("~MODULE_NAME~", parentPackageName);
        ModuleTest.writeFile(new File(resourcePath, "log-test.xml"), testXml);
        String prodXml = LOG_PROD_XML.replaceAll("~MODULE_NAME~", parentPackageName);
        ModuleTest.writeFile(new File(resourcePath, "log-prod.xml"), prodXml);


        File testParent = new File(module + "/" + server + "/src/test/java/" +
                ModuleTest.PACKAGE_PATH + "/" + parentPackageName.replace('.', '/'));
        testParent.mkdirs();

        File testResource = new File(module + "/" + server + "/src/test/resources");
        testResource.mkdirs();
        ModuleTest.writeFile(new File(testResource, packageName + ".sql"), "");

        String test = String.format(TEST_ENUM_HANDLE, parentPackageName,
                parentPackageName, clazzName, comment, clazzName, clazzName);
        ModuleTest.writeFile(new File(testParent, clazzName + "GenerateEnumHandler.java"), test);
    }
}
