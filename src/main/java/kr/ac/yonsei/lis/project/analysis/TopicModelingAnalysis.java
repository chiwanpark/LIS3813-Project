package kr.ac.yonsei.lis.project.analysis;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import kr.ac.yonsei.lis.project.melon.MelonKeywordExtractor;
import kr.ac.yonsei.lis.project.model.Song;
import kr.ac.yonsei.lis.project.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

public class TopicModelingAnalysis {
  private String stopWordsPath;

  public TopicModelingAnalysis() {
    // copy resources
    List<String> resources = new ArrayList<String>();
    resources.add("/stopwords.txt");

    try {
      stopWordsPath = FileUtils.copyResources(resources) + File.separator + "stopwords.txt";
    } catch (IOException e) {
      throw new RuntimeException("Cannot load stop word list");
    }
  }

  public void runAnalysis(Iterable<Song> songList, String outputDir, int numTopics, int numIterations, int numThreads,
                          int numWords, int date) throws Exception {
    List<Pipe> pipes = new ArrayList<Pipe>();

    pipes.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}+|")));
    pipes.add(new TokenSequenceRemoveStopwords(new File(stopWordsPath), "utf-8", false, false, false));
    pipes.add(new TokenSequence2FeatureSequence());

    InstanceList instances = new InstanceList(new SerialPipes(pipes));
    instances.addThruPipe(new SongIterator(songList, date));

    ParallelTopicModel model = new ParallelTopicModel(numTopics, 1.0, 0.01);
    model.setNumIterations(numIterations);
    model.setNumThreads(numThreads);
    model.addInstances(instances);
    model.estimate();

    PrintWriter writer = new PrintWriter(outputDir + File.separator + "topicReport.xml");
    model.topicXMLReport(writer, numWords);
    writer.close();

    model.printDocumentTopics(new File(outputDir + File.separator + "topicDocuments.txt"));
  }

  public static class SongIterator implements Iterator<Instance> {
    private static final Logger LOG = LoggerFactory.getLogger(SongIterator.class);

    private Iterator<Song> data;
    private Song current;
    private MelonKeywordExtractor extractor;
    private int count;
    private String date;

    public SongIterator(Iterable<Song> songs, int date) {
      data = songs.iterator();
      extractor = new MelonKeywordExtractor();
      count = 0;
      if (date == -1) {
        this.date = "";
      } else {
        this.date = String.valueOf(date);
      }
    }

    private boolean isCurrentValid() {
      return ("".equals(date) || (!"".equals(date) && current.date.startsWith(date)));
    }

    private void moveToNext() {
      try {
        do {
          current = data.next();
          ++count;
          if (count % 5000 == 0) {
            LOG.info(count + " Song Loaded");
          }
        } while (!isCurrentValid());
      } catch (NoSuchElementException e) {
        current = null;
      }
    }

    public boolean hasNext() {
      moveToNext();
      return current != null;
    }

    public Instance next() {
      Song song = current;
      List<String> keywords = extractor.extractKeyword(song.lyrics);
      StringBuilder builder = new StringBuilder();

      for (String keyword : keywords) {
        builder.append(keyword).append("|");
      }
      if (builder.length() > 0) {
        builder.deleteCharAt(builder.length() - 1);
      }

      return new Instance(builder.toString(), null, song.id, null);
    }

    public void remove() {
      throw new IllegalStateException("SongIterator doesn't support remove() method.");
    }
  }
}
