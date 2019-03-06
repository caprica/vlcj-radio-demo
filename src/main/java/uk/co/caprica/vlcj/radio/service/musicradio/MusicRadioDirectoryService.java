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

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import uk.co.caprica.vlcj.radio.model.Directory;
import uk.co.caprica.vlcj.radio.service.DirectoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a streaming media station directory service that gets the
 * server directory from the musicradio.com web site.
 */
public class MusicRadioDirectoryService implements DirectoryService {

    private static final String DIRECTORY_URL = "http://media-ice.musicradio.com";

    @Override
    public Directory directory() {
        try {
            WebClient webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            HtmlPage page = webClient.getPage(DIRECTORY_URL);

            List<MusicRadioDirectoryEntry> entries = new ArrayList<MusicRadioDirectoryEntry>(200);

            List<?> elements = page.getByXPath("//div[@class='newscontent']");
            for(Object element : elements) {
                HtmlDivision div = (HtmlDivision)element;

                HtmlAnchor streamLink = (HtmlAnchor)div.getByXPath("div[@class='streamheader']/table/tbody/tr[1]/td[2]/a[1]").get(0);

                List<?> tds = div.getByXPath("table/tbody/tr/td[@class='streamdata']");

                String name = ((HtmlTableDataCell)tds.get(0)).asText();
                String url = DIRECTORY_URL + streamLink.getAttribute("href");
                String type = ((HtmlTableDataCell)tds.get(2)).asText();
                String genre = ((HtmlTableDataCell)tds.get(3)).asText();

                MusicRadioDirectoryEntry entry = new MusicRadioDirectoryEntry(name, url, type, genre);
                entries.add(entry);
            }

            webClient.close();
            return new MusicRadioDirectory(entries);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get directory", e);
        }
    }

}