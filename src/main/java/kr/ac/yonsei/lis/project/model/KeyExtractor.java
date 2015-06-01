package kr.ac.yonsei.lis.project.model;

public interface KeyExtractor {
  Iterable<String> extract(Song song);
}
