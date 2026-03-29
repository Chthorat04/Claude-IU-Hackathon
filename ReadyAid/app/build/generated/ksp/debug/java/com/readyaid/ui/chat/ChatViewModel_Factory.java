package com.readyaid.ui.chat;

import com.readyaid.data.profile.ChatMessageDao;
import com.readyaid.data.profile.UserProfileDao;
import com.readyaid.data.rag.RagClient;
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<RagClient> ragClientProvider;

  private final Provider<UserProfileDao> userDaoProvider;

  private final Provider<ChatMessageDao> chatDaoProvider;

  public ChatViewModel_Factory(Provider<RagClient> ragClientProvider,
      Provider<UserProfileDao> userDaoProvider, Provider<ChatMessageDao> chatDaoProvider) {
    this.ragClientProvider = ragClientProvider;
    this.userDaoProvider = userDaoProvider;
    this.chatDaoProvider = chatDaoProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(ragClientProvider.get(), userDaoProvider.get(), chatDaoProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<RagClient> ragClientProvider,
      Provider<UserProfileDao> userDaoProvider, Provider<ChatMessageDao> chatDaoProvider) {
    return new ChatViewModel_Factory(ragClientProvider, userDaoProvider, chatDaoProvider);
  }

  public static ChatViewModel newInstance(RagClient ragClient, UserProfileDao userDao,
      ChatMessageDao chatDao) {
    return new ChatViewModel(ragClient, userDao, chatDao);
  }
}
