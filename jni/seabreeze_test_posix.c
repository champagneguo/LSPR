/*******************************************************
 * File:    seabreeze_test_linux.c
 * Date:    July 2009
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

#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <unistd.h>
#include "api/SeaBreezeWrapper.h"
#include "com_testseabreeze_MainActivity.h"

#include <android/log.h>
#define TAG "Test_Posix"
#define LOGV(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG  , TAG, __VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO   , TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN   , TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR  , TAG, __VA_ARGS__)

void speed_test(int index);
void get_spectrum_test(int index);
void get_raw_spectrum_test(int index);
void get_wavelengths_test(int index);
void read_eeprom_test(int index);
void test_corrections(int index);
void strobe_enable_test(int index);
void trigger_mode_test(int index);
void tec_test(int index);
void test_irrad_cal(int index);
void read_serial_number_test(int index);
void shutter_test(int index);
void light_source_test(int index);

/* Prototypes from spectral_correction.c */
int is_electric_dark_supported(int index, int *error_code);
int get_edark_corrected_spectrum(int index, int *error_code, double *buffer,
		int buffer_length);
int do_nonlinearity_correction(int index, int *error_code, double *pixels,
		double pixel_count);

const char* get_error_string(int error) {
	static char buffer[32];
	seabreeze_get_error_string(error, buffer, sizeof(buffer));
	return buffer;
}

JNIEXPORT jstring JNICALL Java_com_testseabreeze_MainActivity_testposixFunction(
		JNIEnv *env, jobject thiz) {

	LOGE("start------Java_com_testseabreeze_MainActivity_testposixFunction");

	main();

	LOGE("end------Java_com_testseabreeze_MainActivity_testposixFunction");

	return (*env)->NewStringUTF(env, "Hello test posix !");
}

int main() {
	int flag;
	int error;
	char type[16];
	int device_count = 0;
	int i;

	for (i = 0; i < SEABREEZE_MAX_DEVICES; i++) {
		LOGE("Opening spectrometer %d.\n", i);
		flag = seabreeze_open_spectrometer(i, &error);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		if (0 == flag) {
			device_count++;
		} else {
			break;
		}
	}

	for (i = 0; i < device_count; i++) {
		LOGE("Getting device %d name.\n", i);
		seabreeze_get_model(i, &error, type, sizeof(type));
		LOGE("Result is (%s) [%s]\n", type, get_error_string(error));
	}

	for (i = 0; i < device_count; i++) {
		LOGE("Setting device %d integration time to 100ms.\n", i);
		seabreeze_set_integration_time_microsec(i, &error, 100000);
		LOGE("Result is [%s]\n", get_error_string(error));

	}

	for (i = 0; i < device_count; i++) {
		LOGE("Beginning test suite for device %d\n", i);
		read_serial_number_test(i);
		tec_test(i);
		strobe_enable_test(i);
		trigger_mode_test(i);
		read_eeprom_test(i);
		test_irrad_cal(i);
		get_wavelengths_test(i);
		get_spectrum_test(i);
		get_raw_spectrum_test(i);
		shutter_test(i);
		light_source_test(i);
		speed_test(i);
		test_corrections(i);
	}

	for (i = 0; i < device_count; i++) {
		LOGE("Closing spectrometer %d.\n", i);
		flag = seabreeze_close_spectrometer(i, &error);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
	}

	seabreeze_shutdown();

	return 0;
}

