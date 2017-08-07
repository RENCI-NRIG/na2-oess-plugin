package org.renci.nodeagent2.oess.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class StatusResults extends Results {

	public static class Status {
		int success;

		public int getSuccess() {
			return success;
		}

		public void setSuccess(int success) {
			this.success = success;
		}

		@Override
		public String toString() {
			return "Status [success=" + success + "]";
		}
	}
	
	private List<Status> results; 
	
	public List<Status> getResults() {
		return results;
	}

	public void setResults(List<Status> results) {
		this.results = results;
	}

	@Override
	protected void resultsToString(StringBuilder sb) {
		_resultsToString(sb, results);
	}

}
