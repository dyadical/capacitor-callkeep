# capacitor-callkeep

Capacitor port of callkeep plugin

## Install

```bash
npm install capacitor-callkeep
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)
* [`checkPermissions()`](#checkpermissions)
* [`requestPermissions()`](#requestpermissions)
* [`getInitialEvents()`](#getinitialevents)
* [`removeEventListener(...)`](#removeeventlistener)
* [`setup(...)`](#setup)
* [`hasDefaultPhoneAccount()`](#hasdefaultphoneaccount)
* [`answerIncomingCall(...)`](#answerincomingcall)
* [`registerPhoneAccount()`](#registerphoneaccount)
* [`registerAndroidEvents()`](#registerandroidevents)
* [`displayIncomingCall(...)`](#displayincomingcall)
* [`startCall(...)`](#startcall)
* [`updateDisplay(...)`](#updatedisplay)
* [`checkPhoneAccountEnabled()`](#checkphoneaccountenabled)
* [`isConnectionServiceAvailable()`](#isconnectionserviceavailable)
* [`reportConnectedOutgoingCallWithUUID(...)`](#reportconnectedoutgoingcallwithuuid)
* [`reportConnectingOutgoingCallWithUUID(...)`](#reportconnectingoutgoingcallwithuuid)
* [`reportEndCallWithUUID(...)`](#reportendcallwithuuid)
* [`rejectCall(...)`](#rejectcall)
* [`endCall(...)`](#endcall)
* [`endAllCalls()`](#endallcalls)
* [`setReachable()`](#setreachable)
* [`isCallActive(...)`](#iscallactive)
* [`getCalls()`](#getcalls)
* [`getAudioRoutes()`](#getaudioroutes)
* [`setAudioRoute(...)`](#setaudioroute)
* [`supportConnectionService()`](#supportconnectionservice)
* [`hasPhoneAccount()`](#hasphoneaccount)
* [`hasOutgoingCall()`](#hasoutgoingcall)
* [`setMutedCall(...)`](#setmutedcall)
* [`toggleAudioRouteSpeaker(...)`](#toggleaudioroutespeaker)
* [`setOnHold(...)`](#setonhold)
* [`sendDTMF(...)`](#senddtmf)
* [`checkIfBusy()`](#checkifbusy)
* [`checkSpeaker()`](#checkspeaker)
* [`setAvailable(...)`](#setavailable)
* [`setForegroundServiceSettings(...)`](#setforegroundservicesettings)
* [`canMakeMultipleCalls(...)`](#canmakemultiplecalls)
* [`setCurrentCallActive(...)`](#setcurrentcallactive)
* [`backToForeground()`](#backtoforeground)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => any
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

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


### getInitialEvents()

```typescript
getInitialEvents() => any
```

**Returns:** <code>any</code>

--------------------


### removeEventListener(...)

```typescript
removeEventListener(type: Events) => void
```

| Param      | Type                                                                                                                                                                                                      |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`type`** | <code>"endCall" \| "answerCall" \| "toggleHold" \| "setMutedCall" \| "DTMFAction" \| "startCall" \| "activateAudioSession" \| "checkReachability" \| "showIncomingCallUi" \| "silenceIncomingCall"</code> |

--------------------


### setup(...)

```typescript
setup(options: { selfManaged?: boolean; imageName?: string; foregroundService?: { channelId: string; channelName: string; notificationTitle: string; notificationIcon: string; }; }) => any
```

| Param         | Type                                                                                                                                                                              |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`options`** | <code>{ selfManaged?: boolean; imageName?: string; foregroundService?: { channelId: string; channelName: string; notificationTitle: string; notificationIcon: string; }; }</code> |

**Returns:** <code>any</code>

--------------------


### hasDefaultPhoneAccount()

```typescript
hasDefaultPhoneAccount() => any
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


### registerPhoneAccount()

```typescript
registerPhoneAccount() => any
```

**Returns:** <code>any</code>

--------------------


### registerAndroidEvents()

```typescript
registerAndroidEvents() => any
```

**Returns:** <code>any</code>

--------------------


### displayIncomingCall(...)

```typescript
displayIncomingCall(o: { uuid: string; handle: string; localizedCallerName?: string; handleType?: HandleType; hasVideo?: boolean; options?: Obj; }) => any
```

| Param   | Type                                                                                                                                                           |
| ------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`o`** | <code>{ uuid: string; handle: string; localizedCallerName?: string; handleType?: "number" \| "generic" \| "email"; hasVideo?: boolean; options?: any; }</code> |

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
updateDisplay(o: { uuid: string; displayName: string; handle: string; options?: Obj; }) => any
```

| Param   | Type                                                                               |
| ------- | ---------------------------------------------------------------------------------- |
| **`o`** | <code>{ uuid: string; displayName: string; handle: string; options?: any; }</code> |

**Returns:** <code>any</code>

--------------------


### checkPhoneAccountEnabled()

```typescript
checkPhoneAccountEnabled() => any
```

**Returns:** <code>any</code>

--------------------


### isConnectionServiceAvailable()

```typescript
isConnectionServiceAvailable() => any
```

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


### reportEndCallWithUUID(...)

```typescript
reportEndCallWithUUID(o: { uuid: string; reason: number; }) => any
```

| Param   | Type                                           |
| ------- | ---------------------------------------------- |
| **`o`** | <code>{ uuid: string; reason: number; }</code> |

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


### isCallActive(...)

```typescript
isCallActive(o: { uuid: string; }) => any
```

| Param   | Type                           |
| ------- | ------------------------------ |
| **`o`** | <code>{ uuid: string; }</code> |

**Returns:** <code>any</code>

--------------------


### getCalls()

```typescript
getCalls() => any
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


### supportConnectionService()

```typescript
supportConnectionService() => any
```

**Returns:** <code>any</code>

--------------------


### hasPhoneAccount()

```typescript
hasPhoneAccount() => any
```

**Returns:** <code>any</code>

--------------------


### hasOutgoingCall()

```typescript
hasOutgoingCall() => any
```

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


### toggleAudioRouteSpeaker(...)

```typescript
toggleAudioRouteSpeaker(o: { uuid: string; routeSpeaker: boolean; }) => any
```

| Param   | Type                                                  |
| ------- | ----------------------------------------------------- |
| **`o`** | <code>{ uuid: string; routeSpeaker: boolean; }</code> |

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

| Param   | Type                                        |
| ------- | ------------------------------------------- |
| **`o`** | <code>{ uuid: string; key: string; }</code> |

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


### setAvailable(...)

```typescript
setAvailable(o: { active: boolean; }) => any
```

| Param   | Type                              |
| ------- | --------------------------------- |
| **`o`** | <code>{ active: boolean; }</code> |

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


### Interfaces


#### PermissionStatus

| Prop                   | Type                                                                      |
| ---------------------- | ------------------------------------------------------------------------- |
| **`readPhoneNumbers`** | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`readPhoneState`**   | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`manageOwnCalls`**   | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`callPhone`**        | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |
| **`recordAudio`**      | <code>"prompt" \| "prompt-with-rationale" \| "granted" \| "denied"</code> |

</docgen-api>
