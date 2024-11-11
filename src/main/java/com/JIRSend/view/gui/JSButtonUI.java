package com.JIRSend.view.gui;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class JSButtonUI extends BasicButtonUI {
        protected Color pressedColor;
        
        public void setPressedColor(final Color pressedColor) {
            this.pressedColor = Objects.requireNonNull(pressedColor);
        }
        
        public Color getPressedColor() {
            return pressedColor;
        }

        @Override
        protected void paintButtonPressed(final Graphics g,
                                          final AbstractButton b){
            if (b.isContentAreaFilled()) {
                Dimension size = b.getSize();
                g.setColor(getPressedColor());
                g.fillRect(0, 0, size.width, size.height);
            }
        }
    }