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

type Events =
  | 'endCall'
  | 'answerCall'
  | 'toggleHold'
  | 'setMutedCall'
  | 'DTMFAction'
  | 'startCall'
  | 'activateAudioSession'
  | 'checkReachability'
  | 'showIncomingCallUi'
  | 'silenceIncomingCall';

type CallInfo = { callUUID: string; handle: string; name: string };
interface Listeners {
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
}

export type AudioRoute = {
  name: string;
  type: string;
};

// interface IOptions { ios: { appName: string; imageName?: string; supportsVideo?: boolean; maximumCallGroups?: string; maximumCallsPerCallGroup?: string; ringtoneSound?: string; includesCallsInRecents?: boolean; }; android: { alertTitle: string; alertDescription: string; cancelButton: string; okButton: string; imageName?: string; additionalPermissions: string[]; selfManaged?: boolean; foregroundService?: { channelId: string; channelName: string; notificationTitle: string; notificationIcon?: string; }; }; }

export type DidReceiveStartCallActionPayload = { handle: string };
export type AnswerCallPayload = { callUUID: string };
export type EndCallPayload = AnswerCallPayload;
export type DidDisplayIncomingCallPayload = string | undefined;
export type DidPerformSetMutedCallActionPayload = boolean;

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

export interface AndroidOptions {
  selfManaged?: boolean;
  imageName?: string;
  foregroundService?: {
    channelId: string;
    channelName: string;
    notificationTitle: string;
    notificationIcon: string;
  };
}

export interface IOSOptions {
  appName: string;
  imageName?: string;
  supportsVideo?: boolean;
  maximumCallGroups?: string;
  maximumCallsPerCallGroup?: string;
  ringtoneSound?: string;
  includesCallsInRecents?: boolean;
}

export interface SetupOptions {
  ios?: IOSOptions;
  android?: AndroidOptions;
}

export interface CapCallKeepPlugin extends Listeners {
  echo(options: { value: string }): Promise<{ value: string }>;
  checkPermissions(): Promise<PermissionStatus>;
  requestPermissions(): Promise<PermissionStatus>;
  getInitialEvents(): Promise<Obj[]>;

  removeEventListener(type: Events): void;

  setup(options: SetupOptions): PV;
  setupIOS(options: IOSOptions): PV;
  setupAndroid(options: AndroidOptions): PV;

  hasDefaultPhoneAccount(): PB;

  answerIncomingCall(o: { uuid: string }): PV;

  registerPhoneAccount(): PV;

  registerAndroidEvents(): PV;

  displayIncomingCall(o: {
    uuid: string;
    handle: string;
    localizedCallerName?: string;
    handleType?: HandleType;
    hasVideo?: boolean;
    options?: Obj;
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
    options?: Obj;
  }): PV;

  checkPhoneAccountEnabled(): PB;

  isConnectionServiceAvailable(): PB;

  /**
   * @description reportConnectedOutgoingCallWithUUID method is available only on iOS.
   */
  reportConnectedOutgoingCallWithUUID(args: { uuid: string }): PV;

  /**
   * @description reportConnectedOutgoingCallWithUUID method is available only on iOS.
   */
  reportConnectingOutgoingCallWithUUID(o: { uuid: string }): PV;

  reportEndCallWithUUID(o: { uuid: string; reason: number }): PV;

  rejectCall(o: { uuid: string }): PV;

  endCall(o: { uuid: string }): PV;

  endAllCalls(): PV;

  setReachable(): PV;

  /**
   * @description isCallActive method is available only on iOS.
   */
  isCallActive(o: { uuid: string }): PB;

  getCalls(): Promise<Obj>;

  getAudioRoutes(): PV;

  setAudioRoute(o: { uuid: string; inputName: string }): PV;

  /**
   * @description supportConnectionService method is available only on Android.
   */
  supportConnectionService(): PB;

  /**
   * @description hasPhoneAccount method is available only on Android.
   */
  hasPhoneAccount(): PB;

  hasOutgoingCall(): PB;

  /**
   * @description setMutedCall method is available only on iOS.
   */
  setMutedCall(o: { uuid: string; muted: boolean }): PV;

  /**
   * @description toggleAudioRouteSpeaker method is available only on Android.
   */
  toggleAudioRouteSpeaker(o: { uuid: string; routeSpeaker: boolean }): PV;
  setOnHold(o: { uuid: string; held: boolean }): PV;

  /**
   * @descriptions sendDTMF is used to send DTMF tones to the PBX.
   */
  sendDTMF(o: { uuid: string; key: string }): PV;

  checkIfBusy(): PB;

  checkSpeaker(): PB;

  /**
   * @description setAvailable method is available only on Android.
   */
  setAvailable(o: { active: boolean }): PV;

  setForegroundServiceSettings(o: { settings: Obj }): PV;

  canMakeMultipleCalls(o: { allow: boolean }): PV;

  setCurrentCallActive(o: { callUUID: string }): PV;

  backToForeground(): PV;
}

type Obj = Record<string, string>;
