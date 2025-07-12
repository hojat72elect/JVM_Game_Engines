/*
 * FXGL - JavaFX Game Library. The MIT License (MIT).
 * Copyright (c) AlmasB (almaslvl@gmail.com).
 * See LICENSE for details.
 */

package com.almasb.fxgl.achievement

import javafx.event.Event
import javafx.event.EventType

/**
 * Fired when an achievement is unlocked (achieved).
 *
 * @author Almas Baimagambetov (AlmasB) (almaslvl@gmail.com)
 */
open class AchievementEvent(
        eventType: EventType<out AchievementEvent>,

        /**
         * The achievement with which the event is associated.
         */
        val achievement: Achievement
) : Event(eventType) {

    companion object {
        @JvmField val ANY = EventType<AchievementEvent>(Event.ANY, "ACHIEVEMENT_EVENT")

        @JvmField val ACHIEVED = EventType(ANY, "ACHIEVED")
    }

    constructor(achievement: Achievement) : this(ACHIEVED, achievement) {}

    override fun toString() =
            "AchievementEvent[type=$eventType, name=${achievement.name}, description=${achievement.description}]"
}

/**
 * Fired when a numeric value based achievement has made some progress.
 */
class AchievementProgressEvent(
        achievement: Achievement,

        /**
         * Current value.
         */
        val value: Double,

        /**
         * The value to achieve.
         */
        val max: Double
) : AchievementEvent(PROGRESS, achievement) {

    companion object {
        val PROGRESS = EventType<AchievementProgressEvent>(AchievementEvent.ANY, "PROGRESS")
    }

    override fun toString() = "AchievementProgressEvent[value=$value,max=$max]"
}