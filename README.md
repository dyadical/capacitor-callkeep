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
* [`getInitialEvents()`](#getinitialevents)
* [`addEventListener(...)`](#addeventlistener)
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


### getInitialEvents()

```typescript
getInitialEvents() => any
```

**Returns:** <code>any</code>

--------------------


### addEventListener(...)

```typescript
addEventListener(type: Events, handler: (args: any) => void) => void
```

| Param         | Type                                                                                                                                                                                                                                                                                                                                                                        |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`type`**    | <code>"didReceiveStartCallAction" \| "answerCall" \| "endCall" \| "didActivateAudioSession" \| "didDeactivateAudioSession" \| "didDisplayIncomingCall" \| "didToggleHoldCallAction" \| "didPerformDTMFAction" \| "didResetProvider" \| "checkReachability" \| "didPerformSetMutedCallAction" \| "didLoadWithEvents" \| "showIncomingCallUi" \| "silenceIncomingCall"</code> |
| **`handler`** | <code>(args: any) =&gt; void</code>                                                                                                                                                                                                                                                                                                                                         |

--------------------


### removeEventListener(...)

```typescript
removeEventListener(type: Events) => void
```

| Param      | Type                                                                                                                                                                                                                                                                                                                                                                        |
| ---------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`type`** | <code>"didReceiveStartCallAction" \| "answerCall" \| "endCall" \| "didActivateAudioSession" \| "didDeactivateAudioSession" \| "didDisplayIncomingCall" \| "didToggleHoldCallAction" \| "didPerformDTMFAction" \| "didResetProvider" \| "checkReachability" \| "didPerformSetMutedCallAction" \| "didLoadWithEvents" \| "showIncomingCallUi" \| "silenceIncomingCall"</code> |

--------------------


### setup(...)

```typescript
setup(options: IOptions) => any
```

| Param         | Type                                          |
| ------------- | --------------------------------------------- |
| **`options`** | <code><a href="#ioptions">IOptions</a></code> |

**Returns:** <code>any</code>

--------------------


### hasDefaultPhoneAccount()

```typescript
hasDefaultPhoneAccount() => boolean
```

**Returns:** <code>boolean</code>

--------------------


### answerIncomingCall(...)

```typescript
answerIncomingCall(uuid: string) => void
```

| Param      | Type                |
| ---------- | ------------------- |
| **`uuid`** | <code>string</code> |

--------------------


### registerPhoneAccount()

```typescript
registerPhoneAccount() => void
```

--------------------


### registerAndroidEvents()

```typescript
registerAndroidEvents() => void
```

--------------------


### displayIncomingCall(...)

```typescript
displayIncomingCall(uuid: string, handle: string, localizedCallerName?: string | undefined, handleType?: "number" | "generic" | "email" | undefined, hasVideo?: boolean | undefined, options?: any) => void
```

| Param                     | Type                                          |
| ------------------------- | --------------------------------------------- |
| **`uuid`**                | <code>string</code>                           |
| **`handle`**              | <code>string</code>                           |
| **`localizedCallerName`** | <code>string</code>                           |
| **`handleType`**          | <code>"number" \| "generic" \| "email"</code> |
| **`hasVideo`**            | <code>boolean</code>                          |
| **`options`**             | <code>any</code>                              |

--------------------


### startCall(...)

```typescript
startCall(uuid: string, handle: string, contactIdentifier?: string | undefined, handleType?: "number" | "generic" | "email" | undefined, hasVideo?: boolean | undefined) => void
```

| Param                   | Type                                          |
| ----------------------- | --------------------------------------------- |
| **`uuid`**              | <code>string</code>                           |
| **`handle`**            | <code>string</code>                           |
| **`contactIdentifier`** | <code>string</code>                           |
| **`handleType`**        | <code>"number" \| "generic" \| "email"</code> |
| **`hasVideo`**          | <code>boolean</code>                          |

--------------------


### updateDisplay(...)

```typescript
updateDisplay(uuid: string, displayName: string, handle: string, options?: any) => void
```

| Param             | Type                |
| ----------------- | ------------------- |
| **`uuid`**        | <code>string</code> |
| **`displayName`** | <code>string</code> |
| **`handle`**      | <code>string</code> |
| **`options`**     | <code>any</code>    |

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
reportConnectedOutgoingCallWithUUID(uuid: string) => void
```

| Param      | Type                |
| ---------- | ------------------- |
| **`uuid`** | <code>string</code> |

--------------------


### reportConnectingOutgoingCallWithUUID(...)

```typescript
reportConnectingOutgoingCallWithUUID(uuid: string) => void
```

| Param      | Type                |
| ---------- | ------------------- |
| **`uuid`** | <code>string</code> |

--------------------


### reportEndCallWithUUID(...)

```typescript
reportEndCallWithUUID(uuid: string, reason: number) => void
```

| Param        | Type                |
| ------------ | ------------------- |
| **`uuid`**   | <code>string</code> |
| **`reason`** | <code>number</code> |

--------------------


### rejectCall(...)

```typescript
rejectCall(uuid: string) => void
```

| Param      | Type                |
| ---------- | ------------------- |
| **`uuid`** | <code>string</code> |

--------------------


### endCall(...)

```typescript
endCall(uuid: string) => void
```

| Param      | Type                |
| ---------- | ------------------- |
| **`uuid`** | <code>string</code> |

--------------------


### endAllCalls()

```typescript
endAllCalls() => void
```

--------------------


### setReachable()

```typescript
setReachable() => void
```

--------------------


### isCallActive(...)

```typescript
isCallActive(uuid: string) => any
```

| Param      | Type                |
| ---------- | ------------------- |
| **`uuid`** | <code>string</code> |

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


### supportConnectionService()

```typescript
supportConnectionService() => boolean
```

**Returns:** <code>boolean</code>

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
setMutedCall(uuid: string, muted: boolean) => void
```

| Param       | Type                 |
| ----------- | -------------------- |
| **`uuid`**  | <code>string</code>  |
| **`muted`** | <code>boolean</code> |

--------------------


### toggleAudioRouteSpeaker(...)

```typescript
toggleAudioRouteSpeaker(uuid: string, routeSpeaker: boolean) => void
```

| Param              | Type                 |
| ------------------ | -------------------- |
| **`uuid`**         | <code>string</code>  |
| **`routeSpeaker`** | <code>boolean</code> |

--------------------


### setOnHold(...)

```typescript
setOnHold(uuid: string, held: boolean) => void
```

| Param      | Type                 |
| ---------- | -------------------- |
| **`uuid`** | <code>string</code>  |
| **`held`** | <code>boolean</code> |

--------------------


### sendDTMF(...)

```typescript
sendDTMF(uuid: string, key: string) => void
```

| Param      | Type                |
| ---------- | ------------------- |
| **`uuid`** | <code>string</code> |
| **`key`**  | <code>string</code> |

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
setAvailable(active: boolean) => void
```

| Param        | Type                 |
| ------------ | -------------------- |
| **`active`** | <code>boolean</code> |

--------------------


### setForegroundServiceSettings(...)

```typescript
setForegroundServiceSettings(settings: any) => void
```

| Param          | Type             |
| -------------- | ---------------- |
| **`settings`** | <code>any</code> |

--------------------


### canMakeMultipleCalls(...)

```typescript
canMakeMultipleCalls(allow: boolean) => void
```

| Param       | Type                 |
| ----------- | -------------------- |
| **`allow`** | <code>boolean</code> |

--------------------


### setCurrentCallActive(...)

```typescript
setCurrentCallActive(callUUID: string) => void
```

| Param          | Type                |
| -------------- | ------------------- |
| **`callUUID`** | <code>string</code> |

--------------------


### backToForeground()

```typescript
backToForeground() => void
```

--------------------


### Interfaces


#### IOptions

| Prop          | Type                                                                                                                                                                                                                                                                                                |
| ------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **`ios`**     | <code>{ appName: string; imageName?: string; supportsVideo?: boolean; maximumCallGroups?: string; maximumCallsPerCallGroup?: string; ringtoneSound?: string; includesCallsInRecents?: boolean; }</code>                                                                                             |
| **`android`** | <code>{ alertTitle: string; alertDescription: string; cancelButton: string; okButton: string; imageName?: string; additionalPermissions: {}; selfManaged?: boolean; foregroundService?: { channelId: string; channelName: string; notificationTitle: string; notificationIcon?: string; }; }</code> |

</docgen-api>
