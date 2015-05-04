package kr.ac.yonsei.lis.project;

import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class Constants {
  public static final String MELON_URL = "http://www.melon.com/song/detail.htm?songId=";
  public static final Pattern DATE_PATTERN =
      Pattern.compile("(?<year>[0-9]{4})\\.(?<month>[0-9]{2})");
  public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMM");
}
