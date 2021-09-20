# capacitor-callkeep

We went through hell to make this work for YOU.

Capacitor port of react-native callkeep. iOS CallKit framework and Android ConnectionService for Capacitor. VoIP, FCM tokens, APNS tokens.

We're aiming to publish to npm by roughly September 23, 2021.

## Install

```bash
npm install capacitor-callkeep
npx cap sync
```

## API

<docgen-index>

* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`addListener(...)`](#addlistener)
* [`echo(...)`](#echo)
* [`setupAndroid(...)`](#setupandroid)
* [`supportConnectionService()`](#supportconnectionservice)
* [`registerPhoneAccount()`](#registerphoneaccount)
* [`hasPhoneAccount()`](#hasphoneaccount)
* [`hasDefaultPhoneAccount()`](#hasdefaultphoneaccount)
* [`checkPhoneAccountEnabled()`](#checkphoneaccountenabled)
* [`toggleAudioRouteSpeaker(...)`](#toggleaudioroutespeaker)
* [`setAvailable(...)`](#setavailable)
* [`registerAndroidEvents()`](#registerandroidevents)
* [`isConnectionServiceAvailable()`](#isconnectionserviceavailable)
* [`rejectCall(...)`](#rejectcall)
* [`hasOutgoingCall()`](#hasoutgoingcall)
* [`setForegroundServiceSettings(...)`](#setforegroundservicesettings)
* [`canMakeMultipleCalls(...)`](#canmakemultiplecalls)
* [`setCurrentCallActive(...)`](#setcurrentcallactive)
* [`backToForeground()`](#backtoforeground)
* [`setupIOS(...)`](#setupios)
* [`reportConnectedOutgoingCallWithUUID(...)`](#reportconnectedoutgoingcallwithuuid)
* [`reportConnectingOutgoingCallWithUUID(...)`](#reportconnectingoutgoingcallwithuuid)
* [`isCallActive(...)`](#iscallactive)
* [`setMutedCall(...)`](#setmutedcall)
* [`getInitialEvents()`](#getinitialevents)
* [`getCalls()`](#getcalls)
* [`checkIfBusy()`](#checkifbusy)
* [`checkSpeaker()`](#checkspeaker)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`answerIncomingCall(...)`](#answerincomingcall)
* [`displayIncomingCall(...)`](#displayincomingcall)
* [`startCall(...)`](#startcall)
* [`updateDisplay(...)`](#updatedisplay)
* [`reportEndCallWithUUID(...)`](#reportendcallwithuuid)
* [`endCall(...)`](#endcall)
* [`endAllCalls()`](#endallcalls)
* [`setReachable()`](#setreachable)
* [`getAudioRoutes()`](#getaudioroutes)
* [`setAudioRoute(...)`](#setaudioroute)
* [`setOnHold(...)`](#setonhold)
* [`sendDTMF(...)`](#senddtmf)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### addListener(...)

```typescript
addListener(type: 'endCall', l: L<UUID>) => PLH
```

| Param      | Type                              |
| ---------- | --------------------------------- |
| **`type`** | <code>"endCall"</code>            |
| **`l`**    | <code>(t: UUID) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'answerCall', l: L<UUID>) => PLH
```

| Param      | Type                              |
| ---------- | --------------------------------- |
| **`type`** | <code>"answerCall"</code>         |
| **`l`**    | <code>(t: UUID) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'toggleHold', l: L<UUID & { hold: boolean; }>) => PLH
```

| Param      | Type                                                   |
| ---------- | ------------------------------------------------------ |
| **`type`** | <code>"toggleHold"</code>                              |
| **`l`**    | <code>(t: UUID & { hold: boolean; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'setMutedCall', l: L<UUID & { muted: boolean; }>) => PLH
```

| Param      | Type                                                    |
| ---------- | ------------------------------------------------------- |
| **`type`** | <code>"setMutedCall"</code>                             |
| **`l`**    | <code>(t: UUID & { muted: boolean; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'DTMFAction', l: L<UUID & { digits: string; }>) => PLH
```

| Param      | Type                                                    |
| ---------- | ------------------------------------------------------- |
| **`type`** | <code>"DTMFAction"</code>                               |
| **`l`**    | <code>(t: UUID & { digits: string; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'startCall', l: L<CallInfo>) => PLH
```

| Param      | Type                                  |
| ---------- | ------------------------------------- |
| **`type`** | <code>"startCall"</code>              |
| **`l`**    | <code>(t: CallInfo) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'activateAudioSession', l: L<void>) => PLH
```

| Param      | Type                                |
| ---------- | ----------------------------------- |
| **`type`** | <code>"activateAudioSession"</code> |
| **`l`**    | <code>(t: void) =&gt; void</code>   |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'checkReachability', l: L<void>) => PLH
```

| Param      | Type                              |
| ---------- | --------------------------------- |
| **`type`** | <code>"checkReachability"</code>  |
| **`l`**    | <code>(t: void) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'showIncomingCallUi', l: L<CallInfo>) => PLH
```

| Param      | Type                                  |
| ---------- | ------------------------------------- |
| **`type`** | <code>"showIncomingCallUi"</code>     |
| **`l`**    | <code>(t: CallInfo) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'silenceIncomingCall', l: L<CallInfo>) => PLH
```

| Param      | Type                                  |
| ---------- | ------------------------------------- |
| **`type`** | <code>"silenceIncomingCall"</code>    |
| **`l`**    | <code>(t: CallInfo) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### addListener(...)

```typescript
addListener(type: 'registration', l: L<{ token: string; }>) => PLH
```

iOS only

| Param      | Type                                            |
| ---------- | ----------------------------------------------- |
| **`type`** | <code>"registration"</code>                     |
| **`l`**    | <code>(t: { token: string; }) =&gt; void</code> |

**Returns:** <code>any</code>

--------------------


### echo(...)

```typescript
echo(options: { value: string; }) => any
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>any</code>

--------------------


### setupAndroid(...)

```typescript
setupAndroid(options: AndroidOptions) => any
```

| Param         | Type                                                      |
| ------------- | --------------------------------------------------------- |
| **`options`** | <code><a href="#androidoptions">AndroidOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### supportConnectionService()

```typescript
supportConnectionService() => any
```

**Returns:** <code>any</code>

--------------------


### registerPhoneAccount()

```typescript
registerPhoneAccount() => any
```

**Returns:** <code>any</code>

--------------------


### hasPhoneAccount()

```typescript
hasPhoneAccount() => any
```

**Returns:** <code>any</code>

--------------------


### hasDefaultPhoneAccount()

```typescript
hasDefaultPhoneAccount() => any
```

**Returns:** <code>any</code>

--------------------


### checkPhoneAccountEnabled()

```typescript
checkPhoneAccountEnabled() => any
```

**Returns:** <code>any</code>

--------------------


### toggleAudioRouteSpeaker(...)

```typescript
toggleAudioRouteSpeaker(o: { uuid: string; routeSpeaker: boolean; }) => any
```

| Param   | Type                                                  |
| ------- | ----------------------------------------------------- |
| **`o`** | <code>{ uuid: string; routeSpeaker: boolean; }</code> |

**Returns:** <code>any</code>

--------------------


### setAvailable(...)

```typescript
setAvailable(o: { active: boolean; }) => any
```

| Param   | Type                              |
| ------- | --------------------------------- |
| **`o`** | <code>{ active: boolean; }</code> |

**Returns:** <code>any</code>

--------------------


### registerAndroidEvents()

```typescript
registerAndroidEvents() => any
```

**Returns:** <code>any</code>

--------------------


### isConnectionServiceAvailable()

```typescript
isConnectionServiceAvailable() => any
```

**Returns:** <code>any</code>

--------------------


### rejectCall(...)

```typescript
rejectCall(o: { uuid: string; }) => any
```

| Param   | Type                           |
| ------- | ------------------------------ |
| **`o`** | <code>{ uuid: string; }</code> |

**Returns:** <code>any</code>

--------------------


### hasOutgoingCall()

```typescript
hasOutgoingCall() => any
```

**Returns:** <code>any</code>

--------------------


### setForegroundServiceSettings(...)

```typescript
setForegroundServiceSettings(o: { settings: Obj; }) => any
```

| Param   | Type                            |
| ------- | ------------------------------- |
| **`o`** | <code>{ settings: any; }</code> |

**Returns:** <code>any</code>

--------------------


### canMakeMultipleCalls(...)

```typescript
canMakeMultipleCalls(o: { allow: boolean; }) => any
```

| Param   | Type                             |
| ------- | -------------------------------- |
| **`o`** | <code>{ allow: boolean; }</code> |

**Returns:** <code>any</code>

--------------------


### setCurrentCallActive(...)

```typescript
setCurrentCallActive(o: { callUUID: string; }) => any
```

| Param   | Type                               |
| ------- | ---------------------------------- |
| **`o`** | <code>{ callUUID: string; }</code> |

**Returns:** <code>any</code>

--------------------


### backToForeground()

```typescript
backToForeground() => any
```

**Returns:** <code>any</code>

--------------------


### setupIOS(...)

```typescript
setupIOS(options: IOSOptions) => any
```

| Param         | Type                                              |
| ------------- | ------------------------------------------------- |
| **`options`** | <code><a href="#iosoptions">IOSOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### reportConnectedOutgoingCallWithUUID(...)

```typescript
reportConnectedOutgoingCallWithUUID(args: { uuid: string; }) => any
```

| Param      | Type                           |
| ---------- | ------------------------------ |
| **`args`** | <code>{ uuid: string; }</code> |

**Returns:** <code>any</code>

--------------------


### reportConnectingOutgoingCallWithUUID(...)

```typescript
reportConnectingOutgoingCallWithUUID(o: { uuid: string; }) => any
```

| Param   | Type                           |
| ------- | ------------------------------ |
| **`o`** | <code>{ uuid: string; }</code> |

**Returns:** <code>any</code>

--------------------


### isCallActive(...)

```typescript
isCallActive(o: { uuid: string; }) => any
```

| Param   | Type                           |
| ------- | ------------------------------ |
| **`o`** | <code>{ uuid: string; }</code> |

**Returns:** <code>any</code>

--------------------


### setMutedCall(...)

```typescript
setMutedCall(o: { uuid: string; muted: boolean; }) => any
```

| Param   | Type                                           |
| ------- | ---------------------------------------------- |
| **`o`** | <code>{ uuid: string; muted: boolean; }</code> |

**Returns:** <code>any</code>

--------------------


### getInitialEvents()

```typescript
getInitialEvents() => any
```

**Returns:** <code>any</code>

--------------------


### getCalls()

```typescript
getCalls() => any
```

**Returns:** <code>any</code>

--------------------


### checkIfBusy()

```typescript
checkIfBusy() => any
```

**Returns:** <code>any</code>

--------------------


### checkSpeaker()

```typescript
checkSpeaker() => any
```

**Returns:** <code>any</code>

--------------------


### checkPermissions()

```typescript
checkPermissions() => any
```

**Returns:** <code>any</code>

--------------------


### requestPermissions()

```typescript
requestPermissions() => any
```

**Returns:** <code>any</code>

--------------------


### answerIncomingCall(...)

```typescript
answerIncomingCall(o: { uuid: string; }) => any
```

| Param   | Type                           |
| ------- | ------------------------------ |
| **`o`** | <code>{ uuid: string; }</code> |

**Returns:** <code>any</code>

--------------------


### displayIncomingCall(...)

```typescript
displayIncomingCall(o: { uuid: string; number: string; callerName: string; handleType?: HandleType; hasVideo?: boolean; options?: IncomingCallOptions; }) => any
```

| Param   | Type                                                                                                                                                                                                    |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`o`** | <code>{ uuid: string; number: string; callerName: string; handleType?: "number" \| "generic" \| "email"; hasVideo?: boolean; options?: <a href="#incomingcalloptions">IncomingCallOptions</a>; }</code> |

**Returns:** <code>any</code>

--------------------


### startCall(...)

```typescript
startCall(o: { uuid: string; handle: string; contactIdentifier?: string; handleType?: HandleType; hasVideo?: boolean; }) => any
```

| Param   | Type                                                                                                                                          |
| ------- | --------------------------------------------------------------------------------------------------------------------------------------------- |
| **`o`** | <code>{ uuid: string; handle: string; contactIdentifier?: string; handleType?: "number" \| "generic" \| "email"; hasVideo?: boolean; }</code> |

**Returns:** <code>any</code>

--------------------


### updateDisplay(...)

```typescript
updateDisplay(o: { uuid: string; displayName: string; handle: string; options?: IncomingCallOptions; }) => any
```

| Param   | Type                                                                                                                                  |
| ------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| **`o`** | <code>{ uuid: string; displayName: string; handle: string; options?: <a href="#incomingcalloptions">IncomingCallOptions</a>; }</code> |

**Returns:** <code>any</code>

--------------------


### reportEndCallWithUUID(...)

```typescript
reportEndCallWithUUID(o: { uuid: string; reason: number; }) => any
```

| Param   | Type                                           |
| ------- | ---------------------------------------------- |
| **`o`** | <code>{ uuid: string; reason: number; }</code> |

**Returns:** <code>any</code>

--------------------


### endCall(...)

```typescript
endCall(o: { uuid: string; }) => any
```

| Param   | Type                           |
| ------- | ------------------------------ |
| **`o`** | <code>{ uuid: string; }</code> |

**Returns:** <code>any</code>

--------------------


### endAllCalls()

```typescript
endAllCalls() => any
```

**Returns:** <code>any</code>

--------------------


### setReachable()

```typescript
setReachable() => any
```

**Returns:** <code>any</code>

--------------------


### getAudioRoutes()

```typescript
getAudioRoutes() => any
```

**Returns:** <code>any</code>

--------------------


### setAudioRoute(...)

```typescript
setAudioRoute(o: { uuid: string; inputName: string; }) => any
```

| Param   | Type                                              |
| ------- | ------------------------------------------------- |
| **`o`** | <code>{ uuid: string; inputName: string; }</code> |

**Returns:** <code>any</code>

--------------------


### setOnHold(...)

```typescript
setOnHold(o: { uuid: string; held: boolean; }) => any
```

| Param   | Type                                          |
| ------- | --------------------------------------------- |
| **`o`** | <code>{ uuid: string; held: boolean; }</code> |

**Returns:** <code>any</code>

--------------------


### sendDTMF(...)

```typescript
sendDTMF(o: { uuid: string; key: string; }) => any
```

sendDTMF is used to send DTMF tones to the PBX.

| Param   | Type                                        |
| ------- | ------------------------------------------- |
| **`o`** | <code>{ uuid: string; key: string; }</code> |

**Returns:** <code>any</code>

--------------------


### Interfaces


#### AndroidOptions

| Prop                    | Type                                                                                                          |
| ----------------------- | ------------------------------------------------------------------------------------------------------------- |
| **`selfManaged`**       | <code>boolean</code>                                                                                          |
| **`imageName`**         | <code>string</code>                                                                                           |
| **`foregroundService`** | <code>{ channelId: string; channelName: string; notificationTitle: string; notificationIcon: string; }</code> |


#### IOSOptions

| Prop                           | Type                 |
| ------------------------------ | -------------------- |
| **`appName`**                  | <code>string</code>  |
| **`imageName`**                | <code>string</code>  |
| **`supportsVideo`**            | <code>boolean</code> |
| **`maximumCallGroups`**        | <code>string</code>  |
| **`maximumCallsPerCallGroup`** | <code>string</code>  |
| **`ringtoneSound`**            | <code>string</code>  |
| **`includesCallsInRecents`**   | <code>boolean</code> |


#### Call

| Prop               | Type                 |
| ------------------ | -------------------- |
| **`callUUID`**     | <code>string</code>  |
| **`outgoing`**     | <code>boolean</code> |
| **`onHold`**       | <code>boolean</code> |
| **`hasConnected`** | <code>boolean</code> |
| **`hasEnded`**     | <code>boolean</code> |


#### PermissionStatus

| Prop                   | Type                                                                      |
| ---------------------- | ------------------------------------------------------------------------- |
| **`readPhoneNumbers`** | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`readPhoneState`**   | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`manageOwnCalls`**   | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`callPhone`**        | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`recordAudio`**      | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |


#### IncomingCallOptions

| Prop      | Type                                                                                                                          |
| --------- | ----------------------------------------------------------------------------------------------------------------------------- |
| **`ios`** | <code>{ supportsHolding?: boolean; supportsDTMF?: boolean; supportsGrouping?: boolean; supportsUngrouping?: boolean; }</code> |

</docgen-api>
