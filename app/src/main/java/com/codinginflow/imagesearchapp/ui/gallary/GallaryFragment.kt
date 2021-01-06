package com.codinginflow.imagesearchapp.ui.gallary

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.SearchView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.codinginflow.imagesearchapp.R
import com.codinginflow.imagesearchapp.data.UnsplashPhoto
import com.codinginflow.imagesearchapp.databinding.FragmentGallaryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_gallary.*

@AndroidEntryPoint
class GallaryFragment : Fragment(R.layout.fragment_gallary) , UnsplashPhotoAdapter.OnItemClickListners {

    private val viewModel by viewModels<GallaryViewModel>()
    private var _binding: FragmentGallaryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?)  {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentGallaryBinding.bind(view)

        val adapter = UnsplashPhotoAdapter(this)

        binding.apply {

            recycler_view.setHasFixedSize(true)
            recycler_view.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter {
                    adapter.retry()
                },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
            button_retry.setOnClickListener {
                adapter.retry()
            }
        }
        viewModel.photos.observe(viewLifecycleOwner) {

            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        adapter.addLoadStateListener { loadStat ->
            binding.apply {

                progress_bar.isVisible = loadStat.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadStat.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadStat.source.refresh is LoadState.Error
                textViewError.isVisible = loadStat.source.refresh is LoadState.Error

                if (loadStat.source.refresh is LoadState.NotLoading && loadStat.append.endOfPaginationReached && adapter.itemCount < 1){

                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true

                }else{
                    textViewEmpty.isVisible = false

                }
            }
        }
        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_gallary, menu)

        val serchItem = menu.findItem(R.id.Action_search)
        val serchView = serchItem.actionView as SearchView

        serchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.smoothScrollToPosition(0)
                    viewModel.searchPhotos(query)
                    serchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }


        })


    }

    override fun onItemClick(photo: UnsplashPhoto) {
        val action = GallaryFragmentDirections.actionGallaryFragment2ToDetailsFragment(photo)
        findNavController().navigate(action)
    }


}



