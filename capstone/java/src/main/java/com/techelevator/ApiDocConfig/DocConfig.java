/* package com.techelevator.ApiDocConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;


@Configuration
@EnableSwagger2
public class DocConfig {
    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Project Schedule",
                "Project Schedule application",
                "0.0.1",
                null,
                new Contact("name", "website", "e-mail@email.com"),
                null, null, Collections.emptyList());
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .securitySchemes(Collections.singletonList(HttpAuthenticationScheme
                        .JWT_BEARER_BUILDER
                        .name("JWT")
                        .build())
                )
                .securityContexts(Collections.singletonList(SecurityContext.builder()
                        .securityReferences(Collections.singletonList(SecurityReference.builder()
                                .scopes(new AuthorizationScope[0])
                                .reference("JWT")
                                .build()))
                        .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
                        .build()))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }
}
 */
package com.techelevator.ApiDocConfig;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
public class DocConfig {

        private final String moduleName = "Project Schedule App";
        private final String apiVersion = "0.1";

        /*
         * public DocConfig(
         * 
         * @Value("${module-name}") String moduleName,
         * 
         * @Value("${api-version}") String apiVersion) {
         * this.moduleName = moduleName;
         * this.apiVersion = apiVersion;
         * }
         */

        @Bean
        public OpenAPI customOpenAPI() {
                final String securitySchemeName = "bearerAuth";
                final String apiTitle = String.format("%s API", StringUtils.capitalize(moduleName));
                return new OpenAPI()
                                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                                .components(
                                                new Components()
                                                                .addSecuritySchemes(securitySchemeName,
                                                                                new SecurityScheme()
                                                                                                .name(securitySchemeName)
                                                                                                .type(SecurityScheme.Type.HTTP)
                                                                                                .scheme("bearer")
                                                                                                .bearerFormat("JWT")
                                                                                                .description("Enter JWT")))
                                .info(new Info().title(apiTitle).version(apiVersion));
        }
}