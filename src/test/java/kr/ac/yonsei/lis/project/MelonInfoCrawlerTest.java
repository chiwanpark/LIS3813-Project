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
}