void tec_test(int index) {

	int error;
	double temp;

	LOGE("Setting TEC enable to false\n");
	seabreeze_set_tec_enable(index, &error, 0);
	LOGE("Result is [%s]\n", get_error_string(error));

	if (0 != error) {
		LOGE("Bailing out of lengthy TEC test since not supported.\n");
		return;
	}

	usleep(1000000);

	LOGE("Setting TEC temperature to -5C\n");
	seabreeze_set_tec_temperature(index, &error, -5);
	LOGE("Result is [%s]\n", get_error_string(error));

	LOGE("Setting TEC enable to true\n");
	seabreeze_set_tec_enable(index, &error, 1);
	LOGE("Result is [%s]\n", get_error_string(error));

	LOGE("Getting TEC temperature\n");
	temp = seabreeze_read_tec_temperature(index, &error);
	LOGE("Result is %1.2f C [%s]\n", temp, get_error_string(error));

	usleep(1000000);
	LOGE("Getting TEC temperature\n");
	temp = seabreeze_read_tec_temperature(index, &error);
	LOGE("Result is %1.2f C [%s]\n", temp, get_error_string(error));

}

void strobe_enable_test(int index) {

	int error;

	LOGE("Setting strobe/lamp enable to true\n");
	seabreeze_set_strobe_enable(index, &error, 1);
	LOGE("Result is [%s]\n", get_error_string(error));
	usleep(500000);
	LOGE("Setting strobe/lamp enable to false\n");
	seabreeze_set_strobe_enable(index, &error, 0);
	LOGE("Result is [%s]\n", get_error_string(error));

}

void trigger_mode_test(int index) {

	int error;

	LOGE("Setting trigger mode to 1\n");
	seabreeze_set_trigger_mode(index, &error, 1);
	LOGE("Result is [%s]\n", get_error_string(error));
	usleep(500000);
	LOGE("Setting trigger mode to 0\n");
	seabreeze_set_trigger_mode(index, &error, 0);
	LOGE("Result is [%s]\n", get_error_string(error));

}

void read_serial_number_test(int index) {
	char serial_number[32];
	int flag;
	int error;

	LOGE("Getting serial number.\n");
	flag = seabreeze_get_serial_number(index, &error, serial_number, 32);
	LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
	LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
	serial_number[31] = '\0';
	if (flag > 0) {
		LOGE("Serial number: [%s]\n", serial_number);
		LOGE("Serial number: [%s]\n", serial_number);
	}
}

void read_eeprom_test(int index) {
	unsigned char serial_number[17];
	int flag;
	int error;

	LOGE("Getting EEPROM slot 1.\n");
	flag = seabreeze_read_eeprom_slot(index, &error, 1, serial_number, 17);
	LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
	LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
	serial_number[16] = '\0';
	if (flag > 0) {
		LOGE("From slot 1: %s\n", serial_number);
		LOGE("From slot 1: %s\n", serial_number);

	}
}

void test_irrad_cal(int index) {
	float calibration[4096];
	int flag;
	int error;
	float area;

	LOGE("Getting irradiance calibration.\n");
	flag = seabreeze_read_irrad_calibration(index, &error, calibration, 4096);
	LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));

	if (flag > 100) {
		LOGE("Cal factor 100: %1.4e\n", calibration[100]);

	}

	/* This is optional because it may alter the device.  This can be
	 * enabled for testing, but be aware that any problems may erase the
	 * irradiance calibration stored on the device.
	 */
//#define __TEST_WRITE_IRRAD_CALIBRATION
#ifdef __TEST_WRITE_IRRAD_CALIBRATION
	if(0 != flag && 0 == error) {
		LOGE("Writing back irradiance calibration.\n");
		/* Attempt to write back the calibration */
		flag = seabreeze_write_irrad_calibration(index, &error, calibration, flag);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		LOGE("Reading back calibration:\n");
		flag = seabreeze_read_irrad_calibration(index, &error, calibration, 4096);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		if(flag > 100) {
			LOGE("Cal factor 100: %1.4e\n", calibration[100]);
		}
	}
#endif /* __TEST_WRITE_IRRAD_CALIBRATION */

	LOGE("Checking whether device has irradiance collection area stored.\n");
	flag = seabreeze_has_irrad_collection_area(index, &error);
	LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));

	LOGE("Attempting to read irradiance collection area.\n");
	area = seabreeze_read_irrad_collection_area(index, &error);
	LOGE("Result is [%s]\n", get_error_string(error));

	if (0 == error) {
		LOGE("Reported collection area is [%f]\n", area);
	}

