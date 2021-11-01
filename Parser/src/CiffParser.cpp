#include "../include/CiffParser.hpp"
#include "../include/Utilities.hpp"

using namespace constants;
using namespace utilities;

CiffFile CiffParser::parse(std::vector<unsigned char> buffer) const
{
    auto it = buffer.begin();
    SafeAdvance(it, buffer.end(), MAGICBYTES+FIELDLENGTHBYTES);
    auto headerSize = bytesToInteger(std::vector<unsigned char>(it-FIELDLENGTHBYTES, it));

    SafeAdvance(it, buffer.end(), headerSize);
    auto header = parseHeader_(std::vector<unsigned char>(buffer.begin(), buffer.begin()+headerSize));

    CiffFile ciff;
    ciff.header = header;

    return ciff;
}

CiffFileHeader CiffParser::parseHeader_(std::vector<unsigned char> block) const
{
    auto it = block.begin();
    SafeAdvance(it, block.end(), MAGICBYTES);
    if (std::string magic(it-MAGICBYTES, it); magic.compare("CIFF") != 0)
    {
        throw std::invalid_argument("invalid ciff header magic value");
    }
    SafeAdvance(it, block.end(), FIELDLENGTHBYTES);
    // skipping the header size because we already have it
    SafeAdvance(it, block.end(), IMAGESIZEBYTES);
    auto contentSize = bytesToInteger(std::vector<unsigned char>(it-IMAGESIZEBYTES, it));
    SafeAdvance(it, block.end(), IMAGESIZEBYTES);
    auto imageWidth = bytesToInteger(std::vector<unsigned char>(it-IMAGESIZEBYTES, it));
    SafeAdvance(it, block.end(), IMAGESIZEBYTES);
    auto imageHeight = bytesToInteger(std::vector<unsigned char>(it-IMAGESIZEBYTES, it));

    auto captionEnd = it;
    while(*captionEnd != '\n')
    {
        SafeAdvance(captionEnd, block.end(), 1);
    }
    std::string caption(it, captionEnd);

    it = captionEnd;
    //skip the \n
    SafeAdvance(it, block.end(), 1);

    std::vector<std::string> tags;
    auto tagFirstChar = it;
    while(it != block.end())
    {
        if(*it == '\0')
        {
            tags.push_back(std::string(tagFirstChar, it));
            //skip the \0
            tagFirstChar = it+1;
        }
        SafeAdvance(it, block.end(), 1);
    }

    //it should be block.end so the previous value must be the closing \0 for the last tag
    if (*(it-1) != '\0')
    {
        throw std::invalid_argument("tag must be closed");
    }

    CiffFileHeader header;
    header.contentSize = contentSize;
    header.caption = caption;
    header.width = imageWidth;
    header.height = imageHeight;
    header.tags = tags;

    return header;
}
