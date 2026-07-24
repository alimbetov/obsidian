package lab.mvc.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.Optional;

public class CatalogRestClient {

    private final RestTemplate restTemplate;

    public CatalogRestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CatalogItemDto getBodyOnly(Long id) {
        return restTemplate.getForObject("/catalog/{id}", CatalogItemDto.class, id);
    }

    public ResponseEntity<CatalogItemDto> getEntity(Long id) {
        return restTemplate.getForEntity("/catalog/{id}", CatalogItemDto.class, id);
    }

    public ResponseEntity<CatalogItemDto> create(CreateCatalogItemRequest request) {
        return restTemplate.postForEntity("/catalog", request, CatalogItemDto.class);
    }

    public ResponseEntity<CatalogItemDto> getWithAcceptHeader(Long id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Void> request = new HttpEntity<>(headers);
        return restTemplate.exchange(
                "/catalog/{id}",
                HttpMethod.GET,
                request,
                CatalogItemDto.class,
                id
        );
    }

    public Optional<CatalogItemDto> find(Long id) {
        try {
            return Optional.ofNullable(getBodyOnly(id));
        } catch (HttpClientErrorException.NotFound ex) {
            return Optional.empty();
        }
    }
}
