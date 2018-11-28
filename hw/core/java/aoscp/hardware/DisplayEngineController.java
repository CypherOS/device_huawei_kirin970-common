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

import aoscp.hardware.display.DisplayMode;

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

	private static final DisplayMode[] DISPLAY_MODES = {
        new DisplayMode(0, "Normal"),
        new DisplayMode(1, "Vivid"),
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

    public DisplayMode[] getAvailableModes() {
        if (mDisplayEngineService == null) {
            return new DisplayMode[0];
        }
        return DISPLAY_MODES;
    }

    public DisplayMode getCurrentMode() {
        if (mDisplayEngineService == null) {
            return null;
        }
        return DISPLAY_MODES[mColorMode];
    }

    public boolean setMode(DisplayMode mode) {
        if (mDisplayEngineService == null) {
            return false;
        }
        mColorMode = mode.id;
        if (mColorMode == 0) {
            mDisplayEngineService.enableColorMode(false);
        } else if (mColorMode == 1) {
            mDisplayEngineService.enableColorMode(true);
        }
		// 2 : COLOR_ENHANCEMENT (1 : Eye Comfort)
		mHwSmartDisplayService.nativeSetSmartDisplay(2, mColorMode);
        return true;
    }

    public DisplayMode getDefaultMode() {
        return null;
    }
}