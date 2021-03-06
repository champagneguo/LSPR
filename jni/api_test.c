/*******************************************************
 * File:    api_test_linux.c
 * Date:    February 2012
 * Author:  Ocean Optics, Inc.
 *
 * This is a test program to exercise some of the
 * SeaBreeze functionality.  This will not compile
 * under Windows, so if you are using Visual Studio,
 * remove this file from the project.
 *
 * LICENSE:
 *
 * SeaBreeze Copyright (C) 2014, Ocean Optics Inc
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************/

/* Includes */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>  /* For sleep() */
#include "api/seabreezeapi/SeaBreezeAPI.h"
#include "com_testseabreeze_MainActivity.h"

#include <android/log.h>
#define TAG "API_TEST"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , TAG, __VA_ARGS__)

/* Prototypes */
void test_serial_number_feature(long deviceID);
void test_spectrometer_feature(long deviceID);
void test_shutter_feature(long deviceID);
void test_light_source_feature(long deviceID);
void test_lamp_feature(long deviceID);
void test_eeprom_feature(long deviceID);
void test_irradcal_feature(long deviceID);
void test_tec_feature(long deviceID);
void test_nonlinearity_coeffs_feature(long deviceID);
void test_stray_light_coeffs_feature(long deviceID);
void test_continuous_strobe_feature(long deviceID);

/* Create a type called "testfunc_t" that is just a pointer to any function that
 * has this signature:  void func(long)
 * This will be used to invoke all of the test functions by reference.
 */
typedef void (*testfunc_t)(long);

/* Create a list of functions to run on each device that is found and opened */
static testfunc_t __test_functions[] = { test_serial_number_feature,
		test_spectrometer_feature, test_shutter_feature,
		test_light_source_feature, test_lamp_feature, test_eeprom_feature,
		test_irradcal_feature, test_tec_feature,
		test_nonlinearity_coeffs_feature, test_stray_light_coeffs_feature,
		test_continuous_strobe_feature, };

/* Get a variable that tracks how many test functions have been specified */
static const int __test_function_count = sizeof(__test_functions)
		/ sizeof(testfunc_t);

/**
 * JNIEnv *env 指向可用JNI函数表的接口指针
 * jobject thiz 是类实例的Java对象引用
 *
 */JNIEXPORT jstring JNICALL Java_com_testseabreeze_MainActivity_testFunction(
		JNIEnv *env, jobject thiz) {

	LOGE("start------>>>>>");

	int number_of_devices;
	long *device_ids;
	int i;
	int test_index;
	int flag;
	int error = 0;
	char nameBuffer[80];

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
		for (test_index = 0; test_index < __test_function_count; test_index++) {
			/* Invoke each of the test functions against this device */
			__test_functions[test_index](device_ids[i]);
		}

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

	return (*env)->NewStringUTF(env, "Hello from JNI !");

}

JNIEXPORT jint JNICALL Java_com_testseabreeze_MainActivity_testInt(JNIEnv *env,
		jobject thiz, jint x, jstring y) {
	return 3;

}

