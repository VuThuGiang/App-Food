package vn.dtc.project.grabfood.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.dtc.project.grabfood.data.FavouriteFood
import vn.dtc.project.grabfood.data.Food
import vn.dtc.project.grabfood.databinding.FavouriteItemBinding
import vn.dtc.project.grabfood.helper.getFoodPrice

class FavouriteFoodAdapter : RecyclerView.Adapter<FavouriteFoodAdapter.FavouriteFoodViewHolder>() {

    inner class FavouriteFoodViewHolder(val binding: FavouriteItemBinding):
        RecyclerView.ViewHolder(binding.root){

        fun bind(favouriteFood: FavouriteFood){
            binding.apply {
                Glide.with(itemView).load(favouriteFood.food.images[0]).into(imageFavouriteFood)
                tvFoodFavouriteName.text = favouriteFood.food.name
                val priceAfterPercentage = favouriteFood.food.offerPercentage.getFoodPrice(favouriteFood.food.price)
                tvFoodFavouritePrice.text =  "$ ${String.format("%.2f", priceAfterPercentage)}"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<FavouriteFood>(){
        override fun areItemsTheSame(oldItem: FavouriteFood, newItem: FavouriteFood): Boolean {
            return oldItem.food.id == newItem.food.id
        }

        override fun areContentsTheSame(oldItem: FavouriteFood, newItem: FavouriteFood): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteFoodViewHolder {
        return FavouriteFoodViewHolder(
            FavouriteItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: FavouriteFoodViewHolder, position: Int) {
        val favouriteFood = differ.currentList[position]
        holder.bind(favouriteFood)

        holder.itemView.setOnClickListener{
            onFoodClick?.invoke(favouriteFood)
        }
        holder.binding.imageMinus.setOnClickListener{
            onRemoveClick?.invoke(favouriteFood)
        }
    }

    var onFoodClick:((FavouriteFood) -> Unit)? = null
    var onRemoveClick:((FavouriteFood) -> Unit)? = null
}
