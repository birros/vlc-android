package com.github.birros.vlc.viewmodels

import android.content.Context
import org.videolan.medialibrary.interfaces.AbstractMedialibrary
import com.github.birros.vlc.util.RefreshModel
import com.github.birros.vlc.util.Settings
import com.github.birros.vlc.util.SortModule

abstract class SortableModel(protected val context: Context): ScopedModel(), RefreshModel,
    SortModule
{
    private val settings = Settings.getInstance(context)
    protected open val sortKey : String = this.javaClass.simpleName
    var sort = settings.getInt(sortKey, AbstractMedialibrary.SORT_DEFAULT)
    var desc = settings.getBoolean("${sortKey}_desc", false)

    var filterQuery : String? = null

    fun getKey() = sortKey

    override fun sort(sort: Int) {
        if (canSortBy(sort)) {
            desc = when (this.sort) {
                AbstractMedialibrary.SORT_DEFAULT -> sort == AbstractMedialibrary.SORT_ALPHA
                sort -> !desc
                else -> false
            }
            this.sort = sort
            refresh()
            settings.edit()
                    .putInt(sortKey, sort)
                    .putBoolean("${sortKey}_desc", desc)
                    .apply()
        }
    }

    abstract fun restore()
    abstract fun filter(query: String?)
}