//int main() {
//	int number_of_devices;
//	long *device_ids;
//	int i;
//	int test_index;
//	int flag;
//	int error = 0;
//	char nameBuffer[80];
//
//	/* Give the driver a chance to initialize itself */
//	sbapi_initialize();
//
//	LOGE("Probing for devices...\n");
//	fflush(stdout);
//	sbapi_probe_devices();
//
////#define RS232_TEST
//#ifdef RS232_TEST
//	LOGE("Adding an STS at 9600 baud...\n");
//	/* Uncomment for Linux */
//	//sbapi_add_RS232_device_location("STS", "/dev/ttyS0", 9600);
//	//sbapi_add_RS232_device_location("STS", "/dev/ttyUSB0", 9600);
//	/* Uncomment for Windows */
//	//sbapi_add_RS232_device_location("STS", "COM1", 9600);
//	/* Uncomment for e.g. USB-RS232 adapter under OSX */
//	//sbapi_add_RS232_device_location("STS", "/dev/tty.KeySerial1", 9600);
//	//sbapi_add_RS232_device_location("STS", "/dev/tty.usbserial", 9600);
//#endif
//
//	LOGE("Getting device count...\n");
//	fflush(stdout);
//	number_of_devices = sbapi_get_number_of_device_ids();
//
//	LOGE("Device count is %d\n", number_of_devices);
//	if (0 == number_of_devices) {
//		return 0;
//	}
//
//	LOGE("Getting device IDs...\n");
//	device_ids = (long *) calloc(number_of_devices, sizeof(long));
//	number_of_devices = sbapi_get_device_ids(device_ids, number_of_devices);
//	LOGE("Got %d device ID%s.\n", number_of_devices,
//			number_of_devices == 1 ? "" : "s");
//
//	for (i = 0; i < number_of_devices; i++) {
//		LOGE("%d: Device 0x%02lX:\n", i, device_ids[i]);
//		LOGE("\tGetting device type...\n");
//		flag = sbapi_get_device_type(device_ids[i], &error, nameBuffer, 79);
//		LOGE("\t\tResult is (%d) [%s]\n", flag,
//				sbapi_get_error_string(error));
//		if (flag > 0) {
//			LOGE("\tDevice type: [%s]\n", nameBuffer);
//		}
//
//		/* Open the device */
//		LOGE("\tAttempting to open:\n");
//		flag = sbapi_open_device(device_ids[i], &error);
//		LOGE("\t\tResult is (%d) [%s]\n", flag,
//				sbapi_get_error_string(error));
//		if (flag != 0) {
//			continue;
//		}
//
//		/* Test the device */
//		for (test_index = 0; test_index < __test_function_count; test_index++) {
//			/* Invoke each of the test functions against this device */
//			__test_functions[test_index](device_ids[i]);
//		}
//
//		/* Close the device */
//		LOGE("\tAttempting to close:\n");
//		sbapi_close_device(device_ids[i], &error);
//		LOGE("\t\tResult is (%d) [%s]\n", flag,
//				sbapi_get_error_string(error));
//	}
//	free(device_ids);
//
//	LOGE("Finished testing.\n");
//
//	/* Clean up memory allocated by the driver */
//	sbapi_shutdown();
//
//	return 0;
//}

void test_serial_number_feature(long deviceID) {
	int error = 0;
	int number_of_serial_numbers;
	long *serial_number_ids = 0;
	int i;
	char buffer[80];

	LOGE("\tTesting serial number features:\n");

	LOGE("\t\tGetting number of serial numbers:\n");
	number_of_serial_numbers = sbapi_get_number_of_serial_number_features(
			deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_serial_numbers, sbapi_get_error_string(error));

	if (0 == number_of_serial_numbers) {
		LOGE("\tNo serial number capabilities found.\n");
		return;
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
	int length;
	long integration_time;
	int number_of_spectrometers;
	long *spectrometer_ids = 0;
	int i;
	double *doubleBuffer = 0;
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

		doubleBuffer = (double *) calloc(length, sizeof(double));
		LOGE("\t\t\tGetting spectrum...\n");
		length = sbapi_spectrometer_get_formatted_spectrum(deviceID,
				spectrometer_ids[i], &error, doubleBuffer, length);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));

//		for (i = 0; i < length; i++) {
//			LOGE( "\t\t\t\tPixel indices %d are: %1.2f\n", i, doubleBuffer[i]);
//		}

