/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keybindingstesy;

import java.awt.event.InputEvent;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

public class KeyBinding {

    public enum FocusState {
        WHEN_FOCUSED(JComponent.WHEN_FOCUSED), WHEN_IN_FOCUSED_WINDOW(JComponent.WHEN_IN_FOCUSED_WINDOW), WHEN_ANCESTOR_OF_FOCUSED_COMPONENT(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        private int focusState;
        private FocusState(int focusState) {
            this.focusState = focusState;
        }

        public int getState() {
            return focusState;
        }
    }
    
    private KeyBinding.Key key;
    private Action action;
    
    public KeyBinding(KeyBinding.Key key, Action action) {
        this.key = key;
        this.action = action;
    }

    public KeyBinding.Key getKey() {
        return key;
    }

    public Action getAction() {
        return action;
    }
    
    public void bindTo(JComponent parent) {
        bindTo(parent, FocusState.WHEN_IN_FOCUSED_WINDOW);
    }
    
    public void bindTo(JComponent parent, FocusState state) {
        bind(getKey(), parent, state, getAction());
    }

    public static void bind(KeyBinding.Key key, JComponent parent, Action action) {
        bind(key, parent, FocusState.WHEN_IN_FOCUSED_WINDOW, action);
    }
    
    public static void bind(KeyBinding.Key key, JComponent parent, FocusState state, Action action) {
        InputMap inputMap = parent.getInputMap(state.getState());
        ActionMap actionMap = parent.getActionMap();
        
        inputMap.put(key.getKeyStroke(), key.getName());
        actionMap.put(key.getName(), action);
    }

    public static class Key {
        public enum KeyTrigger {
            PRESSED, RELEASED;

            public boolean isOnReleased() {
                return this == RELEASED;
            }
        }

        public enum Modifier {
            SHIFT(InputEvent.SHIFT_DOWN_MASK), CTRL(InputEvent.CTRL_DOWN_MASK), META(InputEvent.META_DOWN_MASK), ALT(InputEvent.ALT_DOWN_MASK), ALT_GRAPH(InputEvent.ALT_GRAPH_DOWN_MASK), NONE(0);

            private int modifier;

            private Modifier(int modifier) {
                this.modifier = modifier;
            }

            public int getModifier() {
                return modifier;
            }
        }

        private KeyStroke keyStroke;
        private String name;
        private KeyTrigger trigger;
        private Modifier[] modifiers;

        public Key(String name, KeyStroke keyStroke, KeyTrigger state, Modifier... modifiers) {
            this.name = name;
            this.keyStroke = keyStroke;
            this.trigger = state;
            Set<Modifier> modifierSet = new TreeSet<>(Arrays.asList(modifiers));
            modifierSet.remove(Modifier.NONE);
            this.modifiers = modifierSet.toArray(new Modifier[modifierSet.size()]);
        }

        public Key(String name, int key) {
            this(name, key, KeyTrigger.PRESSED);
        }

        public Key(String name, int key, KeyTrigger state) {
            this(name, key, state, Modifier.NONE);
        }

        public Key(String name, int key, Modifier... modifiers) {
            this(name, key, KeyTrigger.PRESSED, modifiers);
        }

        public Key(String name, int key, KeyTrigger state, Modifier... modifiers) {
            this(name, keyStrokeFor(key, state, modifiers), state, modifiers);
        }

        public Key(KeyStroke keyStroke, KeyTrigger state, Modifier... modifiers) {
            this(keyStroke.toString(), keyStroke, state, modifiers);
        }

        public Key(int key) {
            this(key, KeyTrigger.PRESSED);
        }

        public Key(int key, KeyTrigger state) {
            this(key, state, Modifier.NONE);
        }

        public Key(int key, Modifier... modifiers) {
            this(key, KeyTrigger.PRESSED, modifiers);
        }

        public Key(int key, KeyTrigger state, Modifier... modifiers) {
            this(keyStrokeFor(key, state, modifiers), state, modifiers);
        }

        public KeyStroke getKeyStroke() {
            return keyStroke;
        }

        public String getName() {
            return name;
        }

        public KeyTrigger getTrigger() {
            return trigger;
        }

        public Modifier[] getModifiers() {
            return modifiers;
        }
        
        public boolean isOnRelease() {
            return getKeyStroke().isOnKeyRelease();
        }

        public static KeyStroke keyStrokeFor(int key) {
            return keyStrokeFor(key, KeyTrigger.PRESSED);
        }

        public static KeyStroke keyStrokeFor(int key, KeyTrigger state) {
            return keyStrokeFor(key, state, Modifier.NONE);
        }

        public static KeyStroke keyStrokeFor(int key, Modifier... modifiers) {
            return keyStrokeFor(key, KeyTrigger.PRESSED, modifiers);
        }

        public static KeyStroke keyStrokeFor(int key, KeyTrigger state, Modifier... modifiers) {
            Set<Modifier> modifierSet = new TreeSet<>(Arrays.asList(modifiers));
            modifierSet.remove(Modifier.NONE);
            int values = 0;
            for (Modifier modifier : modifierSet) {
                values |= modifier.getModifier();
            }

            return KeyStroke.getKeyStroke(key, values, state.isOnReleased());
        }
    }
}
