package kr.ac.yonsei.lis.project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MelonInfoCrawlerTest {
  private MelonInfoCrawler crawler;

  @Before
  public void setUp() {
    crawler = new MelonInfoCrawler();
  }

  @Test
  public void testDownloadInvalidSongId() {
    int songId = 0;
    String result = crawler.downloadInfoPage(songId);

    assertNull("Result with invalid song id must be null.", result);
  }

  @Test
  public void testDownloadValidSongId() {
    int songId = 3843474;
    String result = crawler.downloadInfoPage(songId);

    assertTrue("Result with valid song id (3843474) must contain string '윤하'", result.contains("윤하"));
  }

  @Test
  public void testExtractSongFromHTML() {
    int songId = 3843474;
    String result = crawler.downloadInfoPage(songId);
    Song song = crawler.extractSongFromHTML(result);

    assertEquals(1, song.artists.size());
    assertTrue(song.artists.contains("윤하"));

    assertEquals(3, song.lyricists.size());
    assertTrue(song.lyricists.contains("윤하"));
    assertTrue(song.lyricists.contains("스코어"));
    assertTrue(song.lyricists.contains("김병석"));

    assertEquals("Supersonic", song.album);
    assertEquals("201207", Constants.DATE_FORMAT.format(song.date));
    assertEquals("rock", song.genre);
    assertEquals(songId, song.id);
  }

  @Test
  public void testExtractSongFromHTMLWithoutLyrics() {
    int songId = 956055;
    String result = crawler.downloadInfoPage(songId);
    Song song = crawler.extractSongFromHTML(result);

    assertNull("Result with song that doesn't have lyrics must be null.", song);
  }
}
