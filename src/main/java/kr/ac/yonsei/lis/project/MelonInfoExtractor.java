package kr.ac.yonsei.lis.project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class MelonInfoExtractor {
  private static final Calendar CALENDAR = Calendar.getInstance();
  private static final Logger LOG = LoggerFactory.getLogger(MelonInfoExtractor.class);

  public Song extractSongFromHTML(File file) {
    Document document;

    try {
      document = Jsoup.parse(file, "utf-8");
    } catch (IOException e) {
      LOG.error("Cannot load html file: " + file.getAbsolutePath());
      return null;
    }

    // extract id
    Elements idElem = document.select("button.btn_like_b");
    int id = Integer.valueOf(idElem.attr("data-song-no"));

    // extract lyrics
    Elements lyricsElem = document.select("div#d_video_summary");
    String lyrics = lyricsElem.text();
    if (lyrics == null || "".equals(lyrics)) {
      LOG.info("Lyrics doesn't exists. (id: " + id + ")");
      return null;
    }

    // extract release date
    Elements dateElem = document.select("dl.song_info > dd:nth-child(6)");
    Matcher matcher = Constants.DATE_PATTERN.matcher(dateElem.text());
    if (!matcher.find()) {
      LOG.info("Date format is invalid. (id: " + id + ")");
      return null;
    }
    int year = Integer.valueOf(matcher.group("year"));
    int month = Integer.valueOf(matcher.group("month")) - 1;
    CALENDAR.set(year, month, 1, 0, 0, 0);
    Date date = CALENDAR.getTime();

    // extract title
    Elements titleElem = document.select("p.songname");
    String title = titleElem.first().ownText();

    // extract artists
    Set<String> artists = new HashSet<String>();
    Elements artistElem = document.select("dl.song_info a.atistname");
    for (Element element : artistElem) {
      artists.add(element.attr("title"));
    }

    // extract album
    Elements albumElem = document.select("dl.song_info > dd:nth-child(4) > a");
    String album = albumElem.attr("title");

    // extract genre
    Elements genreElem = document.select("dl.song_info > dd:nth-child(8)");
    String genre = genreElem.text().toLowerCase();

    // extract lyricists
    Set<String> lyricists = new HashSet<String>();
    Elements lyricistsElem = document.select("div.box_lyric > a");
    for (Element element : lyricistsElem) {
      lyricists.add(element.attr("title"));
    }

    return new Song(id, artists, lyricists, title, album, lyrics, date, genre);
  }
}
