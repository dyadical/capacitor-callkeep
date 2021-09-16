import CallKit
import Capacitor
import Foundation
import PushKit
import UIKit

// Main TODO in order of priority:
// [X] endAllCalls
// [ ] notifyListeners("showIncomingCallUi", data: [ callUUID: string; handle: string; name: string ])
// [ ] checkIfBusy: boolean
// [ ] isCallActive
// [ ] checkSpeaker: boolean
// [ ] setMutedCall
// [ ] getCalls: Call[]
// [X] notifyListeners("setMutedCall", data: [ "callUUID": string, "muted": boolean ])
// [ ] notifyListeners("silenceIncomingCall", data: [ callUUID: string; handle: string; name: string ])

// Other items:
// [ ] notifyListeners("toggleHold", data: [ "callUUID": string, "hold": boolean ])
// [ ] notifyListeners("DTMFAction", data: [ "callUUID": string, "digits": string ])
// [ ] notifyListeners("activateAudioSession", data: [  ])
// [ ] notifyListeners("checkReachability", data: [  ])
// [ ] hasDefaultPhoneAccount
// [ ] reportConnectedOutgoingCallWithUUID
// [ ] reportConnectingOutgoingCallWithUUID
// [ ] getInitialEvents: Event[]
// [ ] displayIncomingCall
// [ ] setReachable
// [ ] getAudioRoutes
// [ ] setAudioRoute
// [ ] setOnHold
// [ ] sendDTMF

let printDebug = true

/**
 *  CallKit Voip Plugin provides native PushKit functionality with apple CallKit to ionic capacitor
 */
@objc(CapCallKeepPlugin)
public class CapCallKeepPlugin: CAPPlugin {
    private var provider: CXProvider?
    // private let implementation          = CallKitVoip()
    private let voipRegistry = PKPushRegistry(queue: nil)
    private var connectionIdRegistry: [UUID: CallConfig] = [:]

    @objc func setupIOS(_ call: CAPPluginCall) {
        dprint("register()")
        let localizedName = call.getString("appName") ?? "App"
        // config PushKit
        voipRegistry.delegate = self
        voipRegistry.desiredPushTypes = [.voIP]

        let config = CXProviderConfiguration(localizedName: localizedName)
        // TODO: lots of these settings could be passed in the call
        config.supportsVideo = true
        config.supportedHandleTypes = [.emailAddress, .phoneNumber, .generic]

        provider = CXProvider(configuration: config)
        provider?.setDelegate(self, queue: DispatchQueue.main)

        call.resolve()
    }

    @objc public func startCall(_ call: CAPPluginCall) {
        dprint("startCall()")
        let hasVideo = (call.getString("hasVideo") ?? "no") == "yes"
        guard let username = call.getString("username") else {
            call.reject("must specify username")
            return
        }
        let calleeType = call.getString("calleeType") ?? "generic"
        let handleTypes = [
            "generic": CXHandle.HandleType.generic,
            "phoneNumber": CXHandle.HandleType.phoneNumber,
            "emailAddress": CXHandle.HandleType.emailAddress,
        ]
        guard let handleType = handleTypes[calleeType] else {
            call.reject("unknown calleeType \(calleeType) must be one of \(handleTypes.keys)")
            return
        }

        let controller = CXCallController()
        let uuid = UUID()
        connectionIdRegistry[uuid] = .init(connectionId: uuid.uuidString, username: username)
        let calleeHandle = CXHandle(type: handleType, value: username)
        let startCallAction = CXStartCallAction(call: uuid, handle: calleeHandle)
        startCallAction.isVideo = hasVideo

        let transaction = CXTransaction(action: startCallAction)
        controller.request(transaction) { error in
            if let error = error {
                call.resolve(["value": "Error requesting transaction: \(error)"])
            } else {
                call.resolve(["value": "Requested transaction successfully"])
            }
        }
        //        self.provider?.reportOutgoingCall(with: uuid, startedConnectingAt: <#T##Date?#>)
        //        DispatchQueue.main.asyncAfter(wallDeadline: DispatchWallTime.now() + 5) {
        //            dprint("Faking call connection")
        //            self.provider?.reportOutgoingCall(with: controller.callObserver.calls[0].uuid, connectedAt: nil)
        //        }
    }

