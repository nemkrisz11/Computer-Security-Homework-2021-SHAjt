#include "../include/CaffParser.hpp"
#include "../include/Utilities.hpp"
#include <stdexcept>
#include <iostream>

using namespace constants;

size_t bytesToInteger(std::vector<unsigned char> bytes)
{
    auto it = bytes.begin();
    size_t value = 0;
    for(unsigned int i = 0; i<bytes.size(); i++)
    {
        value = (size_t) (value | (*it << i*8));
        it++;
    }

    return value;
}

template <typename Itr>
void SafeAdvance(Itr& currentItr, Itr endItr, int advance)
{
    if ((currentItr < endItr) && (distance(currentItr, endItr) >= advance))
    {
        std::advance(currentItr, advance);
    }
    else
    {
        throw std::invalid_argument("Unexpected end of file");
    }
}


CaffFile CaffParser::parse(std::vector<unsigned char> buffer)
{
    if (buffer.empty())
    {
        throw std::invalid_argument("File is empty");
    }
    auto it = buffer.begin();

    SafeAdvance(it, buffer.end(), BLOCKHEADERBYTES);
    BlockHeader firstBlockHeader = this->readNextBlockHeader_(buffer);

    if (firstBlockHeader.type != HeaderBlock)
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

    SafeAdvance(it, buffer.end(), BLOCKHEADERBYTES);
    BlockHeader nextBlockHeader = this->readNextBlockHeader_(std::vector<unsigned char>(it-BLOCKHEADERBYTES,it));

    SafeAdvance(it, buffer.end(), nextBlockHeader.length);
    if (nextBlockHeader.type == CreditsBlock)
    {
        auto credits = this->parseCreditsBlock_(std::vector<unsigned char>(it-nextBlockHeader.length, it), nextBlockHeader.length);
        cf.credits = credits;
    }
    else if (nextBlockHeader.type == AnimationBlock)
    {

    }

    return cf;
}

BlockHeader CaffParser::readNextBlockHeader_(std::vector<unsigned char> buffer)
{
    if (buffer.empty())
    {
        throw std::invalid_argument("buffer is empty");
    }
    BlockHeader bh;

    auto it = buffer.begin();
    if (*it > 3 || *it < 0)
    {
        throw std::invalid_argument("invalid block type");
    }
    else
    {
        bh.type = static_cast<BlockType>(*it);
        it++;
    }

    size_t blockLength = bytesToInteger(std::vector<unsigned char>(it, it+FIELDLENGTHBYTES));
    bh.length = blockLength;

    return bh;
}


CaffHeader CaffParser::parseHeaderBlock_(std::vector<unsigned char> block){
    auto it = block.begin();
    std::string magic(it, it+MAGICBYTES);
    if (magic.compare("CAFF") != 0)
    {
        throw std::invalid_argument("invalid header magic value");
    }
    it += MAGICBYTES;
    size_t headerBlockSize = bytesToInteger(std::vector<unsigned char>(it, it+FIELDLENGTHBYTES));
    // header block always 20 bytes
    if (headerBlockSize != HEADERBLOCKLENGHT)
    {
        throw std::invalid_argument("invalid header block length");
    }
    it += FIELDLENGTHBYTES;

    size_t numberOfCiffs = bytesToInteger(std::vector<unsigned char>(it, it+8));
    CaffHeader ch;
    ch.numOfCiffs = numberOfCiffs;

    return ch;
}

CaffCredits CaffParser::parseCreditsBlock_(std::vector<unsigned char> block, size_t blockLength)
{
    auto it = block.begin();
    int year = (int)bytesToInteger(std::vector<unsigned char>(it, it+2));
    it+=2;
    int month = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;
    int day = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;
    int hour = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;
    int minute = (int)bytesToInteger(std::vector<unsigned char>(it, it+1));
    it++;

    size_t lengthOfCreater = bytesToInteger(std::vector<unsigned char>(it, it+FIELDLENGTHBYTES));
    it += FIELDLENGTHBYTES;
    if((lengthOfCreater + CREDITDATEBITES + FIELDLENGTHBYTES) != blockLength)
    {
        throw std::invalid_argument("invalid credit block length");
    }

    std::string creatorName(it, it+lengthOfCreater);

    CaffCredits cr;
    cr.year = year;
    cr.month = month;
    cr.day = day;
    cr.hour = hour;
    cr.minute = minute;
    cr.creator = creatorName;

    return cr;
}

