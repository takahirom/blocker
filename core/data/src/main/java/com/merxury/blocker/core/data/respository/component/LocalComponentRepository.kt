/*
 * Copyright 2023 Blocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.merxury.blocker.core.data.respository.component

import com.merxury.blocker.core.controllers.ifw.IfwController
import com.merxury.blocker.core.controllers.root.RootController
import com.merxury.blocker.core.controllers.shizuku.ShizukuController
import com.merxury.blocker.core.data.respository.userdata.UserDataRepository
import com.merxury.blocker.core.model.ComponentType
import com.merxury.blocker.core.model.data.ComponentInfo
import com.merxury.blocker.core.model.data.ControllerType.IFW
import com.merxury.blocker.core.model.data.ControllerType.PM
import com.merxury.blocker.core.model.data.ControllerType.SHIZUKU
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import javax.inject.Inject

class LocalComponentRepository @Inject constructor(
    private val localDataSource: LocalComponentDataSource,
    private val userDataRepository: UserDataRepository,
    private val pmController: RootController,
    private val ifwController: IfwController,
    private val shizukuController: ShizukuController,
) : ComponentRepository {

    private val latestListMutex = Mutex()
    private var cachedList: MutableList<ComponentInfo> = mutableListOf()
    private val _data = MutableSharedFlow<List<ComponentInfo>>(
        replay = 1,
        extraBufferCapacity = 1,
    )
    val data = _data.asSharedFlow()

    override fun getComponentList(packageName: String, type: ComponentType) = flow {
        val newList = localDataSource.getComponentList(packageName, type)
            .first()
            .toMutableList()
        latestListMutex.withLock {
            cachedList = newList
        }
        _data.emit(cachedList)
        emit(Unit)
    }

    override fun controlComponent(
        packageName: String,
        componentName: String,
        newState: Boolean,
    ): Flow<Boolean> = flow {
        Timber.d("Control $packageName/$componentName to state $newState")
        val userData = userDataRepository.userData.first()
        val result = when (userData.controllerType) {
            IFW -> controlInIfwMode(packageName, componentName, newState)
            PM -> controlInPmMode(packageName, componentName, newState)
            SHIZUKU -> controlInShizukuMode(packageName, componentName, newState)
        }
        updateComponentStatus(packageName, componentName)
        emit(result)
    }

    private suspend fun controlInIfwMode(
        packageName: String,
        componentName: String,
        newState: Boolean,
    ): Boolean {
        return if (newState) {
            // Need to enable the component by PM controller first
            val blockedByPm = !pmController.checkComponentEnableState(packageName, componentName)
            if (blockedByPm) {
                pmController.enable(packageName, componentName)
            }
            ifwController.enable(packageName, componentName)
        } else {
            ifwController.disable(packageName, componentName)
        }
    }

    private suspend fun controlInPmMode(
        packageName: String,
        componentName: String,
        newState: Boolean,
    ): Boolean {
        return if (newState) {
            // Need to enable the component by PM controller first
            val blockedByIfw = !ifwController.checkComponentEnableState(packageName, componentName)
            if (blockedByIfw) {
                ifwController.enable(packageName, componentName)
            }
            pmController.enable(packageName, componentName)
        } else {
            pmController.disable(packageName, componentName)
        }
    }

    private suspend fun controlInShizukuMode(
        packageName: String,
        componentName: String,
        newState: Boolean,
    ): Boolean {
        // In Shizuku mode, use root privileges as little as possible
        return if (newState) {
            shizukuController.enable(packageName, componentName)
        } else {
            shizukuController.disable(packageName, componentName)
        }
    }

    private suspend fun updateComponentStatus(packageName: String, componentName: String) {
        latestListMutex.withLock {
            val position = cachedList.indexOfFirst { it.name == componentName }
            if (position == -1) {
                Timber.w("Can't find $componentName in the cached list.")
                return
            }
            val newComponentInfo = cachedList[position].copy(
                pmBlocked = !pmController.checkComponentEnableState(packageName, componentName),
                ifwBlocked = !ifwController.checkComponentEnableState(packageName, componentName),
            )
            cachedList[position] = newComponentInfo
        }
        _data.emit(cachedList)
    }
}