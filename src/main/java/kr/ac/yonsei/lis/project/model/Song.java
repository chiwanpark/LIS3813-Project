package kr.ac.yonsei.lis.project.model;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Song {
  public int id;
  public Set<String> artists;
  public Set<String> lyricists;
  public String title;
  public String album;
  public String lyrics;
  public String date;
  public String genre;

  public Song() {
  }

  public Song(int id, Set<String> artists, Set<String> lyricists, String title, String album, String lyrics,
              String date, String genre) {
    this.id = id;
    this.artists = new HashSet<String>(artists);
    this.lyricists = new HashSet<String>(lyricists);
    this.title = title;
    this.album = album;
    this.lyrics = lyrics;
    this.date = date;
    this.genre = genre;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("Song{");

    builder.append("id=").append(id).append(", artists=(");
    Iterator<String> iterator = artists.iterator();
    while (iterator.hasNext()) {
      builder.append(iterator.next());
      if (iterator.hasNext()) {
        builder.append(", ");
      }
    }
    builder.append("), title=").append(title).append(", album=").append(album).append(", genre=").append(genre)
        .append(", date=").append(date).append(", lyricists=(");
    iterator = lyricists.iterator();
    while (iterator.hasNext()) {
      builder.append(iterator.next());
      if (iterator.hasNext()) {
        builder.append(", ");
      }
    }

    return builder.toString();
  }
}
