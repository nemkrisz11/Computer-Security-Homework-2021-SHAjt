# Points to the root of Google Test, relative to where this file is.
# Remember to tweak this if you move this file.
GTEST_DIR = googletest/

SRC_DIR = src/
TEST_DIR = test/
HEADER_DIR = include/

CPPFLAGS += -isystem $(GTEST_DIR)/include

CXXFLAGS += -g -Wall -Werror -pthread -Wextra -std=c++17 

TESTS = ParserTest

GTEST_HEADERS = $(GTEST_DIR)/include/gtest/*.h \
                $(GTEST_DIR)/include/gtest/internal/*.h

GTEST_SRCS_ = $(GTEST_DIR)/src/*.cc $(GTEST_DIR)/src/*.h $(GTEST_HEADERS)

CXX = g++


BuildLib: CaffParser.o CiffParser.o
	$(CXX) $(CXXFLAGS) -shared -fPIC $^ -o libCaffParser.so

BuildMain: Main.o CaffParser.o CiffParser.o
	$(CXX) $(CXXFLAGS) $^ -o output
	
Main.o: $(SRC_DIR)Main.cpp
	$(CXX) $(CXXFLAGS) -c -fPIC $(SRC_DIR)Main.cpp

CaffParser.o: $(SRC_DIR)CaffParser.cpp $(HEADER_DIR)CaffParser.hpp
	$(CXX) $(CXXFLAGS) -c -fPIC $(SRC_DIR)CaffParser.cpp

CiffParser.o: $(SRC_DIR)CiffParser.cpp $(HEADER_DIR)CiffParser.hpp
	$(CXX) $(CXXFLAGS) -c -fPIC $(SRC_DIR)CiffParser.cpp

ParserTest.o: $(TEST_DIR)ParserTest.cc $(HEADER_DIR)CaffParser.hpp $(GTEST_HEADERS)
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -c $(TEST_DIR)ParserTest.cc

ParserTest: CaffParser.o CiffParser.o ParserTest.o gtest_main.a
	$(CXX) $(CPPFLAGS) $(CXXFLAGS) -lpthread $^ -o $@

test: $(TESTS)

clean:
	rm -f *.o *.so output $(HEADER_DIR)*.gch $(TESTS)

# google test objects
gtest-all.o : $(GTEST_SRCS_)
	$(CXX) $(CPPFLAGS) -I$(GTEST_DIR) $(CXXFLAGS) -c \
            $(GTEST_DIR)/src/gtest-all.cc

gtest_main.o : $(GTEST_SRCS_)
	$(CXX) $(CPPFLAGS) -I$(GTEST_DIR) $(CXXFLAGS) -c \
            $(GTEST_DIR)/src/gtest_main.cc

gtest.a : gtest-all.o
	$(AR) $(ARFLAGS) $@ $^

gtest_main.a : gtest-all.o gtest_main.o
	$(AR) $(ARFLAGS) $@ $^