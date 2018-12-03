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

package co.aoscp.hwcontrollers;

import android.content.Context;
import android.os.SystemProperties;

import com.android.server.HwSmartDisplayService;
import com.android.server.display.DisplayEngineService;
import com.android.server.display.DisplayEngineService_V1_0;
import com.android.server.display.DisplayEngineService_V1_1;

import co.aoscp.hwcontrollers.util.FileUtils;

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

	private static final String DEFAULT_PATH = "/data/misc/.displaymodedefault";
	private static final int[] DISPLAY_MODES = {
        0, //DISPLAY_MODE_NORMAL
		1, //DISPLAY_MODE_VIVID
    };

	private static final String DISPLAY_ENGINE_V1_0_PROP = "init.svc.displayengine-hal-1-0";
    private static final String DISPLAY_ENGINE_V1_1_PROP = "init.svc.displayengine-hal-1-1";

    private static DisplayEngineService sDisplayEngineService;
    private static int sColorEnhancementCurrentMode;

    static {
        try {
            if (SystemProperties.get(DISPLAY_ENGINE_V1_0_PROP, "") != "") {
                sDisplayEngineService = new DisplayEngineService_V1_0();
            } else if (SystemProperties.get(DISPLAY_ENGINE_V1_1_PROP, "") != "") {
                sDisplayEngineService = new DisplayEngineService_V1_1();
            }
            sColorEnhancementCurrentMode = 0;

            sDisplayEngineService.setBootComplete(true);
            sDisplayEngineService.enablePowerMode(true);

            if (FileUtils.isFileReadable(DEFAULT_PATH)) {
                setMode(getDefaultMode(), false);
            } else {
                /* If default mode is not set yet, set current mode as default */
                setMode(getCurrentMode(), true);
            }
        } catch (Throwable t) {
            // Ignore, DisplayEngineService not available.
        }
    }

    /*
     * All HAF classes should export this boolean.
     * Real implementations must, of course, return true
     */
    public static boolean isSupported() {
        return sDisplayEngineService != null &&
                sDisplayEngineService.isColorModeSupported() &&
                FileUtils.isFileWritable(DEFAULT_PATH) &&
                FileUtils.isFileReadable(DEFAULT_PATH);
    }

    /*
     * Get the list of available modes. A mode has an integer
     * identifier and a string name.
     *
     * It is the responsibility of the upper layers to
     * map the name to a human-readable format or perform translation.
     */
    public static int[] getAvailableModes() {
        if (sDisplayEngineService == null) {
            return new int[0];
        }
        return DISPLAY_MODES;
    }

    /*
     * Get the name of the currently selected mode. This can return
     * null if no mode is selected.
     */
    public static int getCurrentMode() {
        if (sDisplayEngineService == null) {
            return -1;
        }
        return DISPLAY_MODES[sColorEnhancementCurrentMode];
    }

    /*
     * Selects a mode from the list of available modes by it's
     * string identifier. Returns true on success, false for
     * failure. It is up to the implementation to determine
     * if this mode is valid.
     */
    public static boolean setMode(int mode, boolean makeDefault) {
        if (sDisplayEngineService == null) {
            return false;
        }
        sColorEnhancementCurrentMode = mode;
        if (sColorEnhancementCurrentMode == 0) {
            sDisplayEngineService.enableColorMode(false);
        } else if (sColorEnhancementCurrentMode == 1) {
            sDisplayEngineService.enableColorMode(true);
        }
        if (makeDefault) {
            FileUtils.writeLine(DEFAULT_PATH, String.valueOf(sColorEnhancementCurrentMode));
        }
        return true;
    }

    /*
     * Gets the preferred default mode for this device by it's
     * string identifier. Can return null if there is no default.
     */
    public static int getDefaultMode() {
        if (sDisplayEngineService == null) {
            return -1;
        }
        try {
            int mode = Integer.parseInt(FileUtils.readOneLine(DEFAULT_PATH));
            return DISPLAY_MODES[mode];
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return -1;
        }
    }
}