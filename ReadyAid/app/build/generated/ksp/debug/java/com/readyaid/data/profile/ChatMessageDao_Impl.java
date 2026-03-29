package com.readyaid.data.profile;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ChatMessageDao_Impl implements ChatMessageDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ChatMessageEntity> __insertionAdapterOfChatMessageEntity;

  private final SharedSQLiteStatement __preparedStmtOfClearAllMessages;

  public ChatMessageDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfChatMessageEntity = new EntityInsertionAdapter<ChatMessageEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `chat_messages` (`entryId`,`id`,`text`,`isUser`,`timestamp`,`responseText`,`sources`,`isFirstAid`,`conditionDetected`,`isErrorState`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ChatMessageEntity entity) {
        statement.bindLong(1, entity.getEntryId());
        statement.bindString(2, entity.getId());
        statement.bindString(3, entity.getText());
        final int _tmp = entity.isUser() ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.getTimestamp());
        if (entity.getResponseText() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getResponseText());
        }
        if (entity.getSources() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getSources());
        }
        final int _tmp_1 = entity.isFirstAid() ? 1 : 0;
        statement.bindLong(8, _tmp_1);
        if (entity.getConditionDetected() == null) {
          statement.bindNull(9);
        } else {
          statement.bindString(9, entity.getConditionDetected());
        }
        final int _tmp_2 = entity.isErrorState() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
      }
    };
    this.__preparedStmtOfClearAllMessages = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM chat_messages";
        return _query;
      }
    };
  }

  @Override
  public Object insertMessage(final ChatMessageEntity message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfChatMessageEntity.insert(message);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearAllMessages(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearAllMessages.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearAllMessages.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ChatMessageEntity>> getAllMessages() {
    final String _sql = "SELECT * FROM chat_messages ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"chat_messages"}, new Callable<List<ChatMessageEntity>>() {
      @Override
      @NonNull
      public List<ChatMessageEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfEntryId = CursorUtil.getColumnIndexOrThrow(_cursor, "entryId");
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfText = CursorUtil.getColumnIndexOrThrow(_cursor, "text");
          final int _cursorIndexOfIsUser = CursorUtil.getColumnIndexOrThrow(_cursor, "isUser");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfResponseText = CursorUtil.getColumnIndexOrThrow(_cursor, "responseText");
          final int _cursorIndexOfSources = CursorUtil.getColumnIndexOrThrow(_cursor, "sources");
          final int _cursorIndexOfIsFirstAid = CursorUtil.getColumnIndexOrThrow(_cursor, "isFirstAid");
          final int _cursorIndexOfConditionDetected = CursorUtil.getColumnIndexOrThrow(_cursor, "conditionDetected");
          final int _cursorIndexOfIsErrorState = CursorUtil.getColumnIndexOrThrow(_cursor, "isErrorState");
          final List<ChatMessageEntity> _result = new ArrayList<ChatMessageEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ChatMessageEntity _item;
            final int _tmpEntryId;
            _tmpEntryId = _cursor.getInt(_cursorIndexOfEntryId);
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpText;
            _tmpText = _cursor.getString(_cursorIndexOfText);
            final boolean _tmpIsUser;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsUser);
            _tmpIsUser = _tmp != 0;
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpResponseText;
            if (_cursor.isNull(_cursorIndexOfResponseText)) {
              _tmpResponseText = null;
            } else {
              _tmpResponseText = _cursor.getString(_cursorIndexOfResponseText);
            }
            final String _tmpSources;
            if (_cursor.isNull(_cursorIndexOfSources)) {
              _tmpSources = null;
            } else {
              _tmpSources = _cursor.getString(_cursorIndexOfSources);
            }
            final boolean _tmpIsFirstAid;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsFirstAid);
            _tmpIsFirstAid = _tmp_1 != 0;
            final String _tmpConditionDetected;
            if (_cursor.isNull(_cursorIndexOfConditionDetected)) {
              _tmpConditionDetected = null;
            } else {
              _tmpConditionDetected = _cursor.getString(_cursorIndexOfConditionDetected);
            }
            final boolean _tmpIsErrorState;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsErrorState);
            _tmpIsErrorState = _tmp_2 != 0;
            _item = new ChatMessageEntity(_tmpEntryId,_tmpId,_tmpText,_tmpIsUser,_tmpTimestamp,_tmpResponseText,_tmpSources,_tmpIsFirstAid,_tmpConditionDetected,_tmpIsErrorState);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
