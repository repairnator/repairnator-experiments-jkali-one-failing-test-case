#!/usr/bin/env bash

echo "Start PyseriniEntryPoint..."
sh target/appassembler/bin/PyseriniEntryPoint &
PID_1=$!

echo "Start the Flask server..."
python3 src/main/python/api.py &
PID_2=$!

echo "Start the JavaScript UI..."
pushd src/main/js
npm start &
PID_3=$!
popd

# clean up before exiting
function clean_up {
    kill $PID_3
    kill $PID_2
    kill $PID_1
    exit
}

trap clean_up SIGHUP SIGINT SIGTERM SIGKILL
wait
