package kr.ac.yonsei.lis.project.analysis;

import cc.mallet.pipe.*;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import kr.ac.yonsei.lis.project.melon.MelonKeywordExtractor;
import kr.ac.yonsei.lis.project.model.Song;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class TopicModelingAnalysis {
  public void runAnalysis(Iterable<Song> songList, String outputDir, int numTopics, int numIterations, int numThreads,
                          int numWords) throws Exception {
    List<Pipe> pipes = new ArrayList<Pipe>();

    pipes.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}+|")));
    final File stopWordFile = new File(TopicModelingAnalysis.class.getResource("/stopwords.txt").getFile());
    pipes.add(new TokenSequenceRemoveStopwords(stopWordFile, "utf-8", false, false, false));
    pipes.add(new TokenSequence2FeatureSequence());

    InstanceList instances = new InstanceList(new SerialPipes(pipes));
    instances.addThruPipe(new SongIterator(songList));

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
    private MelonKeywordExtractor extractor;
    private int count;

    public SongIterator(Iterable<Song> songs) {
      data = songs.iterator();
      extractor = new MelonKeywordExtractor();
      count = 0;
    }

    public boolean hasNext() {
      return data.hasNext();
    }

    public Instance next() {
      Song song = data.next();
      List<String> keywords = extractor.extractKeyword(song.lyrics);
      StringBuilder builder = new StringBuilder();

      for (String keyword : keywords) {
        builder.append(keyword).append("|");
      }
      if (builder.length() > 0) {
        builder.deleteCharAt(builder.length() - 1);
      }

      ++count;
      if (count % 5000 == 0) {
        LOG.info(count + " Song Loaded");
      }

      return new Instance(builder.toString(), null, song.id, null);
    }

    public void remove() {
      throw new IllegalStateException("SongIterator doesn't support remove() method.");
    }
  }
}
