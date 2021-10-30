#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include "include/CaffParser.hpp"
#include "include/CiffParser.hpp"

namespace py = pybind11;

PYBIND11_MODULE(caffparser, m) {
    py::class_<CaffFile>(m, "CaffFile")
        .def(py::init<>())
        .def_readwrite("a", &CaffFile::a)
        .def_readwrite("vec", &CaffFile::vec);
}