//		LOGE(
//				"\t\t\t\tPixel indices 19 and 20 are: %1.1f, %1.1f\n", doubleBuffer[19], doubleBuffer[20]);
		free(doubleBuffer);

		LOGE("\t\t\tGetting unformatted spectrum length\n");
		length = sbapi_spectrometer_get_unformatted_spectrum_length(deviceID,
				spectrometer_ids[i], &error);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));

		byteBuffer = (unsigned char *) calloc(length, sizeof(unsigned char));
		LOGE("\t\t\tGetting unformatted spectrum...\n");
		length = sbapi_spectrometer_get_unformatted_spectrum(deviceID,
				spectrometer_ids[i], &error, byteBuffer, length);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
		LOGE(
				"\t\t\t\tByte indices 19 and 20 are: 0x%02X, 0x%02X\n", byteBuffer[19], byteBuffer[20]);
		free(byteBuffer);

		doubleBuffer = (double *) calloc(length, sizeof(double));
		LOGE("\t\t\tGetting wavelengths...\n\n");

		LOGE("\t\t\tGetting wavelengths:deviceID:%ld\n", deviceID);
		LOGE(
				"\t\t\tGetting wavelengths:spectrometer_ids[i]:%ld\n", spectrometer_ids[i]);
		LOGE("\t\t\tGetting wavelengths:&error:%p\n", &error);
		LOGE("\t\t\tGetting wavelengths:doubleBuffer:%p\n", doubleBuffer);
		LOGE("\t\t\tGetting wavelengths:length:%d\n", length);

		length = sbapi_spectrometer_get_wavelengths(deviceID,
				spectrometer_ids[i], &error, doubleBuffer, length);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));

		for (i = 0; i < length; i++) {
			LOGE( "\t\t\t\tPixel indices %d are: %1.2f\n", i, doubleBuffer[i]);
		}
//		LOGE(
//				"\t\t\t\tPixel indices 19 and 20 are: %1.2f, %1.2f\n", doubleBuffer[19], doubleBuffer[20]);
		free(doubleBuffer);

		LOGE("\t\t\tGetting electric dark pixel count...\n");
		length = sbapi_spectrometer_get_electric_dark_pixel_count(deviceID,
				spectrometer_ids[i], &error);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
		if (length > 0) {
			edarkBuffer = (int *) calloc(length, sizeof(int));
			LOGE("\t\t\tGetting electric dark pixels...\n");
			length = sbapi_spectrometer_get_electric_dark_pixel_indices(
					deviceID, spectrometer_ids[i], &error, edarkBuffer, length);
			LOGE(
					"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
			LOGE("\t\t\tIndices: ");
			for (j = 0; j < length; j++) {
				LOGE("%d ", edarkBuffer[j]);
			}
			LOGE("\n");
			free(edarkBuffer);
		} else {
			LOGE("\t\t\t\tSpectrometer has no electric dark pixels.\n");
		}

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, spectrometer 0x%02lX\n", i, deviceID, spectrometer_ids[i]);
	}
	LOGE("\tFinished testing spectrometer capabilities.\n");

	free(spectrometer_ids);
}

void test_shutter_feature(long deviceID) {
	int error = 0;
	int number_of_shutters;
	long *shutter_ids = 0;
	int i;

	LOGE("\tTesting shutter features:\n");

	LOGE("\t\tGetting number of shutters:\n");
	number_of_shutters = sbapi_get_number_of_shutter_features(deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_shutters, sbapi_get_error_string(error));

	if (0 == number_of_shutters) {
		LOGE("\tNo shutter capabilities found.\n");
		return;
	}

	shutter_ids = (long *) calloc(number_of_shutters, sizeof(long));
	LOGE("\t\tGetting shutter feature IDs...\n");
	number_of_shutters = sbapi_get_shutter_features(deviceID, &error,
			shutter_ids, number_of_shutters);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_shutters, sbapi_get_error_string(error));

	for (i = 0; i < number_of_shutters; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, shutter 0x%02lX\n", i, deviceID, shutter_ids[i]);
		LOGE( "\t\t\tNOTE: Some shutter features are synchronized to other\n"
		"\t\t\tNOTE: events, such as spectrometer acquisitions.\n"
		"\t\t\tNOTE: This test only sends the shutter command and this\n"
		"\t\t\tNOTE: may be inadequate to actually toggle the shutter.\n"
		"\t\t\tNOTE: This test is only intended to see if the command\n"
		"\t\t\tNOTE: can be sucessfully created and sent.\n");
		LOGE("\t\t\tAttempting to close shutter.\n");
		sbapi_shutter_set_shutter_open(deviceID, shutter_ids[i], &error, 0);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tAttempting to open shutter.\n");
		sbapi_shutter_set_shutter_open(deviceID, shutter_ids[i], &error, 1);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, shutter 0x%02lX\n", i, deviceID, shutter_ids[i]);
	}
	free(shutter_ids);

	LOGE("\tFinished testing shutter capabilities.\n");
}