#ifdef __TEST_WRITE_IRRAD_CALIBRATION
	if(0 == error) {
		LOGE("Attempting to write back collection area.\n");
		seabreeze_write_irrad_collection_area(index, &error, area);
		LOGE("Result is [%s]\n", get_error_string(error));
		LOGE("Reading back collection area:\n");
		area = seabreeze_read_irrad_collection_area(index, &error);
		LOGE("Result is [%s]\n", get_error_string(error));
		if(0 == error) {
			LOGE("Reported collection area is [%f]\n", area);
		}
	}
#endif /* __TEST_WRITE_IRRAD_CALIBRATION */

}

void get_spectrum_test(int index) {

	int error;
	int flag;
	int spec_length;
	double *spectrum = 0;

	LOGE("Getting formatted spectrum length.\n");

	spec_length = seabreeze_get_formatted_spectrum_length(index, &error);
	LOGE("Result is (%d) [%s]\n", spec_length, get_error_string(error));

	if (spec_length > 0) {
		spectrum = (double *) calloc((size_t) spec_length, sizeof(double));

		LOGE("Getting a formatted spectrum.\n");

		flag = seabreeze_get_formatted_spectrum(index, &error, spectrum,
				spec_length);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		LOGE("Pixel value 20 is %1.2f\n", spectrum[20]);

		free(spectrum);
	}
}

void shutter_test(int index) {
	int error;
	int spec_length;
	double *spectrum = 0;

	LOGE("Attempting to close shutter.\n");
	seabreeze_set_shutter_open(index, &error, 0);
	LOGE("Result is [%s]\n", get_error_string(error));
	spec_length = seabreeze_get_formatted_spectrum_length(index, &error);
	if (spec_length > 0) {
		spectrum = (double *) calloc((size_t) spec_length, sizeof(double));
		seabreeze_get_formatted_spectrum(index, &error, spectrum, spec_length);
		free(spectrum);
	}
	LOGE("Attempting to open shutter.\n");
	seabreeze_set_shutter_open(index, &error, 1);
	LOGE("Result is [%s]\n", get_error_string(error));
	if (spec_length > 0) {
		spectrum = (double *) calloc((size_t) spec_length, sizeof(double));
		seabreeze_get_formatted_spectrum(index, &error, spectrum, spec_length);
		free(spectrum);
	}
}

void light_source_test(int index) {
	int error = 0;
	int light_source_count;
	int i;

	LOGE("Attempting to get number of light sources.\n");
	light_source_count = seabreeze_get_light_source_count(index, &error);
	LOGE("Result is %d [%s]\n", light_source_count, get_error_string(error));

	for (i = 0; i < light_source_count; i++) {
		LOGE("Attempting to enable light source %d\n", i);
		seabreeze_set_light_source_enable(index, &error, i, 1);
		LOGE("Result is [%s]\n", get_error_string(error));
		LOGE("Attempting to set light source %d intensity to 50%%\n", i);
		seabreeze_set_light_source_intensity(index, &error, i, 0.5);
		LOGE("Result is [%s]\n", get_error_string(error));
		LOGE("Attempting to disable light source %d\n", i);
		seabreeze_set_light_source_enable(index, &error, i, 0);
		LOGE("Result is [%s]\n", get_error_string(error));
	}
}

void get_raw_spectrum_test(int index) {

	int flag;
	int error;
	int raw_length;
	unsigned char *raw_spectrum;

	LOGE("Getting unformatted spectrum length.\n");
	raw_length = seabreeze_get_unformatted_spectrum_length(index, &error);
	LOGE("Result is (%d) [%s]\n", raw_length, get_error_string(error));

	if (raw_length > 0) {
		raw_spectrum = (unsigned char *) calloc((size_t) raw_length,
				sizeof(unsigned char));

		LOGE("Getting an unformatted spectrum.\n");
		flag = seabreeze_get_unformatted_spectrum(index, &error, raw_spectrum,
				raw_length);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		LOGE(
				"Buffer values 19 and 20 are 0x%02X%02X\n", raw_spectrum[19], raw_spectrum[20]);

		free(raw_spectrum);
	}
}

