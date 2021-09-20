package com.splashcall.app;

import android.os.Bundle;
import com.dyadical.capcallkeep.CapCallKeepPlugin;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerPlugin(CapCallKeepPlugin.class);
    }
}
