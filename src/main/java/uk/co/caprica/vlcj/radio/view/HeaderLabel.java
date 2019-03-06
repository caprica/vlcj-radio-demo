/*
 * This file is part of VLCJ.
 *
 * VLCJ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * VLCJ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with VLCJ.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2009, 2010, 2011 Caprica Software Limited.
 */

package uk.co.caprica.vlcj.radio.view;

import javax.swing.*;
import java.awt.*;

/**
 * A simple label with adournments.
 */
public class HeaderLabel extends JComponent {

    private static final long serialVersionUID = 1L;

    private Color hiColour = Color.white;
    private Color loColour = Color.black;

    private int beforeSize = 8;
    private int afterSize = 8;
    private int gap = 4;

    private String text;

    public HeaderLabel(String text) {
        this.text = text;
    }

    public void setText(String text) {
        this.text = text;
        revalidate();
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        FontMetrics fm = getFontMetrics(getFont());
        int w = 0;
        int h = 0;
        w += beforeSize;
        w += gap;
        if(text != null) {
            w += fm.stringWidth(text);
        }
        w += afterSize;
        h = fm.getHeight();
        return new Dimension(w, h);
    }

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        Insets insets = getInsets();

        int w = getWidth() - (insets.left + insets.right);
        int h = getHeight() - (insets.top + insets.bottom);

        Font font = getFont().deriveFont(Font.BOLD);

        FontMetrics fontMetrics = g2.getFontMetrics(font);
        int stringWidth = fontMetrics.stringWidth(text);

        int lineX1 = insets.left;
        int lineY1 = h / 2;
        int lineX2 = lineX1 + beforeSize + gap + stringWidth + gap;
        int lineY2 = lineY1 + 1;

        g2.setColor(loColour);
        g2.drawLine(lineX1, lineY1, lineX1 + beforeSize, lineY1);
        g2.drawLine(lineX2, lineY1, lineX2 + (w - lineX2), lineY1);

        g2.setColor(hiColour);
        g2.drawLine(lineX1, lineY2, lineX1 + beforeSize, lineY2);
        g2.drawLine(lineX2, lineY2, lineX2 + (w - lineX2), lineY2);

        int textX = lineX1 + beforeSize + gap;
        int textY = ((h - fontMetrics.getHeight()) / 2) + insets.top + fontMetrics.getAscent();

        g2.setColor(getForeground());
        g2.setFont(font);
        g2.drawString(text, textX, textY);
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

}