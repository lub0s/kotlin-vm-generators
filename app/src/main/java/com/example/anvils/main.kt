package com.example.anvils

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.SavedStateHandle

internal const val tag = "ta-ga-gagagaa"
internal fun tag(what: String) = Log.e(tag, what)

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    val vm =
      Appka.appComponent.getSubcomponentFactory()
        .create(SavedStateHandle())
        .getViewModelMap()[MainViewModel::class.java]!!.get() as ViewModelFactory<MainViewModel>

    findViewById<TextView>(R.id.text).text = vm.create(SavedStateHandle()).provider.a
  }
}

// TODO
// add sample showing AbstractSavedStateViewModelFactory implementation
//
// private fun createFactory(
//   owner: SavedStateRegistryOwner,
//   defaultArgs: Bundle? = null,
// ) = object : AbstractSavedStateViewModelFactory(owner, defaultArgs) { }
