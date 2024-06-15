package sakura.llar.notepadapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.PopupMenu
import android.widget.SearchView
import sakura.llar.notepadapp.Models.Note
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import sakura.llar.notepadapp.Adapter.NotesAdapter
import sakura.llar.notepadapp.Models.NoteViewModel
import sakura.llar.notepadapp.databinding.ActivitySearchBinding


class Search : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private lateinit var viewModel: NoteViewModel
    private lateinit var adapter: NotesAdapter

    private val updateNote =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {
                val note = result.data?.getSerializableExtra("note") as? Note

                if (note != null) {

                    viewModel.updateNote(note)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySearchBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.imgBackTheSearch.setOnClickListener {
            finish()
        }

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)

        binding.svSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchNotes(newText ?: "").observe(this@Search) { filteredNotes ->
                    adapter =
                        NotesAdapter(this@Search, object : NotesAdapter.NotesItemClickListener {
                            override fun onItemClicked(note: Note) {
                                val intent = Intent(this@Search, AddNote::class.java)

                                intent.putExtra("current_note", note)
                                updateNote.launch(intent)
                            }

                            override fun onLongItemClicked(note: Note, cardView: CardView) {
                                val popupMenu = PopupMenu(this@Search, cardView)

                                popupMenu.inflate(R.menu.pop_up_menu)

                                popupMenu.setOnMenuItemClickListener { menuItem ->
                                    when (menuItem.itemId) {
                                        R.id.delete_note -> {
                                            viewModel.deleteNote(note)
                                            true
                                        }

                                        else -> false
                                    }
                                }
                                popupMenu.show()
                            }
                        })

                    adapter.updateList(filteredNotes)
                    binding.searchResult.layoutManager =
                        StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
                    binding.searchResult.adapter = adapter
                }

                return true
            }
        })
    }
}