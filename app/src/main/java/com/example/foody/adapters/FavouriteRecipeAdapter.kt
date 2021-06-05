package com.example.foody.adapters

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.foody.R
import com.example.foody.data.database.entities.FavouritesEntity
import com.example.foody.databinding.FavouriteRecipeRowLayoutBinding
import com.example.foody.ui.fragments.favourites.FavouriteRecipesFragmentDirections
import com.example.foody.util.RecipesDiffUtil
import com.example.foody.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar

class FavouriteRecipeAdapter(
    private val requireActivity:FragmentActivity,
    private val mainViewModel: MainViewModel
) : RecyclerView.Adapter<FavouriteRecipeAdapter.MyViewHolder>(),
    ActionMode.Callback{

    private var multiSelection = false
    private lateinit var rootView:View

    private lateinit var mActionMode: ActionMode

    private var myViewHolders = arrayListOf<MyViewHolder>()
    private var selectedRecipe = arrayListOf<FavouritesEntity>()
    private var favouriteRecipe = emptyList<FavouritesEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder)
        rootView = holder.binding.root
        val recipe = favouriteRecipe[position]

        holder.bind(recipe)
        saveItemStateOnScroll(recipe, holder)

        holder.binding.favouriteRecipeRowLayout.setOnClickListener {
            if(multiSelection){
                applySelection(holder, recipe)
            } else {
                val action = FavouriteRecipesFragmentDirections
                    .actionFavouriteRecipesFragmentToDetailsActivity(recipe.result)
                holder.binding.root.findNavController().navigate(action)
            }
        }

        holder.binding.favouriteRecipeRowLayout.setOnLongClickListener {
            if(!multiSelection){
                multiSelection = true
                requireActivity.startActionMode(this)
                applySelection(holder, recipe)
                true
            } else {
                applySelection(holder, recipe)
                true
            }
        }
    }

    private fun saveItemStateOnScroll(currentRecipe: FavouritesEntity, holder: MyViewHolder){
        if(selectedRecipe.contains(currentRecipe)){
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
        } else {
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
    }

    override fun getItemCount(): Int = favouriteRecipe.size

    fun setData(newFavouriteRecipe: List<FavouritesEntity>){
        val recipesDiffUtil = RecipesDiffUtil(favouriteRecipe, newFavouriteRecipe)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtil)
        favouriteRecipe = newFavouriteRecipe
        diffUtilResult.dispatchUpdatesTo(this)
    }

    private fun applySelection(holder: MyViewHolder, currentRecipe:FavouritesEntity){
        if(selectedRecipe.contains(currentRecipe)){
            selectedRecipe.remove(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
            applyActionModeTitle()
        } else {
            selectedRecipe.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun changeRecipeStyle(holder: MyViewHolder, backgroundColor:Int, strokeColor:Int){
        holder.binding.favouriteRecipeRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.binding.favouriteRowCardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }

    private fun applyActionModeTitle(){
        when(selectedRecipe.size){
            0 -> {
                mActionMode.finish()
                multiSelection = false
            }
            1 -> {
                mActionMode.title = "${selectedRecipe.size} item selected"
            }
            else -> {
                mActionMode.title = "${selectedRecipe.size} items selected"
            }
        }
    }

    class MyViewHolder(val binding: FavouriteRecipeRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(favouritesEntity: FavouritesEntity){
                binding.favouritesEntity = favouritesEntity
                binding.executePendingBindings()
            }

            companion object {
                fun from(parent: ViewGroup):MyViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = FavouriteRecipeRowLayoutBinding.inflate(layoutInflater, parent, false)
                    return MyViewHolder(binding)
                }
            }
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.menuInflater?.inflate(R.menu.favourites_contextual_menu, menu)
        mActionMode = mode!!
        applyStatusBarColor(R.color.contextualStatusBarColor)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        if(item?.itemId == R.id.delete_favourite_recipe_menu){
            selectedRecipe.forEach {
                mainViewModel.deleteFavouriteRecipe(it)
            }
            showSnackBar("${selectedRecipe.size} Recipe/s removed")
            multiSelection = false
            selectedRecipe.clear()
            mode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        multiSelection = false
        selectedRecipe.clear()
        applyStatusBarColor(R.color.statusBarColor)
    }

    private fun showSnackBar(message:String){
        Snackbar.make(
            rootView,
            message,
            Snackbar.LENGTH_SHORT
        ).setAction("Okay"){

        }.show()
    }

    fun clearContextualActionMode(){
        if(this::mActionMode.isInitialized){
            mActionMode.finish()
        }
    }

    private fun applyStatusBarColor(color:Int){
        requireActivity.window.statusBarColor = ContextCompat.getColor(requireActivity, color)
    }
}