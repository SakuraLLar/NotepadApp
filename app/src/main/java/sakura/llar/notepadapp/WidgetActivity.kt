package sakura.llar.notepadapp

import android.content.Intent
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity


class WidgetActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, AddNote::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.putExtra("isFromWidget", true)
        startActivity(intent)
        finish()
    }
}