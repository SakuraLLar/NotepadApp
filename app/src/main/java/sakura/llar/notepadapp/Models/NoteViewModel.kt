package sakura.llar.notepadapp.Models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import sakura.llar.notepadapp.Database.NoteDatabase
import sakura.llar.notepadapp.Database.NotesRepository

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotesRepository

    val allNotes: LiveData<List<Note>>

    init {
        val dao = NoteDatabase.getDatabase(application).getNoteDao()
        repository = NotesRepository(dao)
        allNotes = repository.allNotes
    }

    fun deleteNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.delete(note)
    }

    fun insertNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.insert(note)
    }

    fun updateNote(note: Note) = viewModelScope.launch(Dispatchers.IO) {

        repository.update(note)
    }

    fun searchNotes(query: String): LiveData<List<Note>> {

        return repository.searchNotes(query)
    }
}