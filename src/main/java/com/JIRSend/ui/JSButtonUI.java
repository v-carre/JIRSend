package com.JIRSend.ui;

import java.awt.*;
import java.util.Objects;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

public class JSButtonUI extends BasicButtonUI {
        protected Color selectColor;
        
        public void setSelectColor(final Color selectColor) {
            this.selectColor = Objects.requireNonNull(selectColor);
        }
        
        public Color getSelectColor() {
            return selectColor;
        }
        
        @Override
        protected void paintButtonPressed(final Graphics g,
                                          final AbstractButton b){
            if (b.isContentAreaFilled()) {
                Dimension size = b.getSize();
                g.setColor(getSelectColor());
                g.fillRect(0, 0, size.width, size.height);
            }
        }
    }