package com.example.anvils

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.annotations.ContributesViewModel
import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Provider

typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>

@ContributesViewModel(ViewModelScope::class)
class MainViewModel @Inject constructor(
  val provider: StringsProvider
): ViewModel()

@ContributesViewModel(ViewModelScope::class)
class MainViewModelB @Inject constructor(
  provider: StringsProvider
): ViewModel() {
  init {
      Log.e("ttt", provider.a)
  }
}
