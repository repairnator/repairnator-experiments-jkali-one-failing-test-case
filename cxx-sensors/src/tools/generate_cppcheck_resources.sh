#!/bin/sh

# directory where cppcheck stores description (configuration) files for libraries
# these configurations contain additional rules about undesired (obsolete, not thread-safe etc) functions
#
# typical location:
# * Red Hat based linux: /usr/share/cppcheck/
# * cppcheck sources:    cppcheck/cfg/
CFG_DIR=/usr/share/cppcheck/

CPPCHECK_LIBRARY_ARGS=""
for CFG in ${CFG_DIR}*.cfg; do
   [ -e "${CFG}" ] && CPPCHECK_LIBRARY_ARGS="${CPPCHECK_LIBRARY_ARGS} --library=${CFG}" || echo "no *.cfg files exist in ${CFG_DIR}; no library specific rules will be generated!"
done

cppcheck ${CPPCHECK_LIBRARY_ARGS} --errorlist > cppcheck-errorlist.xml
cppcheck ${CPPCHECK_LIBRARY_ARGS} --errorlist | python cppcheck_createrules.py rules > cppcheck.xml
cppcheck ${CPPCHECK_LIBRARY_ARGS} --errorlist | python cppcheck_createrules.py profile > cppcheck-profile.xml
python cppcheck_createrules.py comparerules sonar-cxx/cxx-sensors/src/main/resources/cppcheck.xml cppcheck.xml > cppcheck-comparison.md
