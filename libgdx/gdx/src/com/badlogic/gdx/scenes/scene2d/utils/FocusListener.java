package com.badlogic.gdx.scenes.scene2d.utils;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.utils.Null;

/**
 * Listener for {@link FocusEvent}.
 */
abstract public class FocusListener implements EventListener {
    public boolean handle(Event event) {
        if (!(event instanceof FocusEvent)) return false;
        FocusEvent focusEvent = (FocusEvent) event;
        switch (focusEvent.getType()) {
            case keyboard:
                keyboardFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
                break;
            case scroll:
                scrollFocusChanged(focusEvent, event.getTarget(), focusEvent.isFocused());
                break;
        }
        return false;
    }

    /**
     * @param actor The event target, which is the actor that emitted the focus event.
     */
    public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
    }

    /**
     * @param actor The event target, which is the actor that emitted the focus event.
     */
    public void scrollFocusChanged(FocusEvent event, Actor actor, boolean focused) {
    }

    /**
     * Fired when an actor gains or loses keyboard or scroll focus. Can be cancelled to prevent losing or gaining focus.
     */
    static public class FocusEvent extends Event {
        private boolean focused;
        private Type type;
        private Actor relatedActor;

        public void reset() {
            super.reset();
            relatedActor = null;
        }

        public boolean isFocused() {
            return focused;
        }

        public void setFocused(boolean focused) {
            this.focused = focused;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type focusType) {
            this.type = focusType;
        }

        /**
         * The actor related to the event. When focus is lost, this is the new actor being focused, or null. When focus is gained,
         * this is the previous actor that was focused, or null.
         */
        public @Null Actor getRelatedActor() {
            return relatedActor;
        }

        /**
         * @param relatedActor May be null.
         */
        public void setRelatedActor(@Null Actor relatedActor) {
            this.relatedActor = relatedActor;
        }

        /**
         *
         */
        static public enum Type {
            keyboard, scroll
        }
    }
}
