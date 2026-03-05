#!/bin/bash
# ============================================================
# SmartHome Build Script
# Requires: JDK 17+ (javac + jar)
# Usage:  chmod +x build.sh && ./build.sh
# Run:    java -jar SmartHome.jar
# ============================================================

set -e

echo "======================================="
echo "  Smart Home App - Build Script"
echo "======================================="

# Find javac
if command -v javac &> /dev/null; then
    JAVAC=javac
    JAR_CMD=jar
    JAVA=java
elif [ -f "/usr/lib/jvm/java-21-openjdk-amd64/bin/javac" ]; then
    JAVAC=/usr/lib/jvm/java-21-openjdk-amd64/bin/javac
    JAR_CMD=/usr/lib/jvm/java-21-openjdk-amd64/bin/jar
    JAVA=/usr/lib/jvm/java-21-openjdk-amd64/bin/java
else
    echo "ERROR: javac not found. Install JDK 17+ first."
    echo "  Ubuntu/Debian: sudo apt install default-jdk"
    echo "  macOS: brew install openjdk"
    exit 1
fi

echo "Using: $($JAVAC --version)"

# Clean
rm -rf out/
mkdir -p out/

# Collect sources
find src -name "*.java" > sources.txt
echo "Found $(wc -l < sources.txt) source files."

# Compile
echo "Compiling..."
$JAVAC --release 17 -d out/ @sources.txt
echo "✅ Compilation successful."

# Create manifest
mkdir -p out/META-INF
cat > out/META-INF/MANIFEST.MF << 'EOF'
Manifest-Version: 1.0
Main-Class: com.smarthome.ui.SmartHomeApp
EOF

# Package JAR
echo "Packaging JAR..."
cd out
$JAR_CMD cfm ../SmartHome.jar META-INF/MANIFEST.MF $(find . -name "*.class" | sort)
cd ..

echo "✅ SmartHome.jar created!"
echo ""
echo "Run with:  java -jar SmartHome.jar"
echo "======================================="

# Optionally auto-run
if [ "$1" == "--run" ]; then
    echo "Launching..."
    $JAVA -jar SmartHome.jar
fi
