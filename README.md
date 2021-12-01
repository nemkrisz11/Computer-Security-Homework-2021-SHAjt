## Backend

### How to build
Simply execute `docker-compose up` to launch the backend application containers.

The API should be accessible on `https://localhost:8080` by default.

To stop the containers, use `docker-compose down`, or `docked-compose down -v --rmi local`, if you wish to remove all residual artifacts.

## CAFF Parser

### How to build
**Important**: The parser is built and installed by the flask container when the backend is launched.
This part is for the separate, local testing (f.e. fuzzing) of the CAFF parser, not for deployment.

In the `Backend/parser` directory:
- `make build all`: Build as an executable
- `make build lib`: Build as a shared library
- `make build test`: Build project and all tests
- `make clean`: Delete artifacts

### Generating test coverage reports
In the `Backend/parser` directory, run `generate_coverage_report.sh`.

Requires `gcov` and `lcov` to be installed.

The html output can be viewed in the `Backend/parser/report` directory.

### Notes
- We believe that enforcing a file size limit on CAFF files is not the role of the parser. Oversized CAFF files (f.e. >50MB) should be discarded by the backend way before parsing them is considered.
- Fuzzing was performed using AFL (morphing `mini.caff`), and all unique errors were investigated. Additional fuzzing may be required in the future, using a different base file.
