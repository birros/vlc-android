package com.github.birros.vlc.gui.tv

interface TvItemAdapter : TvFocusableAdapter {
    fun submitList(pagedList: Any?)

    var focusNext: Int
}
