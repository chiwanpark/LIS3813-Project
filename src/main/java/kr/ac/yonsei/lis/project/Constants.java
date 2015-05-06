package kr.ac.yonsei.lis.project;

import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Constants {
  public static final String MELON_URL = "http://www.melon.com/song/detail.htm?songId=";
  public static final Pattern DATE_MONTH_PATTERN = Pattern.compile("(?<year>[0-9]{4})\\.(?<month>[0-9]{2})");
  public static final Pattern DATE_YEAR_PATTERN = Pattern.compile("(?<year>[0-9]{4})");
  public static final List<LanguageProfile> LANG_PROFILES;

  static {
    List<LanguageProfile> langProfiles;

    try {
      langProfiles = new LanguageProfileReader().readAllBuiltIn();
    } catch (IOException e) {
      langProfiles = new ArrayList<LanguageProfile>();
    }

    LANG_PROFILES = langProfiles;
  }
}
