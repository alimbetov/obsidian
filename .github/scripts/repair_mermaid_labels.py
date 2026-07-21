#!/usr/bin/env python3
"""Apply deterministic repairs to Mermaid labels that fail mermaid-cli parsing."""

from pathlib import Path

REPLACEMENTS = {
    "10_CONCEPTS/Spring/Core/Bean Lifecycle from Definition to Destruction.md": {
        "    F --> G[@PostConstruct]\n": "    F --> G[\"@PostConstruct\"]\n",
        "    G --> H[InitializingBean.afterPropertiesSet]\n": "    G --> H[\"InitializingBean.afterPropertiesSet()\"]\n",
        "    H --> I[Custom init-method]\n": "    H --> I[\"Custom init-method\"]\n",
        "    N --> O[@PreDestroy]\n": "    N --> O[\"@PreDestroy\"]\n",
        "    O --> P[DisposableBean.destroy]\n": "    O --> P[\"DisposableBean.destroy()\"]\n",
        "    P --> Q[Custom destroy-method]\n": "    P --> Q[\"Custom destroy-method\"]\n",
        "    A[Bean creation callbacks] --> B[@PostConstruct / afterPropertiesSet]\n": "    A[Bean creation callbacks] --> B[\"@PostConstruct / afterPropertiesSet()\"]\n",
        "    C[Container runtime callbacks] --> D[Lifecycle / SmartLifecycle]\n": "    C[Container runtime callbacks] --> D[\"Lifecycle / SmartLifecycle\"]\n",
        "    G[Bean destruction callbacks] --> H[@PreDestroy / DisposableBean]\n": "    G[Bean destruction callbacks] --> H[\"@PreDestroy / DisposableBean\"]\n",
    },
    "10_CONCEPTS/Spring/Core/Configuration Profiles and Externalized Properties.md": {
        "    B --> B1[@Configuration and @Bean]\n": "    B --> B1[\"@Configuration and @Bean\"]\n",
        "    B --> B2[@Import]\n": "    B --> B2[\"@Import\"]\n",
        "    B --> B4[@Profile]\n": "    B --> B4[\"@Profile\"]\n",
        "    C --> C3[Placeholders and @Value]\n": "    C --> C3[\"Placeholders and @Value\"]\n",
        "    C -->|Да| D[@Profile]\n": "    C -->|Да| D[\"@Profile\"]\n",
        "    C -->|Нет| E{@Import / selector / registrar / feature architecture}\n": "    C -->|Нет| E{\"@Import / selector / registrar / feature architecture\"}\n",
        "    F -->|Да| G[@Value or Environment at boundary]\n": "    F -->|Да| G[\"@Value or Environment at boundary\"]\n",
        "    H -->|Нет| J[Explicit configuration object/API]\n": "    H -->|Нет| J[\"Explicit configuration object / API\"]\n",
    },
    "10_CONCEPTS/Spring/Core/Spring Core Foundations.md": {
        "    A{Как зарегистрировать object?} -->|Own class in scan tree| B[@Component stereotype]\n": "    A{Как зарегистрировать object?} -->|Own class in scan tree| B[\"@Component stereotype\"]\n",
        "    A -->|Third-party or custom factory| C[@Bean method]\n": "    A -->|Third-party or custom factory| C[\"@Bean method\"]\n",
    },
    "10_CONCEPTS/Spring/Testing/Spring TestContext and Test Slices.md": {
        "    S --> SLICE[@DataJpaTest / @WebMvcTest]\n": "    S --> SLICE[\"@DataJpaTest / @WebMvcTest\"]\n",
        "    A --> FULL[@SpringBootTest]\n": "    A --> FULL[\"@SpringBootTest\"]\n",
    },
    "30_CERTIFICATIONS/Spring/2V0-72.22/CORE-B01/CORE-B01 Cards.md": {
        "    D --> H[@Bean configuration]\n": "    D --> H[\"@Bean configuration\"]\n",
    },
    "30_CERTIFICATIONS/Spring/2V0-72.22/Spring Testing Roadmap.md": {
        "    SL --> JPA[@DataJpaTest]\n": "    SL --> JPA[\"@DataJpaTest\"]\n",
    },
    "40_PRODUCTION_CASES/Spring/Bean Lifecycle Production Cases.md": {
        "    A[Create bean A] --> B[@PostConstruct A]\n": "    A[Create bean A] --> B[\"@PostConstruct A\"]\n",
        "    D[Create bean B] --> E[@PostConstruct B]\n": "    D[Create bean B] --> E[\"@PostConstruct B\"]\n",
    },
    "40_PRODUCTION_CASES/Spring/Dependency Resolution Production Cases.md": {
        "    C -->|Optional| E[Optional Nullable required=false Provider]\n": "    C -->|Optional| E[\"Optional / @Nullable / required=false / ObjectProvider\"]\n",
        "    G -->|Default| H[@Primary]\n": "    G -->|Default| H[\"@Primary\"]\n",
        "    G -->|Category| I[@Qualifier]\n": "    G -->|Category| I[\"@Qualifier\"]\n",
        "    G -->|Specific name| J[@Resource or explicit name contract]\n": "    G -->|Specific name| J[\"@Resource or explicit name contract\"]\n",
        "    L -->|Yes| M[@Order Ordered or explicit composition]\n": "    L -->|Yes| M[\"@Order / Ordered / explicit composition\"]\n",
    },
}


def main() -> None:
    changed = 0
    for file_name, replacements in REPLACEMENTS.items():
        path = Path(file_name)
        text = path.read_text(encoding="utf-8")
        original = text
        for old, new in replacements.items():
            if old not in text:
                raise SystemExit(f"Expected Mermaid fragment not found in {file_name}: {old.strip()}")
            text = text.replace(old, new, 1)
        if text != original:
            path.write_text(text, encoding="utf-8")
            changed += 1
            print(f"repaired {file_name}")
    print(f"repaired files: {changed}")


if __name__ == "__main__":
    main()
