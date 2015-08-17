#include<jni.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <unistd.h>  /* For sleep() */
#include <android/log.h>
#include "api/seabreezeapi/SeaBreezeAPI.h"
#include "com_lspr_graph_RealDataActivity.h"

#define TAG "API_TEST"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , TAG, __VA_ARGS__)

void test_serial_number_feature(long deviceID);
void test_spectrometer_feature(long deviceID);
static char nameBuffer[80];
static char buffer[80];
static long integration_time;
static int length;
static double *spectrumValues;
static double *wavelength;

/*
 * Class:     com_lspr_graph_RealDataActivity
 * Method:    testInit
 * Signature: ()Z
 *
 */JNIEXPORT jboolean Java_com_lspr_1graph_RealDataActivity_testInit(JNIEnv *env,
		jobject thiz) {

	LOGE("start------>>>>>");
	char isInit = 1;
	int number_of_devices;
	long *device_ids;
	int i;
	int test_index;
	int flag;
	int error = 0;

	/* Give the driver a chance to initialize itself */
	sbapi_initialize();

	LOGE("Probing for devices...\n");

	fflush(stdout);
	sbapi_probe_devices();

	LOGE("Getting device count...\n");

	fflush(stdout);
	number_of_devices = sbapi_get_number_of_device_ids();

	LOGE("Device count is %d\n", number_of_devices);

	if (0 == number_of_devices) {
		LOGE("0 == number_of_devices\n");
		return 0;
	}

	LOGE("Getting device IDs...\n");

	device_ids = (long *) calloc(number_of_devices, sizeof(long));
	number_of_devices = sbapi_get_device_ids(device_ids, number_of_devices);

	LOGE(
			"Got %d device ID%s.\n", number_of_devices, number_of_devices == 1 ? "" : "s");

	for (i = 0; i < number_of_devices; i++) {
		LOGE("%d: Device 0x%02lX:\n", i, device_ids[i]);
		LOGE("\tGetting device type...\n");

		flag = sbapi_get_device_type(device_ids[i], &error, nameBuffer, 79);

		LOGE("\t\tResult is (%d) [%s]\n", flag, sbapi_get_error_string(error));

		if (flag > 0) {
			LOGE("\tDevice type: [%s]\n", nameBuffer);

		}

		/* Open the device */
		LOGE("\tAttempting to open:\n");

		flag = sbapi_open_device(device_ids[i], &error);
		LOGE("\t\tResult is (%d) [%s]\n", flag, sbapi_get_error_string(error));

		if (flag != 0) {
			continue;
		}

		/* Test the device */
		test_serial_number_feature(device_ids[i]);
		test_spectrometer_feature(device_ids[i]);

		/* Close the device */
		LOGE("\tAttempting to close:\n");

		sbapi_close_device(device_ids[i], &error);

		LOGE("\t\tResult is (%d) [%s]\n", flag, sbapi_get_error_string(error));
	}
	free(device_ids);

	LOGE("Finished testing.\n");

	/* Clean up memory allocated by the driver */
	sbapi_shutdown();
	LOGE("Finished------->>>>>>");

	return (jboolean) isInit;

}
/*
 * Class:     com_lspr_graph_RealDataActivity
 * Method:    testGetInteragtion
 * Signature: ()I
 */JNIEXPORT jint JNICALL Java_com_lspr_1graph_RealDataActivity_testGetInteragtion(
		JNIEnv *env, jobject thiz) {
	return (jint) integration_time;
}
/*
 * Class:     com_lspr_graph_RealDataActivity
 * Method:    testGetSpectrumType
 * Signature: ()Ljava/lang/String;
 */JNIEXPORT jstring JNICALL Java_com_lspr_1graph_RealDataActivity_testGetSpectrumType(
		JNIEnv *env, jobject thiz) {

	return (*env)->NewStringUTF(env, nameBuffer);

}
/*
 * Class:     com_lspr_graph_RealDataActivity
 * Method:    testGetSpectrumNumber
 * Signature: ()Ljava/lang/String;
 */JNIEXPORT jstring JNICALL Java_com_lspr_1graph_RealDataActivity_testGetSpectrumNumber(
		JNIEnv *env, jobject thiz) {

	return (*env)->NewStringUTF(env, buffer);

}
/*
 * Class:     com_lspr_graph_RealDataActivity
 * Method:    testGetLength
 * Signature: ()I
 */JNIEXPORT jint JNICALL Java_com_lspr_1graph_RealDataActivity_testGetLength(
		JNIEnv *env, jobject thiz) {
	return (jint) length;
}

