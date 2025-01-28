#!/bin/bash
#(c) jmonkeyengine.org
#Author MeFisto94

# This script is build up like a gradle build script. It contains many functions, each for it's distinctive task and every function is calling it's dependency functions.
# This means in order for "unpack" to work, it will first launch "download" etc. While each task is self-explanatory, here's the process in short:
# 1. Download JDK, 2. Unpack JDK (this used to be more work, with SFX Installers from Oracle etc), 3. Compile (this zips the unpacked and processed jdk and
# creates a SFX Installer again from the zip), 4. Build (Build is the more general code to call compile (which calls unpack which calls download) and links the currently
# most up to date JDK version into the main directory (because several old jdk versions are stored as well).

set -e # Quit on Error

jdk_major_version="21"
jvm_impl="hotspot"
jdk_vendor="eclipse"

function download_jdk {
    echo ">>> Downloading the JDK for $1_$2$3"

    if [ -f downloads/jdk-$1_$2$3 ];
    then
        echo "<<< Already existing, SKIPPING."
    else
        curl -# -o downloads/jdk-$1_$2$3 -L https://api.adoptium.net/v3/binary/latest/$jdk_major_version/ga/$2/$1/jdk/$jvm_impl/normal/$jdk_vendor?project=jdk
        echo "<<< OK!"
    fi
}

function unpack_mac_jdk {
    echo ">> Extracting the Mac JDK..."
    #cd local/$jdk_version-$jdk_build_version/

    if [ -f "compiled/jdk-macosx.zip" ];
    then
        echo "< Already existing, SKIPPING."
        #cd ../../
        return 0
    fi

    download_jdk x64 mac .tar.gz
    tar xf downloads/jdk-x64_mac.tar.gz
    cd jdk-$jdk_major_version*/Contents/

    # FROM HERE: build-osx-zip.sh by normen (with changes)
    mv Home jdk # rename folder
    rm -rf jdk/man jdk/legal # ANT got stuck at the symlinks (https://bz.apache.org/bugzilla/show_bug.cgi?id=64053)
    zip -9 -r -y -q ../../compiled/jdk-macosx.zip jdk
    cd ../../
    
    rm -rf jdk-$jdk_major_version*

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-x64_mac.tar.gz
    fi
    #cd ../../

    echo "<< OK!"
}

function build_mac_jdk {
    echo "> Building the Mac JDK"
    if ! [ -f "compiled/jdk-macosx.zip" ];
    then
        unpack_mac_jdk # Depends on "unpack" which depends on "download" (Unpack includes what compile is to other archs)
    fi

    rm -rf ../../jdk-macosx.zip
    ln -rs compiled/jdk-macosx.zip ../../
    echo "< OK!"
}

# PARAMS arch
function unpack_windows {
    echo ">> Extracting the JDK for windows-$1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -d windows-$1 ];
    then
        echo "<< Already existing, SKIPPING."
        # cd ../../
        return 0
    fi

    download_jdk "$1" windows .zip

    mkdir -p windows-$1
    unzip -qq downloads/jdk-$1_windows.zip -d windows-$1
    cd windows-$1/
    
    mv jdk-$jdk_major_version*/* .
    rm -rf jdk-$jdk_major_version*   

    # This seems to be replaced by lib/tools.jar in openJDK
    #unzip -qq tools.zip -d .
    #rm tools.zip

    find . -exec chmod u+w {} \; # Make all file writable to allow uninstaller's cleaner to remove file    
    
    find . -type f \( -name "*.exe" -o -name "*.dll" \) -exec chmod u+rwx {} \; # Make them executable
    
    # Insert fake unpack200.exe
    # See https://github.com/jMonkeyEngine/sdk/issues/491
    touch bin/unpack200.exe
    
    cd ../

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-$1_windows.zip
    fi
    
    echo "<< OK!"
}

function unpack_linux {
    echo ">> Extracting the JDK for linux-$1"
    #cd local/$jdk_version-$jdk_build_version/

    if [ -d linux-$1 ];
    then
        echo "<< Already existing, SKIPPING."
        #cd ../../
        return 0
    fi

    download_jdk "$1" linux .tar.gz

    mkdir -p linux-$1
    cd linux-$1
    tar -xf "../downloads/jdk-$1_linux.tar.gz"
    mv jdk-$jdk_major_version*/* .
    rm -rf jdk-$jdk_major_version*
    
    cd ../

    if [ "$TRAVIS" == "true" ]; then
        rm -rf downloads/jdk-$1.tar.gz
    fi

    echo "<< OK!"
}

