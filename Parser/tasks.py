import invoke
import pathlib
import sys
import os

def print_banner(msg):
    print("==================================================")
    print("= {} ".format(msg))

@invoke.task()
def buildcpplib(c):
    """Build the shared library for the C++ code"""
    print_banner("Building C++ Library")
    invoke.run(
        "make"
    )
    print("* Complete")


def compile_python_module(cpp_name, extension_name):
    invoke.run(
        "g++ -O3 -Wall -Werror -shared -std=c++17 -fPIC "
        "`python3 -m pybind11 --includes` "
        "-I .  "
        "{0} "
        "-o {1}`python3.8-config --extension-suffix` "
        "-L. -lcaffparser -Wl,-rpath,.".format(cpp_name, extension_name)
    )

@invoke.task(buildcppLib)
def buildpybind11(c):
    """Build the pybind11 wrapper library"""
    print_banner("Building PyBind11 Module")
    compile_python_module("pybind11_wrapper.cpp", "caffparser")
    print("* Complete")