/*
 * Class:     com_lspr_graph_RealDataActivity
 * Method:    testGetSpectrumValues
 * Signature: (I)D
 */JNIEXPORT jdouble JNICALL Java_com_lspr_1graph_RealDataActivity_testGetSpectrumValues(
		JNIEnv *env, jobject thiz, jint i) {

	return spectrumValues[i];

}

/*
 * Class:     com_lspr_graph_RealDataActivity
 * Method:    testGetWavelengthValues
 * Signature: (I)D
 */JNIEXPORT jdouble JNICALL Java_com_lspr_1graph_RealDataActivity_testGetWavelengthValues(
		JNIEnv *env, jobject thiz, jint i) {

	return wavelength[i];
}

void test_serial_number_feature(long deviceID) {
	int error = 0;
	int number_of_serial_numbers;
	long *serial_number_ids = 0;
	int i;

	LOGE("\tTesting serial number features:\n");

	LOGE("\t\tGetting number of serial numbers:\n");
	number_of_serial_numbers = sbapi_get_number_of_serial_number_features(
			deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_serial_numbers, sbapi_get_error_string(error));

	if (0 == number_of_serial_numbers) {
		LOGE("\tNo serial number capabilities found.\n");

		;
	}

	serial_number_ids = (long *) calloc(number_of_serial_numbers, sizeof(long));
	LOGE("\t\tGetting serial number feature IDs...\n");
	number_of_serial_numbers = sbapi_get_serial_number_features(deviceID,
			&error, serial_number_ids, number_of_serial_numbers);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_serial_numbers, sbapi_get_error_string(error));

	for (i = 0; i < number_of_serial_numbers; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, serial number 0x%02lX\n", i, deviceID, serial_number_ids[i]);

		LOGE("\t\t\tAttempting to get serial number...\n");
		memset(buffer, (int) 0, sizeof(buffer));
		sbapi_get_serial_number(deviceID, serial_number_ids[i], &error, buffer,
				79);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));
		if (0 == error) {
			LOGE("\t\t\t\tSerial number: [%s]\n", buffer);
		}
		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, serial number 0x%02lX\n", i, deviceID, serial_number_ids[i]);
	}

	free(serial_number_ids);

	LOGE("\tFinished testing serial number capabilities.\n");
}

void test_spectrometer_feature(long deviceID) {

	int error = 0;

	int number_of_spectrometers;
	long *spectrometer_ids = 0;
	int i;
	double *doubleBuffer1 = 0;
	double *doubleBuffer2 = 0;
	unsigned char *byteBuffer = 0;
	int *edarkBuffer = 0;
	int j;

	LOGE("\tTesting spectrometer features:\n");

	LOGE("\t\tGetting number of spectrometers:\n");
	number_of_spectrometers = sbapi_get_number_of_spectrometer_features(
			deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_spectrometers, sbapi_get_error_string(error));

	if (0 == number_of_spectrometers) {
		LOGE("\tNo spectrometer capabilities found.\n");
		return;
	}

	spectrometer_ids = (long *) calloc(number_of_spectrometers, sizeof(long));
	LOGE("\t\tGetting spectrometer feature IDs...\n");
	number_of_spectrometers = sbapi_get_spectrometer_features(deviceID, &error,
			spectrometer_ids, number_of_spectrometers);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_spectrometers, sbapi_get_error_string(error));

	for (i = 0; i < number_of_spectrometers; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, spectrometer 0x%02lX\n", i, deviceID, spectrometer_ids[i]);

		LOGE("\t\t\tAttempting to set trigger mode to 0\n");
		sbapi_spectrometer_set_trigger_mode(deviceID, spectrometer_ids[i],
				&error, 0);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tGetting minimum integration time\n");
		integration_time =
				sbapi_spectrometer_get_minimum_integration_time_micros(deviceID,
						spectrometer_ids[i], &error);
		LOGE(
				"\t\t\t\tResult is %ld [%s]\n", integration_time, sbapi_get_error_string(error));

		LOGE("\t\t\tSetting integration time to minimum:\n");
		sbapi_spectrometer_set_integration_time_micros(deviceID,
				spectrometer_ids[i], &error, integration_time);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tGetting spectrum length\n");
		length = sbapi_spectrometer_get_formatted_spectrum_length(deviceID,
				spectrometer_ids[i], &error);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));

		doubleBuffer1 = (double *) calloc(length, sizeof(double));
		LOGE("\t\t\tGetting spectrum...\n");
		length = sbapi_spectrometer_get_formatted_spectrum(deviceID,
				spectrometer_ids[i], &error, doubleBuffer1, length);
		spectrumValues = doubleBuffer1;
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));

		//		for (i = 0; i < length; i++) {
		//			LOGE( "\t\t\t\tPixel indices %d are: %1.2f\n", i, doubleBuffer[i]);
		//		}

		//		LOGE(
		//				"\t\t\t\tPixel indices 19 and 20 are: %1.1f, %1.1f\n", doubleBuffer[19], doubleBuffer[20]);
		//free(doubleBuffer);

