package com.gokcenaztorgan.kitapuygulamasi.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.gokcenaztorgan.kitapuygulamasi.R
import com.gokcenaztorgan.kitapuygulamasi.databinding.FragmentKitapDetayBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.util.UUID

class KitapDetayFragment : Fragment() {

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var _binding: FragmentKitapDetayBinding? = null

    var secilenGorsel : Uri? = null
    var bitmap : Bitmap? = null


    private lateinit var auth : FirebaseAuth
    private lateinit var storage : FirebaseStorage
    private lateinit var db : FirebaseFirestore

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerLauncher()
        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKitapDetayBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.kitapGorseli.setOnClickListener{
            gorselSec(it)
        }
        binding.addButton.setOnClickListener{
            anasayfadaGoster(it)
        }
    }
    fun anasayfadaGoster(view: View) {
        val uuid = UUID.randomUUID()
        val gorselAdi = "${uuid}.jpg"

        val referance = storage.reference
        val gorselReferansi = referance.child("images").child(gorselAdi)
        if (secilenGorsel != null) {
            gorselReferansi.putFile(secilenGorsel!!).addOnSuccessListener { uploadTask ->
                // url alacağız
                gorselReferansi.downloadUrl.addOnSuccessListener { uri ->
                    val downloadUrl = uri.toString()
                    // veritabanına kaydedeceğiz.
                    val postMap = hashMapOf<String, Any>()
                    postMap.put("downloadurl", downloadUrl)
                    postMap.put("kitapAdi", binding.kitapAdiText.text.toString())

                    db.collection("Books").add(postMap).addOnCompleteListener { task ->

                        if (task.isComplete && task.isSuccessful) {
                            //back
                            val action = KitapDetayFragmentDirections.actionKitapDetayFragmentToAnasayfaFragment()
                            Navigation.findNavController(view).navigate(action)

                        }

                    }.addOnFailureListener { exception ->
                        Toast.makeText(
                            requireContext(),
                            exception.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    fun gorselSec(view: View){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            // read media images
            if(ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED){
                // izin yok
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)){
                    Snackbar.make(view,"Galeriye gitmemiz gerek.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",
                        View.OnClickListener {
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }).show()
                }else{
                    // izin iste
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }else{
                // izin var, galeriye git
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
        else{
            // read external storage
            if(ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                // izin yok
                if(ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)){
                    Snackbar.make(view,"Galeriye gitmemiz gerek.", Snackbar.LENGTH_INDEFINITE).setAction("İzin ver",
                        View.OnClickListener {
                            //izin iste
                            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                        }).show()
                }else{
                    // izin iste
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }else{
                // izin var, galeriye git
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)
            }
        }
    }
    fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        secilenGorsel = intent.data
                        try {
                            if (Build.VERSION.SDK_INT >= 28) {
                                val source = ImageDecoder.createSource(
                                    requireActivity().contentResolver,
                                    secilenGorsel!!
                                )
                                bitmap = ImageDecoder.decodeBitmap(source)
                                binding.kitapGorseli.setImageBitmap(bitmap)


                            } else {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    requireActivity().contentResolver,
                                    secilenGorsel
                                )
                                binding.kitapGorseli.setImageBitmap(bitmap)
                            }

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
            if(result){
                // izin verildi
                val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else{
                // reddedildi
                Toast.makeText(requireContext(),"İzne ihtiyacımız var.", Toast.LENGTH_LONG).show()

            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}