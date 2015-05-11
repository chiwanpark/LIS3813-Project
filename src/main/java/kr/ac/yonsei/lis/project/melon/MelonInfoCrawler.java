package kr.ac.yonsei.lis.project.melon;

import kr.ac.yonsei.lis.project.Constants;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MelonInfoCrawler {
  private static final Logger LOG = LoggerFactory.getLogger(MelonInfoCrawler.class);

  private CloseableHttpClient httpClient;

  public MelonInfoCrawler() {
    RequestConfig globalConfig = RequestConfig.custom()
        .setCookieSpec(CookieSpecs.STANDARD)
        .setConnectionRequestTimeout(5000)
        .build();
    httpClient = HttpClients.custom()
        .setDefaultRequestConfig(globalConfig)
        .setUserAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 " +
            "Safari/537.36")
        .build();
  }

  public String downloadInfoPage(int id) {
    String url = Constants.MELON_URL + id;
    HttpGet request = new HttpGet(url);
    HttpResponse response;
    try {
      response = httpClient.execute(request);
      if (response.getStatusLine().getStatusCode() != 200) {
        LOG.error("Wrong status code from server. <song id: " + id + ">");
        return null;
      }

      String html = EntityUtils.toString(response.getEntity());
      if (html.contains("존재하지 않는 곡 정보입니다.")) {
        LOG.info("Song information doesn't exists. (id: " + id + ")");
        return null;
      }

      return html;
    } catch (IOException e) {
      LOG.error("IOException on downloading. <song id: " + id + ">", e);
      return null;
    }
  }
}
