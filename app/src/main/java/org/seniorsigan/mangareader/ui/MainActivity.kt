package org.seniorsigan.mangareader.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import org.jetbrains.anko.find
import org.seniorsigan.mangareader.R
import org.seniorsigan.mangareader.ui.fragments.ChapterListFragment
import org.seniorsigan.mangareader.ui.fragments.MangaListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = find<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val mangaListFragment = MangaListFragment()
        mangaListFragment.arguments = intent.extras
        mangaListFragment.onItemClickListener = { manga ->
            val chaptersFragment = ChapterListFragment()
            val args = Bundle()
            args.putString(ChapterListFragment.urlArgument, manga.url)
            chaptersFragment.arguments = args
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragments_container, chaptersFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }
        supportFragmentManager.beginTransaction().add(R.id.fragments_container, mangaListFragment).commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
