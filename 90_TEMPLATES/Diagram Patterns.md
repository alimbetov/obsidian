# Diagram Patterns

Схемы должны объяснять механизм, последовательность, зависимости или решение. Не добавляйте диаграмму только ради оформления.

## Concept dependencies

```mermaid
flowchart LR
    A[Prerequisite] --> B[Current Concept]
    B --> C[Related Mechanism]
    B --> D[Production Risk]
```

## Execution lifecycle

```mermaid
flowchart TD
    A[Start] --> B[Acquire resource]
    B --> C[Execute]
    C --> D{Success?}
    D -- Yes --> E[Commit]
    D -- No --> F[Rollback]
    E --> G[Cleanup]
    F --> G
```

## Request sequence

```mermaid
sequenceDiagram
    participant C as Client
    participant S as Service
    participant D as Database

    C->>S: request
    S->>D: query
    D-->>S: result
    S-->>C: response
```

## Comparison

```mermaid
flowchart LR
    P[Problem] --> A[Approach A]
    P --> B[Approach B]
    A --> TA[Trade-offs A]
    B --> TB[Trade-offs B]
```

## Visual rules

- одна диаграмма — одна мысль;
- до 7–9 узлов на одной схеме, если это не mind map;
- длинное объяснение остаётся в Markdown под диаграммой;
- Canvas используется для навигации между заметками;
- Mermaid используется для алгоритмов, lifecycle, sequence и причинно-следственных связей;
- внешние PNG/SVG добавляются только когда Mermaid недостаточен.
