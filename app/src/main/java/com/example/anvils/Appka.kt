package com.example.anvils

import android.app.Application
import androidx.lifecycle.ViewModel
import javax.inject.Inject
import javax.inject.Provider

class Appka : Application() {

  private lateinit var appComponent: AppComponent

  override fun onCreate() {
    super.onCreate()

    appComponent = DaggerAppComponent.builder()
      .build()
  }
}
