package kr.ac.yonsei.lis.project.model;

import java.util.ArrayList;
import java.util.List;

public class DateExtractor implements KeyExtractor {
  public Iterable<String> extract(Song song) {
    List<String> list = new ArrayList<String>();
    list.add(song.date.substring(0, 4));

    return list;
  }
}
