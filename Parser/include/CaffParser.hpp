#ifndef CAFFPARSER_H
#define CAFFPARSER_H

#include <vector>
#include <string>
#include "CiffParser.hpp"


enum BlockType { HeaderBlock = 1, CreditsBlock = 2, AnimationBlock = 3 };

class BlockHeader
{
    public:
        BlockType type;
        size_t length;
};

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

class CaffAnimationImage
{
    public:
        size_t duration;
        CiffFile ciffImage;
};

class CaffFile
{
    public:
        CaffHeader header;
        CaffCredits credits;
        std::vector<CaffAnimationImage> animationImages;
};

class CaffParser
{
    public:
        CaffFile parse(std::vector<unsigned char> buffer);
    private:
        BlockHeader readNextBlockHeader_(std::vector<unsigned char> buffer);
        CaffHeader parseHeaderBlock_(std::vector<unsigned char> block);
        CaffCredits parseCreditsBlock_(std::vector<unsigned char> block, size_t blockLength);
        CaffCredits parseAnimationBlock_(std::vector<unsigned char> block, size_t blockLength);
};

#endif // CAFFPARSER_H
