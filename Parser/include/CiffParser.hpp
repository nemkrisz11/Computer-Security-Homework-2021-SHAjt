#ifndef CIFFPARSER_H
#define CIFFPARSER_H

#include <vector>
#include <string>
#include <cstddef>

class CiffFileHeader
{
    public:
        size_t contentSize;
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
        CiffFile parse(std::vector<unsigned char> buffer) const;
    private:
        CiffFileHeader parseHeader_(std::vector<unsigned char> block) const;
};

#endif // CIFFPARSER_H
