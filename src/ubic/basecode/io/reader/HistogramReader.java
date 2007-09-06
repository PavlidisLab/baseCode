package ubic.basecode.io.reader;

import hep.aida.ref.Histogram1D;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Reads histograms stored in flat files
 * 
 * @author raymond
 */
public class HistogramReader {
	private String title;
	protected BufferedReader in;
	
	public HistogramReader(Reader in, String title ) {
		this.in = new BufferedReader(in);
		this.title = title;
	}
	
	public HistogramReader(String fileName, String title) throws FileNotFoundException {
		this.in = new BufferedReader(new FileReader(fileName));
	}
	
	public Histogram1D read1D() throws IOException {
		int numHeaderLines = 1; // ignore the column header
		Map binCountMap = new HashMap();
		Double min = new Double(Double.POSITIVE_INFINITY);
		Double max = new Double(Double.NEGATIVE_INFINITY);
		while (in.ready()) {
			String line = in.readLine();
			if (line.startsWith("#") || numHeaderLines-- > 0)
				continue;
			String fields[] = line.split("\t");
			Double bin = Double.valueOf(fields[0]);
			Integer count = Integer.valueOf(fields[1]);
			binCountMap.put(bin, count);
			if (bin.compareTo(min) < 0)
				min = bin;
			if (bin.compareTo(max) > 0)
				max = bin;
		}
		int numBins = binCountMap.keySet().size();
		
		Histogram1D hist = new Histogram1D(title, numBins, min.doubleValue(), max.doubleValue());
		for (Iterator it = binCountMap.entrySet().iterator(); it.hasNext(); ) {
			Map.Entry entry = (Map.Entry) it.next();
			Double bin = (Double) entry.getKey();
			Integer count = (Integer) entry.getValue();
			hist.fill(bin.doubleValue(), count.doubleValue());
		}
		return hist;
	}
}
