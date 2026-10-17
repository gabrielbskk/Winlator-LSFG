cmake_minimum_required(VERSION 3.22.1)

add_subdirectory(winlator)
add_subdirectory(vortekrenderer)
add_subdirectory(virglrenderer)
add_subdirectory(midihandler)
add_subdirectory(libadrenotools)
add_subdirectory(gladiorenderer)

option(WINLATOR_DISCORD_RPC "Build the Android Discord Social SDK bridge" OFF)
if(WINLATOR_DISCORD_RPC)
    add_subdirectory(discordrpc)
endif()
