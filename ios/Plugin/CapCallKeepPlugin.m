#import <Capacitor/Capacitor.h>
#import <Foundation/Foundation.h>

// Define the plugin using the CAP_PLUGIN Macro, and
// each method the plugin supports using the CAP_PLUGIN_METHOD macro.
CAP_PLUGIN(CapCallKeepPlugin, "CapCallKeep", CAP_PLUGIN_METHOD(setupIos, CAPPluginReturnPromise);
           CAP_PLUGIN_METHOD(startCall, CAPPluginReturnPromise); CAP_PLUGIN_METHOD(hangupCall, CAPPluginReturnPromise);

)
