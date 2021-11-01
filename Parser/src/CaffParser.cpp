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

    size_t blockLength = bytesToInteger(std::vector<unsigned char>(it, it+FIELDLENGTHBYTES));
    header.length = blockLength;

    return header;
}


CaffHeader CaffParser::parseHeaderBlock_(std::vector<unsigned char> block) const
{
    auto it = block.begin();

    if (std::string magic(it, it+MAGICBYTES); magic.compare("CAFF") != 0)
    {
        throw std::invalid_argument("invalid caff header magic value");
    }
    it += MAGICBYTES;
    // header block always 20 bytes
    if (size_t headerBlockSize = bytesToInteger(std::vector<unsigned char>(it, it+FIELDLENGTHBYTES));
        headerBlockSize != HEADERBLOCKLENGHT)
    {
        throw std::invalid_argument("invalid header block length");
    }
    it += FIELDLENGTHBYTES;

    size_t numberOfCiffs = bytesToInteger(std::vector<unsigned char>(it, it+8));
    CaffHeader header;
    header.numOfCiffs = numberOfCiffs;

    return header;
}

CaffCredits CaffParser::parseCreditsBlock_(std::vector<unsigned char> block, size_t blockLength) const
{
    auto it = block.begin();
    auto year = (int)bytesToInteger(std::vector<unsigned char>(it, it+2));
    it+=2;
    auto month = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;
    auto day = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;
    auto hour = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;
    auto minute = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;

    size_t lengthOfCreater = bytesToInteger(std::vector<unsigned char>(it, it+FIELDLENGTHBYTES));
    it += FIELDLENGTHBYTES;
    if((lengthOfCreater + CREDITDATEBITES + FIELDLENGTHBYTES) != blockLength)
    {
        throw std::invalid_argument("invalid credit block length");
    }

    std::string creatorName(it, it+lengthOfCreater);

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
    auto it = block.begin();
    size_t duration = bytesToInteger(std::vector<unsigned char>(it, it+IMAGEDURATIONBYTES));
    SafeAdvance(it, block.end(),IMAGEDURATIONBYTES);

    CiffParser ciffParser;
    CiffFile ciffImage = ciffParser.parse(std::vector<unsigned char>(it, block.end()));

    CaffAnimationImage animationImage;
    animationImage.duration = duration;
    animationImage.ciffImage = ciffImage;
    return animationImage;
}

