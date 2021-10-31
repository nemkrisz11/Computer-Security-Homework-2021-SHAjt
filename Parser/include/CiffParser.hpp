#ifndef CIFFPARSER_H
#define CIFFPARSER_H

#include <vector>
#include <string>

class CiffFileHeader
{
    public:
        size_t width;
        size_t height;
        std::string caption;
        std::vector<std::string> tags;
};

class CiffFile{
    public:
        CiffFileHeader header;
        std::vector<unsigned char> pixelValues;
};

class CiffParser
{
    public:
        CiffFile parse(std::vector<unsigned char> buffer);
    private:
        CiffFileHeader parseHeader_(std::vector<unsigned char> block);
};

#endif // CIFFPARSER_H
