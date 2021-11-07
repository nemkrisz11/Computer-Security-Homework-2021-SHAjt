# Points to the root of Google Test, relative to where this file is.
# Remember to tweak this if you move this file.
GTEST_DIR = googletest/

SRC_DIR = src/
TEST_DIR = test/
HEADER_DIR = include/
BUILD_DIR=build/

CPPFLAGS += -isystem $(GTEST_DIR)/include

CXXFLAGS += -g -Wall -Werror -pthread -Wextra -std=c++17
COVFLAGS = --coverage

TESTS = ParserTest

GTEST_HEADERS = $(GTEST_DIR)/include/gtest/*.h \
                $(GTEST_DIR)/include/gtest/internal/*.h

GTEST_SRCS_ = $(GTEST_DIR)/src/*.cc $(GTEST_DIR)/src/*.h $(GTEST_HEADERS)

CXX = g++

.PHONY: build clean test

all: BuildMain

lib: BuildLib

BuildLib: $(BUILD_DIR)CaffParser.o $(BUILD_DIR)CiffParser.o
	$(CXX) $(CXXFLAGS) $(COVFLAGS) -shared -fPIC $^ -o libCaffParser.so

BuildMain: $(BUILD_DIR)Main.o $(BUILD_DIR)CaffParser.o $(BUILD_DIR)CiffParser.o
	$(CXX) $(CXXFLAGS) $(COVFLAGS) $^ -o CaffParser
	
$(BUILD_DIR)Main.o: $(SRC_DIR)Main.cpp
	$(CXX) $(CXXFLAGS) $(COVFLAGS) -c -fPIC $(SRC_DIR)Main.cpp -o $@

$(BUILD_DIR)CaffParser.o: $(SRC_DIR)CaffParser.cpp $(HEADER_DIR)CaffParser.hpp
	$(CXX) $(CXXFLAGS) $(COVFLAGS) -c -fPIC $(SRC_DIR)CaffParser.cpp -o $@

$(BUILD_DIR)CiffParser.o: $(SRC_DIR)CiffParser.cpp $(HEADER_DIR)CiffParser.hpp
	$(CXX) $(CXXFLAGS) $(COVFLAGS) -c -fPIC $(SRC_DIR)CiffParser.cpp -o $@

$(BUILD_DIR)ParserTest.o: $(TEST_DIR)ParserTest.cc $(HEADER_DIR)CaffParser.hpp $(HEADER_DIR)CiffParser.hpp $(GTEST_HEADERS)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $(COVFLAGS) -c $(TEST_DIR)ParserTest.cc -o $@

ParserTest: $(BUILD_DIR)CaffParser.o $(BUILD_DIR)CiffParser.o $(BUILD_DIR)ParserTest.o $(BUILD_DIR)gtest_main.a
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) $(COVFLAGS) -lpthread $^ -o $@

test: $(TESTS)

build:
	mkdir -p $(BUILD_DIR)

clean:
	rm -f *.o *.so *.gcno output $(HEADER_DIR)*.gch $(TESTS)
	rm -r $(BUILD_DIR)

# google test objects
$(BUILD_DIR)gtest-all.o : $(GTEST_SRCS_)
	$(CXX) $(CPPFLAGS) -I$(GTEST_DIR) $(CXXFLAGS) -c \
            $(GTEST_DIR)/src/gtest-all.cc -o $@

$(BUILD_DIR)gtest_main.o : $(GTEST_SRCS_)
	$(CXX) $(CPPFLAGS) -I$(GTEST_DIR) $(CXXFLAGS) -c \
            $(GTEST_DIR)/src/gtest_main.cc -o $@

$(BUILD_DIR)gtest.a : $(BUILD_DIR)gtest-all.o
	$(AR) $(ARFLAGS) $@ $^ -o $@

$(BUILD_DIR)gtest_main.a : $(BUILD_DIR)gtest-all.o $(BUILD_DIR)gtest_main.o
	$(AR) $(ARFLAGS) $@ $^ -o $@
