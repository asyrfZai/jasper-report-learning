# Setup

## Requirements

- **JDK 17** (Spring Boot 3.3.2 needs Java 17+; Java 11 won't work).
- Nothing else — the Maven Wrapper (`./mvnw`) and an in-memory H2 database are
  included. First run downloads dependencies into `~/.m2`.

Check Java, and point `JAVA_HOME` at 17 if you have several JDKs:

```bash
java -version                                   # must be 17.x
export JAVA_HOME=$(/usr/libexec/java_home -v 17)   # macOS
```

## Run

```bash
./mvnw spring-boot:run          # then open http://localhost:8080
```

Build a jar instead:

```bash
./mvnw clean package
java -jar target/jasper-report-learning.jar
```

Change the port if 8080 is busy:

```bash
java -jar target/jasper-report-learning.jar --server.port=8899
```

## Good to know

- Reports compile from `.jrxml` at runtime and are cached, so **restart** the app
  to pick up template edits.
- H2 console: http://localhost:8080/h2-console (JDBC `jdbc:h2:mem:jasperdb`,
  user `sa`, no password). Data resets on every restart.
- **XLSX/DOCX export needs `poi-ooxml` (5.2.2), already in `pom.xml`.** There is no
  `jasperreports-poi` artifact in JasperReports 6.x — the OOXML exporters are in
  the core jar but its POI dependency is optional, so it's added explicitly.

## Move to another machine

1. Copy the whole folder (it includes `mvnw` / `.mvn/`).
2. Install JDK 17, set `JAVA_HOME`.
3. `./mvnw spring-boot:run`.
