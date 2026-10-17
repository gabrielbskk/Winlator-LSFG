#define DISCORDPP_IMPLEMENTATION
#include "discordpp.h"
#include <jni.h>
#include <android/log.h>
#include <memory>
#include <string>

namespace {
// All methods and SDK callbacks run on the same Java HandlerThread.
struct Presence {
    std::unique_ptr<discordpp::Client> client;
    int result = 0; // idle, pending, published, failed
    uint64_t revision = 0;
};
using Handle = std::shared_ptr<Presence>;

Handle& state(jlong handle) { return *reinterpret_cast<Handle*>(handle); }

void fail(JNIEnv* env, const char* message) {
    __android_log_print(ANDROID_LOG_WARN, "DiscordRPC", "%s", message);
    env->ThrowNew(env->FindClass("java/lang/IllegalStateException"), message);
}
}

extern "C" JNIEXPORT jlong JNICALL
Java_com_winlator_discord_DiscordSdkBridge_create(JNIEnv* env, jclass, jlong appId) {
    try {
        if (appId <= 0 || discordpp::Client::GetVersionMajor() < 1 ||
            (discordpp::Client::GetVersionMajor() == 1 && discordpp::Client::GetVersionMinor() < 10)) {
            fail(env, "Android RPC requires Discord Social SDK 1.10+ and a valid Application ID");
            return 0;
        }
        auto presence = std::make_shared<Presence>();
        presence->client = std::make_unique<discordpp::Client>();
        presence->client->SetApplicationId(static_cast<uint64_t>(appId));
        // Direct Android RPC: no OAuth, gateway connection, voice, or user token.
        return reinterpret_cast<jlong>(new Handle(std::move(presence)));
    } catch (const std::exception& e) {
        fail(env, e.what());
        return 0;
    }
}

extern "C" JNIEXPORT void JNICALL
Java_com_winlator_discord_DiscordSdkBridge_update(JNIEnv* env, jclass, jlong handle,
                                                 jstring title, jlong startSeconds) {
    const char* chars = env->GetStringUTFChars(title, nullptr);
    if (!chars) return;
    std::string name(chars);
    env->ReleaseStringUTFChars(title, chars);
    try {
        auto& presence = state(handle);
        const auto revision = ++presence->revision;
        presence->result = 1;
        discordpp::Activity activity;
        activity.SetType(discordpp::ActivityTypes::Playing);
        activity.SetDetails(name);
        activity.SetState("Winlator LSFG | Android");
        discordpp::ActivityTimestamps timestamps;
        timestamps.SetStart(static_cast<uint64_t>(startSeconds));
        activity.SetTimestamps(timestamps);
        std::weak_ptr<Presence> weak = presence;
        presence->client->UpdateRichPresence(activity, [weak, revision](discordpp::ClientResult result) {
            if (auto p = weak.lock(); p && p->revision == revision) {
                p->result = result.Successful() ? 2 : 3;
                if (!result.Successful())
                    __android_log_print(ANDROID_LOG_WARN, "DiscordRPC", "Rich Presence update failed");
            }
        });
    } catch (const std::exception& e) { fail(env, e.what()); }
}

extern "C" JNIEXPORT jint JNICALL
Java_com_winlator_discord_DiscordSdkBridge_poll(JNIEnv* env, jclass, jlong handle) {
    try {
        discordpp::RunCallbacks();
        return state(handle)->result;
    } catch (const std::exception& e) { fail(env, e.what()); return 3; }
}

extern "C" JNIEXPORT void JNICALL
Java_com_winlator_discord_DiscordSdkBridge_clear(JNIEnv* env, jclass, jlong handle) {
    try {
        auto& presence = state(handle);
        ++presence->revision;
        presence->result = 0;
        presence->client->ClearRichPresence();
    } catch (const std::exception& e) { fail(env, e.what()); }
}

extern "C" JNIEXPORT void JNICALL
Java_com_winlator_discord_DiscordSdkBridge_destroy(JNIEnv*, jclass, jlong handle) {
    delete reinterpret_cast<Handle*>(handle);
}
