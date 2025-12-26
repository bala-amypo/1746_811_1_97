@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Digital Certificate Generator API")
                        .version("1.0")
                        .description("All APIs for certificate generation & verification"));
    }
}
