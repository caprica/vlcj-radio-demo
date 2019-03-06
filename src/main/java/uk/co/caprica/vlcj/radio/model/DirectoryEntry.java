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

package uk.co.caprica.vlcj.radio.model;

/**
 * Specification for a media station directory entry.
 */
public interface DirectoryEntry extends Comparable<DirectoryEntry>{

    /**
     * Get the directory.
     *
     * @return directory
     */
    String getDirectory();

    /**
     * Get the name.
     *
     * @return name
     */
    String getName();

    /**
     * Get the player URL.
     *
     * @return URL
     */
    String getUrl();

    /**
     * Get the (media) type.
     *
     * @return type
     */
    String getType();

    /**
     * Get the bit-rate of the stream.
     *
     * @return bit rate
     */
    String getBitRate();

    /**
     * Get the number of audio channels in the stream.
     *
     * @return number of channels
     */
    int getChannels();

    /**
     * Get the sample-rate for the stream.
     *
     * @return sample-rate
     */
    int getSampleRate();

    /**
     * Get the genre.
     *
     * @return genre
     */
    String getGenre();

    /**
     * Get the name of the currently playing item.
     *
     * @return item name
     */
    String getNowPlaying();

}