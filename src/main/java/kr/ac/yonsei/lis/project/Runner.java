package kr.ac.yonsei.lis.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.ac.yonsei.lis.project.melon.MelonInfoCrawler;
import kr.ac.yonsei.lis.project.melon.MelonInfoExtractor;
import kr.ac.yonsei.lis.project.melon.MelonKeywordExtractor;
import kr.ac.yonsei.lis.project.model.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
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
    } else if ("keyword-extractor".equals(args[0])) {
      runKeywordExtractor(Arrays.copyOfRange(args, 1, args.length));
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

  public static void runKeywordExtractor(String... args) throws Exception {
    if (args.length != 2) {
      LOG.error("NounExtractor needs input path, output path parameters");
      return;
    }

    if (!validInputFile(args[0]) || !validOutput(args[1])) {
      return;
    }

    int count = 0;
    MelonKeywordExtractor extractor = new MelonKeywordExtractor();
    Gson gson = new GsonBuilder().create();

    BufferedReader reader = new BufferedReader(new FileReader(new File(args[0])));
    BufferedWriter writer = new BufferedWriter(new FileWriter(new File(args[1])));

    while(reader.ready()) {
      Song song = gson.fromJson(reader.readLine(), Song.class);
      List<String> nouns = extractor.extractKeyword(song.lyrics);
      StringBuilder builder = new StringBuilder();

      // song id
      builder.append(song.id).append('\t');

      // artists
      for (String artist : song.artists) {
        builder.append(artist).append(',');
      }
      builder.deleteCharAt(builder.length() - 1);
      builder.append('\t');

      // release date
      builder.append(song.date);
      if (song.date.length() == 4) {
        builder.append("00");
      }
      builder.append('\t');

      // lyricists
      for (String lyricist : song.lyricists) {
        builder.append(lyricist).append(',');
      }
      builder.deleteCharAt(builder.length() - 1);
      builder.append('\t');

      // nouns
      for (String noun : nouns) {
        builder.append(noun).append('|');
      }
      builder.deleteCharAt(builder.length() - 1);

      builder.append('\n');
      writer.write(builder.toString());

      ++count;
      if (count % 5000 == 0) {
        LOG.info("Noun Extraction Processing (" + count + ")");
      }
    }

    reader.close();
    writer.close();
  }
}
