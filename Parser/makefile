
BuildLib: CaffParser.o CiffParser.o
	g++ -Wall -Werror -shared -std=c++17 -fPIC CaffParser.o CiffParser.o -o libCaffParser.so

BuildMain: Main.o CaffParser.o CiffParser.o
	g++ -Wall -Werror -std=c++17 main.o CaffParser.o CiffParser.o -o output
	
Main.o: src/Main.cpp
	g++ -Wall -Werror -std=c++17 -c -fPIC src/Main.cpp

CaffParser.o: src/CaffParser.cpp include/CaffParser.hpp
	g++ -Wall -Werror -std=c++17 -c -fPIC src/CaffParser.cpp

CiffParser.o: src/CiffParser.cpp include/CiffParser.hpp
	g++ -Wall -Werror -std=c++17 -c -fPIC src/CiffParser.cpp
	
clean:
ifneq ("$(wildcard *.o)","")
	rm *.o
endif
ifneq ("$(wildcard output)","")
	rm output
endif
ifneq ("$(wildcard *.so)","")
	rm *.so
endif
ifneq ("$(wildcard include/*.gch)","")
	rm include/*.gch
endif