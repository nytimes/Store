package com.nytimes.android.sample

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Database
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Insert
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.Query
import android.arch.persistence.room.Room
import android.arch.persistence.room.Room.databaseBuilder
import android.arch.persistence.room.RoomDatabase
import android.os.Parcel
import android.os.Parcelable
import com.nytimes.android.external.store3.base.Fetcher
import com.nytimes.android.external.store3.base.RoomPersister
import com.nytimes.android.external.store3.base.impl.RoomInternalStore
import com.nytimes.android.external.store3.base.impl.StalePolicy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single


// File: User.java
@Entity
data class User(
        @PrimaryKey(autoGenerate = true) var uid: Int = 0,
        val name: String,
        val lastName: String)


// File: UserDao.java
@Dao
interface UserDao {
    @Query("SELECT name FROM user")
    fun loadAll(): Flowable<List<String>>


    @Insert
    fun insertAll(vararg users: User)

    @Delete
    fun delete(user: User)
}

// File: AppDatabase.java
@Database(entities = arrayOf(User::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}

val db = Room.databaseBuilder(SampleApp.appContext, AppDatabase::class.java, "db").build()


val persister = object : RoomPersister<User, List<String>, String> {
    override fun read(key: String): Observable<List<String>> {
        return db.userDao().loadAll().toObservable().map { if (it.isEmpty()) throw Exception() else it }
    }

    override fun write(key: String, user: User) {
        db.userDao().insertAll(user)
    }

}
//store

class SampleRoomStore(fetcher: Fetcher<User, String>,
                      persister: RoomPersister<User, List<String>, String>,
                      stalePolicy: StalePolicy = StalePolicy.UNSPECIFIED) :
        RoomInternalStore<User, List<String>, String>(fetcher, persister, stalePolicy)

val fetcher=Fetcher<User,String> { Single.just(User(name = "Mike", lastName = "naki")) }

val store = SampleRoomStore( fetcher, persister)


