vlcj-radio-demo
===============

Some code archaeology here...

A demo application, updated from nearly ten years ago!, that uses vlcj to play streams from
various internet radio stations.

The application will search/scrape some internet radio station directories to find the stations
which you can then search and filter in the UI.

By default these directories are cached on first access, you can manually update them, but
really they don't change all that often.

Do not abuse the directory providers with excessive update requests.

The vlcj code here is minimial - the vast majority of the code in here deals with the user
interface and the station directory access.

Previously with vlcj-3 you would have to deal with playlists and sub-items.

With vlcj-4, now all you need do is play the station MRL like any other local file MRL and it
will just work.

Note that some streams may fail to start, there is nothing necessarily wrong, sometimes streams
go away or are simply not broadcasting all of the time.
