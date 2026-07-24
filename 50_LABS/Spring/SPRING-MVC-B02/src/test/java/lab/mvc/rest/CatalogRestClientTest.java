package lab.mvc.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class CatalogRestClientTest {

    private final RestTemplate restTemplate = new RestTemplate();
    private final MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
    private final CatalogRestClient client = new CatalogRestClient(restTemplate);

    @Test
    void getForObjectReturnsOnlyConvertedBody() {
        server.expect(once(), requestTo("/catalog/42"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":42,\"name\":\"Notebook\",\"quantity\":7}",
                        MediaType.APPLICATION_JSON
                ));

        CatalogItemDto body = client.getBodyOnly(42L);

        assertThat(body.getId()).isEqualTo(42L);
        assertThat(body.getName()).isEqualTo("Notebook");
        server.verify();
    }

    @Test
    void getForEntityExposesStatusHeadersAndBody() {
        server.expect(once(), requestTo("/catalog/42"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":42,\"name\":\"Notebook\",\"quantity\":7}",
                        MediaType.APPLICATION_JSON
                ).header(HttpHeaders.ETAG, "\"catalog-42\""));

        ResponseEntity<CatalogItemDto> response = client.getEntity(42L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getETag()).isEqualTo("\"catalog-42\"");
        assertThat(response.getBody().getName()).isEqualTo("Notebook");
        server.verify();
    }

    @Test
    void postForEntityExposesCreatedLocation() {
        CreateCatalogItemRequest request = new CreateCatalogItemRequest();
        request.setName("Pen");
        request.setQuantity(3);

        server.expect(once(), requestTo("/catalog"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.LOCATION, "/catalog/101")
                        .body("{\"id\":101,\"name\":\"Pen\",\"quantity\":3}"));

        ResponseEntity<CatalogItemDto> response = client.create(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().toString()).isEqualTo("/catalog/101");
        assertThat(response.getBody().getId()).isEqualTo(101L);
        server.verify();
    }

    @Test
    void exchangeSupportsExplicitAcceptHeader() {
        server.expect(once(), requestTo("/catalog/42"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(
                        "{\"id\":42,\"name\":\"Notebook\",\"quantity\":7}",
                        MediaType.APPLICATION_JSON
                ));

        ResponseEntity<CatalogItemDto> response = client.getWithAcceptHeader(42L);

        assertThat(response.getBody().getQuantity()).isEqualTo(7);
        server.verify();
    }

    @Test
    void restTemplate404CanBeMappedToOptionalEmpty() {
        server.expect(once(), requestTo("/catalog/999"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        Optional<CatalogItemDto> result = client.find(999L);

        assertThat(result).isEmpty();
        server.verify();
    }
}
