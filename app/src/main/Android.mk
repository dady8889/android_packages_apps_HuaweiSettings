LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := HuaweiSettings
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, java)
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_STATIC_JAVA_LIBRARIES := \
        android-common \
        android-support-v4 \
        android-support-v7-appcompat

LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.appcompat

LOCAL_PRIVILEGED_MODULE := true

LOCAL_RESOURCE_DIR := \
	$(addprefix $(LOCAL_PATH)/, res) \
    frameworks/support/v7/appcompat/res

include $(BUILD_PACKAGE)