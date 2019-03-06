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

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.matchers.AbstractMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.swing.EventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.radio.model.Directory;
import uk.co.caprica.vlcj.radio.model.DirectoryEntry;
import uk.co.caprica.vlcj.radio.service.CachedDirectoryService;
import uk.co.caprica.vlcj.radio.service.DirectoryService;
import uk.co.caprica.vlcj.radio.service.icecast.CachedIcecastDirectoryService;
import uk.co.caprica.vlcj.radio.service.icecast.IcecastDirectoryService;
import uk.co.caprica.vlcj.radio.service.listenlive.CachedListenLiveDirectoryService;
import uk.co.caprica.vlcj.radio.service.listenlive.ListenLiveDirectoryService;
import uk.co.caprica.vlcj.radio.service.musicradio.CachedMusicRadioDirectoryService;
import uk.co.caprica.vlcj.radio.service.musicradio.MusicRadioDirectoryService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main directory view.
 * <p>
 * This view provides a table of directory entries that can be sorted and 
 * filtered.
 * <p>
 * Double-clicking a directory entry plays that station.
 */
public class DirectoryView extends JPanel {

    private static final long serialVersionUID = 1L;

    public static final String ACTIVATE_COMMAND = "activate";
    public static final String ACTIVATE_CUSTOM_COMMAND = "activate-custom";
    public static final String STOP_COMMAND = "stop";

    private final EventList<DirectoryEntry> directoryEventList = new BasicEventList<DirectoryEntry>();
    private final SortedList<DirectoryEntry> directorySortedList = new SortedList<DirectoryEntry>(directoryEventList);
    private final DirectoryMatcherEditor directoryMatcherEditor = new DirectoryMatcherEditor();
    private final FilterList<DirectoryEntry> directoryFilterList = new FilterList<DirectoryEntry>(directorySortedList, (MatcherEditor<DirectoryEntry>)directoryMatcherEditor);

    private final JPanel topPanel;

    private final FilterPanel filterPanel;
    private final JPanel playPanel;
    private final StatusPanel statusPanel;

    private final JButton playButton;
    private final JButton stopButton;
    private final JTabbedPane tabbedPane;
    private final JLabel customStationLabel;
    private final JTextField customStationTextField;
    private final JScrollPane directoryTableScrollPane;
    private final JTable directoryTable;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

    public DirectoryView() {
        setLayout(new BorderLayout());

        topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(8, 2, 0, 2));
        topPanel.setLayout(new BorderLayout(2, 2));
        add(topPanel, BorderLayout.NORTH);

        filterPanel = new FilterPanel(directoryMatcherEditor);
        topPanel.add(filterPanel, BorderLayout.CENTER);

