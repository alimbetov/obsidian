package lab.mvc.rest;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
public class CatalogController {

    private final CatalogService service;

    public CatalogController(CatalogService service) {
        this.service = service;
    }

    @GetMapping
    public List<CatalogItemDto> list(@RequestParam(defaultValue = "false") boolean includeEmpty) {
        return service.list();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogItemDto> get(@PathVariable Long id) {
        CatalogItemDto item = service.get(id);
        return ResponseEntity
                .ok()
                .eTag("\"catalog-" + id + "\"")
                .cacheControl(CacheControl.maxAge(60, TimeUnit.SECONDS))
                .body(item);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CatalogItemDto> create(
            @Valid @RequestBody CreateCatalogItemRequest request,
            UriComponentsBuilder uriBuilder
    ) {
        CatalogItemDto created = service.create(request);
        URI location = uriBuilder.path("/catalog/{id}").buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CatalogItemDto> replace(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCatalogItemRequest request
    ) {
        return ResponseEntity.ok(service.replace(id, request));
    }

    @PatchMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CatalogItemDto> patch(
            @PathVariable Long id,
            @Valid @RequestBody PatchCatalogItemRequest request
    ) {
        return ResponseEntity.ok(service.patch(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/headers")
    public ResponseEntity<CatalogItemDto> headers() {
        return ResponseEntity
                .ok()
                .header(HttpHeaders.ETAG, "\"headers-v1\"")
                .body(new CatalogItemDto(1L, "Header proof", 1));
    }
}
