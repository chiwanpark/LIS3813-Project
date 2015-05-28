package kr.ac.yonsei.lis.project.melon;

import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MelonKeywordExtractor {
  private Komoran komoran;

  public MelonKeywordExtractor() {
    final URL komoranResources = MelonKeywordExtractor.class.getResource("/komoran-models");
    if (komoranResources == null) {
      throw new RuntimeException("Cannot load komoran model file");
    }

    komoran = new Komoran(komoranResources.getFile());
  }

  public List<String> extractKeyword(String lyrics) {
    List<String> result = new ArrayList<String>();

    List<List<Pair<String, String>>> komoranResult = komoran.analyze(lyrics);
    for (List<Pair<String, String>> eojeol : komoranResult) {
      for (Pair<String, String> wordMorph : eojeol) {
        final String tag = wordMorph.getSecond();
        if ("NNG".equals(tag) || "NNP".equals(tag) || "NP".equals(tag) ||
            "VV".equals(tag) || "VA".equals(tag)) {
          result.add(wordMorph.getFirst());
        }
      }
    }

    return result;
  }
}
