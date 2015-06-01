package kr.ac.yonsei.lis.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.ac.yonsei.lis.project.analysis.StatisticsCalculator;
import kr.ac.yonsei.lis.project.analysis.TopicModelingAnalysis;
import kr.ac.yonsei.lis.project.melon.MelonInfoCrawler;
import kr.ac.yonsei.lis.project.melon.MelonInfoExtractor;
import kr.ac.yonsei.lis.project.model.*;
import kr.ac.yonsei.lis.project.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

public class Runner {
  private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

  public static void main(String... args) throws Exception {
    if (args.length == 0) {
      LOG.error("Running mode must be given!");
      return;
    }

    if ("crawler".equals(args[0])) {
      runCrawler(Arrays.copyOfRange(args, 1, args.length));
    } else if ("extractor".equals(args[0])) {
      runExtractor(Arrays.copyOfRange(args, 1, args.length));
    } else if ("topicModeling".equals(args[0])) {
      runTopicModelingAnalysis(Arrays.copyOfRange(args, 1, args.length));
    } else if ("statisticsByKey".equals(args[0])) {
      runStatisticsByKey(Arrays.copyOfRange(args, 1, args.length));
    }
  }

  public static void runCrawler(String... args) throws Exception {
    if (args.length != 3) {
      LOG.error("Crawler needs startId, endId, output path parameters!");
      return;
    }

    int startId = Integer.valueOf(args[0]);
    int endId = Integer.valueOf(args[1]);
    MelonInfoCrawler crawler = new MelonInfoCrawler();
    for (int i = startId; i <= endId; ++i) {
      String html = crawler.downloadInfoPage(i);
      if (html == null || "".equals(html)) {
        continue;
      }

      BufferedWriter writer = new BufferedWriter(new FileWriter(args[2] + File.separator + i + ".html"));
      writer.write(html);
      writer.close();
    }
  }

  public static void runExtractor(String... args) throws Exception {
    if (args.length != 2) {
      LOG.error("Extractor needs input path, output path parameters!");
      return;
    }

    if (!FileUtils.validInputDirectory(args[0]) || !FileUtils.validOutput(args[1])) {
      return;
    }

    MelonInfoExtractor extractor = new MelonInfoExtractor();

    BufferedWriter writer = new BufferedWriter(new FileWriter(args[1]));
    Gson gson = new GsonBuilder().create();

    for (String path : new File(args[0]).list()) {
      if (!path.endsWith(".html")) {
        continue;
      }

      Song song = null;
      try {
        song = extractor.extractSongFromHTML(new File(new File(args[0]).getAbsolutePath() + File.separator + path));
      } catch (Exception e) {
        LOG.error("Exception caught!", e);
      }

      if (song == null) {
        continue;
      }

      String jsonified = gson.toJson(song);
      writer.write(jsonified);
      writer.newLine();
    }

    writer.close();
  }

  public static void runTopicModelingAnalysis(String... args) throws Exception {
    if (args.length != 7) {
      LOG.error("Topic modeling needs input path, output path, number of topic, number of top words, number " +
          "of iteration, number of thread, and date to analysis parameter");
      return;
    }

    if (!FileUtils.validInputFile(args[0]) || !FileUtils.validInputDirectory(args[1])) {
      return;
    }

    int numTopics = Integer.valueOf(args[2]);
    int numWords = Integer.valueOf(args[3]);
    int numIterations = Integer.valueOf(args[4]);
    int numThreads = Integer.valueOf(args[5]);
    int date = Integer.valueOf(args[6]);

    TopicModelingAnalysis analysis = new TopicModelingAnalysis();
    Iterable<Song> songs = FileUtils.loadSong(args[0]);

    analysis.runAnalysis(songs, args[1], numTopics, numIterations, numThreads, numWords, date);
  }

  public static void runStatisticsByKey(String... args) throws Exception {
    if (args.length != 4) {
      LOG.error("Calculation of Statistics needs path of topic list, path of song information, key of statistics," +
          " and output path parameters");
      return;
    }

    if (!FileUtils.validInputFile(args[0]) || !FileUtils.validInputFile(args[1]) || !FileUtils.validOutput(args[3])) {
      return;
    }


    KeyExtractor keyExtractor;

    Map<Integer, Integer> topics = FileUtils.loadTopics(args[0]);
    Iterable<Song> songs = FileUtils.loadSong(args[1]);

    if ("artists".equals(args[2])) {
      keyExtractor = new ArtistsExtractor();
    } else if ("lyricists".equals(args[2])) {
      keyExtractor = new LyricistsExtractor();
    } else if ("date".equals(args[2])) {
      keyExtractor = new DateExtractor();
    } else {
      return;
    }

    Map<String, Map<Integer, Integer>> statistics = new StatisticsCalculator().calculate(topics, songs, keyExtractor);

    BufferedWriter writer = new BufferedWriter(new FileWriter(args[3]));
    for (String key : statistics.keySet()) {
      List<Map.Entry<Integer, Integer>> topicListByKey =
          new ArrayList<Map.Entry<Integer, Integer>>(statistics.get(key).entrySet());

      Collections.sort(topicListByKey, new Comparator<Map.Entry<Integer, Integer>>() {
        public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
          return o2.getValue() - o1.getValue();
        }
      });

      writer.write(key);
      writer.write('\t');
      for (Map.Entry<Integer, Integer> entry : topicListByKey) {
        writer.write('(');
        writer.write(entry.getKey().toString());
        writer.write(", ");
        writer.write(entry.getValue().toString());
        writer.write(") ");
      }
      writer.write('\n');
    }

    writer.close();
  }
}
