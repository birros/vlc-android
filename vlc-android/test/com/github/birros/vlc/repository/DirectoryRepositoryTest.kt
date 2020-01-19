package com.github.birros.vlc.repository

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.powermock.modules.junit4.PowerMockRunner
import com.github.birros.vlc.database.CustomDirectoryDao
import com.github.birros.vlc.database.MediaDatabase
import com.github.birros.vlc.database.models.CustomDirectory
import com.github.birros.vlc.util.TestUtil
import com.github.birros.vlc.util.argumentCaptor
import com.github.birros.vlc.util.mock
import com.github.birros.vlc.util.uninitialized

@RunWith(PowerMockRunner::class)
class DirectoryRepositoryTest {
    private val customDirectoryDao = mock<CustomDirectoryDao>()
    private lateinit var directoryRepository: DirectoryRepository

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before fun init() {
        val db = mock<MediaDatabase>()
        `when`(db.customDirectoryDao()).thenReturn(customDirectoryDao)
        directoryRepository = DirectoryRepository(customDirectoryDao)
    }

    @Test
    fun insertTwoCustomDirectory_GetAllShouldReturnTwo() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(2)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<CustomDirectory>()
        verify(customDirectoryDao, times(2)).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(2))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))
        assertThat(inserted.allValues[1], `is`(fakeCustomDirectories[1]))


        `when`(customDirectoryDao.getAll()).thenReturn(fakeCustomDirectories)

        val customDirectories = directoryRepository.getCustomDirectories()
        verify(customDirectoryDao).getAll()
        assertThat(customDirectories.size, `is`(2))
    }

    @Test
    fun insertTwoCustomDirectory_DeleteOneShouldDeleteOne() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(2)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<CustomDirectory>()
        verify(customDirectoryDao, times(2)).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(2))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))
        assertThat(inserted.allValues[1], `is`(fakeCustomDirectories[1]))

        directoryRepository.deleteCustomDirectory(fakeCustomDirectories[0].path).join()

        val deleted = argumentCaptor<CustomDirectory>()
        verify(customDirectoryDao).delete(deleted.capture() ?: uninitialized())
        assertThat(deleted.value, `is`(fakeCustomDirectories[0]))
    }

    @Test
    fun insertOneCustomDirectory_CheckExistenceShouldBeTrue() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(1)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<CustomDirectory>()
        verify(customDirectoryDao).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(1))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))

        `when`(customDirectoryDao.get(fakeCustomDirectories[0].path)).thenReturn(fakeCustomDirectories)

        val bool = directoryRepository.customDirectoryExists(fakeCustomDirectories[0].path)
        assertTrue(bool)
    }

    @Test
    fun insertOneCustomDirectory_CheckExistenceForWrongPathShouldBeFalse() = runBlocking{
        val fakeCustomDirectories = TestUtil.createCustomDirectories(1)
        fakeCustomDirectories.forEach {
            directoryRepository.addCustomDirectory(it.path).join()
        }

        val inserted = argumentCaptor<CustomDirectory>()
        verify(customDirectoryDao).insert(inserted.capture() ?: uninitialized())
        assertThat(inserted.allValues.size, `is`(1))
        assertThat(inserted.allValues[0], `is`(fakeCustomDirectories[0]))

        `when`(customDirectoryDao.get(fakeCustomDirectories[0].path)).thenReturn(fakeCustomDirectories)

        val bool = directoryRepository.customDirectoryExists(fakeCustomDirectories[0].path+"foo")
        assertFalse(bool)
    }

}
