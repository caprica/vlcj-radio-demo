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
import uk.co.caprica.vlcj.radio.view.DirectoryView.DirectoryMatcherEditor;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * The table search filters.
 */
public class FilterPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DirectoryMatcherEditor directoryMatcherEditor;

    private enum Filter {
        DIRECTORY,
        NAME,
        ADDRESS,
        TYPE,
        GENRE
    }

    private JLabel directoryLabel;
    private JTextField directoryTextField;

    private JLabel nameLabel;
    private JTextField nameTextField;

    private JLabel genreLabel;
    private JTextField genreTextField;

    private JLabel addressLabel;
    private JTextField addressTextField;

    private JLabel typeLabel;
    private JTextField typeTextField;

    private JButton clearButton;

    public FilterPanel(DirectoryMatcherEditor directoryMatcherEditor) {
        this.directoryMatcherEditor = directoryMatcherEditor;

        setBorder(new TitledBorder(""));
        setLayout(new MigLayout("", "[]rel[]16[]rel[]16[]rel[]16[]rel[]16[]rel[]16[]", ""));

        directoryLabel = new JLabel("Directory:");
        directoryLabel.setDisplayedMnemonic('d');
        directoryTextField = new JTextField();
        directoryTextField.setColumns(10);
        directoryTextField.setFocusAccelerator('d');

        nameLabel = new JLabel("Name:");
        nameLabel.setDisplayedMnemonic('n');
        nameTextField = new JTextField();
        nameTextField.setColumns(20);
        nameTextField.setFocusAccelerator('n');

        genreLabel = new JLabel("Genre:");
        genreLabel.setDisplayedMnemonic('g');
        genreTextField = new JTextField();
        genreTextField.setColumns(10);
        genreTextField.setFocusAccelerator('g');

        addressLabel = new JLabel("Address:");
        addressLabel.setDisplayedMnemonic('d');
        addressTextField = new JTextField();
        addressTextField.setColumns(20);
        addressTextField.setFocusAccelerator('d');

        typeLabel = new JLabel("Type:");
        typeLabel.setDisplayedMnemonic('t');
        typeTextField = new JTextField();
        typeTextField.setColumns(10);
        typeTextField.setFocusAccelerator('t');

        clearButton = new JButton("Clear");
        clearButton.setMnemonic('c');

        add(directoryLabel);
        add(directoryTextField);

        add(nameLabel);
        add(nameTextField);

        add(genreLabel);
        add(genreTextField);

        add(addressLabel);
        add(addressTextField);

        add(typeLabel);
        add(typeTextField);

        add(clearButton);

        directoryTextField.getDocument().addDocumentListener(new FilterDocumentListener(Filter.DIRECTORY));
        nameTextField.getDocument().addDocumentListener(new FilterDocumentListener(Filter.NAME));
        addressTextField.getDocument().addDocumentListener(new FilterDocumentListener(Filter.ADDRESS));
        typeTextField.getDocument().addDocumentListener(new FilterDocumentListener(Filter.TYPE));
        genreTextField.getDocument().addDocumentListener(new FilterDocumentListener(Filter.GENRE));

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                directoryTextField.setText(null);
                nameTextField.setText(null);
                addressTextField.setText(null);
                typeTextField.setText(null);
                genreTextField.setText(null);
                FilterPanel.this.directoryMatcherEditor.clear();
            }
        });
    }

    private class FilterDocumentListener implements DocumentListener {

        private final Filter filter;

        public FilterDocumentListener(Filter filter) {
            this.filter = filter;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateFilter(e);
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateFilter(e);
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateFilter(e);
        }

        private void updateFilter(DocumentEvent e) {
            switch(filter) {
                case DIRECTORY:
                    directoryMatcherEditor.setDirectory(directoryTextField.getText().toLowerCase());
                    break;

                case NAME:
                    directoryMatcherEditor.setName(nameTextField.getText().toLowerCase());
                    break;

                case GENRE:
                    directoryMatcherEditor.setGenre(genreTextField.getText().toLowerCase());
                    break;

                case ADDRESS:
                    directoryMatcherEditor.setAddress(addressTextField.getText().toLowerCase());
                    break;

                case TYPE:
                    directoryMatcherEditor.setType(typeTextField.getText().toLowerCase());
                    break;
            }
        }
    }

}