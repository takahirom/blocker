/*
 * Copyright 2022 Blocker
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.merxury.blocker.data.app

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface InstalledAppDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg installedApp: InstalledApp)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(installedApp: InstalledApp)

    @Delete
    suspend fun delete(installedApp: InstalledApp): Int

    @Query("DELETE FROM installed_app")
    suspend fun deleteAll()

    @Update
    suspend fun update(installedApp: InstalledApp): Int

    @Query("SELECT * FROM installed_app")
    suspend fun getAll(): List<InstalledApp>

    @Query("SELECT * FROM installed_app WHERE package_name = :packageName")
    suspend fun getByPackageName(packageName: String): InstalledApp?

    @Query("SELECT COUNT(package_name) FROM installed_app")
    suspend fun getCount(): Int
}