//		LOGE("\t\t\tGetting unformatted spectrum length\n");
//		length = sbapi_spectrometer_get_unformatted_spectrum_length(deviceID,
//				spectrometer_ids[i], &error);
//		LOGE(
//				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
//
//		byteBuffer = (unsigned char *) calloc(length, sizeof(unsigned char));
//		LOGE("\t\t\tGetting unformatted spectrum...\n");
//		length = sbapi_spectrometer_get_unformatted_spectrum(deviceID,
//				spectrometer_ids[i], &error, byteBuffer, length);
//		LOGE(
//				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
//		LOGE(
//				"\t\t\t\tByte indices 19 and 20 are: 0x%02X, 0x%02X\n", byteBuffer[19], byteBuffer[20]);
//		free(byteBuffer);

		doubleBuffer2 = (double *) calloc(length, sizeof(double));
		LOGE("\t\t\tGetting wavelengths...\n\n");

		LOGE("\t\t\tGetting wavelengths:deviceID:%ld\n", deviceID);
		LOGE(
				"\t\t\tGetting wavelengths:spectrometer_ids[i]:%ld\n", spectrometer_ids[i]);
		LOGE("\t\t\tGetting wavelengths:&error:%p\n", &error);
		LOGE("\t\t\tGetting wavelengths:doubleBuffer:%p\n", doubleBuffer2);
		LOGE("\t\t\tGetting wavelengths:length:%d\n", length);

		length = sbapi_spectrometer_get_wavelengths(deviceID,
				spectrometer_ids[i], &error, doubleBuffer2, length);
		wavelength = doubleBuffer2;
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));

//		for (i = 0; i < length; i++) {
//			LOGE( "\t\t\t\tPixel indices %d are: %1.2f\n", i, doubleBuffer[i]);
//		}
		//		LOGE(
		//				"\t\t\t\tPixel indices 19 and 20 are: %1.2f, %1.2f\n", doubleBuffer[19], doubleBuffer[20]);
		//free(doubleBuffer);

		LOGE("\t\t\tGetting electric dark pixel count...\n");
//		length = sbapi_spectrometer_get_electric_dark_pixel_count(deviceID,
//				spectrometer_ids[i], &error);
//		LOGE(
//				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
//		if (length > 0) {
//			edarkBuffer = (int *) calloc(length, sizeof(int));
//			LOGE("\t\t\tGetting electric dark pixels...\n");
//			length = sbapi_spectrometer_get_electric_dark_pixel_indices(
//					deviceID, spectrometer_ids[i], &error, edarkBuffer, length);
//			LOGE(
//					"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
//			LOGE("\t\t\tIndices: ");
//			for (j = 0; j < length; j++) {
//				LOGE("%d ", edarkBuffer[j]);
//			}
//			LOGE("\n");
//			free(edarkBuffer);
//		} else {
//			LOGE("\t\t\t\tSpectrometer has no electric dark pixels.\n");
//		}

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, spectrometer 0x%02lX\n", i, deviceID, spectrometer_ids[i]);
	}
	LOGE("\tFinished testing spectrometer capabilities.\n");

	free(spectrometer_ids);
}
