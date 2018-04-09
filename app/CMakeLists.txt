# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

include_directories(src/main/cpp/cryptoc)
add_library(crypt-lib SHARED src/main/cpp/cryptoc.c)
find_library(log-lib log)
add_subdirectory(src/main/cpp/cryptoc)
link_directories(src/main/cpp/cryptoc)

target_link_libraries(crypt-lib cryptoc ${log-lib})