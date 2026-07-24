package lab.mvc.rest;

public class CatalogNotFoundException extends RuntimeException {

    public CatalogNotFoundException(Long id) {
        super("Catalog item " + id + " was not found");
    }
}
