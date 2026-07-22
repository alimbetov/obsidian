package lab.mvc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MvcPipelineLabApplication.class)
@AutoConfigureMockMvc
class MvcPipelineIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestMappingHandlerMapping handlerMapping;

    @Test
    void requestTraversesMappingCustomResolverInterceptorAndReturnHandler() throws Exception {
        mockMvc.perform(get("/api/catalog/{id}", 42)
                        .header("X-Tenant-Id", "bank-a")
                        .param("detail", "full")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(header().string("X-MVC-Interceptor", "applied"))
                .andExpect(header().string("X-Handler", "catalog-get"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.detail").value("full"))
                .andExpect(jsonPath("$.tenant").value("bank-a"));
    }

    @Test
    void defaultRequestParameterIsApplied() throws Exception {
        mockMvc.perform(get("/api/catalog/{id}", 7)
                        .header("X-Tenant-Id", "bank-b")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.detail").value("summary"));
    }

    @Test
    void missingCustomHeaderFailsDuringArgumentResolution() throws Exception {
        mockMvc.perform(get("/api/catalog/{id}", 7)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void pathVariableConversionFailureReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/api/catalog/{id}", "not-a-number")
                        .header("X-Tenant-Id", "bank-a")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void modelAttributeBindingConversionAndValidationSucceed() throws Exception {
        mockMvc.perform(get("/api/catalog/search")
                        .param("query", "spring")
                        .param("limit", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.query").value("spring"))
                .andExpect(jsonPath("$.limit").value(2))
                .andExpect(jsonPath("$.results[0]").value("alpha"));
    }

    @Test
    void invalidModelAttributeReturnsBadRequestBeforeControllerBodyCompletes() throws Exception {
        mockMvc.perform(get("/api/catalog/search")
                        .param("query", "")
                        .param("limit", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void requestBodyUsesJacksonConverterAndValidation() throws Exception {
        mockMvc.perform(post("/api/catalog")
                        .header("X-Tenant-Id", "bank-c")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"created-item\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.detail").value("created-item"))
                .andExpect(jsonPath("$.tenant").value("bank-c"));
    }

    @Test
    void invalidJsonBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/catalog")
                        .header("X-Tenant-Id", "bank-c")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid-json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void beanValidationOnRequestBodyReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/catalog")
                        .header("X-Tenant-Id", "bank-c")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void controllerAdviceTranslatesDomainException() throws Exception {
        mockMvc.perform(get("/api/catalog/{id}", 404)
                        .header("X-Tenant-Id", "bank-a")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.code").value("CATALOG_ITEM_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("Catalog item 404 was not found"));
    }

    @Test
    void unknownPathReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/unknown")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void mappingRegistryContainsAnnotatedHandlerMethod() {
        boolean found = handlerMapping.getHandlerMethods().values().stream()
                .anyMatch(handlerMethod -> handlerMethod.getMethod().getName().equals("getItem"));

        assertThat(found).isTrue();
    }
}
