package com.sdu.software.goingmerry.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdu.software.goingmerry.core.ProjectConstant;

import io.swagger.models.parameters.AbstractSerializableParameter;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.json.JacksonModuleRegistrar;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * @author Liyp
 */
@Configuration
public class Swagger2Configurer implements WebMvcConfigurer {

    /**
     * 这个地方要重新注入一下资源文件，不然不会注入资源的，也没有注入requestHandlerMappping,相当于xml配置的
     * <!--swagger资源配置-->
     * <mvc:resources location="classpath:/META-INF/resources/" mapping="swagger-ui.html"/>
     * <mvc:resources location="classpath:/META-INF/resources/webjars/" mapping="/webjars/**"/>
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public JacksonModuleRegistrar customizeJacksonModuleRegistrar() {
        return new JacksonModuleRegistrar() {
            @Override
            public void maybeRegisterModule(ObjectMapper objectMapper) {
                objectMapper.addMixIn(AbstractSerializableParameter.class, IgnoreExampleMixIn.class);
            }
        };
    }

    interface IgnoreExampleMixIn {
        @JsonIgnore
        Object getExample();
    }


    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(ProjectConstant.CONTROLLER_PACKAGE))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("API文档展示")
                .description("通过直接且美观的方式来查看和调试API")
                .version("1.0")
                .build();
    }
}
