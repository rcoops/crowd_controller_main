#!/bin/bash

#kill $(ps aux | grep -E 'stb098.+java -jar crowd' | grep -v grep | tr -s ' ' | cut -d ' ' -f 2) &&
rm crowd-controller-server-*.jar