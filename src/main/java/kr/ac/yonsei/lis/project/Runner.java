package kr.ac.yonsei.lis.project;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
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
    Gson gson = new GsonBuilder().setDateFormat(Constants.DATE_FORMAT.toPattern()).create();
    BufferedWriter writer = new BufferedWriter(new FileWriter(args[2]));
    for (int i = startId; i <= endId; ++i) {
      String html = crawler.downloadInfoPage(i);
      if (html == null || "".equals(html)) {
        continue;
      }
      Song song = crawler.extractSongFromHTML(html);
      if (song == null) {
        continue;
      }

      writer.write(gson.toJson(song));
      writer.newLine();
      if (i % 1000 == 0) {
        writer.flush();
      }
    }
    writer.close();
  }
}
