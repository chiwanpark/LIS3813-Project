package kr.ac.yonsei.lis.project.model;

import java.util.ArrayList;
import java.util.List;

public class ArtistsExtractor implements KeyExtractor {
  public Iterable<String> extract(Song song) {
    List<String> artists = new ArrayList<String>();
    for (String artist : song.artists) {
      for (String parsed : artist.split(",")) {
        artists.add(parsed.trim());
      }
    }
    return artists;
  }
}
