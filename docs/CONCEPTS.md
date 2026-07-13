# JasperReports Concept Glossary

Each concept below links to the example that introduces it in depth - this
file is a compact reference, not a replacement for reading the `.jrxml`
comments themselves.

## The report lifecycle

A `.jrxml` file is never executed directly. It goes through three stages,
implemented once in this project by `com.jasperlearning.report.*`:

1. **Design -> Compile**: `.jrxml` (XML) is parsed into a `JasperDesign`, then
   compiled into a `JasperReport` - a real, loadable object whose expressions
   have been turned into actual bytecode. This is the expensive step; cache it.
2. **Compile + Data -> Fill**: a `JasperReport` is combined with report
   parameters and a data source to produce a `JasperPrint` - a fully paginated,
   laid-out document, independent of any output format.
3. **Fill -> Export**: the same `JasperPrint` is handed to an exporter (PDF,
   XLSX, DOCX, HTML, CSV, ...) to produce final bytes. One fill, many exports.

## Parameters ($P{...})

Values supplied by the CALLER (your Java code) at fill time, one value per
report execution - "which company", "report title", "a live JDBC connection".
Declared with `<parameter name="..." class="...">`, optionally with a
`defaultValueExpression`. See `reports/beginner/03_parameters_greeting.jrxml`.

## Fields ($F{...})

Values supplied by the DATA SOURCE, one value PER ROW. For a JavaBean data
source, field names must match getter names; for SQL, they must match result
column aliases; for a Map data source, they must match Map keys. See
`reports/basic/01_javabean_datasource.jrxml` and `02_sql_datasource.jrxml`.

## Variables ($V{...})

Values the ENGINE computes/accumulates as rows are processed - running sums,
counts, averages - via a declarative `calculation` attribute, or hand-rolled
via `resetType="None"` and a `variableExpression` that does its own math.
`resetType` controls WHEN the accumulator resets: `Report` (never), `Page`
(each page), `Group` (each group break), `None` (never, and ignores
calculation). See `reports/basic/05_variables_expressions.jrxml`.

## Bands

The fundamental layout unit - a horizontal strip of the page. JasperReports
stacks bands vertically: `title` -> `pageHeader` -> `columnHeader` -> (once
per data row) `detail` -> `columnFooter` -> `pageFooter` -> `summary`, plus
`lastPageFooter` (final page only) and `noData` (zero rows). Reports are NOT
laid out via absolute page coordinates - everything is relative to bands. See
`reports/beginner/01_hello_world.jrxml` (simplest possible band usage) and
`reports/intermediate/02_headers_footers_multipage.jrxml` (the full band set).

## Groups

A `<group><groupExpression>` watches for its expression's value CHANGING
between consecutive rows and prints a `groupHeader`/`groupFooter` around each
run of matching rows - the mechanism behind "one subtotal block per
department" style reports. Requires the underlying data to already be sorted
by the group expression (see `<sortField>`). See
`reports/intermediate/01_grouping_sorting.jrxml`.

## Datasets & SubDatasets

A `<subDataset>` is a named, independent field/variable scope declared at the
report level, referenced by name from a `<datasetRun>` inside a Table, List,
Chart, or Crosstab component - letting that component consume a DIFFERENT
data source than the main report. The same subDataset can be referenced from
arbitrarily deep nesting (a list inside a list inside a list). See
`reports/advanced/02_nested_datasets_multilevel.jrxml`.

## Subreports

An entirely separate, independently-compiled `.jrxml`/`JasperReport` embedded
inside a `<subreport>` element. Receives its OWN data via
`dataSourceExpression` (a Java object, e.g. a nested List field) or
`connectionExpression` (a raw JDBC connection, letting it run its own SQL),
plus optional `<subreportParameter>` values forwarded explicitly from the
parent. Subreports can nest arbitrarily deep. See
`reports/intermediate/05_subreport_master_detail.jrxml` (data hand-off),
`06_subreport_parameter_passing.jrxml` (parameter hand-off), and
`reports/advanced/01_nested_subreports.jrxml` (3 levels deep).

## Data Sources

How rows actually reach the engine:
- `JRBeanCollectionDataSource` - wraps a `List` of JavaBeans/DTOs (most common).
- A live `java.sql.Connection` - JasperReports runs the template's own
  `<queryString>` against it directly.
- `JRMapCollectionDataSource` - wraps a `List<Map<String,Object>>`.
- A hand-written `JRDataSource` implementation - the raw two-method interface
  (`next()` / `getFieldValue()`) for data that isn't already list-shaped.
See `reports/advanced/09_collections_maps_custom_datasource.jrxml`.

## Expressions

Java expression snippets embedded throughout a `.jrxml` (`textFieldExpression`,
`variableExpression`, `groupExpression`, `printWhenExpression`, ...) - real
Java, evaluated at fill time, with `$F{}`/`$P{}`/`$V{}` as syntactic sugar for
field/parameter/variable lookups.

## Components

Pluggable, dedicated element types beyond the basic band/staticText/textField
set - all part of the core `jasperreports` artifact, no extra dependency
needed:
- **Table** (`jr:table`) - a genuine column/row grid with per-column
  headers/footers, distinct from a band that merely LOOKS like a table.
- **List** (`jr:list`) - repeats an arbitrary free-form block per row (cards,
  stacked mini-rows), vertically or horizontally.
- **Chart** (`barChart`, `pieChart`, ...) - JFreeChart-backed, aggregates its
  own dataset the same way a `Sum` variable would, just bucketed by category.
- **Crosstab** (`crosstab`) - a pivot table: row bucket x column bucket ->
  aggregated measure per cell.
See `reports/basic/03_table_component.jrxml`, `04_list_component.jrxml`,
`reports/intermediate/04_charts.jrxml`, `reports/advanced/05_crosstab.jrxml`.
