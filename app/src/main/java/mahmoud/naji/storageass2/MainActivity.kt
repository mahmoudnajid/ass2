package mahmoud.naji.storageass2

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var upload: ImageView? = null
    private var imageUri: Uri? = null
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        upload = findViewById(R.id.uploadpdf)

        upload!!.setOnClickListener(View.OnClickListener {
            val galleryIntent = Intent()
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            galleryIntent.type = "application/pdf"
            startActivityForResult(galleryIntent, 1)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            dialog = ProgressDialog(this@MainActivity)
            dialog!!.setMessage("Uploading")
            dialog!!.show()
            imageUri = data!!.data
            val timestamp = "" + System.currentTimeMillis()
            val storageReference = FirebaseStorage.getInstance().getReference()
            val messagePushID = timestamp
            Toast.makeText(this@MainActivity, imageUri.toString(), Toast.LENGTH_SHORT).show()
            val filepath = storageReference.child("$messagePushID.pdf")
            Toast.makeText(this@MainActivity, filepath.name, Toast.LENGTH_SHORT).show()
            tvname.text = filepath.name
            filepath.putFile(imageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    filepath.downloadUrl
                }


                .addOnCompleteListener(OnCompleteListener<Uri> { task ->
                    if (task.isSuccessful) {
                        dialog!!.dismiss()
                        val uri = task.result
                        var myurl: String?
                        myurl = uri.toString()
                        Toast.makeText(this@MainActivity, "Uploaded Successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        dialog!!.dismiss()
                        Toast.makeText(this@MainActivity, "Upload Failed", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }
}
