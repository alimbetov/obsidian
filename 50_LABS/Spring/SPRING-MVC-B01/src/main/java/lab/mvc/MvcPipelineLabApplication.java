package lab.mvc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;

@SpringBootApplication
public class MvcPipelineLabApplication {

    public static void main(String[] args) {
        SpringApplication.run(MvcPipelineLabApplication.class, args);
    }
}

@Configuration
class MvcLabConfiguration implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new TenantIdArgumentResolver());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestTraceInterceptor());
    }
}

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@interface CurrentTenant {
}

final class TenantId {
    private final String value;

    TenantId(String value) {
        this.value = value;
    }

    String getValue() {
        return value;
    }
}

final class TenantIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentTenant.class)
                && TenantId.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            org.springframework.web.bind.support.WebDataBinderFactory binderFactory
    ) throws Exception {
        String tenant = webRequest.getHeader("X-Tenant-Id");
        if (tenant == null || tenant.trim().isEmpty()) {
            throw new ServletRequestBindingException("Missing required header X-Tenant-Id");
        }
        return new TenantId(tenant.trim());
    }
}

final class RequestTraceInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        response.setHeader("X-MVC-Interceptor", "applied");
        request.setAttribute("mvcHandlerType", handler.getClass().getSimpleName());
        return true;
    }
}

@RestController
@RequestMapping(path = "/api/catalog", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
class CatalogController {

    @GetMapping("/{id}")
    ResponseEntity<CatalogItemResponse> getItem(
            @PathVariable long id,
            @RequestParam(defaultValue = "summary") String detail,
            @CurrentTenant TenantId tenant
    ) {
        if (id == 404L) {
            throw new CatalogItemNotFoundException(id);
        }
        CatalogItemResponse body = new CatalogItemResponse(id, detail, tenant.getValue());
        return ResponseEntity.ok()
                .header("X-Handler", "catalog-get")
                .body(body);
    }

    @GetMapping("/search")
    SearchResponse search(@Valid @ModelAttribute SearchCriteria criteria) {
        return new SearchResponse(criteria.getQuery(), criteria.getLimit(), Arrays.asList("alpha", "beta"));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    CatalogItemResponse create(
            @Valid @RequestBody CreateCatalogItemRequest request,
            @CurrentTenant TenantId tenant
    ) {
        return new CatalogItemResponse(100L, request.getName(), tenant.getValue());
    }
}

@RestControllerAdvice
class MvcLabExceptionAdvice {

    @ExceptionHandler(CatalogItemNotFoundException.class)
    ResponseEntity<ApiError> handleNotFound(CatalogItemNotFoundException exception) {
        ApiError error = new ApiError("CATALOG_ITEM_NOT_FOUND", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(error);
    }
}

final class CatalogItemNotFoundException extends RuntimeException {
    CatalogItemNotFoundException(long id) {
        super("Catalog item " + id + " was not found");
    }
}

final class CatalogItemResponse {
    private final long id;
    private final String detail;
    private final String tenant;

    CatalogItemResponse(long id, String detail, String tenant) {
        this.id = id;
        this.detail = detail;
        this.tenant = tenant;
    }

    public long getId() {
        return id;
    }

    public String getDetail() {
        return detail;
    }

    public String getTenant() {
        return tenant;
    }
}

final class SearchCriteria {
    @NotBlank
    private String query;

    @Min(1)
    @Max(50)
    private Integer limit = 10;

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}

final class SearchResponse {
    private final String query;
    private final Integer limit;
    private final List<String> results;

    SearchResponse(String query, Integer limit, List<String> results) {
        this.query = query;
        this.limit = limit;
        this.results = results;
    }

    public String getQuery() {
        return query;
    }

    public Integer getLimit() {
        return limit;
    }

    public List<String> getResults() {
        return results;
    }
}

final class CreateCatalogItemRequest {
    @NotBlank
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

final class ApiError {
    private final String code;
    private final String message;

    ApiError(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
