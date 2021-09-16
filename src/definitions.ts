import type { PluginListenerHandle, PermissionState } from '@capacitor/core';

export interface PermissionStatus {
  readPhoneNumbers: PermissionState;
  readPhoneState: PermissionState;
  manageOwnCalls: PermissionState;
  callPhone: PermissionState;
  recordAudio: PermissionState;
}

type PLH = Promise<PluginListenerHandle> & PluginListenerHandle;
type L<T> = (t: T) => void;

type HandleType = 'generic' | 'number' | 'email';

type UUID = { callUUID: string };

// type Events = | 'endCall' | 'answerCall' | 'toggleHold' | 'setMutedCall' | 'DTMFAction' | 'startCall' | 'activateAudioSession' | 'checkReachability' | 'showIncomingCallUi' | 'silenceIncomingCall';

type CallInfo = { callUUID: string; handle: string; name: string };

interface AndroidOptions {
  selfManaged?: boolean;
  imageName?: string;
  foregroundService?: {
    channelId: string;
    channelName: string;
    notificationTitle: string;
    notificationIcon: string;
  };
}

interface IOSOptions {
  appName: string;
  imageName?: string;
  supportsVideo?: boolean;
  maximumCallGroups?: string;
  maximumCallsPerCallGroup?: string;
  ringtoneSound?: string;
  includesCallsInRecents?: boolean;
}
export interface CapCallKeepPlugin {
  // export interface Listeners {
  addListener(type: 'endCall', l: L<UUID>): PLH;
  addListener(type: 'answerCall', l: L<UUID>): PLH;
  addListener(type: 'toggleHold', l: L<UUID & { hold: boolean }>): PLH;
  addListener(type: 'setMutedCall', l: L<UUID & { muted: boolean }>): PLH;
  addListener(type: 'DTMFAction', l: L<UUID & { digits: string }>): PLH;
  addListener(type: 'startCall', l: L<CallInfo>): PLH;
  addListener(type: 'activateAudioSession', l: L<void>): PLH;
  addListener(type: 'checkReachability', l: L<void>): PLH;
  addListener(type: 'showIncomingCallUi', l: L<CallInfo>): PLH;
  addListener(type: 'silenceIncomingCall', l: L<CallInfo>): PLH;
  /** iOS only */
  addListener(type: 'registration', l: L<{ token: string }>): PLH;
  // }

  // export interface AndroidOnly {
  echo(options: { value: string }): Promise<{ value: string }>;
  setupAndroid(options: AndroidOptions): PV;
  supportConnectionService(): PB;
  registerPhoneAccount(): PV;
  hasPhoneAccount(): PB;
  hasDefaultPhoneAccount(): PB;
  checkPhoneAccountEnabled(): PB;
  toggleAudioRouteSpeaker(o: { uuid: string; routeSpeaker: boolean }): PV;
  setAvailable(o: { active: boolean }): PV;
  registerAndroidEvents(): PV;
  isConnectionServiceAvailable(): PB;
  rejectCall(o: { uuid: string }): PV;
  hasOutgoingCall(): PB;
  setForegroundServiceSettings(o: { settings: Obj }): PV;
  canMakeMultipleCalls(o: { allow: boolean }): PV;
  setCurrentCallActive(o: { callUUID: string }): PV;
  backToForeground(): PV;
  // }

  // export interface IOSOnly {
  setupIOS(options: IOSOptions): PV;
  reportConnectedOutgoingCallWithUUID(args: { uuid: string }): PV;
  reportConnectingOutgoingCallWithUUID(o: { uuid: string }): PV;
  isCallActive(o: { uuid: string }): PB;
  setMutedCall(o: { uuid: string; muted: boolean }): PV;
  getInitialEvents(): Promise<{ name: string; body: string }[]>; // TODO: double check this type
  getCalls(): Promise<Call[]>;
  checkIfBusy(): PB;
  checkSpeaker(): PB;
  // }

  // export interface IOSOrAndroid {
  // Ios or android:
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
  // removeEventListener(type: Events): void; // TODO
  answerIncomingCall(o: { uuid: string }): PV;
  // TODO: different args for below three functions between ios and android
  // See: https://github1s.com/react-native-webrtc/react-native-callkeep/blob/HEAD/index.js#L73-L74
  displayIncomingCall(o: {
    uuid: string;
    handle: string;
    localizedCallerName?: string;
    handleType?: HandleType;
    hasVideo?: boolean;
    options?: IncomingCallOptions;
  }): PV;
  startCall(o: {
    uuid: string;
    handle: string;
    contactIdentifier?: string;
    handleType?: HandleType;
    hasVideo?: boolean;
  }): PV;
  updateDisplay(o: {
    uuid: string;
    displayName: string;
    handle: string;
    options?: IncomingCallOptions;
  }): PV;
  reportEndCallWithUUID(o: { uuid: string; reason: number }): PV;
  endCall(o: { uuid: string }): PV;
  endAllCalls(): PV;
  setReachable(): PV;
  getAudioRoutes(): PV;
  setAudioRoute(o: { uuid: string; inputName: string }): PV;
  setOnHold(o: { uuid: string; held: boolean }): PV;
  /** sendDTMF is used to send DTMF tones to the PBX. */
  sendDTMF(o: { uuid: string; key: string }): PV;
  // }
}

// NOTE: better, but messes up docgen:
// export interface CapCallKeepPlugin extends Listeners, AndroidOnly, IOSOnly, IOSOrAndroid {}

export const CONSTANTS = {
  END_CALL_REASONS: {
    FAILED: 1,
    REMOTE_ENDED: 2,
    UNANSWERED: 3,
    ANSWERED_ELSEWHERE: 4,
    DECLINED_ELSEWHERE: 5 | 2,
    MISSED: 2 | 6,
  },
};

type PV = Promise<void>;
type PB = Promise<{ value: boolean }>;
// TODO: update return type signatures
// (everything returns a promise of an object)

interface Call {
  callUUID: string;
  outgoing: boolean;
  onHold: boolean;
  hasConnected: boolean;
  hasEnded: boolean;
}

type Obj = Record<string, string>;

interface IncomingCallOptions {
  ios?: {
    supportsHolding?: boolean;
    supportsDTMF?: boolean;
    supportsGrouping?: boolean;
    supportsUngrouping?: boolean;
  };
}
