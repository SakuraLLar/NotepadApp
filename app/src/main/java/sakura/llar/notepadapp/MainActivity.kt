package sakura.llar.notepadapp

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import sakura.llar.notepadapp.Adapter.NotesAdapter
import sakura.llar.notepadapp.Database.NoteDatabase
import sakura.llar.notepadapp.Models.Note
import sakura.llar.notepadapp.Models.NoteViewModel
import sakura.llar.notepadapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), NotesAdapter.NotesItemClickListener,
    PopupMenu.OnMenuItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var database: NoteDatabase
    lateinit var viewModel: NoteViewModel
    lateinit var adapter: NotesAdapter
    lateinit var selectedNote: Note

    private val updateNote =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val note = result.data?.getSerializableExtra("note") as? Note

                if (note != null) {

                    viewModel.updateNote(note)
                }
            }
        }

    private val newNoteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            if (intent?.action == "NEW_NOTE_CREATED") {

                val newNote = intent.getSerializableExtra("note") as? Note

                if (newNote != null) {
                    viewModel.insertNote(newNote)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cl_main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initializing the UI
        initUI()

        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(NoteViewModel::class.java)

        viewModel.allNotes.observe(this) { list ->
            list.let {
                adapter.updateList(list)
            }
        }

        val search = findViewById<ImageButton>(R.id.img_search)
        search.setOnClickListener {

            val intent = Intent(this, Search::class.java)
            startActivity(intent)
        }

        database = NoteDatabase.getDatabase(this)

        if (intent?.action == "NEW_NOTE_CREATED" && !isLaunchedFromApp(intent)) {
            handleNewNoteIntent(intent)
        }
    }

    private fun handleNewNoteIntent(intent: Intent?) {

        if (intent?.action == "NEW_NOTE_CREATED") {

            val newNote = intent.getSerializableExtra("note") as? Note

            if (newNote != null) {
                viewModel.insertNote(newNote)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val intentFilter = IntentFilter("NEW_NOTE_CREATED")
        LocalBroadcastManager.getInstance(this).registerReceiver(newNoteReceiver, intentFilter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newNoteReceiver)
    }

    private fun initUI() {

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = StaggeredGridLayoutManager(2, LinearLayout.VERTICAL)
        adapter = NotesAdapter(this, this)
        binding.recyclerView.adapter = adapter

        binding.fbAddNote.setOnClickListener {
            val intent = Intent(this, AddNote::class.java)
            intent.putExtra("requestCode", ADD_NOTE_REQUEST_CODE)
            startActivityForResult(intent, ADD_NOTE_REQUEST_CODE)

        }
    }

    private fun isLaunchedFromApp(intent: Intent): Boolean {
        return intent.getIntExtra("requestCode", -1) == ADD_NOTE_REQUEST_CODE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val newNote = data?.getSerializableExtra("note") as? Note

            if (newNote != null) {
                viewModel.insertNote(newNote)

//                if (intent.action == null) {
//                    viewModel.insertNote(newNote)
//                }
            }
        }
    }

    companion object {
        private const val ADD_NOTE_REQUEST_CODE = 1
    }

    override fun onItemClicked(note: Note) {

        val intent = Intent(this@MainActivity, AddNote::class.java)

        intent.putExtra("current_note", note)
        updateNote.launch(intent)
    }

    override fun onLongItemClicked(note: Note, cardView: CardView) {
        selectedNote = note
        popIpDisplay(cardView)
    }

    private fun popIpDisplay(cardView: CardView) {

        val popup = PopupMenu(this, cardView)

        popup.setOnMenuItemClickListener(this@MainActivity)
        popup.inflate(R.menu.pop_up_menu)
        popup.show()
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.delete_note) {
            viewModel.deleteNote(selectedNote)

            return true
        }

        return false
    }
}