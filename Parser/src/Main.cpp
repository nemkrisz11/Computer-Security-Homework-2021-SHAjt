#include <iostream>
#include <fstream>
#include <vector>

#include "../include/CaffParser.hpp"

int main()
{
    const std::string inputFile = "TestFiles/1.caff";
    std::ifstream inFile(inputFile, std::ios_base::binary);

    inFile.seekg(0, std::ios_base::end);
    size_t length = inFile.tellg();
    inFile.seekg(0, std::ios_base::beg);

    std::vector<unsigned char> buffer;
    buffer.reserve(length);
    std::copy(std::istreambuf_iterator<char>(inFile),
              std::istreambuf_iterator<char>(),
              std::back_inserter(buffer));

    std::cout << (int)buffer[0] << std::endl;

    CaffParser parser;
    CaffFile cf = parser.parse(buffer);

    std::cout << cf.header.numOfCiffs << std::endl;
    std::cout << cf.credits.creator << std::endl;
    for (auto image : cf.animationImages)
    {
        std::cout << image.duration << std::endl;
    }
    std::cin.get();
}
