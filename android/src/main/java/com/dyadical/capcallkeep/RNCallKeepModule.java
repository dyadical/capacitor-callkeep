/*
 * Copyright (c) 2016-2019 The CallKeep Authors (see the AUTHORS file)
 * SPDX-License-Identifier: ISC, MIT
 *
 * Permission to use, copy, modify, and distribute this software for any
 * purpose with or without fee is hereby granted, provided that the above
 * copyright notice and this permission notice appear in all copies.
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 * WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 * ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 * WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 * ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 * OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */

package com.dyadical.capcallkeep;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static com.dyadical.capcallkeep.Constants.ACTION_ANSWER_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_AUDIO_SESSION;
import static com.dyadical.capcallkeep.Constants.ACTION_CHECK_REACHABILITY;
import static com.dyadical.capcallkeep.Constants.ACTION_DTMF_TONE;
import static com.dyadical.capcallkeep.Constants.ACTION_END_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_HOLD_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_MUTE_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_ONGOING_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_ON_SILENCE_INCOMING_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_SHOW_INCOMING_CALL_UI;
import static com.dyadical.capcallkeep.Constants.ACTION_UNHOLD_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_UNMUTE_CALL;
import static com.dyadical.capcallkeep.Constants.ACTION_WAKE_APP;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALLER_NAME;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALL_NUMBER;
import static com.dyadical.capcallkeep.Constants.EXTRA_CALL_UUID;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Icon;
import android.media.AudioDeviceInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telecom.CallAudioState;
import android.telecom.Connection;
import android.telecom.DisconnectCause;
import android.telecom.PhoneAccount;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.WindowManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Dynamic;
import com.facebook.react.bridge.PluginMethod;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule.RCTDeviceEventEmitter;
import com.facebook.react.modules.permissions.PermissionsModule;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

// @see https://github.com/kbagchiGWC/voice-quickstart-android/blob/9a2aff7fbe0d0a5ae9457b48e9ad408740dfb968/exampleConnectionService/src/main/java/com/twilio/voice/examples/connectionservice/VoiceConnectionServiceActivity.java
public class RNCallKeepModule extends ReactContextBaseJavaModule {}
