#ifndef UTILITIES_HPP_INCLUDED
#define UTILITIES_HPP_INCLUDED

#include <stdexcept>
#include <vector>
#include <cstddef>

namespace constants
{
    const size_t HEADERBLOCKLENGHT = 20;
    const size_t BLOCKHEADERBYTES = 9;
    const size_t FIELDLENGTHBYTES = 8;
    const size_t CREDITDATEBITES = 6;
    const size_t MAGICBYTES = 4;
    const size_t IMAGEDURATIONBYTES = 8;
}

namespace utilities
{
    inline size_t bytesToInteger(std::vector<unsigned char> bytes)
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
    inline void SafeAdvance(Itr& currentItr, Itr endItr, int advance)
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
}



#endif // UTILITIES_HPP_INCLUDED