void test_light_source_feature(long deviceID) {
	int error = 0;
	int number_of_light_sources;
	long *light_source_ids = 0;
	int source_count;
	int i;
	int source;
	unsigned char flag;
	float intensity = 0;

	LOGE("\tTesting light source features:\n");

	LOGE("\t\tGetting number of light sources:\n");
	number_of_light_sources = sbapi_get_number_of_light_source_features(
			deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_light_sources, sbapi_get_error_string(error));

	if (0 == number_of_light_sources) {
		LOGE("\tNo light source capabilities found.\n");
		return;
	}

	light_source_ids = (long *) calloc(number_of_light_sources, sizeof(long));
	LOGE("\t\tGetting light source feature IDs...\n");
	number_of_light_sources = sbapi_get_light_source_features(deviceID, &error,
			light_source_ids, number_of_light_sources);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_light_sources, sbapi_get_error_string(error));

	for (i = 0; i < number_of_light_sources; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, light source 0x%02lX\n", i, deviceID, light_source_ids[i]);

		LOGE("\t\t\tAttempting to get light source count for feature.\n");
		source_count = sbapi_light_source_get_count(deviceID,
				light_source_ids[i], &error);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", source_count, sbapi_get_error_string(error));
		for (source = 0; source < source_count; source++) {
			LOGE("\t\t\tExamining light source %d\n", source);

			LOGE(
					"\t\t\t\tAttempting to query whether intensity control is available\n");
			flag = sbapi_light_source_has_variable_intensity(deviceID,
					light_source_ids[i], &error, source);
			LOGE(
					"\t\t\t\tResult is %s [%s]\n", 0 == flag ? "false" : "true", sbapi_get_error_string(error));
			if (0 != flag) {
				LOGE(
						"\t\t\t\tAttempting to get light source %d percent intensity\n", source);
				intensity = sbapi_light_source_get_intensity(deviceID,
						light_source_ids[i], &error, source);
				LOGE(
						"\t\t\t\t\tResult is %1.1f%% [%s]\n", intensity * 100.0, sbapi_get_error_string(error));

				LOGE(
						"\t\t\t\tAttempting to set intensity to half of maximum\n");
				sbapi_light_source_set_intensity(deviceID, light_source_ids[i],
						&error, source, 0.5);
				LOGE(
						"\t\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

				/* Re-read the intensity to see if it was applied */
				LOGE(
						"\t\t\t\tAttempting to get light source %d percent intensity\n", source);
				intensity = sbapi_light_source_get_intensity(deviceID,
						light_source_ids[i], &error, source);
				LOGE(
						"\t\t\t\t\tResult is %1.1f%% [%s]\n", intensity * 100.0, sbapi_get_error_string(error));
			}

			LOGE("\t\t\t\tAttempting to query whether enable is available\n");
			flag = sbapi_light_source_has_enable(deviceID, light_source_ids[i],
					&error, source);
			LOGE(
					"\t\t\t\tResult is %s [%s]\n", 0 == flag ? "false" : "true", sbapi_get_error_string(error));
			if (0 != flag) {
				LOGE(
						"\t\t\t\tAttempting to get light source %d enable status\n", source);
				flag = sbapi_light_source_is_enabled(deviceID,
						light_source_ids[i], &error, source);
				LOGE(
						"\t\t\t\t\tResult is %s [%s]\n", 0 == flag ? "false" : "true", sbapi_get_error_string(error));

				LOGE("\t\t\t\tAttempting to enable light source %d\n", source);
				sbapi_light_source_set_enable(deviceID, light_source_ids[i],
						&error, source, 1);
				LOGE(
						"\t\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

				LOGE(
						"\t\t\t\tAttempting to get light source %d enable status\n", source);
				flag = sbapi_light_source_is_enabled(deviceID,
						light_source_ids[i], &error, source);
				LOGE(
						"\t\t\t\t\tResult is %s [%s]\n", 0 == flag ? "false" : "true", sbapi_get_error_string(error));

				LOGE("\t\t\t\tAttempting to disable light source %d\n", source);
				sbapi_light_source_set_enable(deviceID, light_source_ids[i],
						&error, source, 0);
				LOGE(
						"\t\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

				LOGE(
						"\t\t\t\tAttempting to get light source %d enable status\n", source);
				flag = sbapi_light_source_is_enabled(deviceID,
						light_source_ids[i], &error, source);
				LOGE(
						"\t\t\t\t\tResult is %s [%s]\n", 0 == flag ? "false" : "true", sbapi_get_error_string(error));
			}
		}
	}

	free(light_source_ids);
	LOGE("\tFinished testing light source capabilities.\n");
}

void test_lamp_feature(long deviceID) {
	int error = 0;
	int number_of_lamps;
	long *lamp_ids = 0;
	int i;

	LOGE("\tTesting lamp features:\n");

	LOGE("\t\tGetting number of lamps:\n");
	number_of_lamps = sbapi_get_number_of_lamp_features(deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_lamps, sbapi_get_error_string(error));

	if (0 == number_of_lamps) {
		LOGE("\tNo lamp capabilities found.\n");
		return;
	}

	lamp_ids = (long *) calloc(number_of_lamps, sizeof(long));
	LOGE("\t\tGetting lamp feature IDs...\n");
	number_of_lamps = sbapi_get_lamp_features(deviceID, &error, lamp_ids,
			number_of_lamps);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_lamps, sbapi_get_error_string(error));

	for (i = 0; i < number_of_lamps; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, lamp 0x%02lX\n", i, deviceID, lamp_ids[i]);
		LOGE("\t\t\tNOTE: Some lamp features are synchronized to other\n"
		"\t\t\tNOTE: events, such as spectrometer acquisitions.\n"
		"\t\t\tNOTE: This test only sends the lamp command and this\n"
		"\t\t\tNOTE: may be inadequate to actually toggle the lamp.\n"
		"\t\t\tNOTE: This test is only intended to see if the command\n"
		"\t\t\tNOTE: can be sucessfully created and sent.\n");
		LOGE("\t\t\tAttempting to enable lamp.\n");
		sbapi_lamp_set_lamp_enable(deviceID, lamp_ids[i], &error, 1);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tAttempting to disable lamp.\n");
		sbapi_lamp_set_lamp_enable(deviceID, lamp_ids[i], &error, 0);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, lamp 0x%02lX\n", i, deviceID, lamp_ids[i]);
	}
	free(lamp_ids);
	LOGE("\tFinished testing lamp capabilities.\n");
}

void test_eeprom_feature(long deviceID) {
	int error = 0;
	int number_of_eeproms;
	long *eeprom_ids = 0;
	unsigned char buffer[80];
	int i;

	LOGE("\tTesting EEPROM features:\n");

	LOGE("\t\tGetting number of EEPROMs:\n");
	number_of_eeproms = sbapi_get_number_of_eeprom_features(deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_eeproms, sbapi_get_error_string(error));

	if (0 == number_of_eeproms) {
		LOGE("\tNo EEPROM capabilities found.\n");
		return;
	}

	eeprom_ids = (long *) calloc(number_of_eeproms, sizeof(long));
	LOGE("\t\tGetting EEPROM feature IDs...\n");
	number_of_eeproms = sbapi_get_eeprom_features(deviceID, &error, eeprom_ids,
			number_of_eeproms);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_eeproms, sbapi_get_error_string(error));

	for (i = 0; i < number_of_eeproms; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, eeprom 0x%02lX\n", i, deviceID, eeprom_ids[i]);

		LOGE("\t\t\tAttempting to get EEPROM slot 0...\n");
		memset(buffer, (int) 0, sizeof(buffer));
		sbapi_eeprom_read_slot(deviceID, eeprom_ids[i], &error, 0, buffer, 79);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));
		if (0 == error) {
			LOGE("\t\t\t\tEEPROM slot 0: [%s]\n", buffer);
		}

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, eeprom 0x%02lX\n", i, deviceID, eeprom_ids[i]);
	}
	free(eeprom_ids);
	LOGE("\tFinished testing EEPROM capabilities.\n");
}

