#include <gtest/gtest.h>
#include <fstream>
#include <vector>

#include "../include/CaffParser.hpp"


TEST(ParserTest, FirstTestFileTest) {
    // arrange
    const std::string inputFile = "TestFiles/1.caff";
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

    // act
    CaffFile cf = parser.parse(buffer);

    // assert
    EXPECT_STREQ("Test Creator", cf.credits.creator.c_str());
    EXPECT_EQ(2, cf.header.numOfCiffs);
    for (CaffAnimationImage image : cf.animationImages)
    {
        EXPECT_EQ(1000, image.duration);
        EXPECT_STREQ("Beautiful scenery", image.ciffImage.header.caption.c_str());
        EXPECT_EQ(1000, image.ciffImage.header.width);
        EXPECT_EQ(667, image.ciffImage.header.height);
        EXPECT_EQ(667*1000*3, image.ciffImage.header.contentSize);
        EXPECT_EQ(image.ciffImage.header.contentSize, image.ciffImage.pixelValues.size() );
        EXPECT_EQ(3, image.ciffImage.header.tags.size());
        EXPECT_STREQ("landscape", image.ciffImage.header.tags[0].c_str());
        EXPECT_STREQ("sunset", image.ciffImage.header.tags[1].c_str());
        EXPECT_STREQ("mountains", image.ciffImage.header.tags[2].c_str());
    }
}
