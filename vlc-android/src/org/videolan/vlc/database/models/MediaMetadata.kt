/*
 * ************************************************************************
 *  MediaMetadata.kt
 * *************************************************************************
 * Copyright © 2019 VLC authors and VideoLAN
 * Author: Nicolas POMEPUY
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 * **************************************************************************
 *
 *
 */

/*******************************************************************************
 *  BrowserFav.kt
 * ****************************************************************************
 * Copyright © 2018 VLC authors and VideoLAN
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
 ******************************************************************************/

package org.videolan.vlc.database.models

import androidx.room.*
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "media_metadata", foreignKeys = [ForeignKey(entity = MediaTvshow::class, parentColumns = ["moviepedia_show_id"], childColumns = ["show_id"])])
data class MediaMetadata(
        @PrimaryKey
        @ColumnInfo(name = "ml_id")
        val mlId: Long,
        @ColumnInfo(name = "type")
        val type: Int,
        @ColumnInfo(name = "moviepedia_id")
        val moviepediaId: String,
        @ColumnInfo(name = "title")
        val title: String,
        @ColumnInfo(name = "summary")
        val summary: String,
        @ColumnInfo(name = "genres")
        val genres: String,
        @ColumnInfo(name = "release_date")
        val releaseDate: Date?,
        @ColumnInfo(name = "countries")
        val countries: String,
        @ColumnInfo(name = "current_poster")
        var currentPoster: String,
        @ColumnInfo(name = "season")
        val season: Int?,
        @ColumnInfo(name = "episode")
        val episode: Int?,
        @ColumnInfo(name = "current_backdrop")
        var currentBackdrop: String,
        @ColumnInfo(name = "show_id")
        var show_id: String?

)

class MediaMetadataWithImages {
    @Embedded
    lateinit var metadata: MediaMetadata

    @Relation(parentColumn = "show_id", entityColumn = "moviepedia_show_id", entity = MediaTvshow::class)
    lateinit var show: MediaTvshow

    @Relation(parentColumn = "ml_id", entityColumn = "media_id", entity = MediaImage::class)
    var images: List<MediaImage> = ArrayList()
}

fun MediaMetadataWithImages.subtitle(): String = if (metadata.type == 0) movieSubtitle() else tvshowSubtitle()
fun MediaMetadataWithImages.movieSubtitle(): String {

    val subtitle = ArrayList<String>()
    metadata.releaseDate?.let {
        subtitle.add(SimpleDateFormat("yyyy", Locale.getDefault()).format(it))
    }
    subtitle.add(metadata.genres)
    subtitle.add(metadata.countries)

    return subtitle.filter { it.isNotEmpty() }.joinToString(separator = " · ") { it }
}

fun MediaMetadataWithImages.tvshowSubtitle(): String {

    val subtitle = ArrayList<String>()
    metadata.releaseDate?.let {
        subtitle.add(SimpleDateFormat("yyyy", Locale.getDefault()).format(it))
    }
    subtitle.add(show.name)
    subtitle.add("S${metadata.season.toString().padStart(1, '0')}E${metadata.episode.toString().padStart(1, '0')}")

    return subtitle.filter { it.isNotEmpty() }.joinToString(separator = " · ") { it }
}

@Entity(tableName = "media_person_join",
        primaryKeys = arrayOf("mediaId", "personId", "type"),
        foreignKeys = arrayOf(
                ForeignKey(entity = MediaMetadata::class,
                        parentColumns = arrayOf("ml_id"),
                        childColumns = arrayOf("mediaId")),
                ForeignKey(entity = Person::class,
                        parentColumns = arrayOf("moviepedia_id"),
                        childColumns = arrayOf("personId"))
        )
)
data class MediaPersonJoin(
        val mediaId: Long,
        val personId: String,
        val type: PersonType
)

enum class PersonType(val key: Int) {
    ACTOR(0), DIRECTOR(1), MUSICIAN(2), PRODUCER(3), WRITER(4);

    companion object {
        fun fromKey(key: Int): PersonType {
            values().forEach { if (it.key == key) return it }
            return ACTOR
        }
    }
}

@Entity(tableName = "media_tv_show")
data class MediaTvshow(
        @PrimaryKey
        @ColumnInfo(name = "moviepedia_show_id")
        val moviepediaShowId: String,
        @ColumnInfo(name = "name")
        val name: String

)

@Entity(tableName = "media_metadata_person")
data class Person(
        @PrimaryKey
        @ColumnInfo(name = "moviepedia_id")
        val moviepediaId: String,
        @ColumnInfo(name = "name")
        val name: String,
        @ColumnInfo(name = "image")
        val image: String?
)

@Entity(tableName = "media_metadata_image", foreignKeys = [ForeignKey(entity = MediaMetadata::class, parentColumns = ["ml_id"], childColumns = ["media_id"])])
data class MediaImage(
        @PrimaryKey
        @ColumnInfo(name = "url")
        val url: String,
        @ColumnInfo(name = "media_id")
        val mediaId: Long,
        @ColumnInfo(name = "image_type")
        val imageType: MediaImageType
)

enum class MediaImageType(val key: Int) {
    BACKDROP(0), POSTER(1);

    companion object {
        fun fromKey(key: Int): MediaImageType {
            values().forEach { if (it.key == key) return it }
            return BACKDROP
        }
    }
}