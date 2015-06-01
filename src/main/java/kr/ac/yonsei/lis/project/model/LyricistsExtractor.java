package kr.ac.yonsei.lis.project.model;

import java.util.ArrayList;
import java.util.List;

public class LyricistsExtractor implements KeyExtractor {
  public Iterable<String> extract(Song song) {
    List<String> lyricists = new ArrayList<String>();
    for (String lyricist : song.lyricists) {
      for (String parsed : lyricist.split(",")) {
        lyricists.add(parsed.trim());
      }
    }
    return lyricists;
  }
}
