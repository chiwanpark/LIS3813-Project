package kr.ac.yonsei.lis.project;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class MelonInfoCrawler {
  private static final Calendar CALENDAR = Calendar.getInstance();
  private static final Logger LOG = LoggerFactory.getLogger(MelonInfoCrawler.class);

  private CloseableHttpClient httpClient;

  public MelonInfoCrawler() {
    RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
    httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).build();
  }

  public String downloadInfoPage(int id) {
    String url = Constants.MELON_URL + id;
    HttpGet request = new HttpGet(url);
    HttpResponse response;
    try {
      response = httpClient.execute(request);
      if (response.getStatusLine().getStatusCode() != 200) {
        LOG.error("Wrong status code from server. <song id: " + id + ">");
        return null;
      }

      String html = EntityUtils.toString(response.getEntity());
      if (html.contains("존재하지 않는 곡 정보입니다.")) {
        LOG.info("Song information doesn't exists. (id: " + id + ")");
        return null;
      }

      return html;
    } catch (IOException e) {
      LOG.error("IOException on downloading. <song id: " + id + ">", e);
      return null;
    }
  }

  public Song extractSongFromHTML(String html) {
    Document document = Jsoup.parse(html, "utf-8");

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
