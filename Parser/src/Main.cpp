#include <iostream>
#include <fstream>
#include <vector>

#include "../include/CaffParser.hpp"

int main(int argc, char* argv[])
{
    if (argc != 2) {
        throw std::invalid_argument("Usage: ./CaffParser <filepath>");
    }
    else {
        std::cout << "Parsing " << argv[1] << std::endl;
    }

    const std::string inputFile = argv[1];
    std::ifstream inFile(inputFile, std::ios_base::binary);

    inFile.seekg(0, std::ios_base::end);
    size_t length = inFile.tellg();
    inFile.seekg(0, std::ios_base::beg);

    std::vector<unsigned char> buffer;
    buffer.reserve(length);
    std::copy(std::istreambuf_iterator<char>(inFile),
              std::istreambuf_iterator<char>(),
              std::back_inserter(buffer));

    CaffParser parser;

    try {
        CaffFile cf = parser.parse(buffer);

    std::cout << "num of ciff images: " << cf.header.numOfCiffs << std::endl;
    std::cout << "creator: "<< cf.credits.creator << std::endl;
    for (CaffAnimationImage image : cf.animationImages)
    {
        std::cout << "image duration: " << image.duration << std::endl;
        std::cout << "image caption: " << image.ciffImage.header.caption << " size: " << image.ciffImage.header.caption.size() << std::endl;
        std::cout << "image height: " << image.ciffImage.header.height << std::endl;
        std::cout << "image width: " << image.ciffImage.header.width << std::endl;
        std::cout << "image content size: " << image.ciffImage.header.contentSize << std::endl;
        std::cout << "image pixel values size: " << image.ciffImage.pixelValues.size() << std::endl;
        std::cout << "tags: " << std::endl;
        for (auto tag : image.ciffImage.header.tags)
        {
            std::cout << tag << " size: " << tag.size() << std::endl;
        }
    }} catch(const std::invalid_argument& e) {
        std::cerr << "ERROR: " << e.what() << std::endl;
    }
}
