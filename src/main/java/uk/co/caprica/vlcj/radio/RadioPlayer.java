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

package uk.co.caprica.vlcj.radio;

import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.radio.view.MainFrame;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * An Internet radio player application.
 */
public class RadioPlayer {

    /**
     * Main application frame.
     */
    private MainFrame mainFrame;

    /**
     * Native media player factory.
     */
    private MediaPlayerFactory mediaPlayerFactory;

    /**
     * Native media player.
     */
    private MediaPlayer mediaPlayer;

    /**
     * Application entry point.
     *
     * @param args command line arguments, passed to the native libvlc component
     * @throws Exception if an error occurs
     */
    public static void main(final String[] args) throws Exception {
        setLookAndFeel();

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new RadioPlayer(args);
            }
        });
    }

    /**
     * Create a new radio player application.
     *
     * @param args arguments, passed directly to libvlc
     */
    public RadioPlayer(String[] args) {
        mediaPlayerFactory = new MediaPlayerFactory(args);
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();

        mainFrame = new MainFrame(mediaPlayer);
        mainFrame.setIconImage(new ImageIcon(getClass().getResource("/icons/vlcj-logo.png")).getImage());
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.pack();
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(mediaPlayer != null) {
                    mediaPlayer.controls().stop();
                }
            }

            @Override
            public void windowClosed(WindowEvent e) {
                if(mediaPlayer != null) {
                    mediaPlayer.release();
                    mediaPlayerFactory.release();
                }
            }
        });

        mainFrame.setVisible(true);

        mainFrame.start();
    }

    /**
     * Set the cross platform look and feel.
     *
     * @throws Exception if an error occurs
     */
    private static void setLookAndFeel() throws Exception {
        String lookAndFeelClassName = null;
        LookAndFeelInfo[] lookAndFeelInfos = UIManager.getInstalledLookAndFeels();
        for(LookAndFeelInfo lookAndFeel : lookAndFeelInfos) {
            if("Nimbus".equals(lookAndFeel.getName())) {
                lookAndFeelClassName = lookAndFeel.getClassName();
            }
        }
        if(lookAndFeelClassName == null) {
            lookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        }
        UIManager.setLookAndFeel(lookAndFeelClassName);
    }

}