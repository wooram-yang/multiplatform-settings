/*
 * Copyright 2019 Russell Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.stateIn

@ExperimentalSettingsApi
private inline fun <T> ObservableSettings.createStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    defaultValue: T,
    crossinline getter: Settings.(String, T) -> T,
    crossinline addListener: ObservableSettings.(String, T, (T) -> Unit) -> SettingsListener
): StateFlow<T> = callbackFlow {
    val listener = addListener(key, defaultValue) {
        trySend(it)
    }
    awaitClose {
        listener.deactivate()
    }
}.stateIn(coroutineScope, SharingStarted.Eagerly, getter(key, defaultValue))

@ExperimentalSettingsApi
private inline fun <T> ObservableSettings.createNullableStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    crossinline getter: Settings.(String) -> T?,
    crossinline addListener: ObservableSettings.(String, (T?) -> Unit) -> SettingsListener
): StateFlow<T?> =
    createStateFlow<T?>(
        coroutineScope,
        key,
        null,
        { it, _ -> getter(it) },
        { it, _, callback -> addListener(it, callback) })

/**
 * Create a new flow, based on observing the given [key] as an `Int`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getIntStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    defaultValue: Int
): StateFlow<Int> =
    createStateFlow(coroutineScope, key, defaultValue, Settings::getInt, ObservableSettings::addIntListener)

/**
 * Create a new flow, based on observing the given [key] as a `Long`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getLongStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    defaultValue: Long
): StateFlow<Long> =
    createStateFlow(coroutineScope, key, defaultValue, Settings::getLong, ObservableSettings::addLongListener)

/**
 * Create a new flow, based on observing the given [key] as a `String`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getStringStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    defaultValue: String
): StateFlow<String> =
    createStateFlow(coroutineScope, key, defaultValue, Settings::getString, ObservableSettings::addStringListener)

/**
 * Create a new flow, based on observing the given [key] as a `Float`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getFloatStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    defaultValue: Float
): StateFlow<Float> =
    createStateFlow(coroutineScope, key, defaultValue, Settings::getFloat, ObservableSettings::addFloatListener)

/**
 * Create a new flow, based on observing the given [key] as a `Double`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getDoubleStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    defaultValue: Double
): StateFlow<Double> =
    createStateFlow(coroutineScope, key, defaultValue, Settings::getDouble, ObservableSettings::addDoubleListener)

/**
 * Create a new flow, based on observing the given [key] as a `Boolean`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getBooleanStateFlow(
    coroutineScope: CoroutineScope,
    key: String,
    defaultValue: Boolean
): StateFlow<Boolean> =
    createStateFlow(coroutineScope, key, defaultValue, Settings::getBoolean, ObservableSettings::addBooleanListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Int`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getIntOrNullStateFlow(coroutineScope: CoroutineScope, key: String): StateFlow<Int?> =
    createNullableStateFlow(coroutineScope, key, Settings::getIntOrNull, ObservableSettings::addIntOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Long`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getLongOrNullStateFlow(coroutineScope: CoroutineScope, key: String): StateFlow<Long?> =
    createNullableStateFlow(coroutineScope, key, Settings::getLongOrNull, ObservableSettings::addLongOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `String`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getStringOrNullStateFlow(
    coroutineScope: CoroutineScope,
    key: String
): StateFlow<String?> =
    createNullableStateFlow(coroutineScope, key, Settings::getStringOrNull, ObservableSettings::addStringOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Float`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getFloatOrNullStateFlow(coroutineScope: CoroutineScope, key: String): StateFlow<Float?> =
    createNullableStateFlow(coroutineScope, key, Settings::getFloatOrNull, ObservableSettings::addFloatOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Double`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getDoubleOrNullStateFlow(
    coroutineScope: CoroutineScope,
    key: String
): StateFlow<Double?> =
    createNullableStateFlow(coroutineScope, key, Settings::getDoubleOrNull, ObservableSettings::addDoubleOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Boolean`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getBooleanOrNullStateFlow(
    coroutineScope: CoroutineScope,
    key: String
): StateFlow<Boolean?> =
    createNullableStateFlow(
        coroutineScope,
        key,
        Settings::getBooleanOrNull,
        ObservableSettings::addBooleanOrNullListener
    )

