/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package keybindingstesy;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Main {
    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                JFrame frame = new JFrame();
                frame.add(new TestPane());
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        });
    }

    public class TestPane extends JPanel {

        protected interface ActionKeyObserver {
            public void actionKeyWasPressed(ActionKey key);
            public void actionKeyWasReleased(ActionKey key);
        }

        protected enum ActionKey {
            UP(KeyBinding.Key.KeyType.W), DOWN(KeyBinding.Key.KeyType.S), LEFT(KeyBinding.Key.KeyType.A), RIGHT(KeyBinding.Key.KeyType.D);

            private final KeyBinding.Key pressedKey;
            private final KeyBinding.Key releasedKey;

            private ActionKey(KeyBinding.Key.KeyType keyType) {
                this.pressedKey = new KeyBinding.Key(keyType, KeyBinding.Key.KeyTrigger.PRESSED);
                this.releasedKey = new KeyBinding.Key(keyType, KeyBinding.Key.KeyTrigger.RELEASED);
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

        private Set<ActionKey> activeKeys = new TreeSet<>();
        private static final int SIZE = 50;

        public TestPane() {
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

            Timer timer = new Timer(5, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    repaint();
                }
            });
            timer.start();
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 200);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;

            if (activeKeys.contains(ActionKey.LEFT)) {
                int y = centerY - (SIZE / 2);
                int x = centerX - (SIZE / 2) - SIZE - 4;
                g2d.setColor(Color.BLUE);
                g2d.fillRect(x, y, SIZE, SIZE);
            }
            if (activeKeys.contains(ActionKey.RIGHT)) {
                int y = centerY - (SIZE / 2);
                int x = centerX - (SIZE / 2) + SIZE + 4;
                g2d.setColor(Color.BLUE);
                g2d.fillRect(x, y, SIZE, SIZE);
            }
            if (activeKeys.contains(ActionKey.DOWN)) {
                int y = centerY - (SIZE / 2);
                int x = centerX - (SIZE / 2);
                g2d.setColor(Color.BLUE);
                g2d.fillRect(x, y, SIZE, SIZE);
            }
            if (activeKeys.contains(ActionKey.UP)) {
                int y = centerY - (SIZE / 2) - SIZE - 4;
                int x = centerX - (SIZE / 2);
                g2d.setColor(Color.BLUE);
                g2d.fillRect(x, y, SIZE, SIZE);
            }

            g2d.setColor(Color.DARK_GRAY);

            // LEFT
            int y = centerY - (SIZE / 2);
            int x = centerX - (SIZE / 2) - SIZE - 4;
            g2d.drawRect(x, y, SIZE, SIZE);

            // RIGHT
            y = centerY - (SIZE / 2);
            x = centerX - (SIZE / 2) + SIZE + 4;
            g2d.drawRect(x, y, SIZE, SIZE);

            // DOWN
            y = centerY - (SIZE / 2);
            x = centerX - (SIZE / 2);
            g2d.drawRect(x, y, SIZE, SIZE);

            // UP
            y = centerY - (SIZE / 2) - SIZE - 4;
            x = centerX - (SIZE / 2);
            g2d.drawRect(x, y, SIZE, SIZE);

            g2d.dispose();
        }

        protected static class ActionKeyAction extends AbstractAction {
            private ActionKey actionKey;
            private ActionKeyObserver observer;
            private boolean onRelease;

            public ActionKeyAction(ActionKey actionKey, ActionKeyObserver observer, boolean onRelease) {
                this.actionKey = actionKey;
                this.observer = observer;
                this.onRelease = onRelease;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (onRelease) {
                    observer.actionKeyWasReleased(actionKey);
                } else {
                    observer.actionKeyWasPressed(actionKey);
                }
            }
        }

    }
}
