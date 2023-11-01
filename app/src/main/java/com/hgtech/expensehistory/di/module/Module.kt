package com.hgtech.expensehistory.di.module

import android.app.Application
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hgtech.expensehistory.database.config.AppDatabase
import com.hgtech.expensehistory.database.converter.Converters
import com.hgtech.expensehistory.database.dao.CategoryDao
import com.hgtech.expensehistory.database.dao.CurrencyDao
import com.hgtech.expensehistory.database.dao.ItemDao
import com.hgtech.expensehistory.database.dao.UserDao
import com.hgtech.expensehistory.database.repository.BackupRestoreRepository
import com.hgtech.expensehistory.database.repository.CategoryRepository
import com.hgtech.expensehistory.database.repository.FilterItemsRepository
import com.hgtech.expensehistory.database.repository.SortingListRepository
import com.hgtech.expensehistory.database.repository.UserRepository
import com.hgtech.expensehistory.ui.viewModel.AddUpdateItemViewModel
import com.hgtech.expensehistory.ui.viewModel.AddUpdateRootViewModel
import com.hgtech.expensehistory.ui.viewModel.ItemViewModel
import com.hgtech.expensehistory.ui.viewModel.SortListViewModel
import com.hgtech.expensehistory.ui.viewModel.UserCategoryViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


object Module {

    var DB_VERSION: Int = 0
    const val DB_NAME = "expense_history"


    val databaseModule = module {

        fun provideDatabase(application: Application): AppDatabase {

            val rdc: RoomDatabase.Callback = object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    // ADD YOUR "Math - Sport - Art - Music" here
                    db.execSQL("Insert into UserTable values (1,0,\"Root\",1)")
                    db.execSQL("Insert into CurrencyTable values (1,\"United State Dollars\",\"$\")")
                    db.execSQL("Insert into CategoryTable values (1,0,1,\"Supermarket\",0,1)")
                    db.execSQL("Insert into CategoryTable values (2,0,1,\"Food Extra\",1,1)")
                    db.execSQL("Insert into CategoryTable values (3,0,1,\"Vegetable & Fruits\",0,1)")
                    db.execSQL("Insert into CategoryTable values (4,0,1,\"Transport\",0,1)")
                    db.execSQL("Insert into CategoryTable values (5,0,1,\"Medical\",1,1)")
                }
            }

            return Room.databaseBuilder(
                application,
                AppDatabase::class.java,
                DB_NAME
            )
                .addTypeConverter(Converters())
                .addCallback(rdc)
                .build().also {
                    DB_VERSION = it.openHelper.readableDatabase.version
                }

        }


        fun provideItemDao(database: AppDatabase): ItemDao {
            return database.itemDao
        }

        fun provideCurrencyDao(database: AppDatabase): CurrencyDao {
            return database.currencyDao
        }

        fun provideCategoryDao(database: AppDatabase): CategoryDao {
            return database.categoryDao
        }

        fun provideUserDao(database: AppDatabase): UserDao {
            return database.userDao
        }
        single { provideDatabase(androidApplication()) }
        single { provideItemDao(get()) }
        single { provideCategoryDao(get()) }
        single { provideCurrencyDao(get()) }
        single { provideUserDao(get()) }
    }

    val viewModule = module {
        viewModel { AddUpdateItemViewModel(get(), get()) }
        viewModel { ItemViewModel(get(), get()) }
        viewModel { SortListViewModel(get()) }
        viewModel { UserCategoryViewModel(get(), get()) }
        viewModel { AddUpdateRootViewModel(get(), get()) }
    }

    val repositories = module {
        single { SortingListRepository(get(), get()) }
        single { FilterItemsRepository(get(), get()) }
        single { CategoryRepository(get()) }
        single { UserRepository(get()) }
        single { BackupRestoreRepository(get(), get(), get(), get()) }
    }
}