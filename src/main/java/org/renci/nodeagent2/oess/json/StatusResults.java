package org.renci.nodeagent2.oess.json;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class StatusResults extends Results {

	int status;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Status [status=" + status + "]";
	}

	@Override
	protected void resultsToString(StringBuilder sb) {
		_resultsToString(sb, status);
	}

}
