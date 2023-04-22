package com.gremoryyx.kerjasama

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagerAdapter (fm: Fragment, private val fragment: List<Fragment>) : FragmentStateAdapter(fm) {

    override fun getItemCount(): Int {
        return fragment.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragment[position]
    }
}