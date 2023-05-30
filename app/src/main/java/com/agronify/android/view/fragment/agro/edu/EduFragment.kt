package com.agronify.android.view.fragment.agro.edu

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.agronify.android.databinding.FragmentEduBinding
import com.agronify.android.model.remote.response.Edu
import com.agronify.android.util.Constants.EXTRA_EDU_CONTENT
import com.agronify.android.util.Constants.EXTRA_EDU_ID
import com.agronify.android.util.Constants.EXTRA_EDU_IMAGE
import com.agronify.android.util.Constants.EXTRA_EDU_TAGS
import com.agronify.android.util.Constants.EXTRA_EDU_TITLE
import com.agronify.android.view.activity.agro.edu.EduDetailActivity
import com.agronify.android.view.activity.main.MainActivity
import com.agronify.android.view.adapter.EduAdapter
import com.agronify.android.viewmodel.EduViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.Serializable

@AndroidEntryPoint
class EduFragment : Fragment() {
    private val binding by lazy {
        FragmentEduBinding.inflate(layoutInflater)
    }
    private val eduViewModel: EduViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
        setupAction()
    }

    private fun setupView() {
        getEdu()
    }

    private fun setupAction() {
        onSearch()
        onSwipeRefresh()
    }

    private fun getEdu() {
        if (isAdded) {
            eduViewModel.apply {
                isLoading.observe(viewLifecycleOwner) {
                    if (!it) {
                        val edu = edu.value
                        binding.cpiEdu.visibility = View.GONE
                        if (!edu.isNullOrEmpty()) {
                            binding.ivEmpty.visibility = View.INVISIBLE
                            showEdu(edu)
                        } else {
                            binding.ivEmpty.visibility = View.VISIBLE
                        }
                    } else {
                        binding.cpiEdu.visibility = View.VISIBLE
                    }
                }

                getEdu()
            }
        }
    }

    private fun showEdu(eduList: List<Edu>) {
        val eduAdapter = EduAdapter()
        eduAdapter.submitList(eduList)

        binding.rvAgroEdu.apply {
            adapter = eduAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        eduAdapter.setOnClickCallback(object: EduAdapter.OnClickCallback {
            override fun onClicked(edu: Edu) {
                val tags: ArrayList<String> = arrayListOf()
                for (tag in edu.tags) {
                    tags.add(tag)
                }
                Intent(requireContext(), EduDetailActivity::class.java).also {
                    it.putExtra(EXTRA_EDU_IMAGE, edu.image)
                    it.putExtra(EXTRA_EDU_TITLE, edu.title)
                    it.putExtra(EXTRA_EDU_CONTENT, edu.content)
                    it.putExtra(EXTRA_EDU_TAGS, tags)
                    startActivity(it)
                }
            }
        })
    }

    private fun onSearch() {
        binding.apply {
            edSearch.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val searchedEdu = eduViewModel.searchEdu(s.toString())
                    showEdu(searchedEdu)
                }
            })
        }
    }

    private fun onSwipeRefresh() {
        binding.apply {
            swipeEdu.apply {
                setOnRefreshListener {
                    getEdu()
                    isRefreshing = false
                }
            }
        }
    }
}