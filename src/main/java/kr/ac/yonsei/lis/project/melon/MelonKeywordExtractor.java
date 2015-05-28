package kr.ac.yonsei.lis.project.melon;

import kr.ac.yonsei.lis.project.util.FileUtils;
import kr.co.shineware.nlp.komoran.core.analyzer.Komoran;
import kr.co.shineware.util.common.model.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MelonKeywordExtractor {
  private Komoran komoran;

  public MelonKeywordExtractor() {
    // copy komoran model files to temp directory
    List<String> resources = new ArrayList<String>();
    resources.add("/komoran-models/irregular.model");
    resources.add("/komoran-models/observation.model");
    resources.add("/komoran-models/pos.table");
    resources.add("/komoran-models/transition.model");

    try {
      String destPath = FileUtils.copyResources(resources);
      komoran = new Komoran(destPath);
    } catch (IOException e) {
      throw new RuntimeException("Cannot load komoran model file");
    }
  }

  @SuppressWarnings("unchecked")
  public List<String> extractKeyword(String lyrics) {
    List<String> result = new ArrayList<String>();

    List<List<Pair<String, String>>> komoranResult = komoran.analyze(lyrics);
    for (List<Pair<String, String>> eojeol : komoranResult) {
      for (Pair<String, String> wordMorph : eojeol) {
        final String tag = wordMorph.getSecond();
        if ("NNG".equals(tag) || "NNP".equals(tag) || "NP".equals(tag)) {
          result.add(wordMorph.getFirst());
        } else if ("VV".equals(tag) || "VA".equals(tag)) {
          result.add(wordMorph.getFirst() + "다");
        }
      }
    }

    return result;
  }
}
