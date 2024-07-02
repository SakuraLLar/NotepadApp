package sakura.llar.notepadapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import sakura.llar.notepadapp.Models.Note
import sakura.llar.notepadapp.databinding.ActivityAddNoteBinding
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.text.SimpleDateFormat
import java.util.Date

class AddNote : AppCompatActivity() {

    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var note: Note
    private lateinit var oldNote: Note
    var isUpdate = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->

            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        try {

            oldNote = intent.getSerializableExtra("current_note") as Note
            binding.etTitle.setText(oldNote.title)
            binding.etNote.setText(oldNote.note)
            isUpdate = true
        } catch (e: Exception) {

            isUpdate = false
        }

        binding.imgDone.setOnClickListener {

            val title = binding.etTitle.text.toString()
            val noteDesc = binding.etNote.text.toString()

            if (title.isNotEmpty() || noteDesc.isNotEmpty()) {

                val formatter = SimpleDateFormat("EEE, d MMM yyyy HH:mm a")

                note = if (isUpdate && ::oldNote.isInitialized) {
                    Note(oldNote.id, title, noteDesc, formatter.format(Date()))
                } else {
                    Note(null, title, noteDesc, formatter.format(Date()))
                }

                sendNoteToMainActivity(note)

                val resultIntent = Intent()
                resultIntent.putExtra("note", note)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()

            } else {

                Toast.makeText(
                    this@AddNote, "Please enter some data",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }
        }

        binding.imgBackTheAdd.setOnClickListener {
            finish()
        }
    }

    private fun sendNoteToMainActivity(note: Note) {

        if (intent.getBooleanExtra("isFromWidget", false)) {

            val intent = Intent("NEW_NOTE_CREATED")
            intent.putExtra("note", note)

            Handler(Looper.getMainLooper()).postDelayed({

                LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
            }, 100)
        }
    }
}