void test_irradcal_feature(long deviceID) {
	int error = 0;
	int number_of_irradcals;
	long *irradcal_ids = 0;
	float buffer[20];
	int i;
	int length;
	int flag;
	float area;

	LOGE("\tTesting irradiance calibration features:\n");

	LOGE("\t\tGetting number of irradiance calibration features:\n");
	number_of_irradcals = sbapi_get_number_of_irrad_cal_features(deviceID,
			&error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_irradcals, sbapi_get_error_string(error));

	if (0 == number_of_irradcals) {
		LOGE("\tNo irradiance calibration capabilities found.\n");
		return;
	}

	irradcal_ids = (long *) calloc(number_of_irradcals, sizeof(long));
	LOGE("\t\tGetting irradiance calibration feature IDs...\n");
	number_of_irradcals = sbapi_get_irrad_cal_features(deviceID, &error,
			irradcal_ids, number_of_irradcals);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_irradcals, sbapi_get_error_string(error));

	for (i = 0; i < number_of_irradcals; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, irradcal 0x%02lX\n", i, deviceID, irradcal_ids[i]);

		// Note: this test can fail on OBP devices (STS) if no irradiance calibration
		//       has yet been written to the device (per STS Data Sheet, p26)
		LOGE("\t\t\tAttempting to get partial irradiance calibration...\n");
		memset(buffer, (int) 0, sizeof(buffer));
		length = sbapi_irrad_calibration_read(deviceID, irradcal_ids[i], &error,
				buffer, 20);
		LOGE(
				"\t\t\t\tRead %d values [%s]\n", length, sbapi_get_error_string(error));
		if (0 == error && length > 0) {
			LOGE("\t\t\t\tFirst calibration term: %1.2e\n", buffer[0]);
		}

		LOGE("\t\t\tAttempting to check for collection area...\n");
		flag = sbapi_irrad_calibration_has_collection_area(deviceID,
				irradcal_ids[i], &error);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", flag, sbapi_get_error_string(error));
		if (0 != flag) {
			LOGE("\t\t\tAttempting to read collection area...\n");
			area = sbapi_irrad_calibration_read_collection_area(deviceID,
					irradcal_ids[i], &error);
			LOGE(
					"\t\t\t\tResult is %1.2e [%s]\n", area, sbapi_get_error_string(error));
		} else {
			LOGE("\t\t\tNo collection area available.\n");
		}

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, irradcal 0x%02lX\n", i, deviceID, irradcal_ids[i]);
	}
	free(irradcal_ids);

	LOGE("\tFinished testing irradiance calibration capabilities.\n");
}

