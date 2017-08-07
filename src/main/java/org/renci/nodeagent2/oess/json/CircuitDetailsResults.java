package org.renci.nodeagent2.oess.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class CircuitDetailsResults extends Results {

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class UserDetails {
		String status, family_name, given_names, auth_name, email;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getFamily_name() {
			return family_name;
		}

		public void setFamily_name(String family_name) {
			this.family_name = family_name;
		}

		public String getGiven_names() {
			return given_names;
		}

		public void setGiven_names(String given_names) {
			this.given_names = given_names;
		}

		public String getAuth_name() {
			return auth_name;
		}

		public void setAuth_name(String auth_name) {
			this.auth_name = auth_name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		@Override
		public String toString() {
			return "UserDetails [status=" + status + ", family_name=" + family_name + ", given_names=" + given_names
					+ ", auth_name=" + auth_name + ", email=" + email + "]";
		}
	}
	
	public static class LinkDetails {
		String link_id, name;

		public String getLink_id() {
			return link_id;
		}

		public void setLink_id(String link_id) {
			this.link_id = link_id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "LinkDetails [link_id=" + link_id + ", name=" + name + "]";
		}
		
	}
	
	public static class PathDetails {
		String circuit_id, path_id, path_type, path_state;
		int status;
		List<LinkDetails> links;
		public String getCircuit_id() {
			return circuit_id;
		}
		public void setCircuit_id(String circuit_id) {
			this.circuit_id = circuit_id;
		}
		public String getPath_id() {
			return path_id;
		}
		public void setPath_id(String path_id) {
			this.path_id = path_id;
		}
		public String getPath_type() {
			return path_type;
		}
		public void setPath_type(String path_type) {
			this.path_type = path_type;
		}
		public String getPath_state() {
			return path_state;
		}
		public void setPath_state(String path_state) {
			this.path_state = path_state;
		}
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public List<LinkDetails> getLinks() {
			return links;
		}
		public void setLinks(List<LinkDetails> links) {
			this.links = links;
		}
		@Override
		public String toString() {
			return "PathDetails [circuit_id=" + circuit_id + ", path_id=" + path_id + ", path_type=" + path_type
					+ ", path_state=" + path_state + ", status=" + status + ", links=" + links + "]";
		}
	}
	
	public static class PathsDetails {
		PathDetails primary, backup;

		public PathDetails getPrimary() {
			return primary;
		}

		public void setPrimary(PathDetails primary) {
			this.primary = primary;
		}

		public PathDetails getBackup() {
			return backup;
		}

		public void setBackup(PathDetails backup) {
			this.backup = backup;
		}

		@Override
		public String toString() {
			return "PathsDetails [primary=" + primary + ", backup=" + backup + "]";
		}
	}
	
	// main container of information
	public class CircuitDetails {
		UserDetails last_modified_by;
		PathsDetails paths;
		String state;
		String created_on;
		
		public UserDetails getLast_modified_by() {
			return last_modified_by;
		}
		public void setLast_modified_by(UserDetails last_modified_by) {
			this.last_modified_by = last_modified_by;
		}
		public PathsDetails getPaths() {
			return paths;
		}
		public void setPaths(PathsDetails paths) {
			this.paths = paths;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getCreated_on() {
			return created_on;
		}
		public void setCreated_on(String created_on) {
			this.created_on = created_on;
		}
		@Override
		public String toString() {
			return "CircuitDetails [last_modified_by=" + last_modified_by + ", paths=" + paths + ", state=" + state
					+ ", created_on=" + created_on + "]";
		}
	}
	
	CircuitDetails results;
	
	public CircuitDetails getResults() {
		return results;
	}

	public void setResults(CircuitDetails results) {
		this.results = results;
	}

	@Override
	public String toString() {
		return "CircuitDetailsResults [results=" + results + "]";
	}

	@Override
	protected void resultsToString(StringBuilder sb) {
		_resultsToString(sb, results);
	}

}
