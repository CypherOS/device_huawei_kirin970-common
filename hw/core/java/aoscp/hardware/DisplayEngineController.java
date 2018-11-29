/*
 * Copyright (C) 2018 The LineageOS Project
 * Copyright (C) 2018 CypherOS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package aoscp.hardware;

import android.content.Context;
import android.os.SystemProperties;

import aoscp.hardware.display.DisplayMode;

import com.android.server.HwSmartDisplayService;
import com.android.server.display.DisplayEngineService;
import com.android.server.display.DisplayEngineService_V1_0;
import com.android.server.display.DisplayEngineService_V1_1;

/*
 * Display Engine API for Display Modes
 *
 * A device may implement a list of preset display modes for different
 * viewing intents, such as movies, photos, or extra vibrance. These
 * modes may have multiple components such as gamma correction, white
 * point adjustment, etc, but are activated by a single control point.
 *
 * This API provides support for enumerating and selecting the
 * modes supported by the hardware.
 */

public class DisplayEngineController {

	private static final String DISPLAY_MODE_NORMAL = "Normal";
	private static final String DISPLAY_MODE_VIVID  = "Vivid";

	private static final int[] AVAILABLE_MODES = {
        0, //DISPLAY_MODE_NORMAL
		1, //DISPLAY_MODE_VIVID
    };

	private static final String DISPLAY_ENGINE_V1_0_PROP = "init.svc.displayengine-hal-1-0";
    private static final String DISPLAY_ENGINE_V1_1_PROP = "init.svc.displayengine-hal-1-1";

    private Context mContext;
	private DisplayEngineService mDisplayEngineService;
	private HwSmartDisplayService mHwSmartDisplayService;
	
	private int mColorMode;

    public DisplayEngineController(Context context) {
		mContext = context;
	}

	public void init() {
		try {
            if (SystemProperties.get(DISPLAY_ENGINE_V1_0_PROP, "") != "") {
                mDisplayEngineService = new DisplayEngineService_V1_0();
            } else if (SystemProperties.get(DISPLAY_ENGINE_V1_1_PROP, "") != "") {
                mDisplayEngineService = new DisplayEngineService_V1_1();
            }
			
			mHwSmartDisplayService = new HwSmartDisplayService();
            mHwSmartDisplayService.init_native();
			
            mColorMode = 0;

            mDisplayEngineService.setBootComplete(true);
            mDisplayEngineService.enablePowerMode(true);

            setMode(getCurrentMode());
        } catch (Throwable t) {
            // Ignore, DisplayEngineService not available.
        }
	}

    public boolean isAvailable() {
        return mDisplayEngineService != null &&
                mDisplayEngineService.isColorModeSupported();
    }

    public int[] getAvailableModes() {
        if (mDisplayEngineService == null) {
            return -1;
        }
        return AVAILABLE_MODES;
    }

    public int getCurrentMode() {
        if (mDisplayEngineService == null) {
            return -1;
        }
        return mColorMode;
    }

    public boolean setMode(int mode) {
        if (mDisplayEngineService == null) {
            return false;
        }
        mColorMode = mode;
        if (mColorMode == 0) {
            mDisplayEngineService.enableColorMode(false);
        } else if (mColorMode == 1) {
            mDisplayEngineService.enableColorMode(true);
        }
		// 2 : COLOR_ENHANCEMENT (1 : Eye Comfort)
		mHwSmartDisplayService.nativeSetSmartDisplay(2, mColorMode);
        return true;
    }

    public int getDefaultMode() {
        if (mDisplayEngineService == null) {
            return -1;
        }
		return 0;
    }

	public String getModeEntry(int mode) {
		String entry = null;
		if (mode == 0) {
			entry = DISPLAY_MODE_NORMAL;
		} else if (mode == 1) {
			entry = DISPLAY_MODE_VIVID;
		}
        return entry;
    }
}