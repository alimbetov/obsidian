package lab.mvc.rest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class CatalogControllerRestTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReturnsJsonBodyHeadersAndStatus() throws Exception {
        mockMvc.perform(get("/catalog/42").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"catalog-42\""))
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.name", is("Notebook")))
                .andExpect(jsonPath("$.quantity", is(7)));
    }

    @Test
    void postCreatesResourceWithLocationHeader() throws Exception {
        mockMvc.perform(post("/catalog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pen\",\"quantity\":3}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", endsWith("/catalog/101")))
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.name", is("Pen")));
    }

    @Test
    void putReplacesFullResource() throws Exception {
        mockMvc.perform(put("/catalog/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Book\",\"quantity\":9}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.name", is("Book")))
                .andExpect(jsonPath("$.quantity", is(9)));
    }

    @Test
    void patchUpdatesOnlySuppliedFields() throws Exception {
        mockMvc.perform(patch("/catalog/42")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"quantity\":11}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(42)))
                .andExpect(jsonPath("$.name", is("Notebook")))
                .andExpect(jsonPath("$.quantity", is(11)));
    }

    @Test
    void deleteReturnsNoContent() throws Exception {
        mockMvc.perform(delete("/catalog/42"))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));
    }

    @Test
    void validationFailureReturnsRestErrorContract() throws Exception {
        mockMvc.perform(post("/catalog")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"\",\"quantity\":-1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code", is("VALIDATION_FAILED")));
    }

    @Test
    void notFoundReturns404RestErrorContract() throws Exception {
        mockMvc.perform(get("/catalog/999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code", is("CATALOG_NOT_FOUND")));
    }

    @Test
    void unsupportedContentTypeReturns415BeforeControllerBody() throws Exception {
        mockMvc.perform(post("/catalog")
                        .contentType(MediaType.TEXT_PLAIN)
                        .accept(MediaType.APPLICATION_JSON)
                        .content("name=Pen"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void unacceptableAcceptHeaderReturns406() throws Exception {
        mockMvc.perform(get("/catalog/42").accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void responseEntityCanExposeExplicitHeaders() throws Exception {
        mockMvc.perform(get("/catalog/headers").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("ETag", "\"headers-v1\""))
                .andExpect(jsonPath("$.name", is("Header proof")));
    }
}
