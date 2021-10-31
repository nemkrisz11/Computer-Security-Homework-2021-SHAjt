
BuildLib: CaffParser.o CiffParser.o
	g++ -Wall -Werror -shared -std=c++17 -fPIC CaffParser.o CiffParser.o -o libCaffParser.so

BuildMain: Main.o CaffParser.o CiffParser.o
	g++ -Wall -Werror -std=c++17 main.o CaffParser.o CiffParser.o -o output
	
Main.o: src/Main.cpp
	g++ -Wall -Werror -std=c++17 -c src/Main.cpp

CaffParser.o: src/CaffParser.cpp include/CaffParser.hpp
	g++ -Wall -Werror -std=c++17 -c src/CaffParser.cpp

CiffParser.o: src/CiffParser.cpp include/CiffParser.hpp
	g++ -Wall -Werror -std=c++17 -c src/CiffParser.cpp
	
clean:
	rm *.o output *.so include/*.gch