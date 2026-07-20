# Java Backend Knowledge System

Структурированная база знаний для Obsidian по Java backend-разработке.

## Цели

- вспомнить тему за 30 секунд перед собеседованием;
- изучить концепцию достаточно глубоко, чтобы объяснить внутреннее устройство и trade-offs;
- готовиться к сертификационным экзаменам Java и Spring;
- тренировать interview questions, code-output questions и production scenarios;
- связать Java, Spring, базы данных, очереди, распределённые системы, тестирование и DevOps в едином графе знаний.

## С чего начать

Откройте [[00_HOME/Java Backend Knowledge System]].

Визуальная точка входа: [[01_MAPS/Java Backend Map.canvas]].

## Модель знаний

```text
Concept
   ├── Interview questions
   ├── Certification questions
   ├── Production cases
   ├── Comparisons
   └── Labs
```

Концепция описывается один раз. Вопросы, тесты и кейсы ссылаются на неё, а не копируют теорию.

## Основные карты

- [[01_MAPS/Java Map]]
- [[01_MAPS/Spring Map]]
- [[01_MAPS/Databases Map]]
- [[01_MAPS/Messaging Map]]
- [[01_MAPS/Distributed Systems Map]]
- [[20_QUESTIONS/Interview/Interview Questions MOC]]
- [[30_CERTIFICATIONS/Certification MOC]]

## Структура репозитория

```text
00_HOME/              точки входа и dashboards
01_MAPS/              Canvas и текстовые карты знаний
10_CONCEPTS/          канонические заметки по концепциям
20_QUESTIONS/         интервью, сертификация, code-output и troubleshooting
30_CERTIFICATIONS/    exam objectives и маршруты подготовки
40_PRODUCTION_CASES/  реальные инженерные ситуации
50_LABS/              запускаемые примеры
60_BASES/             динамические представления Obsidian Bases
90_TEMPLATES/         шаблоны заметок
99_ATTACHMENTS/       изображения и вложения
```

## Открытие в Obsidian

1. Клонировать репозиторий.
2. В Obsidian выбрать **Open folder as vault**.
3. Включить core plugins: Canvas, Backlinks, Graph view, Properties view, Templates и Bases.
4. Указать каталог шаблонов `90_TEMPLATES`.
5. Включить автоматическое обновление внутренних ссылок при переименовании файлов.

На первом этапе обязательные community plugins не используются. База должна оставаться переносимым набором Markdown-файлов.

## Языковая стратегия

- названия Java/Spring API и основные технические термины сохраняются на английском;
- объяснения пишутся на русском;
- экзаменационные формулировки могут дублироваться на английском;
- aliases используются для альтернативных русских и английских названий.

## Шкала уверенности

| Значение | Состояние |
|---:|---|
| 0 | тема не изучена |
| 1 | узнаю термин |
| 2 | понимаю с подсказкой |
| 3 | могу объяснить самостоятельно |
| 4 | могу решить практический кейс |
| 5 | могу защитить решение на Senior-интервью |

## Типы заметок

- `concept`
- `interview-question`
- `certification-question`
- `production-case`
- `comparison`
- `lab`
- `moc`

## Правила именования

- Одна концепция — одна каноническая заметка.
- Не создавать копии `ThreadLocal Java 8.md` и `ThreadLocal Java 21.md`.
- Версии Java и Spring хранить в properties.
- Использовать точные названия: `Transaction Propagation`, а не `Transactions 2`.
- Название вопроса должно быть сформулировано как вопрос.
- Production case называется по наблюдаемой проблеме.

## Текущий фундамент

Первая версия содержит карты Java, Spring, databases, messaging и distributed systems, маршруты интервью и сертификации, шаблоны и полный вертикальный пример по `ThreadLocal`.
