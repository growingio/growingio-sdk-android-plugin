/*
 *   Copyright (c) 2022 Beijing Yishu Technology Co., Ltd.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.growingio.android.descriptor

import android.widget.*
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.SeekBar.OnSeekBarChangeListener
import com.google.android.material.slider.RangeSlider
import com.google.android.material.slider.Slider
import com.growingio.inject.annotation.SuperInject
import com.growingio.inject.annotation.Belong

/**
 * <p>
 *     控件值变化注入
 * @author cpacm 2022/5/10
 */
@Belong(className = "com.growingio.android.sdk.autotrack.inject.ViewChangeInjector")
interface ViewChangeInjector {

    @SuperInject(
        clazz = OnSeekBarChangeListener::class,
        method = "onStopTrackingTouch",
        parameterTypes = [SeekBar::class]
    )
    fun seekBarOnStopTrackingTouch(listener: OnSeekBarChangeListener, seekBar: SeekBar)

    @SuperInject(
        clazz = OnRatingBarChangeListener::class,
        method = "onRatingChanged",
        parameterTypes = [RatingBar::class, Float::class, Boolean::class]
    )
    fun ratingBarOnRatingChange(
        listener: OnRatingBarChangeListener,
        ratingBar: RatingBar,
        rating: Float,
        fromUser: Boolean
    )

    @SuperInject(
        clazz = CompoundButton.OnCheckedChangeListener::class,
        method = "onCheckedChanged",
        parameterTypes = [CompoundButton::class, Boolean::class]
    )
    fun compoundButtonOnChecked(
        listener: CompoundButton.OnCheckedChangeListener,
        button: CompoundButton,
        checked: Boolean
    )

    @SuperInject(
        clazz = Slider.OnSliderTouchListener::class,
        method = "onStopTrackingTouch",
        parameterTypes = [Slider::class]
    )
    fun sliderOnStopTrackingTouch(listener: Slider.OnSliderTouchListener, slider: Slider)

    @SuperInject(
        clazz = RangeSlider.OnSliderTouchListener::class,
        method = "onStopTrackingTouch",
        parameterTypes = [RangeSlider::class]
    )
    fun rangeSliderOnStopTrackingTouch(listener: RangeSlider.OnSliderTouchListener, rangeSlider: RangeSlider)

}