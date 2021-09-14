export type Events =
  | 'didReceiveStartCallAction'
  | 'answerCall'
  | 'endCall'
  | 'didActivateAudioSession'
  | 'didDeactivateAudioSession'
  | 'didDisplayIncomingCall'
  | 'didToggleHoldCallAction'
  | 'didPerformDTMFAction'
  | 'didResetProvider'
  | 'checkReachability'
  | 'didPerformSetMutedCallAction'
  | 'didLoadWithEvents'
  | 'showIncomingCallUi'
  | 'silenceIncomingCall'

type HandleType = 'generic' | 'number' | 'email'

export type AudioRoute = {
  name: string
  type: string
}

interface IOptions {
  ios: {
    appName: string
    imageName?: string
    supportsVideo?: boolean
    maximumCallGroups?: string
    maximumCallsPerCallGroup?: string
    ringtoneSound?: string
    includesCallsInRecents?: boolean
  }
  android: {
    alertTitle: string
    alertDescription: string
    cancelButton: string
    okButton: string
    imageName?: string
    additionalPermissions: string[]
    selfManaged?: boolean
    foregroundService?: {
      channelId: string
      channelName: string
      notificationTitle: string
      notificationIcon?: string
    }
  }
}

export type DidReceiveStartCallActionPayload = { handle: string }
export type AnswerCallPayload = { callUUID: string }
export type EndCallPayload = AnswerCallPayload
export type DidDisplayIncomingCallPayload = string | undefined
export type DidPerformSetMutedCallActionPayload = boolean

export const CONSTANTS = {
  END_CALL_REASONS: {
    FAILED: 1,
    REMOTE_ENDED: 2,
    UNANSWERED: 3,
    ANSWERED_ELSEWHERE: 4,
    DECLINED_ELSEWHERE: 5 | 2,
    MISSED: 2 | 6,
  },
}

export interface CapCallKeepPlugin {
  echo(options: { value: string }): Promise<{ value: string }>

  getInitialEvents(): Promise<Obj[]>

  addEventListener(type: Events, handler: (args: any) => void): void

  removeEventListener(type: Events): void

  setup(options: IOptions): Promise<boolean>

  hasDefaultPhoneAccount(): boolean

  answerIncomingCall(uuid: string): void

  registerPhoneAccount(): void

  registerAndroidEvents(): void

  displayIncomingCall(
    uuid: string,
    handle: string,
    localizedCallerName?: string,
    handleType?: HandleType,
    hasVideo?: boolean,
    options?: Obj,
  ): void

  startCall(
    uuid: string,
    handle: string,
    contactIdentifier?: string,
    handleType?: HandleType,
    hasVideo?: boolean,
  ): void

  updateDisplay(
    uuid: string,
    displayName: string,
    handle: string,
    options?: Obj,
  ): void

  checkPhoneAccountEnabled(): Promise<boolean>

  isConnectionServiceAvailable(): Promise<boolean>

  /**
   * @description reportConnectedOutgoingCallWithUUID method is available only on iOS.
   */
  reportConnectedOutgoingCallWithUUID(uuid: string): void

  /**
   * @description reportConnectedOutgoingCallWithUUID method is available only on iOS.
   */
  reportConnectingOutgoingCallWithUUID(uuid: string): void

  reportEndCallWithUUID(uuid: string, reason: number): void

  rejectCall(uuid: string): void

  endCall(uuid: string): void

  endAllCalls(): void

  setReachable(): void

  /**
   * @description isCallActive method is available only on iOS.
   */
  isCallActive(uuid: string): Promise<boolean>

  getCalls(): Promise<Obj>

  getAudioRoutes(): Promise<void>

  setAudioRoute: (uuid: string, inputName: string) => Promise<void>

  /**
   * @description supportConnectionService method is available only on Android.
   */
  supportConnectionService(): boolean

  /**
   * @description hasPhoneAccount method is available only on Android.
   */
  hasPhoneAccount(): Promise<boolean>

  hasOutgoingCall(): Promise<boolean>

  /**
   * @description setMutedCall method is available only on iOS.
   */
  setMutedCall(uuid: string, muted: boolean): void

  /**
   * @description toggleAudioRouteSpeaker method is available only on Android.
   * @param uuid
   * @param routeSpeaker
   */
  toggleAudioRouteSpeaker(uuid: string, routeSpeaker: boolean): void
  setOnHold(uuid: string, held: boolean): void

  /**
   * @descriptions sendDTMF is used to send DTMF tones to the PBX.
   */
  sendDTMF(uuid: string, key: string): void

  checkIfBusy(): Promise<boolean>

  checkSpeaker(): Promise<boolean>

  /**
   * @description setAvailable method is available only on Android.
   */
  setAvailable(active: boolean): void

  setForegroundServiceSettings(settings: Obj): void

  canMakeMultipleCalls(allow: boolean): void

  setCurrentCallActive(callUUID: string): void

  backToForeground(): void
}

type Obj = Record<string, string>
