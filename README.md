## How to build
In the `Parser` directory:
- `make build all`: Build as an executable
- `make build lib`: Build as a shared library
- `make build test`: Build project and all tests
- `make clean`: Delete artifacts

# Generating test coverage reports
In the `Parser` directory, run `generate_coverage_report.sh`.

Requires `gcov` and `lcov` to be installed.

The html output can be viewed in the `Parser/report` directory.

# Notes
- We believe that enforcing a file size limit on CAFF files is not the role of the parser. Oversized CAFF files (f.e. >50MB) should be discarded by the backend way before parsing them is considered.
- Fuzzing was performed using AFL (morphing `mini.caff`), and all unique errors were investigated. Additional fuzzing may be required in the future, using a different base file.
