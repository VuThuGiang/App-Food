package vn.dtc.project.grabfood.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vn.dtc.project.grabfood.data.CartFood
import vn.dtc.project.grabfood.data.Food
import vn.dtc.project.grabfood.databinding.CartFoodItemBinding
import vn.dtc.project.grabfood.helper.getFoodPrice

class CartFoodAdapter: RecyclerView.Adapter<CartFoodAdapter.CartFoodViewHolder>() {

    inner class CartFoodViewHolder( val binding: CartFoodItemBinding):
        RecyclerView.ViewHolder(binding.root){
        @SuppressLint("SetTextI18n")
        fun bind(cartFood: CartFood){
            binding.apply {
                Glide.with(itemView).load(cartFood.food.images[0]).into(imageCartFood)
                tvFoodCartName.text = cartFood.food.name
                tvCartFoodQuantity.text = cartFood.quantity.toString()

                val priceAfterPercentage = cartFood.food.offerPercentage.getFoodPrice(cartFood.food.price)
                tvFoodCartPrice.text = "$ ${String.format("%.2f",priceAfterPercentage)}"
            }
        }
    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartFood>(){
        override fun areItemsTheSame(oldItem: CartFood, newItem: CartFood): Boolean {
            return oldItem.food.id == newItem.food.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: CartFood, newItem: CartFood): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this, diffCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartFoodViewHolder {
        return CartFoodViewHolder(
            CartFoodItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CartFoodViewHolder, position: Int) {
        val cartFood = differ.currentList[position]
        holder.bind(cartFood)

        holder.itemView.setOnClickListener{
            onFoodClick?.invoke(cartFood)
        }
        holder.binding.imagePlus.setOnClickListener{
            onPlusClick?.invoke(cartFood)
        }
        holder.binding.imageMinus.setOnClickListener{
            onMinusClick?.invoke(cartFood)
        }
    }

    var onFoodClick:((CartFood) -> Unit)? = null
    var onPlusClick:((CartFood) -> Unit)? = null
    var onMinusClick:((CartFood) -> Unit)? = null

}