        playButton = new JButton("Play");
        playButton.setMnemonic('p');
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(tabbedPane.getSelectedIndex() == 0) {
                    fireEvent(ACTIVATE_COMMAND);
                }
                else {
                    fireEvent(ACTIVATE_CUSTOM_COMMAND);
                }
            }
        });

        stopButton = new JButton("Stop");
        stopButton.setMnemonic('s');
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEvent(STOP_COMMAND);
            }
        });

        playPanel = new JPanel();
        playPanel.setBorder(new TitledBorder(""));
        playPanel.setLayout(new FlowLayout());
        playPanel.add(playButton);
        playPanel.add(stopButton);
        topPanel.add(playPanel, BorderLayout.EAST);

        statusPanel = new StatusPanel();
        add(statusPanel, BorderLayout.SOUTH);

        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBorder(new CompoundBorder(new EmptyBorder(2, 2, 2, 2), new TitledBorder("")));

        tabbedPane = new JTabbedPane();

        directoryTable = new JTable();
        directoryTable.setModel(new DirectoryTableModel(directoryFilterList));
        directoryTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        directoryTableScrollPane = new JScrollPane();
        directoryTableScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        directoryTableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        directoryTableScrollPane.setViewportView(directoryTable);

        tabbedPane.addTab("Directory", directoryTableScrollPane);

        JPanel customPane = new JPanel();
        customPane.setLayout(new MigLayout("insets 16", "[r]rel[l]", ""));

        customStationLabel = new JLabel("Custom Station:");
        customStationLabel.setDisplayedMnemonic('c');
        customStationTextField = new JTextField(60);
        customStationTextField.setFocusAccelerator('m');
        customStationTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = customStationTextField.getText().trim();
                if(address.length() > 0) {
                    fireEvent(ACTIVATE_CUSTOM_COMMAND);
                }
            }
        });

        customPane.add(customStationLabel);
        customPane.add(customStationTextField);

        tabbedPane.addTab("Custom Station", customPane);

        mainContent.add(tabbedPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        TableComparatorChooser.install(directoryTable, directorySortedList, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);

        directoryTable.addMouseListener(new TableMouseListener());
    }

    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public void addDirectoryListSelectionListener(ListSelectionListener listener) {
        directoryTable.getSelectionModel().addListSelectionListener(listener);
    }

    /**
     * Invoked after the application main view has been created and shown.
     * <p>
     * Start a modal dialog box with an indeterminate progress bar while the
     * media directory is loaded and parsed in a background thread.
     */
    public void start() {
        loadDirectory();
    }

    public DirectoryEntry getSelectedEntry() {
        int selectedRow = directoryTable.getSelectedRow();
        if(selectedRow != -1) {
            return directoryFilterList.get(selectedRow);
        }
        else {
            return null;
        }
    }

    public String getCustomEntry() {
        return customStationTextField.getText().trim();
    }

    public void setNowPlaying(DirectoryEntry selectedEntry) {
        statusPanel.setModel(getSelectedEntry());
    }

    public void setNowPlaying(String customEntry) {
        statusPanel.setModel(customEntry);
    }

    private void loadDirectory() {
        executorService.execute(new UpdateDirectoryRunnable(false));
    }

    public void updateDirectory() {
        executorService.execute(new UpdateDirectoryRunnable(true));
    }

    private void fireEvent(String command) {
        ActionListener[] listeners = listenerList.getListeners(ActionListener.class);
        if(listeners.length > 0) {
            ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, command);
            for(int i = listeners.length - 1; i >= 0; i--) {
                listeners[i].actionPerformed(event);
            }
        }
    }

    private class TableMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if(e.getClickCount() == 2) {
                fireEvent(ACTIVATE_COMMAND);
            }
        }
    }

    class DirectoryMatcherEditor extends AbstractMatcherEditor<DirectoryEntry> implements Matcher<DirectoryEntry> {

        private String directory;
        private String name;
        private String address;
        private String type;
        private String genre;

        @Override
        public boolean matches(DirectoryEntry entry) {
            return match(directory, entry.getDirectory()) &&
                match(name     , entry.getName     ()) &&
                match(address  , entry.getUrl      ()) &&
                match(type     , entry.getType     ()) &&
                match(genre    , entry.getGenre    ()
                );
        }

        void setDirectory(String directory) {
            this.directory = directory;
            fireChanged(this);
        }

        void setName(String name) {
            this.name = name;
            fireChanged(this);
        }

        void setAddress(String address) {
            this.address = address;
            fireChanged(this);
        }

        void setType(String type) {
            this.type = type;
            fireChanged(this);
        }

        void setGenre(String genre) {
            this.genre = genre;
            fireChanged(this);
        }

        void clear() {
            directory = name = address = type = genre = null;
            fireMatchAll();
        }

        private boolean match(String s, String value) {
            return s == null || s.trim().length() == 0 || (value != null && value.toLowerCase().contains(s));
        }
    }

    @SuppressWarnings("unchecked")
    private static class DirectoryTableModel extends EventTableModel<DirectoryEntry> {

        private static final long serialVersionUID = 1L;

        public DirectoryTableModel(EventList<DirectoryEntry> source) {
            super(source, new DirectoryTableFormat());
        }
    }

    private class UpdateDirectoryRunnable implements Runnable {

        private final boolean forceUpdate;

        private UpdateDirectoryRunnable(boolean forceUpdate) {
            this.forceUpdate = forceUpdate;
        }

        @Override
        public void run() {
            final JDialog dlg = createLoadingDialog();

            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    dlg.setVisible(true);
                }
            });

            // Clear the current directory
            directoryEventList.getReadWriteLock().writeLock().lock();
            directoryEventList.clear();
            directoryEventList.getReadWriteLock().writeLock().unlock();

            // Load each directory individually, swallowing exceptions so we can
            // continue with subsequent directories even on error...

            try {
                loadDirectory(new IcecastDirectoryService(), new CachedIcecastDirectoryService(), forceUpdate);
            }
            catch(Throwable t) {
                System.err.println("Warning: failed to retrieve Icecast directory");
            }

            try {
                loadDirectory(new ListenLiveDirectoryService(), new CachedListenLiveDirectoryService(), forceUpdate);
            }
            catch(Throwable t) {
                System.err.println("Warning: failed to retrieve ListenLive directory");
            }

            try {
                loadDirectory(new MusicRadioDirectoryService(), new CachedMusicRadioDirectoryService(), forceUpdate);
            }
            catch(Throwable t) {
                System.err.println("Warning: failed to retrieve MusicRadio directory");
            }

            dlg.setVisible(false);
            dlg.dispose();
        }

        /**
         *
         *
         * @return
         */
        private JDialog createLoadingDialog() {
            JFrame parentFrame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, DirectoryView.this);
            final JDialog dlg = new JDialog(parentFrame, "Please wait...", true);
            JPanel cp = new JPanel();
            cp.setLayout(new MigLayout("fill, insets 16", "[c]", ""));
            cp.add(new JLabel("Please wait, loading station directories..."), "wrap");
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            cp.add(progressBar, "growx");
            dlg.setContentPane(cp);
            dlg.setUndecorated(true);
            dlg.setResizable(false);
            dlg.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            dlg.pack();
            dlg.setLocationRelativeTo(parentFrame);
            return dlg;
        }

        private Directory loadDirectory(DirectoryService directoryService, CachedDirectoryService cachedDirectoryService, boolean forceUpdate) {
            // First try loading from the cache...
            Directory directory = null;
            if(!forceUpdate) {
                try {
                    directory = cachedDirectoryService.directory();
                }
                catch(Throwable t) {
                    // Swallow this error so we can continue
                }
            }
            // If the cached directory is not available, go to the source
            if(directory == null) {
                // This may throw an exception
                directory = directoryService.directory();
                cachedDirectoryService.store(directory);
            }
            // Update the UI
            directoryEventList.getReadWriteLock().writeLock().lock();
            directoryEventList.addAll(directory.entries());
            directoryEventList.getReadWriteLock().writeLock().unlock();
            // Finally, return the directory
            return directory;
        }
    }

    /**
     *
     */
    private static class DirectoryTableFormat implements TableFormat<DirectoryEntry> {

        /**
         * Table column header labels.
         */
        private static final String[] COLUMN_LABELS = {"Directory", "Name", "Genre", "Address", "Type"};

        @Override
        public int getColumnCount() {
            return COLUMN_LABELS.length;
        }

        @Override
        public String getColumnName(int col) {
            return COLUMN_LABELS[col];
        }

        @Override
        public Object getColumnValue(DirectoryEntry value, int col) {
            switch(col) {
                case 0:
                    return value.getDirectory();

                case 1:
                    return value.getName();

                case 2:
                    return value.getGenre();

                case 3:
                    return value.getUrl();

                case 4:
                    return value.getType();
            }
            return null;
        }
    }

}