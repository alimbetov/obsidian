---
type: visual-deep-dive
domain: java
subdomain: java-versions
route: JAVA-LTS-B01
status: published
visual_diagrams: 15
java_versions:
  - 11
  - 17
  - 21
tags:
  - java-11
  - java-17
  - java-21
  - visual-learning
  - migration
---

# Java 11, 17 and 21 Visual Deep Dive

> [!summary]
> Fifteen visual models for release roles, feature states, language/API evolution, migration, compatibility and runtime proof.

# 1. Cumulative LTS topology

```mermaid
flowchart LR
    J8["Java 8 heritage"] --> J11["Java 11 compatibility"]
    J11 --> J17["Java 17 certification"]
    J17 --> J21["Java 21 production"]
```

# 2. Feature-status decision

```mermaid
flowchart TD
    F["Feature in a JDK"] --> S{"Exact status?"}
    S --> P["Permanent"]
    S --> PRE["Preview"]
    S --> INC["Incubator"]
    S --> EXP["Experimental VM"]
    S --> DEP["Deprecated or removed"]
    PRE --> FLAGS["Compile and run with preview flags"]
    INC --> MOD["Explicit incubator module"]
```

# 3. Version roles

```mermaid
flowchart TB
    KNOW["Complete Java knowledge"] --> J11["Java 11: maintain and migrate"]
    KNOW --> J17["Java 17: answer 1Z0-829"]
    KNOW --> J21["Java 21: design modern production"]
```

# 4. Java 8 to 11 migration

```mermaid
flowchart LR
    BUILD["Update build/plugins"] --> RUN["Run old bytecode on JDK 11"]
    RUN --> JDEPS["Run jdeps"]
    JDEPS --> REMOVED["Replace removed modules"]
    REMOVED --> REFLECT["Fix reflective access"]
    REFLECT --> RELEASE["Compile --release 11"]
    RELEASE --> TEST["Functional and operational proof"]
```

# 5. Java 11 to 17 migration

```mermaid
flowchart LR
    LIBS["Upgrade libraries"] --> ENC["Strong encapsulation"]
    ENC --> REM["Removed tools/components"]
    REM --> SEC["Security/serialization review"]
    SEC --> RELEASE["Compile --release 17"]
    RELEASE --> EVIDENCE["Integration + JFR/GC evidence"]
```

# 6. Java 17 to 21 migration

```mermaid
flowchart LR
    BASE["Run existing code on JDK 21"] --> CHARSET["Verify UTF-8/default behavior"]
    CHARSET --> VT["Evaluate virtual threads"]
    VT --> LIMITS["Preserve downstream limits"]
    LIMITS --> API["Adopt sequenced/pattern APIs"]
    API --> STATUS["Isolate preview/incubator features"]
```

# 7. Language evolution

```mermaid
flowchart LR
    J11["Java 11"] --> J17["Java 17"]
    J17 --> J21["Java 21"]
    J11 --> L11["var lambda parameters"]
    J17 --> L17["switch expressions, text blocks, records, sealed, instanceof patterns"]
    J21 --> L21["record patterns and pattern switch"]
```

# 8. API evolution

```mermaid
flowchart TB
    J11["Java 11 APIs"] --> HTTP["HTTP Client"]
    J11 --> STR["String/Files additions"]
    J17["Java 17 APIs"] --> STREAM["Stream.toList and runtime refinements"]
    J21["Java 21 APIs"] --> SEQ["Sequenced collections"]
    J21 --> VT["Virtual-thread APIs"]
    J21 --> KEM["KEM API"]
```

# 9. `--release` boundary

```mermaid
flowchart TD
    SRC["Source code"] --> COMP["Newer javac"]
    COMP --> REL["--release 17"]
    REL --> LANG["Java 17 language"]
    REL --> API["Java 17 documented APIs"]
    REL --> CLASS["Java 17 class-file target"]
```

# 10. Compatibility dimensions

```mermaid
mindmap
  root((Migration proof))
    Source
      Compiles
    Binary
      Links and loads
    Behavioral
      Same outcomes
    Tooling
      Build agents plugins
    Operational
      GC TLS charset locale containers
```

# 11. Virtual-thread capacity boundary

```mermaid
flowchart LR
    REQUESTS["Many blocking tasks"] --> VT["Virtual thread per task"]
    VT --> DB["Database pool"]
    VT --> REMOTE["Remote rate limit"]
    VT --> FD["File descriptors/sockets"]
    DB --> LIMIT["Admission control"]
    REMOTE --> LIMIT
    FD --> LIMIT
```

# 12. Module encapsulation progression

```mermaid
flowchart LR
    CP["Classpath broad visibility"] --> MODULE["Module readability and exports"]
    MODULE --> OPENS["opens for deep reflection"]
    OPENS --> STRONG["Strong JDK encapsulation by Java 17"]
    STRONG --> MIG["Upgrade library or temporary add-opens"]
```

# 13. CI version matrix

```mermaid
flowchart TB
    SRC["JAVA-LTS-B01 source sets"] --> L11["JDK 11 lane"]
    SRC --> L17["JDK 17 lane"]
    SRC --> L21["JDK 21 lane"]
    L11 --> B11["Compile Java 11 source"]
    L17 --> B17["Compile Java 11 + 17 source"]
    L21 --> B21["Compile Java 11 + 17 + 21 source"]
```

# 14. Java 17 exam boundary

```mermaid
flowchart TD
    Q["Question target: Java 17"] --> JLS["Use JLS/API 17"]
    JLS --> VALID["Java 17 syntax and APIs"]
    Q --> TRAP11["Java 11 historical distractor"]
    Q --> TRAP21["Java 21-only distractor"]
    TRAP11 --> REJECT["Reject if not Java 17 semantics"]
    TRAP21 --> REJECT
```

# 15. Runtime selection decision

```mermaid
flowchart TD
    START["Choose runtime"] --> FRAME["Framework/library support"]
    FRAME --> OPS["Vendor and operations support"]
    OPS --> FEATURE["Required permanent features"]
    FEATURE --> COST["Migration cost and evidence"]
    COST --> TARGET{"Target"}
    TARGET -->|"legacy compatibility"| J11["Java 11"]
    TARGET -->|"certification/stable modernization"| J17["Java 17"]
    TARGET -->|"modern production"| J21["Java 21"]
```

# Visual recall prompts

1. Reconstruct the three migration paths without notes.
2. Explain why `--release` covers more than `-source/-target`.
3. Draw the resource boundary around virtual threads.
4. Distinguish permanent, preview and incubator status.
5. Explain why a Java 17 exam answer must reject Java 21-only APIs.

# Related material

- [[10_CONCEPTS/Java/Versions/Java 11 17 21 LTS Evolution]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Roadmap]]
- [[30_CERTIFICATIONS/Java/JAVA-LTS-B01/JAVA-LTS-B01 Assessment]]
- [[50_LABS/Java/JAVA-LTS-B01/README]]
- [[98_SOURCES/Java 11 17 21 Official Sources]]
