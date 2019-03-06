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
import uk.co.caprica.vlcj.radio.model.DirectoryEntry;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * Simple status panel showing the currently selected station.
 */
public class StatusPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JLabel nameLabel;
    private JLabel nameValueLabel;

    private JLabel genreLabel;
    private JLabel genreValueLabel;

    private JLabel addressLabel;
    private JLabel addressValueLabel;

    private JLabel typeLabel;
    private JLabel typeValueLabel;

    public StatusPanel() {
        setBorder(new TitledBorder("Now Playing..."));
        setLayout(new MigLayout("", "[]rel[]16[]rel[]16[]rel[]16[]rel[]", "[t]"));

        nameLabel = new JLabel("Name:");
        nameValueLabel = new JLabel("<n/a>");

        genreLabel = new JLabel("Genre:");
        genreValueLabel = new JLabel("<n/a>");

        addressLabel = new JLabel("Address:");
        addressValueLabel = new JLabel("<n/a>");

        typeLabel = new JLabel("Type:");
        typeValueLabel = new JLabel("<n/a>");

        add(nameLabel);
        add(nameValueLabel);

        add(genreLabel);
        add(genreValueLabel);

        add(addressLabel);
        add(addressValueLabel);

        add(typeLabel);
        add(typeValueLabel);
    }

    public void setModel(DirectoryEntry entry) {
        if(entry != null) {
            nameValueLabel.setText("<html><b>" + (entry.getName() != null ? entry.getName() : "") + "</b></html>");
            genreValueLabel.setText("<html><b>" + (entry.getGenre() != null ? entry.getGenre() : "") + "</b></html>");
            addressValueLabel.setText("<html><b>" + entry.getUrl() + "</b></html>");
            typeValueLabel.setText("<html><b>" + (entry.getType() != null ? entry.getType() : "") + "</b></html>");
        }
        else {
            nameValueLabel.setText("<n/a>");
            genreValueLabel.setText("<n/a>");
            addressValueLabel.setText("<n/a>");
            typeValueLabel.setText("<n/a>");
        }
    }

    public void setModel(String customEntry) {
        if(customEntry != null) {
            nameValueLabel.setText("<n/a>");
            genreValueLabel.setText("<n/a>");
            addressValueLabel.setText("<html><b>" + customEntry + "</b></html>");
            typeValueLabel.setText("<n/a>");
        }
        else {
            nameValueLabel.setText("<n/a>");
            genreValueLabel.setText("<n/a>");
            addressValueLabel.setText("<n/a>");
            typeValueLabel.setText("<n/a>");
        }
    }

}