    public func notifyEvent(eventName: String, uuid: UUID, info: [String: Any]? = nil) {
        dprint("notifyEvent(\(eventName))")
        if let config = connectionIdRegistry[uuid] {
            var data: [String: Any] = [
                "callUUID": config.connectionId,
                "handle": config.username,
            ]
            info?.forEach { key, value in data[key] = value }
            notifyListeners(eventName, data: data)
            // connectionIdRegistry[uuid] = nil
            // TODO: clear uuid from registry on hangup
        } else {
            dprint("warning: uuid \(uuid) not in the registry")
        }
    }

    public func incomingCall(from: String, connectionId: String) {
        dprint("incomingCall()")
        let update = CXCallUpdate()
        update.remoteHandle = CXHandle(type: .generic, value: from)
        update.hasVideo = true

        let uuid = UUID()
        connectionIdRegistry[uuid] = .init(connectionId: connectionId, username: from)
        provider?.reportNewIncomingCall(with: uuid, update: update, completion: { _ in })
    }

    @objc public func endCall(_ call: CAPPluginCall) {
        dprint("endCall()")
        let controller = CXCallController()
        let uuidString = call.getString("uuid") ?? ""
        guard let uuid = uuidString == "" ? controller.callObserver.calls[0].uuid : UUID(uuidString: uuidString)
        else { call.reject("no uuid provided or no known one")
            return
        }
        // TODO: call.reject() if no calls
        // let uuid = controller.callObserver.calls[0].uuid
//        let controller = CXCallController()
        let transaction = CXTransaction(action: CXEndCallAction(call: uuid))
        controller.request(transaction, completion: { _ in })
        call.resolve()
    }

    @objc public func endAllCalls(_ call: CAPPluginCall) {
        dprint("endAllCalls()")
        let controller = CXCallController()
        for c in controller.callObserver.calls {
            let transaction = CXTransaction(action: CXEndCallAction(call: c.uuid))
            controller.request(transaction, completion: { _ in })
        }
        call.resolve()
    }
}

// MARK: CallKit events handler

extension CapCallKeepPlugin: CXProviderDelegate {
    public func providerDidReset(_: CXProvider) {
        dprint("provider did reset")
    }

    public func provider(_: CXProvider, perform action: CXAnswerCallAction) {
        dprint("CXAnswerCallAction()")
        notifyEvent(eventName: "answerCall", uuid: action.callUUID)
        // TODO: add hook to end call
        action.fulfill()
        //        endCall(uuid: action.callUUID)
    }

    public func provider(_: CXProvider, perform action: CXEndCallAction) {
        dprint("CXEndCallAction()")
        notifyEvent(eventName: "endCall", uuid: action.callUUID)
        action.fulfill()
    }

    public func provider(_: CXProvider, perform action: CXStartCallAction) {
        dprint("CXStartCallAction")
        notifyEvent(eventName: "startCall", uuid: action.callUUID)
        action.fulfill()
    }

    public func provider(_: CXProvider, perform action: CXPlayDTMFCallAction) {
        dprint("CXPlayDTMFCallAction")
        notifyEvent(eventName: "DTMFAction", uuid: action.callUUID) // TODO: add digits
        action.fulfill()
    }

    public func provider(_: CXProvider, perform action: CXSetMutedCallAction) {
        dprint("CXSetMutedCallAction")
        notifyEvent(eventName: "setMutedCall", uuid: action.callUUID, info: ["muted": action.isMuted])
        action.fulfill()
    }
}

// MARK: PushKit events handler

extension CapCallKeepPlugin: PKPushRegistryDelegate {
    public func pushRegistry(_: PKPushRegistry, didUpdate pushCredentials: PKPushCredentials, for _: PKPushType) {
        dprint("pushRegistry didUpdate")
        let parts = pushCredentials.token.map { String(format: "%02.2hhx", $0) }
        let token = parts.joined()
        notifyListeners("registration", data: ["token": token])
    }

    public func pushRegistry(_: PKPushRegistry, didReceiveIncomingPushWith payload: PKPushPayload, for _: PKPushType, completion _: @escaping () -> Void) {
        dprint("pushRegistry didReceiveIncomingPushWith")

        guard let connectionId = payload.dictionaryPayload["ConnectionId"] as? String else {
            print("ConnectionId key not found in ")
            return
        }

        let username = (payload.dictionaryPayload["Username"] as? String) ?? "Anonymus"

        incomingCall(from: username, connectionId: connectionId)
    }
}

extension CapCallKeepPlugin {
    struct CallConfig {
        let connectionId: String
        let username: String
    }

    struct CallInfo {
        let callUUID: String
        let handle: String
        let name: String
    }
}

func dprint(_ message: String) {
    if printDebug {
        print("CapCallKeepPlugin:", message)
    }
}
