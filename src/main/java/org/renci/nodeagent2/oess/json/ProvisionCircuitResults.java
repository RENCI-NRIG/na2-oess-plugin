package org.renci.nodeagent2.oess.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ProvisionCircuitResults extends Results {

	public static class Provision {
		private String circuit_id;
		private int success;
		
		public String getCircuit_id() {
			return circuit_id;
		}
		public void setCircuit_id(String circuit_id) {
			this.circuit_id = circuit_id;
		}
		public int getSuccess() {
			return success;
		}
		public void setSuccess(int success) {
			this.success = success;
		}
		@Override
		public String toString() {
			return "Provision [circuit_id=" + circuit_id + ", success=" + success + "]";
		}
	}
	
	private Provision results;
	
	public Provision getResults() {
		return results;
	}

	public void setResults(Provision results) {
		this.results = results;
	}

	@Override
	protected void resultsToString(StringBuilder sb) {
		_resultsToString(sb, results);
	}
}
