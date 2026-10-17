#include <jni.h>
#include <malloc.h>
#include <android/log.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>

#define VK_NO_PROTOTYPES 1
#include <vulkan/vulkan.h>
#include <EGL/egl.h>

#include "winlator.h"
#include "file_utils.h"

EGLContext globalEGLContext = EGL_NO_CONTEXT;

/* Probe the system loader only: never load Qualcomm libraries to identify Mali. */
JNIEXPORT jstring JNICALL
Java_com_winlator_core_GPUHelper_vkGetRenderer(JNIEnv *env, jclass obj) {
    jstring renderer = NULL;
    VkInstance instance = VK_NULL_HANDLE;
    void* library = dlopen(LIBVULKAN_PATH, RTLD_NOW | RTLD_LOCAL);
    if (!library) return NULL;
    PFN_vkCreateInstance create = dlsym(library, "vkCreateInstance");
    PFN_vkDestroyInstance destroy = dlsym(library, "vkDestroyInstance");
    PFN_vkEnumeratePhysicalDevices enumerate = dlsym(library, "vkEnumeratePhysicalDevices");
    PFN_vkGetPhysicalDeviceProperties getProperties = dlsym(library, "vkGetPhysicalDeviceProperties");
    PFN_vkGetInstanceProcAddr getProc = dlsym(library, "vkGetInstanceProcAddr");
    if (!create || !destroy || !enumerate || !getProperties || !getProc) goto cleanup;
    VkApplicationInfo app = {.sType = VK_STRUCTURE_TYPE_APPLICATION_INFO, .apiVersion = VK_API_VERSION_1_1};
    VkInstanceCreateInfo info = {.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO, .pApplicationInfo = &app};
    VkResult result = create(&info, NULL, &instance);
    if (result != VK_SUCCESS) {
        app.apiVersion = VK_API_VERSION_1_0;
        if (create(&info, NULL, &instance) != VK_SUCCESS) goto cleanup;
    }
    uint32_t count = 1;
    VkPhysicalDevice device = VK_NULL_HANDLE;
    result = enumerate(instance, &count, &device);
    if ((result != VK_SUCCESS && result != VK_INCOMPLETE) || count == 0 || !device) goto cleanup;
    VkPhysicalDeviceProperties properties = {0};
    getProperties(device, &properties);
    renderer = (*env)->NewStringUTF(env, properties.deviceName);
    PFN_vkGetPhysicalDeviceProperties2 properties2 = (PFN_vkGetPhysicalDeviceProperties2)getProc(instance, "vkGetPhysicalDeviceProperties2");
    PFN_vkEnumerateDeviceExtensionProperties extensions = (PFN_vkEnumerateDeviceExtensionProperties)getProc(instance, "vkEnumerateDeviceExtensionProperties");
    VkBool32 driverProperties = properties.apiVersion >= VK_API_VERSION_1_2;
    uint32_t extensionCount = 0;
    if (!driverProperties && extensions && extensions(device, NULL, &extensionCount, NULL) == VK_SUCCESS && extensionCount) {
        VkExtensionProperties* list = calloc(extensionCount, sizeof(*list));
        if (list) {
            if (extensions(device, NULL, &extensionCount, list) == VK_SUCCESS)
                for (uint32_t i = 0; i < extensionCount; i++)
                    if (!strcmp(list[i].extensionName, VK_KHR_DRIVER_PROPERTIES_EXTENSION_NAME)) driverProperties = VK_TRUE;
            free(list);
        }
    }
    if (app.apiVersion >= VK_API_VERSION_1_1 && properties2 && driverProperties) {
        VkPhysicalDeviceDriverProperties driver = {.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_DRIVER_PROPERTIES};
        VkPhysicalDeviceProperties2 propertiesInfo = {.sType = VK_STRUCTURE_TYPE_PHYSICAL_DEVICE_PROPERTIES_2, .pNext = &driver};
        properties2(device, &propertiesInfo);
        __android_log_print(ANDROID_LOG_INFO, "AuroraGPU", "Vulkan renderer=%s driver=%s info=%s", properties.deviceName, driver.driverName, driver.driverInfo);
    }
cleanup:
    if (instance && destroy) destroy(instance, NULL);
    dlclose(library);
    return renderer;
}

