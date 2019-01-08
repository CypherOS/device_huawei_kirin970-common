/*
 * Copyright (C) 2019 The LineageOS Project
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

#ifndef VENDOR_AOSCP_TOUCH_V1_0_KEYDISABLER_H
#define VENDOR_AOSCP_TOUCH_V1_0_KEYDISABLER_H

#include <vendor/aoscp/touch/1.0/IFingerprintNavigation.h>
#include <vendor/huawei/hardware/biometrics/fingerprint/2.1/IExtBiometricsFingerprint.h>
#include <hidl/MQDescriptor.h>
#include <hidl/Status.h>

namespace vendor {
namespace aoscp {
namespace touch {
namespace V1_0 {
namespace kirin970 {

using ::android::hardware::hidl_array;
using ::android::hardware::hidl_memory;
using ::android::hardware::hidl_string;
using ::android::hardware::hidl_vec;
using ::android::hardware::Return;
using ::android::hardware::Void;
using ::android::sp;

using ::vendor::huawei::hardware::biometrics::fingerprint::V2_1::IExtBiometricsFingerprint;

class FingerprintNavigation : public IFingerprintNavigation {
  public:
    FingerprintNavigation();

    // Methods from ::vendor::aoscp::touch::V1_0::IFingerprintNavigation follow.
    Return<bool> isSupported() override;
    Return<bool> setEnabled(bool canUse) override;

  private:
    sp<IExtBiometricsFingerprint> mHwHal;
};

}  // namespace kirin970
}  // namespace V1_0
}  // namespace touch
}  // namespace aoscp
}  // namespace vendor

#endif  // VENDOR_AOSCP_TOUCH_V1_0_KEYDISABLER_H
