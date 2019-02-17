/*
 * Copyright (C) 2019 The LineageOS Project
 * Copyright (C) 2019 CypherOS
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

#define LOG_TAG "vendor.aoscp.touch@1.0-service.kirin970"

#include <android-base/logging.h>
#include <binder/ProcessState.h>
#include <hidl/HidlTransportSupport.h>

#include "FingerprintNavigation.h"

using android::hardware::configureRpcThreadpool;
using android::hardware::joinRpcThreadpool;
using android::sp;
using android::status_t;
using android::OK;

using ::vendor::aoscp::touch::V1_0::IFingerprintNavigation;
using ::vendor::aoscp::touch::V1_0::kirin970::FingerprintNavigation;

int main() {
    sp<IFingerprintNavigation> fingerprintNav;
    status_t status;

    LOG(INFO) << "AOSCP Touch HAL service is starting.";

    fingerprintNav = new FingerprintNavigation();
    if (fingerprintNav == nullptr) {
        LOG(ERROR) << "Can not create an instance of AOSCP Touch HAL FingerprintNavigation Iface, exiting.";
        goto shutdown;
    }

    configureRpcThreadpool(2, true /*callerWillJoin*/);

    status = fingerprintNav->registerAsService();
    if (status != OK) {
        LOG(ERROR) << "Could not register service for AOSCP Touch HAL FingerprintNavigation Iface (" << status << ")";
        goto shutdown;
    }

    LOG(INFO) << "AOSCP Touch HAL service is ready.";
    joinRpcThreadpool();
    // Should not pass this line

shutdown:
    // In normal operation, we don't expect the thread pool to shutdown
    LOG(ERROR) << "AOSCP Touch HAL service is shutting down.";
    return 1;
}
