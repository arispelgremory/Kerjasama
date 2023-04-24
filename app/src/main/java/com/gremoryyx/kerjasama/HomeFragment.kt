import TabPagerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.gremoryyx.kerjasama.JobListFragment
import com.gremoryyx.kerjasama.R
import com.gremoryyx.kerjasama.RegisteredJobFragment

class HomeFragment : Fragment() {

    private lateinit var tabPagerAdapter: TabPagerAdapter
//    private lateinit var jobAdapter: JobAdapter
//    private lateinit var jobListArrayList: ArrayList<Job>
//    private lateinit var jobListBottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val fragment = listOf(
            JobListFragment(),
            RegisteredJobFragment()
        )

        val tabLayout: TabLayout = view.findViewById(R.id.tab_layout)
        val viewPager: ViewPager2 = view.findViewById(R.id.view_pager)
        val tabTitles = listOf("Job List", "Registered") // add the tab titles here

//        jobListArrayList = ArrayList()
//        jobAdapter = JobAdapter(jobListArrayList)

        // Initialize the TabPagerAdapter (Step 2)
        tabPagerAdapter = TabPagerAdapter(this, fragment)
        viewPager.adapter = tabPagerAdapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()

        // Add search bar functionality
        val jobSearchView: SearchView = view.findViewById(R.id.jobSearchView)

        jobSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    val jobListFragment = tabPagerAdapter.fragments[0] as JobListFragment
                    if (newText.isEmpty()) {
                        jobListFragment.resetJobList() // Add this function in JobListFragment
                    } else {
                        jobListFragment.onSearchInput(newText)
                    }
                }
                return true
            }
        })
        return view
    }
}