JNIEXPORT jobjectArray JNICALL
Java_com_winlator_core_GPUHelper_vkGetDeviceExtensions(JNIEnv *env, jclass obj) {
    VkInstanceCreateInfo createInfo = {0};
    createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;
    VkExtensionProperties* properties = NULL;
    jobjectArray stringArray = NULL;
    VkResult result;
    VkInstance instance = VK_NULL_HANDLE;

    void* libvulkan = dlopen(LIBVULKAN_PATH, RTLD_NOW | RTLD_LOCAL);
    if (!libvulkan) goto done;

    PFN_vkCreateInstance vkCreateInstance = dlsym(libvulkan, "vkCreateInstance");
    PFN_vkDestroyInstance vkDestroyInstance = dlsym(libvulkan, "vkDestroyInstance");
    PFN_vkEnumeratePhysicalDevices vkEnumeratePhysicalDevices = dlsym(libvulkan, "vkEnumeratePhysicalDevices");
    PFN_vkEnumerateDeviceExtensionProperties vkEnumerateDeviceExtensionProperties = dlsym(libvulkan, "vkEnumerateDeviceExtensionProperties");

    if (!vkCreateInstance || !vkDestroyInstance || !vkEnumeratePhysicalDevices || !vkEnumerateDeviceExtensionProperties) goto done;

    result = vkCreateInstance(&createInfo, NULL, &instance);
    if (result != VK_SUCCESS) goto done;

    uint32_t deviceCount = 1;
    VkPhysicalDevice physicalDevice;
    result = vkEnumeratePhysicalDevices(instance, &deviceCount, &physicalDevice);
    if ((result != VK_SUCCESS && result != VK_INCOMPLETE) || deviceCount == 0) goto done;

    uint32_t propertyCount = 0;
    result = vkEnumerateDeviceExtensionProperties(physicalDevice, NULL, &propertyCount, NULL);
    if (result != VK_SUCCESS || propertyCount == 0) goto done;

    properties = calloc(propertyCount, sizeof(VkExtensionProperties));
    if (!properties) goto done;
    result = vkEnumerateDeviceExtensionProperties(physicalDevice, NULL, &propertyCount, properties);
    if (result != VK_SUCCESS) goto done;

    stringArray = (*env)->NewObjectArray(env, propertyCount, (*env)->FindClass(env, "java/lang/String"), 0);
    for (int i = 0; i < propertyCount; i++) {
        jstring string = (*env)->NewStringUTF(env, properties[i].extensionName);
        (*env)->SetObjectArrayElement(env, stringArray, i, string);
        (*env)->DeleteLocalRef(env, string);
    }

done:
    if (instance) vkDestroyInstance(instance, NULL);
    if (properties) free(properties);
    if (!stringArray) stringArray = (*env)->NewObjectArray(env, 0, (*env)->FindClass(env, "java/lang/String"), 0);
    if (libvulkan) dlclose(libvulkan);
    return stringArray;
}

JNIEXPORT jint JNICALL
Java_com_winlator_core_GPUHelper_vkGetApiVersion() {
    int version = 0;
    char* content = fileGetContents(APP_CACHE_DIR "/.vk-api-version", NULL, NULL);
    if (content) {
        version = strtol(content, NULL, 10);
        MEMFREE(content);
        if (version > 0) return version;
    }

    VkResult result;
    VkInstance instance = VK_NULL_HANDLE;

    void* libvulkan = dlopen(LIBVULKAN_PATH, RTLD_NOW | RTLD_LOCAL);
    if (!libvulkan) goto done;

    PFN_vkCreateInstance vkCreateInstance = dlsym(libvulkan, "vkCreateInstance");
    PFN_vkDestroyInstance vkDestroyInstance = dlsym(libvulkan, "vkDestroyInstance");
    PFN_vkEnumeratePhysicalDevices vkEnumeratePhysicalDevices = dlsym(libvulkan, "vkEnumeratePhysicalDevices");
    PFN_vkGetPhysicalDeviceProperties vkGetPhysicalDeviceProperties = dlsym(libvulkan, "vkGetPhysicalDeviceProperties");
    PFN_vkGetPhysicalDeviceFeatures vkGetPhysicalDeviceFeatures = dlsym(libvulkan, "vkGetPhysicalDeviceFeatures");

    if (!vkCreateInstance || !vkDestroyInstance || !vkEnumeratePhysicalDevices || !vkGetPhysicalDeviceProperties || !vkGetPhysicalDeviceFeatures) goto done;

    VkInstanceCreateInfo createInfo = {0};
    createInfo.sType = VK_STRUCTURE_TYPE_INSTANCE_CREATE_INFO;

    result = vkCreateInstance(&createInfo, NULL, &instance);
    if (result != VK_SUCCESS) goto done;

    uint32_t deviceCount = 1;
    VkPhysicalDevice physicalDevice;
    result = vkEnumeratePhysicalDevices(instance, &deviceCount, &physicalDevice);
    if ((result != VK_SUCCESS && result != VK_INCOMPLETE) || deviceCount == 0) goto done;

    VkPhysicalDeviceProperties properties = {0};
    vkGetPhysicalDeviceProperties(physicalDevice, &properties);

    version = properties.apiVersion;
    if (version >= VK_MAKE_VERSION(1, 3, 0)) {
        VkPhysicalDeviceFeatures features = {0};
        vkGetPhysicalDeviceFeatures(physicalDevice, &features);
        VkPhysicalDeviceFeatures requiredFeatures = {VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_FALSE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_FALSE, VK_TRUE, VK_TRUE, VK_FALSE, VK_FALSE, VK_TRUE, VK_TRUE, VK_TRUE, VK_FALSE, VK_TRUE, VK_TRUE, VK_TRUE, VK_TRUE, VK_FALSE, VK_TRUE, VK_TRUE};

        for (int offset = 0; offset < sizeof(VkPhysicalDeviceFeatures); offset += sizeof(VkBool32)) {
            VkBool32 srcValue = *(VkBool32*)(((char*)&features) + offset);
            VkBool32 dstValue = *(VkBool32*)(((char*)&requiredFeatures) + offset);

            if (srcValue != dstValue) {
                version = VK_MAKE_VERSION(1, 2, 0);
                break;
            }
        }
    }

    char value[32] = {0};
    sprintf(value, "%d", version);
    filePutContents(APP_CACHE_DIR "/.vk-api-version", value, strlen(value));

done:
    if (instance) vkDestroyInstance(instance, NULL);
    if (libvulkan) dlclose(libvulkan);
    return version;
}

JNIEXPORT void JNICALL
Java_com_winlator_core_GPUHelper_setGlobalEGLContext(JNIEnv *env, jclass obj) {
    globalEGLContext = eglGetCurrentContext();
}
