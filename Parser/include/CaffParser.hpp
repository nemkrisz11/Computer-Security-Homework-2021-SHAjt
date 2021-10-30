#ifndef CAFFPARSER_H
#define CAFFPARSER_H

#include <vector>
#include <string>

enum BlockType { HeaderBlock = 1, CreditsBlock = 2, AnimationBlock = 3 };

typedef struct BlockHeader
{
    public:
        BlockType type;
        size_t length;
} BlockHeader;

class CaffHeader
{
    public:
        size_t numOfCiffs;
};

class CaffCredits
{
    public:
        int year;
        int month;
        int day;
        int hour;
        int minute;
        std::string creator;

};

class CaffFile
{
    public:
        CaffHeader header;
        CaffCredits credits;

};

class CaffParser
{
    public:
        CaffFile parse(std::vector<unsigned char> buffer);
    private:
        BlockHeader readNextBlockHeader_(std::vector<unsigned char> buffer);
        CaffHeader parseHeader_(std::vector<unsigned char> block);
        CaffCredits parseCredits_(std::vector<unsigned char> block, size_t blockLength);

        const unsigned int blockHeaderBytes_ = 9;
        const unsigned int fieldLengthBytes_ = 8;
        const unsigned int creditDateBytes_ = 6;
};

#endif // CAFFPARSER_H
