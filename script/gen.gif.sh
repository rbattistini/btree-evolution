#!/usr/bin/bash

cd ../src/main/resources/res/ && convert -resize 64x64 -delay 20 -loop 0 $(ls -v) res.gif