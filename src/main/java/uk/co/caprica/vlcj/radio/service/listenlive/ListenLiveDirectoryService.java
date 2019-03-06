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

package uk.co.caprica.vlcj.radio.service.listenlive;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlBold;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTableCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;
import uk.co.caprica.vlcj.radio.model.Directory;
import uk.co.caprica.vlcj.radio.service.DirectoryService;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of a streaming media station directory service that gets the
 * directory of streams from the ListenLive web-site.
 * <p>
 * There is no web-service API or XML feed so this implementation scrapes all 
 * of the pages directly.
 * <p>
 * This directory provider returns some directory entries that are not simply
 * "http://" URLs and instead launch another web-page to play the stream - 
 * these stations therefore can not be played directly and require special
 * handling.
 */
public class ListenLiveDirectoryService implements DirectoryService {

    @Override
    public Directory directory() {
        try {
            WebClient webClient = new WebClient();
            webClient.getOptions().setCssEnabled(false);
            webClient.getOptions().setJavaScriptEnabled(false);

            HtmlPage page = webClient.getPage("http://www.listenlive.eu/uk.html");

            List<ListenLiveDirectoryEntry> entries = new ArrayList<ListenLiveDirectoryEntry>(200);

            List<?> elements = page.getByXPath("//table[@id='thetable3'][1]/tbody/tr[position()>1]");
            for(Object element : elements) {
                HtmlTableRow tr = (HtmlTableRow)element;

                List<?> tds = tr.getByXPath("td");
                HtmlTableCell td;
                td = (HtmlTableCell)tds.get(0);
                String name = ((HtmlBold)td.getByXPath("descendant::a/b").get(0)).asText();

                td = (HtmlTableCell)tds.get(4);
                String genre = td.asText();

                // Individual links/bit-rates all in the same table cell
                td = (HtmlTableCell)tds.get(3);

                List<?> anchors = td.getByXPath("a");
                for(int i = 0; i < anchors.size(); i++) {
                    HtmlAnchor a = (HtmlAnchor)anchors.get(i);
                    String bitRate = a.asText();
                    String url = a.getAttribute("href");

                    // FIXME
                    // for now these will be removed
                    if(!url.startsWith("javascript")) {
                        entries.add(new ListenLiveDirectoryEntry(name, url, "", bitRate, genre));
                    }
                }
            }

            webClient.close();
            return new ListenLiveDirectory(entries);
        }
        catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to get directory", e);
        }
    }

}
