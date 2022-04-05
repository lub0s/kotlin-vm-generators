package com.example.anvils

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.annotations.ContributesViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Provider

typealias ViewModelMap = Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModelFactory<*>>>

interface ViewModelFactory<T: ViewModel> {
  fun create(handle: SavedStateHandle): T
}

@ContributesViewModel(ViewModelScope::class)
class MainViewModel @AssistedInject constructor(
  val provider: StringsProvider,
  @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel()

@ContributesViewModel(ViewModelScope::class)
class MainViewModelB @AssistedInject constructor(
  provider: StringsProvider,
  @Assisted private val savedStateHandle: SavedStateHandle
): ViewModel() {
  init {
    tag(provider.a)
  }
}
