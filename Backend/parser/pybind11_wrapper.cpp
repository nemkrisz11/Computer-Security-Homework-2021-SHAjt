#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include "include/CaffParser.hpp"
#include "include/CiffParser.hpp"

namespace py = pybind11;

PYBIND11_MODULE(caffparser, m) {
    py::class_<CaffParser>(m, "CaffParser")
        .def(py::init<>())
        .def("parse", &CaffParser::parse, py::return_value_policy::copy);

    py::class_<CaffFile>(m, "CaffFile")
        .def(py::init<>())
        .def_readwrite("header", &CaffFile::header)
        .def_readwrite("credits", &CaffFile::credits)
        .def_readwrite("animationImages", &CaffFile::animationImages);

    py::class_<CaffAnimationImage>(m, "CaffAnimationImage")
        .def(py::init<>())
        .def_readwrite("duration", &CaffAnimationImage::duration)
        .def_readwrite("ciffImage", &CaffAnimationImage::ciffImage);

    py::class_<CaffCredits>(m, "CaffCredits")
        .def(py::init<>())
        .def_readwrite("year", &CaffCredits::year)
        .def_readwrite("month", &CaffCredits::month)
        .def_readwrite("day", &CaffCredits::day)
        .def_readwrite("hour", &CaffCredits::hour)
        .def_readwrite("minute", &CaffCredits::minute)
        .def_readwrite("creator", &CaffCredits::creator);

    py::class_<CaffHeader>(m, "CaffHeader")
        .def(py::init<>())
        .def_readwrite("numOfCiffs", &CaffHeader::numOfCiffs);

    py::class_<CiffFile>(m, "CiffFile")
        .def(py::init<>())
        .def_readwrite("header", &CiffFile::header)
        .def_readwrite("pixelValues", &CiffFile::pixelValues);

    py::class_<CiffFileHeader>(m, "CiffFileHeader")
        .def(py::init<>())
        .def_readwrite("contentSize", &CiffFileHeader::contentSize)
        .def_readwrite("width", &CiffFileHeader::width)
        .def_readwrite("height", &CiffFileHeader::height)
        .def_readwrite("caption", &CiffFileHeader::caption)
        .def_readwrite("tags", &CiffFileHeader::tags);
}