void get_wavelengths_test(int index) {

	int error;
	int flag;
	int spec_length;
	double *wls = 0;

	LOGE("Getting formatted spectrum length.\n");
	spec_length = seabreeze_get_formatted_spectrum_length(index, &error);
	LOGE("Result is (%d) [%s]\n", spec_length, get_error_string(error));

	if (spec_length > 0) {
		wls = (double *) calloc((size_t) spec_length, sizeof(double));

		LOGE("Getting wavelengths.\n");
		flag = seabreeze_get_wavelengths(index, &error, wls, spec_length);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		LOGE("Pixel 20 is wavelength %1.2f nm\n", wls[20]);

		free(wls);
	}
}

void test_corrections(int index) {
	int error = 0;
	int flag;
	int spec_length;
	double *spectrum = 0;

	LOGE("Testing electric dark and nonlinearity correction.\n");
	if (0 == is_electric_dark_supported(index, &error)) {
		LOGE("Electric dark correction is not supported for this device.\n");
		return;
	}

	LOGE("Getting formatted spectrum length.\n");
	spec_length = seabreeze_get_formatted_spectrum_length(index, &error);
	LOGE("Result is (%d) [%s]\n", spec_length, get_error_string(error));

	if (spec_length > 0) {
		spectrum = (double *) calloc((size_t) spec_length, sizeof(double));

		LOGE("Getting a spectrum corrected for electrical dark.\n");
		flag = get_edark_corrected_spectrum(index, &error, spectrum,
				spec_length);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		LOGE("Pixel value 20 is %1.2f\n", spectrum[20]);

		LOGE("Applying nonlinearity correction\n");
		flag = do_nonlinearity_correction(index, &error, spectrum, spec_length);
		LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));
		LOGE("Pixel value 20 is %1.2f\n", spectrum[20]);

		free(spectrum);
	}
}

void speed_test(int index) {
	struct timeval start;
	struct timeval end;
	int error;
	unsigned char c;
	int scans = 100;
	long usec;
	int i;
	int flag;
	long minimum_time;

	LOGE("Getting minimum integration time from device.\n");
	minimum_time = seabreeze_get_min_integration_time_microsec(index, &error);
	LOGE(
			"Minimum is %ld microseconds, result is [%s]\n", minimum_time, get_error_string(error));
	if (minimum_time < 0) {
		/* If there was an error, reset to a time that is supported widely. */
		minimum_time = 15000;
	}

	LOGE("Setting integration time to %ld usec.\n", minimum_time);
	seabreeze_set_integration_time_microsec(index, &error, minimum_time);
	LOGE("Result is [%s]\n", get_error_string(error));

	LOGE("Starting speed test with %d scans.\n", scans);

	gettimeofday(&start, NULL);
	for (i = 0; i < scans; i++) {
		flag = seabreeze_get_unformatted_spectrum(index, &error, &c, 1);
		if (error != 0) {
			LOGE(
					"Read error (%s) on speed test at iteration %d\n", get_error_string(error), i);
			break;
		}
	}
	gettimeofday(&end, NULL);

	time_t secDiff = end.tv_sec - start.tv_sec;
	usec = (long) (end.tv_usec - start.tv_usec); /* This may be negative */
	usec += secDiff * 1e6;

	LOGE("Result is (%d) [%s]\n", flag, get_error_string(error));

	LOGE(
			"Average acquisition time (msec) over %d scans: %1.3f\n", scans, usec / (1.0 * scans * 1000));
	LOGE("Total time elapsed: %1.2f seconds\n", usec / 1e6);
}
