#include "../include/CaffParser.hpp"
#include <stdexcept>
#include <iostream>

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

CaffFile CaffParser::parse(std::vector<unsigned char> buffer)
{
    if (buffer.empty())
    {
        throw std::invalid_argument("File is empty");
    }
    auto it = buffer.begin();

    BlockHeader firstBlockHeader = this->readNextBlockHeader_(buffer);

    if (firstBlockHeader.type != HeaderBlock)
    {
        throw std::invalid_argument("First block must be the header block");
    }

    // header block always 20 bytes
    if (firstBlockHeader.length != 20)
    {
        throw std::invalid_argument("invalid header block length");
    }
    it += this->blockHeaderBytes_;

    auto header = this->parseHeader_(std::vector<unsigned char>(it, it+firstBlockHeader.length));
    it += firstBlockHeader.length;

    CaffFile cf;
    cf.header = header;

    /*while (it < buffer.end())
    {
        BlockHeader nextBlockHeader = this->readNextBlockHeader_(std::vector<unsigned char>(it, it+this->blockHeaderBytes_));
        it += this->blockHeaderBytes_;

        if (nextBlockHeader.type == CreditsBlock)
        {
            auto credits = this->parseCredits_(std::vector<unsigned char>(it, it+nextBlockHeader.length));
            cf.credits = credits;
        }

        it += nextBlockHeader.length;
    }*/


    BlockHeader nextBlockHeader = this->readNextBlockHeader_(std::vector<unsigned char>(it, it+this->blockHeaderBytes_));
    it += this->blockHeaderBytes_;

    if (nextBlockHeader.type == CreditsBlock)
    {
        auto credits = this->parseCredits_(std::vector<unsigned char>(it, it+nextBlockHeader.length), nextBlockHeader.length);
        cf.credits = credits;
    }

    it += nextBlockHeader.length;

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

    size_t blockLength = bytesToInteger(std::vector<unsigned char>(it, it+8));
    bh.length = blockLength;

    return bh;
}


CaffHeader CaffParser::parseHeader_(std::vector<unsigned char> block){
    auto it = block.begin();
    std::string magic(it, it+4);
    if (magic.compare("CAFF") != 0)
    {
        throw std::invalid_argument("invalid header magic value");
    }
    it += 4;
    size_t headerBlockSize = bytesToInteger(std::vector<unsigned char>(it, it+8));
    // header block always 20 bytes
    if (headerBlockSize != 20)
    {
        throw std::invalid_argument("invalid header block length");
    }
    it += 8;

    size_t numberOfCiffs = bytesToInteger(std::vector<unsigned char>(it, it+8));
    CaffHeader ch;
    ch.numOfCiffs = numberOfCiffs;

    return ch;
}

CaffCredits CaffParser::parseCredits_(std::vector<unsigned char> block, size_t blockLength)
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

    size_t lengthOfCreater = bytesToInteger(std::vector<unsigned char>(it, it+this->fieldLengthBytes_));
    it += this->fieldLengthBytes_;
    if((lengthOfCreater + this->creditDateBytes_ + this->fieldLengthBytes_) != blockLength)
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

