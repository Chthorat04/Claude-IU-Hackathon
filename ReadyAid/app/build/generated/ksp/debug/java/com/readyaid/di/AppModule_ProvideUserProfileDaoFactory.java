package com.readyaid.di;

import com.readyaid.data.profile.AppDatabase;
import com.readyaid.data.profile.UserProfileDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class AppModule_ProvideUserProfileDaoFactory implements Factory<UserProfileDao> {
  private final Provider<AppDatabase> appDatabaseProvider;

  public AppModule_ProvideUserProfileDaoFactory(Provider<AppDatabase> appDatabaseProvider) {
    this.appDatabaseProvider = appDatabaseProvider;
  }

  @Override
  public UserProfileDao get() {
    return provideUserProfileDao(appDatabaseProvider.get());
  }

  public static AppModule_ProvideUserProfileDaoFactory create(
      Provider<AppDatabase> appDatabaseProvider) {
    return new AppModule_ProvideUserProfileDaoFactory(appDatabaseProvider);
  }

  public static UserProfileDao provideUserProfileDao(AppDatabase appDatabase) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideUserProfileDao(appDatabase));
  }
}
