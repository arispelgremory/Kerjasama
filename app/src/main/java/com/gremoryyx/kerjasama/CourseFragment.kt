package com.gremoryyx.kerjasama

import TabPagerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class CourseFragment : Fragment() {

    private lateinit var tabPagerAdapter: TabPagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_course, container, false)

        val fragment = listOf(
            CourseListFragment(),
            CourseRegisteredFragment()
        )

        val tabLayout: TabLayout = view.findViewById(R.id.courseTabLayout)
        val viewPager: ViewPager2 = view.findViewById(R.id.courseViewPager)
        val tabTitles = listOf("Course List", "Registered")

        // Initialize the TabPagerAdapter (Step 2)
        tabPagerAdapter = TabPagerAdapter(this, fragment)
        viewPager.adapter = tabPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        val courseSearchView: SearchView = view.findViewById(R.id.courseSearchView)

        courseSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val courseListFragment = tabPagerAdapter.fragments[0] as CourseListFragment
                    if (newText.isEmpty()) {
                        courseListFragment.resetCourseList()
                    } else {
                        courseListFragment.onSearchInput(newText)
                    }
                }
                return true
            }
        })

        return view
    }
}