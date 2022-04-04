package com.example.anvils

import androidx.lifecycle.ViewModel
// import com.squareup.anvil.annotations.MergeComponent
import dagger.Component
import javax.inject.Provider
import javax.inject.Singleton

abstract class AppScope private constructor()

typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>

@Singleton
@Component(modules = [ViewModelsModule::class])
//@MergeComponent(AppScope::class)
interface AppComponent {

  fun vms():  ViewModelMap
}

