package com.example.pinterest.adapter

import androidx.fragment.app.*
import androidx.viewpager2.adapter.FragmentStateAdapter

class ChatVPAdapter(requireActivity: FragmentActivity) : FragmentStateAdapter(requireActivity) {

    private val fragments: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int = fragments.size


    override fun createFragment(position: Int): Fragment = fragments[position]

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
    }
}