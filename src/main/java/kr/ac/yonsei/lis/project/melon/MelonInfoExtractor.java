package kr.ac.yonsei.lis.project.melon;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import kr.ac.yonsei.lis.project.Constants;
import kr.ac.yonsei.lis.project.model.Song;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

public class MelonInfoExtractor {
  private static final Logger LOG = LoggerFactory.getLogger(MelonInfoExtractor.class);

  private LanguageDetector langDetector;
  private TextObjectFactory textObjectFactory;

  public MelonInfoExtractor() {
    langDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
        .withProfiles(Constants.LANG_PROFILES).build();
    textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
  }

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
    TextObject lyricsObj = textObjectFactory.forText(lyrics);
    LdLocale language = langDetector.detect(lyricsObj).orNull();
    if (language == null || !"ko".equalsIgnoreCase(language.getLanguage())) {
      LOG.info("Language is not Korean. (id: " + id + ")");
      return null;
    }

    // extract release date
    Elements dateElem = document.select("dl.song_info > dd:nth-child(6)");
    String dateText = dateElem.text();
    String date;
    Matcher matcher = Constants.DATE_MONTH_PATTERN.matcher(dateText);
    if (!matcher.find()) {
      matcher = Constants.DATE_YEAR_PATTERN.matcher(dateText);
      if (!matcher.find()) {
        LOG.info("Date format is invalid. (id: " + id + ")");
        return null;
      }

      date = matcher.group("year");
    } else {
      date = matcher.group("year") + matcher.group("month");
    }

    // extract title
    Elements titleElem = document.select("p.songname");
    String title = titleElem.first().ownText();

    // extract artists
    Set<String> artists = new HashSet<String>();
    Elements artistElem = document.select("dl.song_info a.atistname");
    for (Element element : artistElem) {
      artists.add(element.attr("title"));
    }
    if (artists.size() == 0) {
      artists.add("NO ARTIST");
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
    if (lyricists.size() == 0) {
      lyricists.add("NO LYRICISTS");
    }

    return new Song(id, artists, lyricists, title, album, lyrics, date, genre);
  }
}
