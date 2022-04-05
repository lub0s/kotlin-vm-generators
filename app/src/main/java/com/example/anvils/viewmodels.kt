package com.example.anvils

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.annotations.ContributesViewModel
import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.assisted.AssistedInject
import javax.inject.Inject
import javax.inject.Provider

typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModelFactory<*>>>

@ContributesViewModel(ViewModelScope::class)
class MainViewModel @AssistedInject constructor(
  val provider: StringsProvider
): ViewModel()

@ContributesViewModel(ViewModelScope::class)
class MainViewModelB @AssistedInject constructor(
  provider: StringsProvider
): ViewModel() {
  init {
      Log.e("ttt", provider.a)
  }
}

interface ViewModelFactory<T: ViewModel> {
  fun create(): T
}
