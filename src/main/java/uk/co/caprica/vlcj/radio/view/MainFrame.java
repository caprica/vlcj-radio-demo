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

import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.radio.model.DirectoryEntry;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

// TODO need to add special case handling for the javascript: radio stations (need to scrape the page and search for the streaming url)

/**
 * Main application frame.
 * <p>
 * This component manages the native media player.
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private DirectoryView directoryView;

    private final MediaPlayer mediaPlayer;

    private boolean record = false;

    public MainFrame(MediaPlayer mediaPlayer) {
        super("vlcj radio");

        this.mediaPlayer = mediaPlayer;

        directoryView = new DirectoryView();

        setLayout(new BorderLayout());
        setContentPane(directoryView);

        directoryView.addActionListener(new DirectoryActionListener());
        directoryView.addDirectoryListSelectionListener(new DirectoryListSelectionListener());

        JMenuBar menuBar = new JMenuBar();

        JMenu radioMenu = new JMenu("Radio");
        radioMenu.setMnemonic('r');

        JMenuItem radioUpdateDirectoryMenuItem = new JMenuItem("Update Directory");
        radioUpdateDirectoryMenuItem.setMnemonic('u');
        radioUpdateDirectoryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                directoryView.updateDirectory();
            }
        });
        radioMenu.add(radioUpdateDirectoryMenuItem);

        radioMenu.add(new JSeparator());

        final JCheckBoxMenuItem radioEnableRecordingMenuItem = new JCheckBoxMenuItem("Enable Recording...");
        radioEnableRecordingMenuItem.setMnemonic('r');
        radioEnableRecordingMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(radioEnableRecordingMenuItem.isSelected()) {
                    JOptionPane.showMessageDialog(MainFrame.this, "Recording will be started the next time you play a stream.");
                    record = true;
                }
                else {
                    JOptionPane.showMessageDialog(MainFrame.this, "Recording will be stopped the next time you play a stream.");
                    record = false;
                }
            }
        });
        radioMenu.add(radioEnableRecordingMenuItem);

        radioMenu.add(new JSeparator());

        JMenuItem radioExitMenuItem = new JMenuItem("Exit");
        radioExitMenuItem.setMnemonic('x');
        radioExitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        radioMenu.add(radioExitMenuItem);

        menuBar.add(radioMenu);

        JMenu aboutMenu = new JMenu("About");
        aboutMenu.setMnemonic('a');

        JMenuItem aboutAboutMenuItem = new JMenuItem("About...");
        aboutAboutMenuItem.setMnemonic('a');
        aboutAboutMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AboutDialog dialog = new AboutDialog(MainFrame.this);
                dialog.setModal(true);
                dialog.setResizable(false);
                dialog.setLocationRelativeTo(MainFrame.this);
                dialog.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                dialog.setVisible(true);
            }
        });
        aboutMenu.add(aboutAboutMenuItem);

        menuBar.add(aboutMenu);

        setJMenuBar(menuBar);
    }

    public void start() {
        directoryView.start();
    }

    private class DirectoryActionListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if(DirectoryView.ACTIVATE_COMMAND.equals(command)) {
                DirectoryEntry selectedEntry = directoryView.getSelectedEntry();
                if(selectedEntry != null) {
                    directoryView.setNowPlaying(selectedEntry);
                    String[] mediaOptions = record ? getRecordMediaOptions(selectedEntry.getUrl()) : new String[0];
                    mediaPlayer.media().play(selectedEntry.getUrl(), mediaOptions);
                }
                else {
                    directoryView.setNowPlaying((DirectoryEntry)null);
                }
            }
            else if(DirectoryView.ACTIVATE_CUSTOM_COMMAND.equals(command)) {
                String customEntry = directoryView.getCustomEntry();
                directoryView.setNowPlaying(customEntry);
                String[] mediaOptions = record ? getRecordMediaOptions(customEntry) : new String[0];
                mediaPlayer.media().play(customEntry, mediaOptions);
            }
            else if(DirectoryView.STOP_COMMAND.equals(command)) {
                mediaPlayer.controls().stop();
                directoryView.setNowPlaying((String)null);
            }
        }
    }

    private class DirectoryListSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
        }
    }

    private String[] getRecordMediaOptions(String address) {
        File file = getFile(address);
        StringBuilder sb = new StringBuilder(200);
        sb.append("sout=#transcode{acodec=mp3,channels=2,ab=192,samplerate=44100}:duplicate{dst=display,dst=std{access=file,mux=raw,dst=");
        sb.append(file.getPath());
        sb.append("}}");
        return new String[] {sb.toString()};
    }

    private File getFile(String address) {
        StringBuilder sb = new StringBuilder(100);
        try {
            URL url = new URL(address);
            sb.append(new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()));
            sb.append('-');
            sb.append(url.getHost().replace('.', '_'));
            sb.append('-');
            sb.append(url.getPort());
            sb.append(".mp3");

            File userHomeDirectory = new File(System.getProperty("user.home"));
            File saveDirectory = new File(userHomeDirectory, "vlcj-radio");
            if(!saveDirectory.exists()) {
                saveDirectory.mkdirs();
            }
            return new File(saveDirectory, sb.toString());
        }
        catch(MalformedURLException e) {
            throw new RuntimeException("Unable to create a URL for '" + address + "'");
        }
    }

}