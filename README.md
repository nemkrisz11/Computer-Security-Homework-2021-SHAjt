## How to build
In the `Parser` directory:
- `make build all`: Build as an executable
- `make build lib`: Build as a shared library
- `make build test`: Build project and all tests

# Generating test coverage reports
In the `Parser` directory, run `generate_coverage_report.sh`.
Requires `gcov` and `lcov` to be installed.
The html output can be viewed in the `Parser/report` directory.
