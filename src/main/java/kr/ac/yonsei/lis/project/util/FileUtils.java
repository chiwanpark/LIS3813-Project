package kr.ac.yonsei.lis.project.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class FileUtils {
  public static String copyResources(List<String> resourcePath) throws IOException{
    Path dir = Files.createTempDirectory("lis3813-temp");
    for (String path : resourcePath) {
      final Path destPath = new File(dir.toAbsolutePath().toString(), new File(path).getName()).toPath();
      InputStream inputStream = FileUtils.class.getResourceAsStream(path);
      Files.copy(inputStream, destPath, StandardCopyOption.REPLACE_EXISTING);
      inputStream.close();
    }

    return dir.toAbsolutePath().toString();
  }
}
