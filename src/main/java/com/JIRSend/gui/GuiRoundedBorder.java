package com.JIRSend.gui;

import java.awt.*;
import javax.swing.border.*;

public class GuiRoundedBorder implements Border {

    private int radius;
    private int top, left, bottom, right;

    GuiRoundedBorder(int radius, int top, int left, int bottom, int right) {
        this.radius = radius;
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        ;
        this.right = right;
    }

    GuiRoundedBorder(int radius) {
        this(radius, 0, 0, 0, 0);
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(this.radius + 1 + top, this.radius + 1 + left, this.radius + 2 + bottom, this.radius + right);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}