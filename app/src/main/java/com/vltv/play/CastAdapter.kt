package com.vltv.play

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vltv.play.R

data class CastMember(val name: String, val profilePath: String?)

class CastAdapter(private val cast: List<CastMember>) : RecyclerView.Adapter<CastAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgActor: ImageView = view.findViewById(R.id.imgActor)
        val tvName: TextView = view.findViewById(R.id.tvActorName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cast, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val actor = cast[position]
        holder.tvName.text = actor.name
        
        val fullUrl = "https://image.tmdb.org/t/p/w185${actor.profilePath}"
        Glide.with(holder.itemView.context)
            .load(if (actor.profilePath != null) fullUrl else R.drawable.ic_user_placeholder)
            .circleCrop()
            .into(holder.imgActor)
    }

    override fun getItemCount() = cast.size
}
