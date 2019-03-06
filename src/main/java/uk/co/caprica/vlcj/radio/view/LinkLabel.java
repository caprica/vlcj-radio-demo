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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.AttributedString;

/**
 * A simple hyper-link label.
 * <p>
 * This is used rather than an HTML JLabel mainly because of focus and keyboard
 * activation. 
 */
public class LinkLabel extends JComponent {

    private static final long serialVersionUID = 1L;

    private final Stroke focusStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[] {1.0f}, 0.0f);

    private String text;
    private String link;
    private Color linkForeground;
    private Color focusColour;

    private boolean hasFocus;

    public LinkLabel(String text, String link) {
        this.text = text;
        this.link = link;
        this.linkForeground = new Color(34, 0, 193);
        this.focusColour = this.linkForeground;

        getActionMap().put("activate", new AbstractAction() {
            private static final long serialVersionUID = 1L;
            @Override
            public void actionPerformed(ActionEvent e) {
                activate(e);
            }
        });

        getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("ENTER"), "activate");

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new HoverMouseListener());

        // Don't understand why isFocusOwner() doesn't work
        addFocusListener(new OwnerFocusListener());
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        revalidate();
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Color getLinkForeground() {
        return linkForeground;
    }

    public void setLinkForeground(Color linkForeground) {
        this.linkForeground = linkForeground;
        repaint();
    }

    public Color getFocusColour() {
        return focusColour;
    }

    public void setFocusColour(Color focusColour) {
        this.focusColour = focusColour;
        repaint();
    }

    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public void removeActionListener(ActionListener listener) {
        listenerList.remove(ActionListener.class, listener);
    }

    @Override
    public Dimension getPreferredSize() {
        Insets insets = getInsets();
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int w = insets.left + fontMetrics.stringWidth(getText()) + insets.right;
        int h = insets.top + fontMetrics.getHeight() + insets.bottom;
        return new Dimension(w, h);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

        AttributedString attributedString = new AttributedString(getText());
        attributedString.addAttribute(TextAttribute.FONT, getFont());
        attributedString.addAttribute(TextAttribute.FOREGROUND, getLinkForeground());
        attributedString.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

        FontMetrics fontMetrics = g2.getFontMetrics(getFont());
        Insets insets = getInsets();
        int x = insets.left;
        int y = insets.top + fontMetrics.getAscent();

        g2.drawString(attributedString.getIterator(), x, y);

        if(hasFocus) {
            g2.setStroke(focusStroke);
            g2.setColor(getFocusColour());
            g2.drawRect(insets.left, insets.top, getWidth() - (insets.left + insets.right) - 1, getHeight() - (insets.top + insets.bottom) - 1);
        }
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    private void activate(ActionEvent e) {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        if(listeners.length > 0) {
            notifyListeners(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getLink(), e.getWhen(), e.getModifiers()));
        }
    }

    private void activate(MouseEvent e) {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        if(listeners.length > 0) {
            notifyListeners(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, getLink(), e.getWhen(), e.getModifiers()));
        }
    }

    private void notifyListeners(ActionEvent e) {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        for(int i = listeners.length - 1; i >= 0; i--) {
            listeners[i].actionPerformed(e);
        }
    }

    private class HoverMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            activate(e);
        }
    }

    private class OwnerFocusListener implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            hasFocus = true;
            repaint();
        }

        @Override
        public void focusLost(FocusEvent e) {
            hasFocus = false;
            repaint();
        }
    }

}