void test_tec_feature(long deviceID) {
	int error = 0;
	int number_of_tec;
	long *tec_ids = 0;
	int i;
	float temperature;

	LOGE("\tTesting TEC features:\n");

	LOGE("\t\tGetting number of TECs:\n");
	number_of_tec = sbapi_get_number_of_thermo_electric_features(deviceID,
			&error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_tec, sbapi_get_error_string(error));

	if (0 == number_of_tec) {
		LOGE("\tNo TEC capabilities found.\n");
		return;
	}

	tec_ids = (long *) calloc(number_of_tec, sizeof(long));
	LOGE("\t\tGetting TEC feature IDs...\n");
	number_of_tec = sbapi_get_thermo_electric_features(deviceID, &error,
			tec_ids, number_of_tec);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_tec, sbapi_get_error_string(error));

	for (i = 0; i < number_of_tec; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, tec 0x%02lX\n", i, deviceID, tec_ids[i]);

		LOGE("\t\t\tAttempting to read TEC temperature...\n");
		temperature = (float) sbapi_tec_read_temperature_degrees_C(deviceID,
				tec_ids[i], &error);
		LOGE(
				"\t\t\t\tResult is %1.2f [%s]\n", temperature, sbapi_get_error_string(error));

		LOGE("\t\t\tAttempting to enable TEC...\n");
		sbapi_tec_set_enable(deviceID, tec_ids[i], &error, 1);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tAttempting to set TEC temperature to -5C...\n");
		sbapi_tec_set_temperature_setpoint_degrees_C(deviceID, tec_ids[i],
				&error, -5.0);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, tec 0x%02lX\n", i, deviceID, tec_ids[i]);
	}

	free(tec_ids);
	LOGE("\tFinished testing TEC capabilities.\n");
}

