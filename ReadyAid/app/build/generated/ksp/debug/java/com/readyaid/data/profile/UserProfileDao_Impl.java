package com.readyaid.data.profile;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class UserProfileDao_Impl implements UserProfileDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<UserProfile> __insertionAdapterOfUserProfile;

  private final SharedSQLiteStatement __preparedStmtOfClearProfile;

  public UserProfileDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfUserProfile = new EntityInsertionAdapter<UserProfile>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `user_profile` (`id`,`fullName`,`age`,`bloodType`,`conditions`,`allergies`,`medications`,`medicalHistory`,`emergencyContact1Name`,`emergencyContact1Phone`,`emergencyContact2Name`,`emergencyContact2Phone`,`profileCompleted`,`disclaimerAccepted`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final UserProfile entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getFullName());
        statement.bindLong(3, entity.getAge());
        statement.bindString(4, entity.getBloodType());
        statement.bindString(5, entity.getConditions());
        statement.bindString(6, entity.getAllergies());
        statement.bindString(7, entity.getMedications());
        statement.bindString(8, entity.getMedicalHistory());
        statement.bindString(9, entity.getEmergencyContact1Name());
        statement.bindString(10, entity.getEmergencyContact1Phone());
        if (entity.getEmergencyContact2Name() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getEmergencyContact2Name());
        }
        if (entity.getEmergencyContact2Phone() == null) {
          statement.bindNull(12);
        } else {
          statement.bindString(12, entity.getEmergencyContact2Phone());
        }
        final int _tmp = entity.getProfileCompleted() ? 1 : 0;
        statement.bindLong(13, _tmp);
        final int _tmp_1 = entity.getDisclaimerAccepted() ? 1 : 0;
        statement.bindLong(14, _tmp_1);
      }
    };
    this.__preparedStmtOfClearProfile = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM user_profile";
        return _query;
      }
    };
  }

  @Override
  public Object insertProfile(final UserProfile profile,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfUserProfile.insert(profile);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object clearProfile(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearProfile.acquire();
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
          __preparedStmtOfClearProfile.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<UserProfile> getUserProfile() {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"user_profile"}, new Callable<UserProfile>() {
      @Override
      @Nullable
      public UserProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfBloodType = CursorUtil.getColumnIndexOrThrow(_cursor, "bloodType");
          final int _cursorIndexOfConditions = CursorUtil.getColumnIndexOrThrow(_cursor, "conditions");
          final int _cursorIndexOfAllergies = CursorUtil.getColumnIndexOrThrow(_cursor, "allergies");
          final int _cursorIndexOfMedications = CursorUtil.getColumnIndexOrThrow(_cursor, "medications");
          final int _cursorIndexOfMedicalHistory = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalHistory");
          final int _cursorIndexOfEmergencyContact1Name = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact1Name");
          final int _cursorIndexOfEmergencyContact1Phone = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact1Phone");
          final int _cursorIndexOfEmergencyContact2Name = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact2Name");
          final int _cursorIndexOfEmergencyContact2Phone = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact2Phone");
          final int _cursorIndexOfProfileCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "profileCompleted");
          final int _cursorIndexOfDisclaimerAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "disclaimerAccepted");
          final UserProfile _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpFullName;
            _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            final int _tmpAge;
            _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            final String _tmpBloodType;
            _tmpBloodType = _cursor.getString(_cursorIndexOfBloodType);
            final String _tmpConditions;
            _tmpConditions = _cursor.getString(_cursorIndexOfConditions);
            final String _tmpAllergies;
            _tmpAllergies = _cursor.getString(_cursorIndexOfAllergies);
            final String _tmpMedications;
            _tmpMedications = _cursor.getString(_cursorIndexOfMedications);
            final String _tmpMedicalHistory;
            _tmpMedicalHistory = _cursor.getString(_cursorIndexOfMedicalHistory);
            final String _tmpEmergencyContact1Name;
            _tmpEmergencyContact1Name = _cursor.getString(_cursorIndexOfEmergencyContact1Name);
            final String _tmpEmergencyContact1Phone;
            _tmpEmergencyContact1Phone = _cursor.getString(_cursorIndexOfEmergencyContact1Phone);
            final String _tmpEmergencyContact2Name;
            if (_cursor.isNull(_cursorIndexOfEmergencyContact2Name)) {
              _tmpEmergencyContact2Name = null;
            } else {
              _tmpEmergencyContact2Name = _cursor.getString(_cursorIndexOfEmergencyContact2Name);
            }
            final String _tmpEmergencyContact2Phone;
            if (_cursor.isNull(_cursorIndexOfEmergencyContact2Phone)) {
              _tmpEmergencyContact2Phone = null;
            } else {
              _tmpEmergencyContact2Phone = _cursor.getString(_cursorIndexOfEmergencyContact2Phone);
            }
            final boolean _tmpProfileCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfProfileCompleted);
            _tmpProfileCompleted = _tmp != 0;
            final boolean _tmpDisclaimerAccepted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDisclaimerAccepted);
            _tmpDisclaimerAccepted = _tmp_1 != 0;
            _result = new UserProfile(_tmpId,_tmpFullName,_tmpAge,_tmpBloodType,_tmpConditions,_tmpAllergies,_tmpMedications,_tmpMedicalHistory,_tmpEmergencyContact1Name,_tmpEmergencyContact1Phone,_tmpEmergencyContact2Name,_tmpEmergencyContact2Phone,_tmpProfileCompleted,_tmpDisclaimerAccepted);
          } else {
            _result = null;
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

  @Override
  public Object getUserProfileSync(final Continuation<? super UserProfile> $completion) {
    final String _sql = "SELECT * FROM user_profile WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<UserProfile>() {
      @Override
      @Nullable
      public UserProfile call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfFullName = CursorUtil.getColumnIndexOrThrow(_cursor, "fullName");
          final int _cursorIndexOfAge = CursorUtil.getColumnIndexOrThrow(_cursor, "age");
          final int _cursorIndexOfBloodType = CursorUtil.getColumnIndexOrThrow(_cursor, "bloodType");
          final int _cursorIndexOfConditions = CursorUtil.getColumnIndexOrThrow(_cursor, "conditions");
          final int _cursorIndexOfAllergies = CursorUtil.getColumnIndexOrThrow(_cursor, "allergies");
          final int _cursorIndexOfMedications = CursorUtil.getColumnIndexOrThrow(_cursor, "medications");
          final int _cursorIndexOfMedicalHistory = CursorUtil.getColumnIndexOrThrow(_cursor, "medicalHistory");
          final int _cursorIndexOfEmergencyContact1Name = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact1Name");
          final int _cursorIndexOfEmergencyContact1Phone = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact1Phone");
          final int _cursorIndexOfEmergencyContact2Name = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact2Name");
          final int _cursorIndexOfEmergencyContact2Phone = CursorUtil.getColumnIndexOrThrow(_cursor, "emergencyContact2Phone");
          final int _cursorIndexOfProfileCompleted = CursorUtil.getColumnIndexOrThrow(_cursor, "profileCompleted");
          final int _cursorIndexOfDisclaimerAccepted = CursorUtil.getColumnIndexOrThrow(_cursor, "disclaimerAccepted");
          final UserProfile _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpFullName;
            _tmpFullName = _cursor.getString(_cursorIndexOfFullName);
            final int _tmpAge;
            _tmpAge = _cursor.getInt(_cursorIndexOfAge);
            final String _tmpBloodType;
            _tmpBloodType = _cursor.getString(_cursorIndexOfBloodType);
            final String _tmpConditions;
            _tmpConditions = _cursor.getString(_cursorIndexOfConditions);
            final String _tmpAllergies;
            _tmpAllergies = _cursor.getString(_cursorIndexOfAllergies);
            final String _tmpMedications;
            _tmpMedications = _cursor.getString(_cursorIndexOfMedications);
            final String _tmpMedicalHistory;
            _tmpMedicalHistory = _cursor.getString(_cursorIndexOfMedicalHistory);
            final String _tmpEmergencyContact1Name;
            _tmpEmergencyContact1Name = _cursor.getString(_cursorIndexOfEmergencyContact1Name);
            final String _tmpEmergencyContact1Phone;
            _tmpEmergencyContact1Phone = _cursor.getString(_cursorIndexOfEmergencyContact1Phone);
            final String _tmpEmergencyContact2Name;
            if (_cursor.isNull(_cursorIndexOfEmergencyContact2Name)) {
              _tmpEmergencyContact2Name = null;
            } else {
              _tmpEmergencyContact2Name = _cursor.getString(_cursorIndexOfEmergencyContact2Name);
            }
            final String _tmpEmergencyContact2Phone;
            if (_cursor.isNull(_cursorIndexOfEmergencyContact2Phone)) {
              _tmpEmergencyContact2Phone = null;
            } else {
              _tmpEmergencyContact2Phone = _cursor.getString(_cursorIndexOfEmergencyContact2Phone);
            }
            final boolean _tmpProfileCompleted;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfProfileCompleted);
            _tmpProfileCompleted = _tmp != 0;
            final boolean _tmpDisclaimerAccepted;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfDisclaimerAccepted);
            _tmpDisclaimerAccepted = _tmp_1 != 0;
            _result = new UserProfile(_tmpId,_tmpFullName,_tmpAge,_tmpBloodType,_tmpConditions,_tmpAllergies,_tmpMedications,_tmpMedicalHistory,_tmpEmergencyContact1Name,_tmpEmergencyContact1Phone,_tmpEmergencyContact2Name,_tmpEmergencyContact2Phone,_tmpProfileCompleted,_tmpDisclaimerAccepted);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
