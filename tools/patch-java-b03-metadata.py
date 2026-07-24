#!/usr/bin/env python3
from __future__ import annotations

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
HUB = "10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns.md"
ROADMAP = "30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap.md"
DRILLS = "30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills.md"
LAB = "50_LABS/Java/JAVA-B03/README.md"
RUN = "50_LABS/Java/JAVA-B03/run.sh"
SHARED_LABS = [
    "50_LABS/Java/JAVA-B03/src/shared/java/lab/b03/InitializationProof.java",
    "50_LABS/Java/JAVA-B03/src/shared/java/lab/b03/ObjectModelProof.java",
    "50_LABS/Java/JAVA-B03/src/shared/java/lab/b03/RecordsSealedProof.java",
]
JAVA21_LAB = "50_LABS/Java/JAVA-B03/src/java21/java/lab/b03/RecordPatternProof.java"
CARD_PREFIXES = [
    "JAVA-OBJECT-B03-C",
    "JAVA-INIT-B03-C",
    "JAVA-INHERIT-B03-C",
    "JAVA-TYPES-B03-C",
]
ATOMIC_NOTES = [
    "10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle.md",
    "10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes.md",
    "10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order.md",
    "10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection.md",
    "10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var.md",
    "10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism.md",
    "10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces.md",
    "10_CONCEPTS/Java/Object Model/Java Records.md",
    "10_CONCEPTS/Java/Object Model/Java Enums.md",
    "10_CONCEPTS/Java/Object Model/Java Sealed Types.md",
    "10_CONCEPTS/Java/Object Model/Java Record Patterns.md",
    "10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness.md",
]


def load_json(path: str):
    return json.loads((ROOT / path).read_text(encoding="utf-8"))


