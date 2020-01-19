package com.github.birros.vlc.extensions.api;

import com.github.birros.vlc.extensions.api.IExtensionHost;
import com.github.birros.vlc.extensions.api.VLCExtensionItem;

interface IExtensionService {
    // Protocol version 1
    oneway void onInitialize(int index, in IExtensionHost host);
    oneway void browse(String stringId);
    oneway void refresh();
}
