package com.gokcenaztorgan.kitapuygulamasi.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import com.gokcenaztorgan.kitapuygulamasi.R
import com.gokcenaztorgan.kitapuygulamasi.databinding.FragmentKullaniciGirisBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class KullaniciGirisFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    private var _binding: FragmentKullaniciGirisBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentKullaniciGirisBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.girisButton.setOnClickListener{
            girisYap(it)
        }
        binding.kayitButton.setOnClickListener{
            kayitOl(it)
        }
        val kullanici = auth.currentUser
        if(kullanici != null){
            val action = KullaniciGirisFragmentDirections.actionKullaniciGirisFragmentToAnasayfaFragment()
            Navigation.findNavController(requireView()).navigate(action)
        }
    }

    fun kayitOl(view: View){
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).
                    addOnCompleteListener { task->
                        if (task.isSuccessful) {
                            val action = KullaniciGirisFragmentDirections.actionKullaniciGirisFragmentToAnasayfaFragment()
                            Navigation.findNavController(requireView()).navigate(action)
                        }
                    }.
                    addOnFailureListener{e ->
                        Toast.makeText(requireContext(), e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }

    }
    fun girisYap(view: View){
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        if (email.isNotEmpty() && password.isNotEmpty()) {
            auth.signInWithEmailAndPassword(email,password).addOnSuccessListener {
                val action = KullaniciGirisFragmentDirections.actionKullaniciGirisFragmentToAnasayfaFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), e.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}