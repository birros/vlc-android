package com.github.birros.vlc.viewmodels.tv

import org.videolan.medialibrary.media.MediaLibraryItem
import com.github.birros.vlc.providers.HeaderProvider

interface TvBrowserModel {

    var currentItem: MediaLibraryItem?
    var nbColumns: Int
    val provider: HeaderProvider
}
