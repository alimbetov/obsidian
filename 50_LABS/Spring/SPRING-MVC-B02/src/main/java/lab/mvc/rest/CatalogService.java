package lab.mvc.rest;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CatalogService {

    private final Map<Long, CatalogItemDto> items = new LinkedHashMap<>();
    private final AtomicLong sequence = new AtomicLong(100);

    @PostConstruct
    void seed() {
        items.put(42L, new CatalogItemDto(42L, "Notebook", 7));
    }

    public List<CatalogItemDto> list() {
        return new ArrayList<>(items.values());
    }

    public CatalogItemDto get(Long id) {
        CatalogItemDto item = items.get(id);
        if (item == null) {
            throw new CatalogNotFoundException(id);
        }
        return item;
    }

    public CatalogItemDto create(CreateCatalogItemRequest request) {
        Long id = sequence.incrementAndGet();
        CatalogItemDto item = new CatalogItemDto(id, request.getName(), request.getQuantity());
        items.put(id, item);
        return item;
    }

    public CatalogItemDto replace(Long id, UpdateCatalogItemRequest request) {
        get(id);
        CatalogItemDto item = new CatalogItemDto(id, request.getName(), request.getQuantity());
        items.put(id, item);
        return item;
    }

    public CatalogItemDto patch(Long id, PatchCatalogItemRequest request) {
        CatalogItemDto current = get(id);
        if (request.getName() != null) {
            current.setName(request.getName());
        }
        if (request.getQuantity() != null) {
            current.setQuantity(request.getQuantity());
        }
        return current;
    }

    public void delete(Long id) {
        get(id);
        items.remove(id);
    }
}
