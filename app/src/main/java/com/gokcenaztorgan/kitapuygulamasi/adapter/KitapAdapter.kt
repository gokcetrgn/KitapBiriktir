package com.gokcenaztorgan.kitapuygulamasi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gokcenaztorgan.kitapuygulamasi.databinding.RecyclerRowBinding
import com.gokcenaztorgan.kitapuygulamasi.model.Kitap
import com.squareup.picasso.Picasso

class KitapAdapter(val list : ArrayList<Kitap>): RecyclerView.Adapter<KitapAdapter.KitapHolder>(){
    class KitapHolder(val binding : RecyclerRowBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KitapHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return KitapHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: KitapHolder, position: Int) {
        holder.binding.kitapAdi.text = list[position].kitapAdi
        Picasso.get().load(list[position].gorsel).into(holder.binding.imageView2)
    }
}