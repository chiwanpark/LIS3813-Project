package kr.ac.yonsei.lis.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.ac.yonsei.lis.project.melon.MelonInfoCrawler;
import kr.ac.yonsei.lis.project.melon.MelonInfoExtractor;
import kr.ac.yonsei.lis.project.model.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

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

    MelonInfoExtractor extractor = new MelonInfoExtractor();
    File inputPath = new File(args[0]);
    if (!inputPath.isDirectory() || !inputPath.canRead()) {
      LOG.error("Input path is invalid!");
      return;
    }

    File outputPath = new File(args[1]);
    if (outputPath.exists()) {
      LOG.error("Output path is invalid!");
      return;
    }

    BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT.toPattern()).create();

    for (String path : inputPath.list()) {
      if (!path.endsWith(".html")) {
         continue;
      }

      Song song = extractor.extractSongFromHTML(new File(inputPath.getAbsolutePath() + File.separator + path));
      if (song == null) {
        continue;
      }

      String jsonified = gson.toJson(song);
      writer.write(jsonified);
    }

    writer.close();
  }
}
