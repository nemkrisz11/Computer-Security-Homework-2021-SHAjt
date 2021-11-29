make build test &&\
./ParserTest &&\
cd build &&\
lcov -c --directory . --output-file main_coverage.info &&\
lcov --remove main_coverage.info -o main_coverage_relevant_only.info '/usr/include/c++/*' '*/googletest/*' &&\
genhtml main_coverage_relevant_only.info --output-directory ../report &&\
cp main_coverage_relevant_only.info ../report/ &&\
cd ../ &&\
make clean
