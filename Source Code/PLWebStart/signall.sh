#!/bin/bash

find plwebstart -name "*.jar" -exec jarsigner -keystore keystore -storepass plwebkey -keypass plwebkey {} plweb \; -print
