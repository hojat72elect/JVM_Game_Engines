package com.badlogic.gdx.scenes.scene2d.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.Layout;
import com.badlogic.gdx.utils.Null;

/**
 * Value placeholder, allowing the value to be computed on request. Values can be provided an actor for context to reduce the
 * number of value instances that need to be created and reduce verbosity in code that specifies values.
 */
abstract public class Value {
    /**
     * A value that is always zero.
     */
    static public final Fixed zero = new Fixed(0);
    /**
     * Value that is the minWidth of the actor in the cell.
     */
    static public Value minWidth = new Value() {
        public float get(@Null Actor context) {
            if (context instanceof Layout) return ((Layout) context).getMinWidth();
            return context == null ? 0 : context.getWidth();
        }
    };
    /**
     * Value that is the minHeight of the actor in the cell.
     */
    static public Value minHeight = new Value() {
        public float get(@Null Actor context) {
            if (context instanceof Layout) return ((Layout) context).getMinHeight();
            return context == null ? 0 : context.getHeight();
        }
    };
    /**
     * Value that is the prefWidth of the actor in the cell.
     */
    static public Value prefWidth = new Value() {
        public float get(@Null Actor context) {
            if (context instanceof Layout) return ((Layout) context).getPrefWidth();
            return context == null ? 0 : context.getWidth();

        }
    };
    /**
     * Value that is the prefHeight of the actor in the cell.
     */
    static public Value prefHeight = new Value() {
        public float get(@Null Actor context) {
            if (context instanceof Layout) return ((Layout) context).getPrefHeight();
            return context == null ? 0 : context.getHeight();
        }
    };
    /**
     * Value that is the maxWidth of the actor in the cell.
     */
    static public Value maxWidth = new Value() {
        public float get(@Null Actor context) {
            if (context instanceof Layout) return ((Layout) context).getMaxWidth();
            return context == null ? 0 : context.getWidth();
        }
    };
    /**
     * Value that is the maxHeight of the actor in the cell.
     */
    static public Value maxHeight = new Value() {
        public float get(@Null Actor context) {
            if (context instanceof Layout) return ((Layout) context).getMaxHeight();
            return context == null ? 0 : context.getHeight();
        }
    };

    /**
     * Returns a value that is a percentage of the actor's width.
     */
    static public Value percentWidth(final float percent) {
        return new Value() {
            public float get(@Null Actor actor) {
                return actor.getWidth() * percent;
            }
        };
    }

    /**
     * Returns a value that is a percentage of the actor's height.
     */
    static public Value percentHeight(final float percent) {
        return new Value() {
            public float get(@Null Actor actor) {
                return actor.getHeight() * percent;
            }
        };
    }

    /**
     * Returns a value that is a percentage of the specified actor's width. The context actor is ignored.
     */
    static public Value percentWidth(final float percent, final Actor actor) {
        if (actor == null) throw new IllegalArgumentException("actor cannot be null.");
        return new Value() {
            public float get(@Null Actor context) {
                return actor.getWidth() * percent;
            }
        };
    }

    /**
     * Returns a value that is a percentage of the specified actor's height. The context actor is ignored.
     */
    static public Value percentHeight(final float percent, final Actor actor) {
        if (actor == null) throw new IllegalArgumentException("actor cannot be null.");
        return new Value() {
            public float get(@Null Actor context) {
                return actor.getHeight() * percent;
            }
        };
    }

    /**
     * Calls {@link #get(Actor)} with null.
     */
    public float get() {
        return get(null);
    }

    /**
     * @param context May be null.
     */
    abstract public float get(@Null Actor context);

    /**
     * A fixed value that is not computed each time it is used.
     */
    static public class Fixed extends Value {
        static final Fixed[] cache = new Fixed[111];

        private final float value;

        public Fixed(float value) {
            this.value = value;
        }

        static public Fixed valueOf(float value) {
            if (value == 0) return zero;
            if (value >= -10 && value <= 100 && value == (int) value) {
                Fixed fixed = cache[(int) value + 10];
                if (fixed == null) cache[(int) value + 10] = fixed = new Fixed(value);
                return fixed;
            }
            return new Fixed(value);
        }

        public float get(@Null Actor context) {
            return value;
        }

        public String toString() {
            return Float.toString(value);
        }
    }
}
