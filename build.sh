#!/bin/sh
set -e

cat Header.html > JUnitRules.html
./redcarpet JUnitRules.markdown >> JUnitRules.html
cat Footer.html >> JUnitRules.html

