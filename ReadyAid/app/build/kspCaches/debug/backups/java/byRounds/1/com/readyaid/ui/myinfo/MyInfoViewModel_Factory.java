package com.readyaid.ui.myinfo;

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
public final class MyInfoViewModel_Factory implements Factory<MyInfoViewModel> {
  private final Provider<UserProfileDao> userDaoProvider;

  public MyInfoViewModel_Factory(Provider<UserProfileDao> userDaoProvider) {
    this.userDaoProvider = userDaoProvider;
  }

  @Override
  public MyInfoViewModel get() {
    return newInstance(userDaoProvider.get());
  }

  public static MyInfoViewModel_Factory create(Provider<UserProfileDao> userDaoProvider) {
    return new MyInfoViewModel_Factory(userDaoProvider);
  }

  public static MyInfoViewModel newInstance(UserProfileDao userDao) {
    return new MyInfoViewModel(userDao);
  }
}
