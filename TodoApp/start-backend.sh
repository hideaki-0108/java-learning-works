#!/bin/bash

echo "=== Java Backend Server Starting ==="
echo ""
echo "1. Compiling Java sources..."
mvn clean compile

if [ $? -ne 0 ]; then
    echo "Maven compilation failed!"
    exit 1
fi

echo ""
echo "2. Starting Java application..."
echo "Server will start on http://localhost:8080"
echo "API endpoints available at http://localhost:8080/api/todos"
echo ""
echo "Press Ctrl+C to stop the server"
echo ""

mvn exec:java -Dfile.encoding=UTF-8 -Dexec.args="-Dfile.encoding=UTF-8"