def write_json(path: str, value) -> None:
    (ROOT / path).write_text(json.dumps(value, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    if new in text:
        return text
    if old not in text:
        raise RuntimeError(f"Missing replacement anchor for {label}")
    return text.replace(old, new, 1)


def update_text(path: str, mutator) -> None:
    file = ROOT / path
    before = file.read_text(encoding="utf-8")
    after = mutator(before)
    if after != before:
        file.write_text(after, encoding="utf-8")


def evidence(source: str, prefixes: list[str], include_java21: bool = False) -> dict:
    labs = [LAB, RUN, *SHARED_LABS]
    if include_java21:
        labs.append(JAVA21_LAB)
    return {
        "canonical": [HUB],
        "card_prefixes": prefixes,
        "cases": [DRILLS],
        "labs": labs,
        "roadmaps": [ROADMAP],
        "sources": [source],
    }


def patch_manifests() -> None:
    j17_path = ".github/objectives/java-1Z0-829.json"
    j17 = load_json(j17_path)
    coverage = j17["coverage"]
    coverage["mapped_objectives"] = 4
    coverage["lab_proven_objectives"] = 3
    routes = coverage.setdefault("implemented_routes", [])
    if "JAVA-B03" not in routes:
        routes.append("JAVA-B03")
    for objective in j17["objectives"]:
        if objective["id"] == "JAVA-B03":
            objective["status"] = "lab-proven"
            objective["mapping_granularity"] = "route"
            objective["evidence"] = evidence("98_SOURCES/Java SE 17 1Z0-829 Sources.md", CARD_PREFIXES)
    write_json(j17_path, j17)

    j21_path = ".github/objectives/java-1Z0-830.json"
    j21 = load_json(j21_path)
    coverage = j21["coverage"]
    coverage["mapped_objectives"] = 13
    coverage["fully_implemented_objectives"] = 11
    coverage["latest_complete_route"] = "JAVA-B03"
    routes = coverage.setdefault("implemented_routes", [])
    for route in ["JAVA-B01", "JAVA-B02", "JAVA-B03"]:
        if route not in routes:
            routes.append(route)
    coverage["traceability_status"] = "JAVA-B01, JAVA-B02 and JAVA-B03 are lab-proven with version-bound compile/runtime evidence"

    prefixes = {
        "JAVA21-3.1": ["JAVA-OBJECT-B03-C"],
        "JAVA21-3.2": ["JAVA-INIT-B03-C", "JAVA-TYPES-B03-C"],
        "JAVA21-3.3": ["JAVA-INIT-B03-C"],
        "JAVA21-3.4": ["JAVA-INIT-B03-C"],
        "JAVA21-3.5": ["JAVA-INHERIT-B03-C", "JAVA-TYPES-B03-C"],
        "JAVA21-3.6": ["JAVA-INHERIT-B03-C"],
        "JAVA21-3.7": ["JAVA-TYPES-B03-C"],
    }
    for objective in j21["objectives"]:
        if objective["id"] in prefixes:
            objective["status"] = "lab-proven"
            objective["mapping_granularity"] = "objective"
            objective["evidence"] = evidence(
                "98_SOURCES/Java SE 21 1Z0-830 Sources.md",
                prefixes[objective["id"]],
                include_java21=objective["id"] == "JAVA21-3.5",
            )
    write_json(j21_path, j21)


def patch_route_registry() -> None:
    path = ".github/knowledge-routes.json"
    registry = load_json(path)
    route = {
        "id": "JAVA-B03",
        "status": "published",
        "hub": ROADMAP,
        "entry_points": [
            "README.md",
            "00_HOME/Java Learning Dashboard.md",
            "00_HOME/Knowledge Route Registry.md",
        ],
        "indexes": [
            ROADMAP,
            HUB,
            "00_HOME/Knowledge Route Registry.md",
            "01_MAPS/Java Object Model and Record Patterns Map.canvas",
        ],
        "artifacts": [
            HUB,
            *ATOMIC_NOTES,
            "01_MAPS/Java Object Model and Record Patterns Map.canvas",
            "30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03A Object Lifecycle Cards.md",
            "30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03B Initialization and Scope Cards.md",
            "30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03C Inheritance and Interfaces Cards.md",
            "30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03D Records Sealed and Patterns Cards.md",
            DRILLS,
            LAB,
            "98_SOURCES/Java SE 17 1Z0-829 Sources.md",
            "98_SOURCES/Java SE 21 1Z0-830 Sources.md",
        ],
    }
    registry["routes"] = [item for item in registry["routes"] if item["id"] != "JAVA-B03"]
    insert_at = next((i for i, item in enumerate(registry["routes"]) if item["id"].startswith("SPRING-")), len(registry["routes"]))
    registry["routes"].insert(insert_at, route)
    write_json(path, registry)


def patch_readme(text: str) -> str:
    section = """### JAVA-B03 — Object Model, Records and Record Patterns

```text
12 atomic concepts
115 base cards
35 drills
4 positive proof classes
17 expected compile failures
JDK 17 and 21 proof lanes
status: lab-proven
```

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[01_MAPS/Java Object Model and Record Patterns Map.canvas]]

"""
    text = replace_once(text, "### Navigation inventory\n", section + "### Navigation inventory\n", "README B03 section")
    text = text.replace("canonical route hubs             2", "canonical route hubs             3")
    text = text.replace("atomic Java concept notes       17", "atomic Java concept notes       29")
    text = text.replace("machine-registered Java routes   2", "machine-registered Java routes   3")
    text = replace_once(
        text,
        "JAVA-B03 — Object Model, Records, Sealed Types and Record Patterns",
        "JAVA-B05 — Collections, Generics and Sequenced Collections",
        "README next route",
    )
    text = text.replace("B03 should be implemented in a separate route PR after this foundation and navigation slice is reviewed.\n\n", "")
    text = text.replace("Dedicated Java B01 and B02 workflows pass", "Dedicated Java B01, B02 and B03 workflows pass")
    return text


def patch_dashboard(text: str) -> str:
    text = text.replace("current_next_route: JAVA-B03", "current_next_route: JAVA-B05")
    text = text.replace("  - JAVA-B02\nlab_proven_routes:", "  - JAVA-B02\n  - JAVA-B03\nlab_proven_routes:")
    text = text.replace("  - JAVA-B02\npublished_base_cards:", "  - JAVA-B02\n  - JAVA-B03\npublished_base_cards:")
    text = text.replace("published_base_cards: 135", "published_base_cards: 250")
    text = text.replace("published_drills: 35", "published_drills: 70")
    text = replace_once(
        text,
        "| `next` | JAVA-B03 — Object Model, Records and Record Patterns | planned next PR |",
        "| `lab-proven` | JAVA-B03 — Object Model, Records and Record Patterns | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]] |\n| `next` | JAVA-B05 — Collections, Generics and Sequenced Collections | planned next route |",
        "dashboard continue table",
    )
    text = replace_once(
        text,
        "- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch|JAVA-B02 canonical hub]]",
        "- [[10_CONCEPTS/Java/Core/Java Control Flow and Pattern Switch|JAVA-B02 canonical hub]]\n- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns|JAVA-B03 canonical hub]]",
        "dashboard hubs",
    )
    text = replace_once(
        text,
        "- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills|B02 — 20 drills]]",
        "- [[30_CERTIFICATIONS/Java/JAVA-B02/JAVA-B02 Drills|B02 — 20 drills]]\n- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills|B03 — 35 drills]]",
        "dashboard drills",
    )
    text = replace_once(
        text,
        "- [[50_LABS/Java/JAVA-B02/README|B02 positive and expected-failure proof]]",
        "- [[50_LABS/Java/JAVA-B02/README|B02 positive and expected-failure proof]]\n- [[50_LABS/Java/JAVA-B03/README|B03 object-model and record-pattern proof]]",
        "dashboard labs",
    )
    b03_map = """## JAVA-B03 concept map

| Order | Concept | Practice |
|---:|---|---|
| 1 | [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]] | `JAVA-OBJECT-B03` |
| 2 | [[10_CONCEPTS/Java/Object Model/Java Nested Local and Anonymous Classes]] | `JAVA-OBJECT-B03` |
| 3 | [[10_CONCEPTS/Java/Object Model/Java Fields Initializers and Constructor Order]] | `JAVA-INIT-B03` |
| 4 | [[10_CONCEPTS/Java/Object Model/Java Overloading Varargs and Method Selection]] | `JAVA-INIT-B03` |
| 5 | [[10_CONCEPTS/Java/Object Model/Java Scope Encapsulation Immutability and var]] | `JAVA-INIT-B03` |
| 6 | [[10_CONCEPTS/Java/Object Model/Java Inheritance Overriding Hiding and Polymorphism]] | `JAVA-INHERIT-B03` |
| 7 | [[10_CONCEPTS/Java/Object Model/Java Abstract Classes and Interfaces]] | `JAVA-INHERIT-B03` |
| 8 | [[10_CONCEPTS/Java/Object Model/Java Records]] | `JAVA-TYPES-B03` |
| 9 | [[10_CONCEPTS/Java/Object Model/Java Enums]] | `JAVA-TYPES-B03` |
| 10 | [[10_CONCEPTS/Java/Object Model/Java Sealed Types]] | `JAVA-TYPES-B03` |
| 11 | [[10_CONCEPTS/Java/Object Model/Java Record Patterns]] | `JAVA-TYPES-B03` |
| 12 | [[10_CONCEPTS/Java/Object Model/Java Nested Patterns and Exhaustiveness]] | `JAVA-TYPES-B03` |

"""
    text = replace_once(text, "## Current delivered inventory\n", b03_map + "## Current delivered inventory\n", "dashboard B03 map")
    old = """lab-proven Java exam routes       2
atomic concept notes             17
base cards                       135
drills                            35
positive proof classes             5
B02 expected compile failures     11
JDK lanes                      17, 21"""
    new = """lab-proven Java exam routes       3
atomic concept notes             29
base cards                       250
drills                            70
positive proof classes             9
expected compile failures         28
JDK lanes                      17, 21"""
    return replace_once(text, old, new, "dashboard inventory")


def patch_registry(text: str) -> str:
    text = text.replace("B01/B02 published", "B01/B02/B03 published")
    section = """### JAVA-B03 — Object Model, Records and Record Patterns

| Role | Artifact |
|---|---|
| Roadmap | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]] |
| Canonical hub | [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]] |
| Atomic concepts | 12 linked notes |
| Canvas | [[01_MAPS/Java Object Model and Record Patterns Map.canvas]] |
| Cards | 115 across lifecycle, initialization, inheritance and type batches |
| Drills | [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Drills|35 drills]] |
| Lab | [[50_LABS/Java/JAVA-B03/README]] |
| Negative evidence | 17 expected compile failures |
| Sources | [[98_SOURCES/Java SE 17 1Z0-829 Sources]], [[98_SOURCES/Java SE 21 1Z0-830 Sources]] |

Status: `lab-proven`, JDK 17/21 PASS.

"""
    text = replace_once(text, "### Current Java inventory\n", section + "### Current Java inventory\n", "registry B03 section")
    old = """published exam routes            2
atomic concept notes            17
base cards                      135
drills                           35
positive proof classes            5
expected compile-fail cases      11"""
    new = """published exam routes            3
atomic concept notes            29
base cards                      250
drills                           70
positive proof classes            9
expected compile-fail cases      28"""
    text = replace_once(text, old, new, "registry inventory")
    old_rows = """| 1 | `JAVA-B03` — Object Model, Records, Sealed Types, Record Patterns | next |
| 2 | `JAVA-B05` — Collections, Generics, Sequenced Collections | planned |
| 3 | `JAVA-B06` — Lambdas and Streams | planned |
| 4 | `JAVA-B04` — Exceptions and Try-with-resources | planned |
| 5 | `JAVA-B07` — Modules and Deployment | planned |
| 6 | `JAVA-B08` — Concurrency and Virtual Threads | planned |
| 7 | `JAVA-B09` — I/O, NIO.2 and Serialization | planned |
| 8 | `JAVA-B10` — JDBC for 1Z0-829 | planned |
| 9 | `JAVA-B11` — Localization | planned |
| 10 | `JAVA-SUP-B01` — Logging, Annotations and supplementary Generics | planned |"""
    new_rows = """| 1 | `JAVA-B05` — Collections, Generics, Sequenced Collections | next |
| 2 | `JAVA-B06` — Lambdas and Streams | planned |
| 3 | `JAVA-B04` — Exceptions and Try-with-resources | planned |
| 4 | `JAVA-B07` — Modules and Deployment | planned |
| 5 | `JAVA-B08` — Concurrency and Virtual Threads | planned |
| 6 | `JAVA-B09` — I/O, NIO.2 and Serialization | planned |
| 7 | `JAVA-B10` — JDBC for 1Z0-829 | planned |
| 8 | `JAVA-B11` — Localization | planned |
| 9 | `JAVA-SUP-B01` — Logging, Annotations and supplementary Generics | planned |"""
    return replace_once(text, old_rows, new_rows, "registry route sequence")


def patch_moc(text: str) -> str:
    text = text.replace("B01 and B02 lab-proven", "B01, B02 and B03 lab-proven")
    text = text.replace("B01 and B02 published", "B01, B02 and B03 published")
    section = """## JAVA-B03 — Object Model, Records and Record Patterns

Status: **lab-proven**.

```text
12 atomic concepts
115 base cards
35 drills
4 positive proof classes
17 expected compile failures
JDK 17 / JDK 21 PASS
```

- [[30_CERTIFICATIONS/Java/JAVA-B03/JAVA-B03 Roadmap]]
- [[10_CONCEPTS/Java/Object Model/Java Object Model Records and Record Patterns]]
- [[50_LABS/Java/JAVA-B03/README]]
- [[01_MAPS/Java Object Model and Record Patterns Map.canvas]]

Atomic start: [[10_CONCEPTS/Java/Object Model/Java Object Creation Reachability and Lifecycle]].

"""
    text = replace_once(text, "## Java route sequence\n", section + "## Java route sequence\n", "MOC B03 section")
    text = text.replace("| 3 | JAVA-B03 Object Model | records/sealed | record patterns | next |", "| 3 | JAVA-B03 Object Model | records/sealed | record patterns | lab-proven |")
    text = text.replace("| 4 | JAVA-B05 Collections/Generics | included | sequenced collections | planned |", "| 4 | JAVA-B05 Collections/Generics | included | sequenced collections | next |")
    old = """published exam routes          2
atomic notes                  17
base cards                   135
drills                        35
positive proof classes         5
expected compile failures     11"""
    new = """published exam routes          3
atomic notes                  29
base cards                   250
drills                        70
positive proof classes         9
expected compile failures     28"""
    text = replace_once(text, old, new, "MOC inventory")
    return text.replace("Java B01/B02 route-specific checks pass", "Java B01/B02/B03 route-specific checks pass")


def patch_readiness(text: str) -> str:
    text = replace_once(
        text,
        "| JAVA-B02 | 8 | 60 | 20 | 2 positive classes + 11 negative cases, JDK 17/21 | lab-proven |",
        "| JAVA-B02 | 8 | 60 | 20 | 2 positive classes + 11 negative cases, JDK 17/21 | lab-proven |\n| JAVA-B03 | 12 | 115 | 35 | 4 positive classes + 17 negative cases, JDK 17/21 | lab-proven |",
        "readiness route table",
    )
    old = """lab-proven exam routes             2
atomic concepts                   17
base cards                       135
compile/output drills             35
positive proof classes             5
expected compile-fail cases       11
runtime baselines             17, 21"""
    new = """lab-proven exam routes             3
atomic concepts                   29
base cards                       250
compile/output drills             70
positive proof classes             9
expected compile-fail cases       28
runtime baselines             17, 21"""
    text = replace_once(text, old, new, "readiness inventory")
    text = replace_once(
        text,
        "JAVA21-2.1 program flow and final pattern switch",
        "JAVA21-2.1 program flow and final pattern switch\nJAVA21-3.1..3.7 object model, initialization, overloading, inheritance, interfaces, records, enums, sealed types and record patterns",
        "readiness Java21 objectives",
    )
    text = replace_once(
        text,
        "JAVA-B02 control flow                 lab-proven route evidence\nJAVA-B03 ... JAVA-B11                 planned or supporting evidence",
        "JAVA-B02 control flow                 lab-proven route evidence\nJAVA-B03 object model                  lab-proven route evidence\nJAVA-B04 ... JAVA-B11                 planned or supporting evidence",
        "readiness Java17 objectives",
    )
    text = text.replace("JAVA-B03  Object Model, Records, Sealed Types, Record Patterns\n", "")
    text = text.replace("visual deep dives and production cases for B01/B02", "visual deep dives and production cases for B01/B02/B03")
    text = text.replace("1. Use Java Learning Dashboard for B01/B02.", "1. Use Java Learning Dashboard for B01/B02/B03.")
    text = text.replace("3. Start JAVA-B03 in a separate route PR.", "3. Continue with JAVA-B05 in a separate route PR.")
    return text


def patch_program(text: str) -> str:
    text = text.replace("B1[JAVA-B01 complete] --> B2[JAVA-B02 complete]\n    B2 --> B3[JAVA-B03 next]\n    B3 --> B5[JAVA-B05]", "B1[JAVA-B01 complete] --> B2[JAVA-B02 complete]\n    B2 --> B3[JAVA-B03 complete]\n    B3 --> B5[JAVA-B05 next]")
    text = text.replace("JAVA-B03 — Object Model, Records, Sealed Types and Record Patterns", "JAVA-B05 — Collections, Generics and Sequenced Collections")
    text = text.replace("B03 may assume all B01 expression/text rules and B02 flow/switch rules.", "B05 may assume B01 expression rules, B02 flow rules and B03 object-model and generic-type foundations.")
    return text


def patch_canvas() -> None:
    path = "01_MAPS/Java Certification Routes.canvas"
    canvas = load_json(path)
    nodes = {node["id"]: node for node in canvas["nodes"]}
    nodes["b03"].pop("text", None)
    nodes["b03"].update({
        "type": "file",
        "file": ROADMAP,
        "width": 420,
        "height": 180,
        "color": "2",
    })
    extra_nodes = [
        {"id":"b03hub","type":"file","file":HUB,"x":670,"y":920,"width":450,"height":180,"color":"2"},
        {"id":"b03atomic","type":"text","text":"JAVA-B03 ATOMIC NOTES\n1 Lifecycle and reachability\n2 Nested/local/anonymous\n3 Initialization order\n4 Overloading and varargs\n5 Scope/encapsulation/var\n6 Inheritance/polymorphism\n7 Abstract/interface\n8 Records\n9 Enums\n10 Sealed types\n11 Record patterns\n12 Nested patterns/exhaustiveness","x":650,"y":1210,"width":500,"height":360,"color":"2"},
        {"id":"b05","type":"text","text":"NEXT ROUTE\nJAVA-B05\nCollections\nGenerics\nSequenced Collections","x":1220,"y":620,"width":360,"height":220,"color":"6"},
    ]
    for node in extra_nodes:
        if node["id"] not in nodes:
            canvas["nodes"].append(node)
    edges = {edge["id"] for edge in canvas["edges"]}
    extra_edges = [
        {"id":"e19","fromNode":"b03","fromSide":"bottom","toNode":"b03hub","toSide":"top"},
        {"id":"e20","fromNode":"b03hub","fromSide":"bottom","toNode":"b03atomic","toSide":"top"},
        {"id":"e21","fromNode":"b03atomic","fromSide":"right","toNode":"evidence","toSide":"left"},
        {"id":"e22","fromNode":"b03","fromSide":"right","toNode":"b05","toSide":"left"},
    ]
    for edge in extra_edges:
        if edge["id"] not in edges:
            canvas["edges"].append(edge)
    write_json(path, canvas)


def main() -> None:
    patch_manifests()
    patch_route_registry()
    update_text("README.md", patch_readme)
    update_text("00_HOME/Java Learning Dashboard.md", patch_dashboard)
    update_text("00_HOME/Knowledge Route Registry.md", patch_registry)
    update_text("30_CERTIFICATIONS/Certification MOC.md", patch_moc)
    update_text("00_HOME/Certification 99 Percent Readiness Dashboard.md", patch_readiness)
    update_text("00_HOME/Oracle Java 17 and 21 Certification Program.md", patch_program)
    patch_canvas()
    print("JAVA-B03 metadata and navigation synchronized")


if __name__ == "__main__":
    main()
