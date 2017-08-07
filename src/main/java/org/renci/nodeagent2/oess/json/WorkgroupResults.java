package org.renci.nodeagent2.oess.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class WorkgroupResults extends Results {
	
	public static class Workgroup {
		private String name;
		private String type;
		private int workgroup_id;
	
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public int getWorkgroup_id() {
			return workgroup_id;
		}
		public void setWorkgroup_id(int workgroup_id) {
			this.workgroup_id = workgroup_id;
		}
		
		@Override
		public String toString() {
			return "Workgroup [name=" + name + ", type=" + type + ", workgroup_id=" + workgroup_id + "]";
		}
	}
	
	private List<Workgroup> results;

	public List<Workgroup> getResults() {
		return results;
	}

	public void setResults(List<Workgroup> results) {
		this.results = results;
	}

	@Override
	public void resultsToString(StringBuilder sb) {
		_resultsToString(sb, results);
	}
}
