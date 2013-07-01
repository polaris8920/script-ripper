#! /bin/sh

# File:      ScriptRipper.sh
# Author:    Daniel Deveau    100104346
# Date:      2013-03-19
# Version:   1.0

# Purpose:
# Find and extract C code from script files using the ScriptRipper
# java file.

find . -name "*.txt" -print0 | xargs -0 java ScriptRipper 
