#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include "include/CaffParser.hpp"
#include "include/CiffParser.hpp"

namespace py = pybind11;

PYBIND11_MODULE(caffparser, m) {
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

