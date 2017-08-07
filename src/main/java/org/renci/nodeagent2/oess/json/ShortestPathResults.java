package org.renci.nodeagent2.oess.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ShortestPathResults extends Results {

	public static class Link {
		String link;

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		@Override
		public String toString() {
			return "Link [link=" + link + "]";
		}
	}
	
	private List<Link> results;
	
	public List<Link> getResults() {
		return results;
	}

	public void setResults(List<Link> results) {
		this.results = results;
	}

	@Override
	protected void resultsToString(StringBuilder sb) {
		_resultsToString(sb, results);
	}

}