void test_nonlinearity_coeffs_feature(long deviceID) {
	int error = 0;
	int number_of_nonlinearity_coeff_features;
	long *nonlinearity_coeff_feature_ids = 0;
	double buffer[10];
	int i;
	int length;

	LOGE("\tTesting nonlinearity coefficient features:\n");

	LOGE("\t\tGetting number of nonlinearity coefficient features:\n");
	number_of_nonlinearity_coeff_features =
			sbapi_get_number_of_nonlinearity_coeffs_features(deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_nonlinearity_coeff_features, sbapi_get_error_string(error));

	if (0 == number_of_nonlinearity_coeff_features) {
		LOGE("\tNo nonlinearity coefficient capabilities found.\n");
		return;
	}

	nonlinearity_coeff_feature_ids = (long *) calloc(
			number_of_nonlinearity_coeff_features, sizeof(long));
	LOGE("\t\tGetting nonlinearity coefficient feature IDs...\n");
	number_of_nonlinearity_coeff_features =
			sbapi_get_nonlinearity_coeffs_features(deviceID, &error,
					nonlinearity_coeff_feature_ids,
					number_of_nonlinearity_coeff_features);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_nonlinearity_coeff_features, sbapi_get_error_string(error));

	for (i = 0; i < number_of_nonlinearity_coeff_features; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, nonlinearity coeffs 0x%02lX\n", i, deviceID, nonlinearity_coeff_feature_ids[i]);

		LOGE("\t\t\tAttempting to get nonlinearity coefficients...\n");
		memset(buffer, (int) 0, sizeof(buffer));
		length = sbapi_nonlinearity_coeffs_get(deviceID,
				nonlinearity_coeff_feature_ids[i], &error, buffer, 10);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
		if (0 == error && length > 0) {
			LOGE("\t\t\t\tFirst calibration term: %1.2e\n", buffer[0]);
		}

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, nonlinearity coeffs 0x%02lX\n", i, deviceID, nonlinearity_coeff_feature_ids[i]);
	}
	free(nonlinearity_coeff_feature_ids);

	LOGE("\tFinished testing nonlinearity coefficient capabilities.\n");
}

