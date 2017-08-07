package org.renci.nodeagent2.oess.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class Results {

	private int error;
	private String error_text;
	
	public Integer getError() {
		return error;
	}
	public void setError(Integer error) {
		this.error = error;
	}
	public String getError_text() {
		return error_text;
	}
	public void setError_text(String error_text) {
		this.error_text = error_text;
	}

	protected abstract void resultsToString(StringBuilder sb);
	
	protected static <T> void _resultsToString(StringBuilder sb, List<T> res) {
		if (res != null) {
			sb.append("Results<" + res.get(0).getClass().getSimpleName() + "> [");
			for (T t: res) {
				sb.append(t.toString() + ", ");
			}
			sb.append("] ");
		} else 
			sb.append("Results<unknown> [ null ] ");
	}
	
	protected static <T> void _resultsToString(StringBuilder sb, T res) {
		if (res != null) {
			sb.append("Results<" + res.getClass().getSimpleName() + "> {");
			sb.append(res.toString() + "} ");
		} else 
			sb.append("Results<unknown> { null } ");
	}
	
	@Override
	public String toString() {
		if (error_text != null) {
			return "Results {error_text=" + error_text + ", error=" + error + "}";
		} else {
			StringBuilder sb = new StringBuilder();
			resultsToString(sb);
			return sb.toString(); 
		} 
	}
}
