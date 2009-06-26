#ifndef __INC_SCANLIB_H
#define __INC_SCANLIB_H

#ifdef __cplusplus
extern "C" {
#endif

#if defined(WIN32) && ! defined(STANDALONE)
#   include <windows.h>
#   ifdef BUILD_DLL
        /* DLL export */
#       define EXPORT __declspec(dllexport)
#   else
        /* EXE import */
#       define EXPORT __declspec(dllimport)
#   endif
#else
#   define EXPORT
#endif


#ifdef WIN32
#   include <windows.h>
#   include <tchar.h>
#else
#   define char   char
#   define _T(x)   x
#   define _tmain  main
#endif

typedef struct sScPixelLoc {
	int x;
	int y;
} ScPixelLoc;

typedef struct sScPixelFrame {
	int x0; // top
	int y0; // left
	int x1; // bottom
	int y1; // right
} ScPixelFrame;

/**
 * Specifies the region to scan.
 */
typedef struct sScFrame {
	int frameId;
	double x0; // top
	double y0; // left
	double x1; // bottom
	double y1; // right
} ScFrame;

const int SC_SUCCESS               =  0;
const int SC_FAIL                  = -1;
const int SC_TWAIN_UAVAIL          = -2;
const int SC_CALIBRATOR_NO_REGIONS = -3;
const int SC_CALIBRATOR_ERROR      = -4;
const int SC_INI_FILE_ERROR        = -5;
const int SC_INVALID_DPI           = -6;
const int SC_INVALID_PLATE_NUM     = -7;
const int SC_FILE_SAVE_ERROR       = -8;
const int SC_INVALID_VALUE         = -9;

EXPORT int slIsTwainAvailable();

typedef int (*SL_IS_TWAIN_AVAILABLE) ();

EXPORT int slSelectSourceAsDefault();

typedef int (*SL_SELECT_SOURCE_AS_DEFAULT) ();

/**
 * Brightness value to be used for scanning.
 *
 * @param brightness a value between -1000 and 1000.
 *
 * @return SC_SUCCESS if valid. SC_INVALID_VALUE if bad value.
 */
EXPORT int slConfigScannerBrightness(int brightness);

typedef int (*SL_CONFIG_SCANNER_BRIGHTNESS) (int brightness);

/**
 * Contrast value to be used for scanning.
 *
 * @param contrast a value between -1000 and 1000.
 *
 * @return SC_SUCCESS if valid. SC_INVALID_VALUE if bad value.
 */
EXPORT int slConfigScannerContrast(int contrast);

typedef int (*SL_CONFIG_SCANNER_CONTRAST) (int contrast);

EXPORT int slConfigPlateFrame(unsigned plateNum, double left, double top,
		double right, double bottom);

typedef int (*SL_CONFIG_PLATE_FRAME) (unsigned, double x0, double y0, double x1,
		double y1);

EXPORT int slScanImage(unsigned dpi, double left, double top,
		double right, double bottom, char * filename);

typedef int (*SL_SCAN_IMAGE) (char * filename, double x0, double y0,
		double x1, double y1);

EXPORT int slScanPlate(unsigned dpi, unsigned plateNum, char * filename);

typedef int (*SL_SCAN_PLATE) (unsigned dpi, unsigned plateNum, char * filename);

EXPORT int slCalibrateToPlate(unsigned dpi, unsigned plateNum);

typedef int (*SL_CALIBRATE_TO_PLATE) (unsigned dpi, unsigned plateNum);

EXPORT int slDecodePlate(unsigned dpi, unsigned plateNum);

typedef int (*SL_DECODE_PLATE) (unsigned dpi, unsigned plateNum);

EXPORT int slDecodeImage(unsigned plateNum, char * filename);

typedef int (*SL_DECODE_IMAGE)(unsigned plateNum, char * filename);

#ifdef __cplusplus
}
#endif

#endif /* __INC_SCANLIB_H */
