package kr.ac.yonsei.lis.project.model;

import java.util.Comparator;
import java.util.Map;

public class ValueDescComparator<T> implements Comparator<Map.Entry<T, Integer>> {
  public int compare(Map.Entry<T, Integer> o1, Map.Entry<T, Integer> o2) {
    return o2.getValue() - o1.getValue();
  }
}
