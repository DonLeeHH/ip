#!/usr/bin/env bash

# ----- Colors (TTY + tput) -----
if [ -t 1 ] && command -v tput >/dev/null 2>&1 && [ "$(tput colors 2>/dev/null)" -ge 8 ]; then
  RED="$(tput setaf 1)"; GREEN="$(tput setaf 2)"; BOLD="$(tput bold)"; RESET="$(tput sgr0)"
else
  RED=""; GREEN=""; BOLD=""; RESET=""
fi

# create bin directory if it doesn't exist
if [ ! -d "../bin" ]; then
  mkdir ../bin
fi

# delete output from previous run
[ -e "./ACTUAL.TXT" ] && rm ACTUAL.TXT

# compile the code into the bin folder, terminate if error occurred
if ! javac -cp ../src/main/java -Xlint:none -d ../bin ../src/main/java/sid/*.java; then
  printf "%s%s********** BUILD FAILURE **********%s\n" "$RED" "$BOLD" "$RESET"
  exit 1
fi

# run the program, feed commands from input.txt file and redirect the output to ACTUAL.TXT
java -classpath ../bin Sid < input.txt > ACTUAL.TXT

# convert to UNIX format (fallback if dos2unix is missing)
cp EXPECTED.TXT EXPECTED-UNIX.TXT
if command -v dos2unix >/dev/null 2>&1; then
  dos2unix ACTUAL.TXT EXPECTED-UNIX.TXT >/dev/null 2>&1
else
  # strip CRs
  tr -d '\r' < ACTUAL.TXT > ACTUAL.tmp && mv ACTUAL.tmp ACTUAL.TXT
  tr -d '\r' < EXPECTED-UNIX.TXT > EXPECTED-UNIX.tmp && mv EXPECTED-UNIX.tmp EXPECTED-UNIX.TXT
fi

# compare the output to the expected output
diff ACTUAL.TXT EXPECTED-UNIX.TXT
status=$?

if [ $status -eq 0 ]; then
  printf "%s%sTest result: PASSED%s\n" "$GREEN" "$BOLD" "$RESET"
else
  printf "%s%sTest result: FAILED%s\n" "$RED" "$BOLD" "$RESET"
fi
exit $status
