package kr.ac.yonsei.lis.project.analysis;

import kr.ac.yonsei.lis.project.model.KeyExtractor;
import kr.ac.yonsei.lis.project.model.Song;

import java.util.HashMap;
import java.util.Map;

public class StatisticsCalculator {
  public Map<String, Map<Integer, Integer>> calculate(Map<Integer, Integer> topics, Iterable<Song> songs,
                                                      KeyExtractor extractor) {
    Map<String, Map<Integer, Integer>> statistics = new HashMap<String, Map<Integer, Integer>>();

    for (Song song : songs) {
      Iterable<String> keys = extractor.extract(song);
      int topic = topics.get(song.id);
      for (String key : keys) {
        if ("".equals(key)) {
          continue;
        }

        Map<Integer, Integer> countMap;
        if (statistics.containsKey(key)) {
          countMap = statistics.get(key);
        } else {
          countMap = new HashMap<Integer, Integer>();
        }

        if (countMap.containsKey(topic)) {
          countMap.put(topic, countMap.get(topic) + 1);
        } else {
          countMap.put(topic, 1);
        }

        statistics.put(key, countMap);
      }
    }

    return statistics;
  }
}