void test_stray_light_coeffs_feature(long deviceID) {
	int error = 0;
	int number_of_stray_light_coeff_features;
	long *stray_light_coeff_feature_ids = 0;
	double buffer[5];
	int i;
	int length;

	LOGE("\tTesting stray light coefficient features:\n");

	LOGE("\t\tGetting number of stray light coefficient features:\n");
	number_of_stray_light_coeff_features =
			sbapi_get_number_of_stray_light_coeffs_features(deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_stray_light_coeff_features, sbapi_get_error_string(error));

	if (0 == number_of_stray_light_coeff_features) {
		LOGE("\tNo stray light coefficient capabilities found.\n");
		return;
	}

	stray_light_coeff_feature_ids = (long *) calloc(
			number_of_stray_light_coeff_features, sizeof(long));
	LOGE("\t\tGetting stray light coefficient feature IDs...\n");
	number_of_stray_light_coeff_features =
			sbapi_get_stray_light_coeffs_features(deviceID, &error,
					stray_light_coeff_feature_ids,
					number_of_stray_light_coeff_features);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_stray_light_coeff_features, sbapi_get_error_string(error));

	for (i = 0; i < number_of_stray_light_coeff_features; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, stray light coeffs 0x%02lX\n", i, deviceID, stray_light_coeff_feature_ids[i]);

		LOGE("\t\t\tAttempting to get stray light coefficients...\n");
		memset(buffer, (int) 0, sizeof(buffer));
		length = sbapi_stray_light_coeffs_get(deviceID,
				stray_light_coeff_feature_ids[i], &error, buffer, 5);
		LOGE(
				"\t\t\t\tResult is %d [%s]\n", length, sbapi_get_error_string(error));
		if (0 == error && length > 0) {
			LOGE("\t\t\t\tFirst calibration term: %1.2e\n", buffer[0]);
		}

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, stray light coeffs 0x%02lX\n", i, deviceID, stray_light_coeff_feature_ids[i]);
	}
	free(stray_light_coeff_feature_ids);

	LOGE("\tFinished testing stray light coefficient capabilities.\n");
}

void test_continuous_strobe_feature(long deviceID) {
	int error = 0;
	int number_of_cont_strobes;
	long *cont_strobe_ids = 0;
	int i;

	LOGE("\tTesting continuous strobe features:\n");

	LOGE("\t\tGetting number of continuous strobes:\n");
	number_of_cont_strobes = sbapi_get_number_of_continuous_strobe_features(
			deviceID, &error);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_cont_strobes, sbapi_get_error_string(error));

	if (0 == number_of_cont_strobes) {
		LOGE("\tNo continuous strobe capabilities found.\n");
		return;
	}

	cont_strobe_ids = (long *) calloc(number_of_cont_strobes, sizeof(long));
	LOGE("\t\tGetting continuous strobe feature IDs...\n");
	number_of_cont_strobes = sbapi_get_continuous_strobe_features(deviceID,
			&error, cont_strobe_ids, number_of_cont_strobes);
	LOGE(
			"\t\t\tResult is %d [%s]\n", number_of_cont_strobes, sbapi_get_error_string(error));

	for (i = 0; i < number_of_cont_strobes; i++) {
		LOGE(
				"\t\t%d: Testing device 0x%02lX, continuous strobe 0x%02lX\n", i, deviceID, cont_strobe_ids[i]);
		LOGE("\t\t\tAttempting to enable continous strobe.\n");
		sbapi_continuous_strobe_set_continuous_strobe_enable(deviceID,
				cont_strobe_ids[i], &error, 1);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tAttempting to set period to 20ms.\n");
		sbapi_continuous_strobe_set_continuous_strobe_period_micros(deviceID,
				cont_strobe_ids[i], &error, 20000);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tDelaying to allow verification.\n");
		sleep(2);

		LOGE("\t\t\tAttempting to set period to 50ms.\n");
		sbapi_continuous_strobe_set_continuous_strobe_period_micros(deviceID,
				cont_strobe_ids[i], &error, 50000);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE("\t\t\tDelaying to allow verification.\n");
		sleep(2);

		LOGE("\t\t\tAttempting to disable continous strobe.\n");
		sbapi_continuous_strobe_set_continuous_strobe_enable(deviceID,
				cont_strobe_ids[i], &error, 0);
		LOGE("\t\t\t\tResult is [%s]\n", sbapi_get_error_string(error));

		LOGE(
				"\t\t%d: Finished testing device 0x%02lX, continuous strobe 0x%02lX\n", i, deviceID, cont_strobe_ids[i]);
	}
	free(cont_strobe_ids);

	LOGE("\tFinished testing continuous strobe capabilities.\n");
}
