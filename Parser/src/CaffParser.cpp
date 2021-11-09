#include "../include/CaffParser.hpp"
#include "../include/Utilities.hpp"
#include <stdexcept>
#include <iostream>

using namespace constants;
using namespace utilities;

CaffFile CaffParser::parse(std::vector<unsigned char> buffer) const
{
    if (buffer.empty())
    {
        throw std::invalid_argument("File is empty");
    }
    auto it = buffer.begin();

    SafeAdvance(it, buffer.end(), BLOCKHEADERBYTES);
    BlockHeader firstBlockHeader = this->readNextBlockHeader_(buffer);

    if (firstBlockHeader.type != BlockType::HeaderBlock)
    {
        throw std::invalid_argument("First block must be the header block");
    }

    // header block always 20 bytes
    if (firstBlockHeader.length != HEADERBLOCKLENGHT)
    {
        throw std::invalid_argument("invalid header block length");
    }

    SafeAdvance(it, buffer.end(), firstBlockHeader.length);
    auto header = this->parseHeaderBlock_(std::vector<unsigned char>(it-firstBlockHeader.length, it));

    CaffFile cf;
    cf.header = header;

    while (it < buffer.end())
    {
        SafeAdvance(it, buffer.end(), BLOCKHEADERBYTES);
        BlockHeader nextBlockHeader = this->readNextBlockHeader_(std::vector<unsigned char>(it-BLOCKHEADERBYTES,it));

        SafeAdvance(it, buffer.end(), nextBlockHeader.length);
        if (nextBlockHeader.type == BlockType::CreditsBlock)
        {
            auto credits = this->parseCreditsBlock_(std::vector<unsigned char>(it-nextBlockHeader.length, it), nextBlockHeader.length);
            cf.credits = credits;
        }
        else if (nextBlockHeader.type == BlockType::AnimationBlock)
        {
            auto animationImage = this->parseAnimationBlock_(std::vector<unsigned char>(it-nextBlockHeader.length, it));
            cf.animationImages.insert(cf.animationImages.begin(), animationImage);
        }
    }

    return cf;
}

BlockHeader CaffParser::readNextBlockHeader_(std::vector<unsigned char> buffer) const
{
    if (buffer.empty())
    {
        throw std::invalid_argument("buffer is empty");
    }
    BlockHeader header;

    auto it = buffer.begin();

    if (*it > 3 || *it == 0)
    {
        throw std::invalid_argument("invalid block type");
    }
    else
    {
        header.type = static_cast<BlockType>(*it);
        it++;
    }

    SafeAdvance(it, buffer.end(), FIELDLENGTHBYTES);
    size_t blockLength = bytesToInteger(std::vector<unsigned char>(it-FIELDLENGTHBYTES, it));
    header.length = blockLength;

    return header;
}


CaffHeader CaffParser::parseHeaderBlock_(std::vector<unsigned char> block) const
{
    if (block.empty())
    {
        throw std::invalid_argument("block is empty");
    }

    auto it = block.begin();

    SafeAdvance(it, block.end(), MAGICBYTES);
    if (std::string magic(it-MAGICBYTES, it); magic.compare("CAFF") != 0)
    {
        throw std::invalid_argument("invalid caff header magic value");
    }

    SafeAdvance(it, block.end(), FIELDLENGTHBYTES);
    // header block always 20 bytes
    if (size_t headerBlockSize = bytesToInteger(std::vector<unsigned char>(it-FIELDLENGTHBYTES, it));
        headerBlockSize != HEADERBLOCKLENGHT)
    {
        throw std::invalid_argument("invalid header block length");
    }

    SafeAdvance(it, block.end(), 8);
    size_t numberOfCiffs = bytesToInteger(std::vector<unsigned char>(it-8, it));
    CaffHeader header;
    header.numOfCiffs = numberOfCiffs;

    return header;
}

CaffCredits CaffParser::parseCreditsBlock_(std::vector<unsigned char> block, size_t blockLength) const
{
    if (block.empty())
    {
        throw std::invalid_argument("block is empty");
    }

    auto it = block.begin();
    SafeAdvance(it, block.end(), 2);
    auto year = (int)bytesToInteger(std::vector<unsigned char>(it-2, it));
    SafeAdvance(it, block.end(), 1);
    auto month = (int)bytesToInteger(std::vector<unsigned char>(it-1, it));
    SafeAdvance(it, block.end(), 1);
    auto day = (int)bytesToInteger(std::vector<unsigned char>(it-1, it));
    SafeAdvance(it, block.end(), 1);
    auto hour = (int)bytesToInteger(std::vector<unsigned char>(it-1, it));
    SafeAdvance(it, block.end(), 1);
    auto minute = (int)bytesToInteger(std::vector<unsigned char>(it-1, it));

    SafeAdvance(it, block.end(), FIELDLENGTHBYTES);
    size_t lengthOfCreater = bytesToInteger(std::vector<unsigned char>(it-FIELDLENGTHBYTES, it));
    if((lengthOfCreater + CREDITDATEBITES + FIELDLENGTHBYTES) != blockLength)
    {
        throw std::invalid_argument("invalid credit block length");
    }

    SafeAdvance(it, block.end(), lengthOfCreater);
    std::string creatorName(it-lengthOfCreater, it);

    CaffCredits credits;
    credits.year = year;
    credits.month = month;
    credits.day = day;
    credits.hour = hour;
    credits.minute = minute;
    credits.creator = creatorName;

    return credits;
}

CaffAnimationImage CaffParser::parseAnimationBlock_(std::vector<unsigned char> block) const
{
    if (block.empty())
    {
        throw std::invalid_argument("block is empty");
    }

    auto it = block.begin();
    SafeAdvance(it, block.end(),IMAGEDURATIONBYTES);
    size_t duration = bytesToInteger(std::vector<unsigned char>(it-IMAGEDURATIONBYTES, it));

    CiffParser ciffParser;
    CiffFile ciffImage = ciffParser.parse(std::vector<unsigned char>(it, block.end()));

    CaffAnimationImage animationImage;
    animationImage.duration = duration;
    animationImage.ciffImage = ciffImage;
    return animationImage;
}

