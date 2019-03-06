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

package uk.co.caprica.vlcj.radio.service;

import org.simpleframework.xml.core.Persister;
import uk.co.caprica.vlcj.radio.model.Directory;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Implementation of a directory service that operates on a local file cache.
 */
public class CachedDirectoryService implements DirectoryService {

    /**
     * Concrete type of directory (required for proper XML binding).
     */
    private final Class<? extends Directory> type;

    /**
     * Name of the local cache file.
     */
    private final String cacheFileName;

    /**
     * XML binding parser.
     */
    private final Persister persister;

    /**
     * Create a directory service component.
     *
     * @param type concrete type of the directory
     * @param cacheFileName name of the local cache file
     */
    public CachedDirectoryService(Class<? extends Directory>type, String cacheFileName) {
        this.type = type;
        this.cacheFileName = cacheFileName;
        this.persister = new Persister();
    }

    @Override
    public Directory directory() {
        File cacheFile = getCacheFile();
        if(cacheFile.exists()) {
            try {
                return persister.read(type, cacheFile);
            }
            catch(Exception e) {
                throw new RuntimeException("Failed to read directory", e);
            }
        }
        return null;
    }

    /**
     * Store a directory in the cache.
     *
     * @param directory directory
     */
    public final void store(Directory directory) {
        BufferedOutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(getCacheFile()));
            persister.write(directory, out);
        }
        catch(Exception e) {
            throw new RuntimeException("Failed to store directory", e);
        }
        finally {
            if(out != null) {
                try {
                    out.close();
                }
                catch(IOException e) {
                }
            }
        }
    }

    /**
     * Get the local cache file.
     *
     * @return file
     */
    private File getCacheFile() {
        File userHomeDirectory = new File(System.getProperty("user.home"));
        File cacheDirectory = new File(userHomeDirectory, "vlcj-radio");
        if(!cacheDirectory.exists()) {
            cacheDirectory.mkdirs();
        }
        return new File(cacheDirectory, cacheFileName);
    }

}
