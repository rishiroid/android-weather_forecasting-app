package net.rishiz.weather.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.rishiz.weather.databinding.FutureForecastRowBinding

class FutureForecastAdapter(val data:List<FutureForecastAdapter>):
    RecyclerView.Adapter<FutureForecastAdapter.FutureForecastHolder>() {
    class FutureForecastHolder(binding: FutureForecastRowBinding) :RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FutureForecastHolder {
        val inflater=LayoutInflater.from(parent.context)
        val view=FutureForecastRowBinding.inflate(inflater,parent,false)
        return FutureForecastHolder(view)
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: FutureForecastHolder, position: Int) {
        TODO("Not yet implemented")
    }
}