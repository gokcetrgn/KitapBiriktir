package com.gokcenaztorgan.kitapuygulamasi.view

import android.opengl.ETC1
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.gokcenaztorgan.kitapuygulamasi.R
import com.gokcenaztorgan.kitapuygulamasi.adapter.KitapAdapter
import com.gokcenaztorgan.kitapuygulamasi.databinding.FragmentAnasayfaBinding
import com.gokcenaztorgan.kitapuygulamasi.databinding.FragmentKitapDetayBinding
import com.gokcenaztorgan.kitapuygulamasi.model.Kitap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class AnasayfaFragment : Fragment(), PopupMenu.OnMenuItemClickListener {
    private var _binding: FragmentAnasayfaBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    private var adapter : KitapAdapter?= null
    private lateinit var db : FirebaseFirestore

    val list : ArrayList<Kitap> = ArrayList()
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        db = Firebase.firestore
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnasayfaBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.floatingActionButton.setOnClickListener{
            floatingButtonTiklandi(it)
        }
        firebaseVerileriAl()
        adapter = KitapAdapter(list)
        binding.recyclerView.adapter = adapter
    }

    fun firebaseVerileriAl() {
        db.collection("Books").addSnapshotListener { snapshot, exception ->
            if (exception != null) {
                Toast.makeText(requireContext(), exception.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            } else {

                if (snapshot != null) {
                    if (!snapshot.isEmpty) {

                        list.clear()

                        val documents = snapshot.documents
                        for (document in documents) {
                            val kitapAdi = document.get("kitapAdi")
                            val gorselUrl = document.get("downloadurl")
                            val post = Kitap(kitapAdi.toString(), gorselUrl.toString())
                            list.add(post)
                        }
                        adapter!!.notifyDataSetChanged()

                    }
                }

            }
        }

    }

    fun floatingButtonTiklandi(view: View){
        val popMenu = PopupMenu(requireContext(),binding.floatingActionButton)
        val inflater = popMenu.menuInflater
        inflater.inflate(R.menu.my_popup_menu,popMenu.menu)
        popMenu.setOnMenuItemClickListener(this)
        popMenu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.yuklemeEkran){
            val action = AnasayfaFragmentDirections.actionAnasayfaFragmentToKitapDetayFragment(0)
            Navigation.findNavController(requireView()).navigate(action)
        } else if (item?.itemId == R.id.cikisEkran) {
            auth.signOut()
            val action = AnasayfaFragmentDirections.actionAnasayfaFragmentToKullaniciGirisFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
        return true
    }


}