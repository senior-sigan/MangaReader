package org.seniorsigan.mangareader.ui

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.DialogFragment
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.find
import org.seniorsigan.mangareader.App
import org.seniorsigan.mangareader.INTENT_MANGA
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.models.MangaItem
import org.seniorsigan.mangareader.ui.fragments.BookmarkListFragment
import org.seniorsigan.mangareader.ui.fragments.FeedListFragment
import org.seniorsigan.mangareader.ui.fragments.FilterDialogFragment
import org.seniorsigan.mangareader.ui.fragments.MangaListFragment

class MainActivity :
        AppCompatActivity(),
        NavigationView.OnNavigationItemSelectedListener,
        MangaListFragment.OnItemClickListener,
        BookmarkListFragment.OnItemClickListener,
        FilterDialogFragment.FilterDialogListener
{
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onItemClick(item: MangaItem) {
        startActivity(with(Intent(this, MangaActivity::class.java), {
            putExtra(INTENT_MANGA, item)
            this
        }))
    }

    override fun onSelected(dialogFragment: DialogFragment) {
        onNavigationItemSelected(navigationView.menu.getItem(0))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawer = find<DrawerLayout>(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        navigationView = find<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.menu.getItem(0))
        }
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        if (id == R.id.menu_search) {
            startActivity(Intent(this, SearchActivity::class.java))
            return true
        }

        if (id == R.id.menu_filter) {
            val dialog = FilterDialogFragment()
            dialog.show(supportFragmentManager, "FilterDialog")
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        val fragmentClass = when (id) {
            R.id.nav_popular -> MangaListFragment::class
            R.id.nav_bookmarks -> BookmarkListFragment::class
            R.id.nav_settings -> MangaListFragment::class
            R.id.nav_feed -> FeedListFragment::class
            else -> MangaListFragment::class
        }

        val fragment = fragmentClass.java.newInstance()
        fragment.arguments = intent.extras
        supportFragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit()
        item.isChecked = true
        title = item.title

        val drawer = find<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}
