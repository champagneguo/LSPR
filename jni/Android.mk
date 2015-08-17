LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)
LOCAL_MODULE:= static
LOCAL_SRC_FILES := libCombine.a 
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE := api_test
LOCAL_SRC_FILES := main.c spectral_correction.c
#api_test.c seabreeze_test_posix.c
LOCAL_C_INCLUDES := $(LOCAL_PATH)/include
LOCAL_STATIC_LIBRARIES := static
LOCAL_LDLIBS := -llog
#LOCAL_LDLIBS := -L$(SYSROOT)/usr/lib -llog

#打印$(LOCAL_SRC_FILES) 信息
#$(warning $(LOCAL_SRC_FILES))
#$(warning $(LOCAL_C_INCLUDES))

LOCAL_LDFLAGS += -L$(NDK_ROOT)/sources/cxx-stl/gnu-libstdc++/4.8/libs/armeabi/ -lgnustl_static -lsupc++
#LOCAL_LDFLAGS += -shared

LOCAL_CPPFLAGS += -frtti
LOCAL_CPPFLAGS += -fexceptions


#include $(BUILD_STATIC_LIBRARY)
include $(BUILD_SHARED_LIBRARY)
#include $(BUILD_EXECUTABLE)
