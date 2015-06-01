package kr.ac.yonsei.lis.project.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kr.ac.yonsei.lis.project.model.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class FileUtils {
  public static final Logger LOG = LoggerFactory.getLogger(FileUtils.class);

  public static String copyResources(List<String> resourcePath) throws IOException {
    Path dir = Files.createTempDirectory("lis3813-temp");
    for (String path : resourcePath) {
      final Path destPath = new File(dir.toAbsolutePath().toString(), new File(path).getName()).toPath();
      InputStream inputStream = FileUtils.class.getResourceAsStream(path);
      Files.copy(inputStream, destPath, StandardCopyOption.REPLACE_EXISTING);
      inputStream.close();
    }

    return dir.toAbsolutePath().toString();
  }

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

  public static Iterable<Song> loadSong(String inputPath) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(inputPath)));
    Gson gson = new GsonBuilder().create();

    LinkedList<Song> songs = new LinkedList<Song>();

    while (reader.ready()) {
      songs.addLast(gson.fromJson(reader.readLine(), Song.class));
    }

    return songs;
  }

  public static Map<Integer, Integer> loadTopics(String topicPath) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(new File(topicPath)));
    Map<Integer, Integer> topics = new HashMap<Integer, Integer>();

    while (reader.ready()) {
      String line = reader.readLine();
      String[] split = line.split("\t");
      int id = Integer.valueOf(split[1]);
      int topic = Integer.valueOf(split[2]);

      topics.put(id, topic);
    }

    return topics;
  }
}
