from caffparser import CiffFile, CiffFileHeader, CaffFile, CaffCredits, CaffHeader, CaffParser
from PIL import Image
import numpy as np

dtype = np.dtype('B')
with open("1.caff", "rb") as f:
    numpy_data = np.fromfile(f, dtype)

caffParser = CaffParser()
cf = caffParser.parse(numpy_data)
print("Header ----------------------------------------")
print(f"number of ciffs: {cf.header.numOfCiffs}")
print("Credits ---------------------------------------")
print(f"year: {cf.credits.year}")
print(f"month: {cf.credits.month}")
print(f"day: {cf.credits.day}")
print(f"hour: {cf.credits.hour}")
print(f"minute: {cf.credits.minute}")
print(f"creator: {cf.credits.creator}")
print("Animation images -------------------------------")
i = 0
for animation in cf.animationImages:
    print("animation image ----------------------------")
    print(f"duration: {animation.duration}")
    print("ciff header --------------------------------")
    print(f"contentSize: {animation.ciffImage.header.contentSize}")
    print(f"width: {animation.ciffImage.header.width}")
    print(f"height: {animation.ciffImage.header.height}")
    print(f"caption: {animation.ciffImage.header.caption}")
    print(f"tags: {animation.ciffImage.header.tags}")
    print(type(animation.ciffImage.pixelValues))
    print(type(animation.ciffImage.pixelValues[0]))
    width = animation.ciffImage.header.width
    height = animation.ciffImage.header.height
    pixel_array = np.array(animation.ciffImage.pixelValues, dtype=np.uint8)
    pixel_array.resize(height, width, 3)
    new_image = Image.fromarray(pixel_array)
    new_image.save(f'{i}new.png')
    i += 1
    print("--------------------------------------------")




