cmake_minimum_required(VERSION 3.22.1)
project(DiscordRPC LANGUAGES CXX)

find_package(discord_partner_sdk REQUIRED CONFIG)
add_library(winlator_discord SHARED discord_rpc.cpp)
target_compile_features(winlator_discord PRIVATE cxx_std_20)
target_link_libraries(winlator_discord PRIVATE discord_partner_sdk::discord_partner_sdk log)