# PARAMS: os arch arch_unzipsfx
function compile_other {
    echo "> Compiling JDK for $1-$2"

    if [ $1 == "windows" ]; then
        name="jdk-$1-$3.exe"
    elif [ $1 == "linux" ]; then
        name="jdk-$1-$3.bin"
    else
        echo "Unknown Platform $1. ERROR!!!"
        exit 1
    fi

    if [ -f "compiled/$name" ]; then
        echo "< Already existing, SKIPPING."
        return 0
    fi

    # Depends on UNPACK and thus DOWNLOAD
    if [ $1 == "windows" ]; then
        unpack_windows $2
    elif [ $1 == "linux" ]; then
        unpack_linux $2
    fi

    unzipsfxname="../../unzipsfx/unzipsfx-$1-$3"
    if [ ! -f "$unzipsfxname" ]; then
        echo "No unzipsfx for platform $1-$3 found at $unzipsfxname, cannot continue"
        exit 1
    fi

    echo "> Zipping JDK"
    cd $1-$2 # zip behaves differently between 7zip and Info-Zip, so simply change wd
    zip -9 -qry ../jdk_tmp_sfx.zip *
    cd ../
    echo "> Building SFX"
    cat $unzipsfxname jdk_tmp_sfx.zip > compiled/$name
    chmod +x compiled/$name
    rm -rf jdk_tmp_sfx.zip

    if [ "$TRAVIS" == "true" ]; then
        rm -rf $1-$2
    fi

    echo "< OK!"
}

# PARAMS: os arch arch_unzipsfx
function build_other_jdk {
    echo "> Building Package for $1-$2"
    compile_other $1 $2 $3 # Depend on Compile

    if [ $1 == "windows" ]; then
        name="jdk-$1-$3.exe"
    elif [ $1 == "linux" ]; then
        name="jdk-$1-$3.bin"
    fi

    rm -rf ../../$name
    ln -rs compiled/$name ../../
    echo "< OK!"
}

mkdir -p local/$jdk_major_version/downloads
mkdir -p local/$jdk_major_version/compiled

cd local/$jdk_major_version

if [ "x$TRAVIS" != "x" ]; then
    if [ "x$BUILD_X64" != "x" ]; then
        build_other_jdk windows x64 x64
        build_other_jdk linux x64 x64
    else
        # We have to save space at all cost, so force-delete x64 jdks, which might come from the build cache.
        # that's bad because they won't be cached anymore, but we have to trade time for space.
        rm -rf compiled/jdk-windows-x64.exe compiled/jdk-linux-x64.bin
    fi
    if [ "x$BUILD_X86" != "x" ]; then
        build_other_jdk windows x86-32 x86
        #build_other_jdk linux x86 i586
    else
        rm -rf compiled/jdk-windows-x86.exe compiled/jdk-linux-x86.bin
    fi
    if [ "x$BUILD_OTHER" != "x" ]; then
        build_mac_jdk
    else
        rm -rf compiled/jdk-macosx.zip
    fi
else
    if [ "x$PARALLEL" != "x" ];
    then
        build_mac_jdk &
        build_other_jdk linux x64 x64 &
        # Windows 32bit not by default build_other_jdk windows x86-32 x86 &
        build_other_jdk windows x64 x64 &
    else
        build_mac_jdk
        build_other_jdk linux x64 x64
        ## Windows 32bit not by default build_other_jdk windows x86-32 x86
        build_other_jdk windows x64 x64
        # Linux 32bit not supported... build_other_jdk linux x86-32
    fi
    
fi

if [ "x$PARALLEL" != "x" ];
then
    wait
fi
cd ../../
