package kr.ac.yonsei.lis.project;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class MelonInfoExtractorTest {
  private MelonInfoExtractor extractor;

  @Before
  public void setUp() {
    extractor = new MelonInfoExtractor();
  }

  @Test
  public void testExtractSongFromHTML() {
    int songId = 3843474;
    URL path = this.getClass().getResource("/3843474.html");
    File file = new File(path.getFile());

    Song song = extractor.extractSongFromHTML(file);

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
    URL path = this.getClass().getResource("/956055.html");
    File file = new File(path.getFile());

    Song song = extractor.extractSongFromHTML(file);

    assertNull("Result with song that doesn't have lyrics must be null.", song);
  }
}
