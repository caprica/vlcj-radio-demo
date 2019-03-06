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

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.support.Info;
import uk.co.caprica.vlcj.support.version.Version;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

/**
 * A simple "about" dialog.
 */
public class AboutDialog extends BaseDialog {

    private static final long serialVersionUID = 1L;

    private JLabel playerVersionLabel;
    private JLabel copyrightLabel;
    private LinkLabel vendorHomePageLabel;
    private JLabel termsLabel;
    private LinkLabel licensePageLabel;
    private JLabel vlcjVersionLabel;
    private JButton okButton;

    public AboutDialog(Frame parentFrame) {
        super(parentFrame, "About vlcj radio");

        Version vlcjVersion = Info.getInstance().vlcjVersion();

        playerVersionLabel = new JLabel("vlcj radio 2.0.0");
        copyrightLabel = new JLabel("(C)2010-2019");
        vendorHomePageLabel = new LinkLabel("Caprica Software Limited", "http://www.capricasoftware.co.uk");
        termsLabel = new JLabel("This software is disributed under the terms of the");
        licensePageLabel = new LinkLabel("GPL 3.0 License", "http://www.gnu.org/licenses/gpl-3.0.html");

        vlcjVersionLabel = new JLabel("vlcj version " + vlcjVersion.version());

        okButton = new JButton("OK");

        createUI();
    }

    private void createUI() {
        JPanel cp = new JPanel();

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });

        cp.setBorder(new EmptyBorder(16, 16, 16, 16));
        cp.setLayout(new MigLayout("wrap 2", "[][100::, grow]", "[][][]20[][][][][]20[]"));

        cp.add(playerVersionLabel, "c, span 2");

        JPanel vendorPanel = new JPanel();
        vendorPanel.add(copyrightLabel);
        vendorPanel.add(vendorHomePageLabel);

        cp.add(vendorPanel, "c, span 2");

        JPanel termsPanel = new JPanel();
        termsPanel.add(termsLabel);
        termsPanel.add(licensePageLabel);

        cp.add(termsPanel, "c, span 2");

        cp.add(vlcjVersionLabel, "c, span 2");

        cp.add(okButton, "r, span 2, tag ok");

        setLayout(new BorderLayout());
        add(cp, BorderLayout.CENTER);

        LinkListener linkListener = new LinkListener();
        vendorHomePageLabel.addActionListener(linkListener);
        licensePageLabel.addActionListener(linkListener);

        pack();

        okButton.requestFocusInWindow();
    }

    private class LinkListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            if(Desktop.isDesktopSupported()) {
                try {
                    URI uri = new URI(evt.getActionCommand());
                    Desktop.getDesktop().browse(uri);
                }
                catch(Exception e) {
                }
            }
        }
    }

}