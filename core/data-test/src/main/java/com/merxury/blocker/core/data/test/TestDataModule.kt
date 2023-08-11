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

package com.merxury.blocker.core.data.test

import com.merxury.blocker.core.data.di.DataModule
import com.merxury.blocker.core.data.respository.componentdetail.ComponentDetailRepository
import com.merxury.blocker.core.data.respository.componentdetail.OfflineFirstComponentDetailRepository
import com.merxury.blocker.core.data.respository.fake.FakeComponentDetailRepository
import com.merxury.blocker.core.data.respository.fake.FakeGeneralRuleRepository
import com.merxury.blocker.core.data.respository.fake.FakeUserDataRepository
import com.merxury.blocker.core.data.respository.generalrule.GeneralRuleRepository
import com.merxury.blocker.core.data.respository.userdata.LocalUserDataRepository
import com.merxury.blocker.core.data.respository.userdata.UserDataRepository
import com.merxury.blocker.core.data.util.NetworkMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class],
)
interface TestDataModule {
    @Binds
    fun bindUserDataRepository(
        userDataRepository: FakeUserDataRepository,
    ): UserDataRepository
    @Binds
    fun bindsTestGeneralRuleRepository(
        testGeneralRuleRepository: FakeGeneralRuleRepository,
    ): GeneralRuleRepository

    @Binds
    fun bindsComponentDetailRepository(
        componentDetailRepository: FakeComponentDetailRepository,
    ): ComponentDetailRepository

    @Binds
    fun bindsNetworkMonitor(
        networkMonitor: AlwaysOnlineNetworkMonitor,
    ): NetworkMonitor
}
