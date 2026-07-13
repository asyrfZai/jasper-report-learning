# JasperReports Learning Project

Learn JasperReports from beginner to advanced in a running Spring Boot app.
Each example is a REST endpoint that renders a `.jrxml` and exports it to
PDF / XLSX / DOCX / HTML / CSV.

## Run

Needs **JDK 17**. Maven Wrapper is included.

```bash
./mvnw spring-boot:run
```

Open **http://localhost:8080** for the catalog of all reports. Uses in-memory
H2 — nothing to install. Details: [SETUP.md](SETUP.md).

## What's inside

- **Beginner** (`reports/beginner`) — static text, images, parameters, formatting
- **Basic** (`reports/basic`) — JavaBean & SQL sources, tables/lists, variables, conditional styles
- **Intermediate** (`reports/intermediate`) — grouping, headers/footers, charts, subreports
- **Advanced** (`reports/advanced`) — nested subreports & datasets, crosstabs, master-detail, multiple data sources

Work through the tiers in order; each `.jrxml` has explanatory comments.

## How it works

One class — `report/JasperReportService` — does it all: **compile** `.jrxml` →
**fill** with data → **export** to any format.

## Docs

[SETUP.md](SETUP.md) · [CONCEPTS](docs/CONCEPTS.md) (glossary of JasperReports concepts)
