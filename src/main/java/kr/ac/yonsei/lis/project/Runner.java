package kr.ac.yonsei.lis.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.ac.yonsei.lis.project.analysis.TopicModelingAnalysis;
import kr.ac.yonsei.lis.project.melon.MelonInfoCrawler;
import kr.ac.yonsei.lis.project.melon.MelonInfoExtractor;
import kr.ac.yonsei.lis.project.model.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Runner {
  private static final Logger LOG = LoggerFactory.getLogger(Runner.class);

  public static boolean validInputDirectory(String input) {
    File inputPath = new File(input);
    if (!inputPath.isDirectory() || !inputPath.canRead()) {
      LOG.error("Input path is invalid!");
      return false;
    }

    return true;
  }

  public static boolean validInputFile(String input) {
    File inputPath = new File(input);
    if (!inputPath.canRead()) {
      LOG.error("Input path is invalid!");
      return false;
    }

    return true;
  }

  public static boolean validOutput(String output) {
    File outputPath = new File(output);
    if (outputPath.exists()) {
      LOG.error("Output path is invalid!");
      return false;
    }

    return true;
  }

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

    if (!validInputDirectory(args[0]) || !validOutput(args[1])) {
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
    if (args.length != 6) {
      LOG.error("Topic modeling needs input path, output path, number of topic, number of top words, number " +
          "of iteration, and number of thread parameters");
      return;
    }

    if (!validInputFile(args[0]) || !validInputDirectory(args[1])) {
      return;
    }

    int numTopics = Integer.valueOf(args[2]);
    int numWords = Integer.valueOf(args[3]);
    int numIterations = Integer.valueOf(args[4]);
    int numThreads = Integer.valueOf(args[5]);

    List<Song> songs = new LinkedList<Song>();
    Gson gson = new GsonBuilder().create();
    TopicModelingAnalysis analysis = new TopicModelingAnalysis();
    BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));

    while (reader.ready()) {
      Song song = gson.fromJson(reader.readLine(), Song.class);
      songs.add(song);
    }

    analysis.runAnalysis(songs, args[1], numTopics, numIterations, numThreads, numWords);
  }
}
