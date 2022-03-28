# Java Swing, Key Bindings API

The intention of this project is to explore the possible ways in which the key bindings API might be made "easier" to setup and make use of it

Swing [Key bindings](https://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html) can take some effort to setup and configure, which can make their entry point somewhat higher for new devs, meaning that they are more likely to fallback to `KeyListener` and the pollute Stackoverflow with questions about why they don't work.

Setting up a single binding typically looks something like...

	Action action = ...;
	InputMap inputMap = parent.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
    ActionMap actionMap = parent.getActionMap();
        
    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "pressed.enter");
    actionMap.put("pressed.enter", action);

Now add in the need to have different bindings for pressed and released keys and this quickly begins to build up.

The intention of this API is to try and reduce this down to something more like...

    KeyBinding enterKeyBinding = new KeyBinding(new KeyBinding.Key(KeyEvent.VK_ENTER), new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Enter was pressed
        }
    });
    enterKeyBinding.bindTo(this);

So, ths makes use of some "common" defaults.

1. The "trigger" state is "pressed" (ie when the key is pressed)
2. The "focus state" is `JComponent.WHEN_IN_FOCUSED_WINDOW`

But wait, you want to know when the key was released, not pressed, no worries:

    KeyBinding enterKeyBinding = new KeyBinding(new KeyBinding.Key(KeyEvent.VK_ENTER, KeyBinding.Key.KeyTrigger.RELEASED), new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Enter was pressed
        }
    });
    enterKeyBinding.bindTo(this);

Want to change the focus state requirements? No worries...

    KeyBinding enterKeyBinding = new KeyBinding(new KeyBinding.Key(KeyEvent.VK_ENTER, KeyBinding.Key.KeyTrigger.RELEASED), new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Enter was RELEASED
        }
    });
    enterKeyBinding.bindTo(this, KeyBinding.FocusState.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

The API makes use of a number of `enum`s to make it easier to understand what is required and reduce the risk of providing incorrect values.

But what about modifiers?  Modifiers are constent source of issues (with both the key bindings API and `KeyListener`).  To that end, the API attempts to simplify there use, through the use of, you guessed it, a `enum`

    KeyBinding enterKeyBinding = new KeyBinding(new KeyBinding.Key(KeyEvent.VK_ENTER, KeyBinding.Key.KeyTrigger.RELEASED, KeyBinding.Key.Modifier.CTRL, KeyBinding.Key.Modifier.SHIFT), new AbstractAction() {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Enter was RELEASED
        }
    });

One of the goals of the API was also to provide a number of different workflows, so that you're not bogged down to a single workflow.

One of the (many) uses of key bindings is for monitoring keyboard input for games.  This typically requires the need to monitor the pressed and released events of a key

To this end, it would be possible to establish a series of "actions" the game can respond to and then estbalish the bindings via it instead, for example...

    protected enum ActionKey {
        UP(KeyEvent.VK_W), DOWN(KeyEvent.VK_S), LEFT(KeyEvent.VK_A), RIGHT(KeyEvent.VK_D);

        private final KeyBinding.Key pressedKey;
        private final KeyBinding.Key releasedKey;

        private ActionKey(int keyEvent) {
            this.pressedKey = new KeyBinding.Key(keyEvent, KeyBinding.Key.KeyTrigger.PRESSED);
            this.releasedKey = new KeyBinding.Key(keyEvent, KeyBinding.Key.KeyTrigger.RELEASED);
        }

        public KeyBinding.Key getPressedKey() {
            return pressedKey;
        }

        public KeyBinding.Key getReleasedKey() {
            return releasedKey;
        }

        protected void install(KeyBinding.Key key, JComponent component, ActionKeyObserver observer) {
            KeyBinding.bind(key, component, new ActionKeyAction(this, observer, key.isOnRelease()));
        }

        public void install(JComponent component, ActionKeyObserver observer) {
            install(getPressedKey(), component, observer);
            install(getReleasedKey(), component, observer);
        }

        public static void installAll(JComponent component, ActionKeyObserver observer) {
            UP.install(component, observer);
            DOWN.install(component, observer);
            LEFT.install(component, observer);
            RIGHT.install(component, observer);
        }
    }

*this is taken from the sample code, so the other source files are included there*

And then, when needed, it's as simple as...

    ActionKeyObserver observer = new ActionKeyObserver() {
        @Override
        public void actionKeyWasPressed(ActionKey key) {
            activeKeys.add(key);
        }

        @Override
        public void actionKeyWasReleased(ActionKey key) {
            activeKeys.remove(key);
        }
    };
    ActionKey.installAll(this, observer);

to setup and install.