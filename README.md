```
_._     _,-'""`-._
(,-.`._,'(       |\`-/|
    `-.-' \ )-`( , o o)
          `-    \`_`"'-
                                                  ___
                             (O)                 __/_  `.  .-"""-.
                                                 \_,` | \-'  /   )`-')
                                                  "") `"`    \  ((`"`
                                                 ___Y  ,    .'7 /|
                                                (_,___/...-` (_/_/ 
,,\|//,.\\|//,,\|//,.\\|//,,\|//,.\\|//,,\|//,.\\|//,,\|//,.\\|//,,\|//
```
# Web Crawler

A Java web crawler that mirrors a single-domain website by recursively
downloading HTML pages, images, stylesheets, and scripts. Built for the
JDK 11 documentation at `https://docs.muzoo.io/`.

## Build and run

Requires Java 21 and Maven.

    mvn clean package
    mvn exec:java

The mirrored site is written to `./docs/`. To clean and re-run:

    rm -rf docs/
    mvn exec:java

Progress is printed to stdout in the format:

    > 18.5% (1,023/20,004 urls are downloaded) - {current url}

URLs that fail (HTTP 4xx/5xx, timeouts, network errors) are skipped and
logged to stderr with a `!` prefix.

## Tests

    mvn test

All tests are unit tests with no network dependencies. `FakeDownloader`
serves canned responses; `@TempDir` provides isolated filesystem
sandboxes.

## Project structure

All classes live in `io.muzoo.ssc`:

| Class                     | Responsibility                                          |
|---------------------------|---------------------------------------------------------|
| `Main`                    | Composition root: wires dependencies, starts the crawl  |
| `WebCrawler`              | BFS orchestration: queue, visited set, control flow     |
| `Downloader` (interface)  | Abstraction over URL fetching                           |
| `HttpDownloader`          | Apache HttpComponents 5 implementation                  |
| `DownloadResult`          | Bytes + Content-Type returned from a download           |
| `DownloadException`       | Recoverable per-URL download failure                    |
| `LinkExtractor` (interface) | Abstraction over HTML link extraction                 |
| `HtmlLinkExtractor`       | jsoup-based implementation                              |
| `UrlNormalizer`           | Resolves relatives, strips `?` and `#`, filters domain  |
| `UrlToPathMapper`         | Maps URLs to disk paths per Hints #1 and #2             |
| `FileSaver`               | Writes bytes to disk, creating parent directories       |
| `ProgressReporter` (interface) | Abstraction over progress output                   |
| `ConsoleProgressReporter` | Prints in the assignment's required format             |


## Known trade-offs

- `https://docs.muzoo.io/foo/` and `https://docs.muzoo.io/foo/index.html`
  are treated as distinct in the visited set, even though the server
  serves the same content for both. This causes a small amount of
  redundant downloading. Fixable by a one-line
  canonicalization in `UrlNormalizer` if needed.
- Output directory is hardcoded to `docs/`. Easily configurable
  via a CLI argument (`UrlToPathMapper` already accepts an arbitrary
  output root via constructor).
