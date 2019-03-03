
cmake_minimum_required(VERSION 3.4.1)

#创建一个变量
file(GLOB source_file src/main/cpp/*.cpp)

add_library(
             native-lib
             SHARED
            ${source_file}
)

include_directories(src/main/cpp/include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -D__STDC_CONSTANT_MACROS -L${CMAKE_SOURCE_DIR}/src/main/cpp/libs/${ANDROID_ABI}")

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

target_link_libraries( # Specifies the target library.
                       native-lib
        #静态库顺序不能出问题，一个挨着一个
         avfilter avformat avcodec avutil swresample swscale
                       # Links the target library to the log library
                       # included in the NDK.
                        #z 是因为ffmpeg需要依赖该库
                        #android 是为了能够使用ANativeWindow
                        #OpenSLES 是为了能够播放声音
                       ${log-lib} z android OpenSLES)