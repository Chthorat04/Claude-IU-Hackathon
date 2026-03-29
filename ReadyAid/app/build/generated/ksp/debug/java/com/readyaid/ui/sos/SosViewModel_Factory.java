package com.readyaid.ui.sos;

import com.readyaid.data.profile.UserProfileDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class SosViewModel_Factory implements Factory<SosViewModel> {
  private final Provider<UserProfileDao> userDaoProvider;

  public SosViewModel_Factory(Provider<UserProfileDao> userDaoProvider) {
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public SosViewModel get() {
    return newInstance(userDaoProvider.get());
  }

  public static SosViewModel_Factory create(Provider<UserProfileDao> userDaoProvider) {
    return new SosViewModel_Factory(userDaoProvider);
  }

  public static SosViewModel newInstance(UserProfileDao userDao) {
    return new SosViewModel(userDao);
  }
}
