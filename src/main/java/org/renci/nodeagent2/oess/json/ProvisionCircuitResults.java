package org.renci.nodeagent2.oess.json;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ProvisionCircuitResults extends Results {
	private String circuit_id;
	
	public String getCircuit_id() {
		return circuit_id;
	}
	public void setCircuit_id(String cid) {
		this.circuit_id = cid;
	}
	
	@Override
	protected void resultsToString(StringBuilder sb) {
		_resultsToString(sb, "circuit_id=" + circuit_id);
	}
}
