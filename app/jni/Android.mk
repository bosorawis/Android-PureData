LOCAL_PATH := $(call my-dir)

#---------------------------------------------------------------

include $(CLEAR_VARS)
LOCAL_MODULE := pd
LOCAL_EXPORT_C_INCLUDES := ../../PdCore/jni/libpd/pure-data/src
LOCAL_SRC_FILES := ../../PdCore/libs/$(TARGET_ARCH_ABI)/libpd.so
ifneq ($(MAKECMDGOALS),clean)
    include $(PREBUILT_SHARED_LIBRARY)
endif


#---------------------------------------------------------------
include $(CLEAR_VARS)
LOCAL_MODULE := freeverb_tilde
LOCAL_CFLAGS := -DPD
LOCAL_SRC_FILES := freeverb~.c
LOCAL_SHARED_LIBRARIES = pd
include $(BUILD_SHARED_LIBRARY)

#---------------------------------------------------------------

include $(CLEAR_VARS)
LOCAL_MODULE := spigot_tilde
LOCAL_CFLAGS := -DPD
LOCAL_SRC_FILES := spigot~.c
LOCAL_SHARED_LIBRARIES = pd
include $(BUILD_SHARED_LIBRARY)

#---------------------------------------------------------------

include $(CLEAR_VARS)
LOCAL_MODULE := arraycopy
LOCAL_CFLAGS := -DPD
LOCAL_SRC_FILES := arraycopy.c
LOCAL_SHARED_LIBRARIES = pd
include $(BUILD_SHARED_LIBRARY)

#---------------------------------------------------------------

include $(CLEAR_VARS)
LOCAL_MODULE := arraysize
LOCAL_CFLAGS := -DPD
LOCAL_SRC_FILES := arraysize.c
LOCAL_SHARED_LIBRARIES = pd
include $(BUILD_SHARED_LIBRARY)

#---------------------------------------------------------------

include $(CLEAR_VARS)
LOCAL_MODULE := demux
LOCAL_CFLAGS := -DPD
LOCAL_SRC_FILES := demultiplex.c
LOCAL_SHARED_LIBRARIES = pd
include $(BUILD_SHARED_LIBRARY)

#---------------------------------------------------------------
