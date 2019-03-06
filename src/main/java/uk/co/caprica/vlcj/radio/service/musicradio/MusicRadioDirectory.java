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

package uk.co.caprica.vlcj.radio.service.musicradio;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import uk.co.caprica.vlcj.radio.model.Directory;
import uk.co.caprica.vlcj.radio.model.DirectoryEntry;

import java.util.Collections;
import java.util.List;

/**
 * Implementation of a directory.
 */
@Root(name = "directory")
public class MusicRadioDirectory implements Directory {

    /**
     * Collection of directory entries.
     */
    @ElementList(entry = "entry", inline = true)
    private List<MusicRadioDirectoryEntry> entries;

    /**
     * Default constructor (required for XML binding).
     */
    public MusicRadioDirectory() {
    }

    /**
     * Create a directory.
     *
     * @param entries directory entries.
     */
    public MusicRadioDirectory(List<MusicRadioDirectoryEntry> entries) {
        this.entries = Collections.unmodifiableList(entries);
    }

    @Override
    public List<? extends DirectoryEntry> entries() {
        return entries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(4096);
        for(int i = 0; i < entries.size(); i++) {
            if(sb.length() > 0) {
                sb.append('\n');
            }
            sb.append(entries.get(i));
        }
        return sb.toString();
    }

}