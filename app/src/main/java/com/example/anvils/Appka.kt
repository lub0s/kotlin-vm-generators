package com.example.anvils

import android.app.Application

class Appka : Application() {

  private lateinit var appComponent: AppComponent

  override fun onCreate() {
    super.onCreate()

    appComponent = DaggerAppComponent.builder()
      .build()
  }
}
