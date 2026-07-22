---
type: source-index
domain: spring
subdomain: spring-mvc
route: SPRING-MVC-B01
status: published
verified_at: 2026-07-22
exam_baseline:
  - spring-framework-5.3.39
  - spring-boot-2.5.15
current_delta:
  - spring-framework-7.0.8
  - spring-framework-6.2.19
  - spring-boot-current
tags:
  - sources
  - spring-mvc
  - dispatcher-servlet
---

# Spring MVC DispatcherServlet Sources

> [!summary]
> Primary-source index for `SPRING-MVC-B01`. The certification baseline is Spring Framework 5.3 and Spring Boot 2.5. Current documentation is used only to identify stable architecture and explicit production deltas.

# Official exam baseline

- Broadcom Spring Professional Develop Exam Guide: https://docs.broadcom.com/doc/vmw-spring-professional-develop-exam-guide
- Spring Framework 5.3.39 Web on Servlet Stack: https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/web.html
- Spring Framework 5.3.39 Testing and MockMvc: https://docs.spring.io/spring-framework/docs/5.3.x/reference/html/testing.html
- Spring Boot 2.5.15 Reference PDF: https://docs.spring.io/spring-boot/docs/2.5.15/reference/pdf/spring-boot-reference.pdf
- Spring Boot 2.5 Servlet MVC auto-configuration package: https://docs.spring.io/spring-boot/docs/2.5.x/api/org/springframework/boot/autoconfigure/web/servlet/package-summary.html

# Framework 5.3 API contracts

- `DispatcherServlet`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/DispatcherServlet.html
- `HandlerMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/HandlerMapping.html
- `HandlerAdapter`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/HandlerAdapter.html
- `HandlerExecutionChain`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/HandlerExecutionChain.html
- `HandlerInterceptor`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/HandlerInterceptor.html
- `HandlerExceptionResolver`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/HandlerExceptionResolver.html
- `ViewResolver`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/ViewResolver.html
- `RequestMappingHandlerMapping`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerMapping.html
- `RequestMappingHandlerAdapter`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/servlet/mvc/method/annotation/RequestMappingHandlerAdapter.html
- `HandlerMethodArgumentResolver`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/method/support/HandlerMethodArgumentResolver.html
- `HandlerMethodReturnValueHandler`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/method/support/HandlerMethodReturnValueHandler.html
- `WebDataBinder`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/web/bind/WebDataBinder.html
- `HttpMessageConverter`: https://docs.spring.io/spring-framework/docs/5.3.x/javadoc-api/org/springframework/http/converter/HttpMessageConverter.html
- `MockMvcBuilders`: https://docs.spring.io/spring/docs/5.3.x/javadoc-api/org/springframework/test/web/servlet/setup/MockMvcBuilders.html

# Current Spring Framework reference

- Spring Web MVC overview: https://docs.spring.io/spring-framework/reference/web/webmvc.html
- DispatcherServlet: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet.html
- Request-processing sequence: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/sequence.html
- Special strategy bean types: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/special-bean-types.html
- MVC Java configuration: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/config.html
- Annotated controllers: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller.html
- Handler method arguments: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/arguments.html
- Handler method return values: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-methods/return-types.html
- Model attributes: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-modelattrib-methods.html
- Data binding: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-data-binding.html
- Exception handling: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-exceptionhandler.html
- Controller advice: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-controller/ann-advice.html
- View resolution: https://docs.spring.io/spring-framework/reference/web/webmvc/mvc-servlet/viewresolver.html
- MockMvc: https://docs.spring.io/spring-framework/reference/testing/mockmvc.html
- MockMvc setup options: https://docs.spring.io/spring-framework/reference/testing/mockmvc/setup-options.html

# Current Spring Boot reference

- Servlet web applications: https://docs.spring.io/spring-boot/reference/web/servlet.html
- Spring MVC how-to guide: https://docs.spring.io/spring-boot/how-to/spring-mvc.html
- Embedded web servers: https://docs.spring.io/spring-boot/how-to/webserver.html

# Stable architectural conclusions

The following model is stable across the baseline and current references:

```text
DispatcherServlet
→ HandlerMapping
→ HandlerExecutionChain
→ HandlerAdapter
→ annotated method argument resolution
→ method invocation
→ return-value handling
→ body conversion or view rendering
→ HandlerExceptionResolver on failures
```

Stable concepts:

- front-controller architecture;
- ordered strategy beans;
- separation of mapping and invocation;
- annotated method argument and return-value extension points;
- `WebDataBinder`, conversion, and validation boundaries;
- message-converter and view-resolver paths;
- MVC exception-resolution chain;
- `MockMvc` execution through `DispatcherServlet` without a live server.

# Version-boundary matrix

| Concern | Spring 5.3 / Boot 2.5 baseline | Current delta |
|---|---|---|
| Servlet namespace | `javax.servlet` | `jakarta.servlet` |
| MVC architecture | `DispatcherServlet` strategy model | same core model |
| Path matching | Ant-style baseline plus available `PathPatternParser` option | newer defaults and refinements |
| Error response | status/advice/custom DTO patterns | `ProblemDetail` and newer error abstractions available |
| MockMvc | Hamcrest result matchers and WebTestClient bridge | `MockMvcTester` may also be available |
| Boot customization | `WebMvcConfigurer` additive; `@EnableWebMvc` takes control | same ownership boundary with newer packages/APIs |
| Deployment | embedded Servlet container or WAR | same broad models with current container compatibility |

# Evidence policy

Use these sources to verify behavior claims. Runtime PASS still requires the executable lab:

```bash
mvn --batch-mode --no-transfer-progress \
  -f 50_LABS/Spring/SPRING-MVC-B01/pom.xml test
```

# Related material

- [[10_CONCEPTS/Spring/MVC/DispatcherServlet and Annotated Controller Pipeline]]
- [[30_CERTIFICATIONS/Spring/2V0-72.22/SPRING-MVC-B01/SPRING-MVC-B01 Roadmap]]
- [[50_LABS/Spring/SPRING-MVC-B01/README]]
