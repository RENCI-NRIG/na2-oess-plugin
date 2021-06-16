package org.renci.nodeagent2.oess.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/*
 * OESS produces a lot of information, these beans cherry-pick only what is essential
 */

@JsonIgnoreProperties(ignoreUnknown=true)
public class CircuitDetailsResults extends Results {

	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class UserDetails {
		String last_name, first_name, username, email;

		public String getLast_name() {
			return last_name;
		}

		public void setLast_name(String last_name) {
			this.last_name = last_name;
		}
		
		public String getFirst_name() {
			return last_name;
		}

		public void setFirst_name(String first_name) {
			this.first_name = first_name;
		}

		
		public void setUsername(String username) {
			this.username = username;
		}
		
		public String getUsername() {
			return this.username;
		}

		public void setEmail(String email) {
			this.email = email;
		}
		
		public String getEmail() {
			return email;
		}

		@Override
		public String toString() {
			return "UserDetails [email=" + this.email + " First Name/Last Name=" + this.first_name + "/" + this.last_name + "]";
		}
	}
	
	@JsonIgnoreProperties(ignoreUnknown=true)
	public static class EndpointDetails {
		String node, intface, description;
		int tag, bandwidth;
		
		public String getNode() {
			return node;
		}
		public void setNode(String node) {
			this.node = node;
		}
		public int getTag() {
			return tag;
		}
		public void setTag(int tag) {
			this.tag = tag;
		}
		public String getInterface() {
			return intface;
		}
		public void setInterface(String iface) {
			this.intface = iface;
		}
		public String getDescription() {
			return this.description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public int getBandwidth() {
			return bandwidth;
		}
		public void setBandwidth(int bw) {
			this.bandwidth = bw;
		}
		@Override
		public String toString() {
			return "EndpointDetails [node=" + node + ", interface=" + intface + ", tag=" + tag + ", bandwidth=" + bandwidth
					+ ", description=" + description + "]";
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
		String circuit_id, path_id, mpls_type, type, state;
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
		public String getMpls_type() {
			return mpls_type;
		}
		public void setMpls_type(String mpls_type) {
			this.mpls_type = mpls_type;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getState() {
			return state;
		}
		public void setState(String Sstate) {
			this.state = Sstate;
		}
		public List<LinkDetails> getLinks() {
			return links;
		}
		public void setLinks(List<LinkDetails> links) {
			this.links = links;
		}
		@Override
		public String toString() {
			return "PathDetails [circuit_id=" + circuit_id + ", path_id=" + path_id + ", mpls_type=" + mpls_type + ", type=" + type
					+ ", state=" + state + ", links=" + links + "]";
		}
	}
	
	// main container of information
	public static class CircuitDetail {
		UserDetails last_modified_by;
		UserDetails created_by;
		List<PathDetails> paths;
		List<EndpointDetails> endpoints;
		String state;
		String created_on;
		String last_modified_on;
		String description;
		
		public UserDetails getLast_modified_by() {
			return last_modified_by;
		}
		public void setLast_modified_by(UserDetails last_modified_by) {
			this.last_modified_by = last_modified_by;
		}
		public UserDetails getCreated_by() {
			return created_by;
		}
		public void setCreated_by(UserDetails created_by) {
			this.created_by = created_by;
		}
		public List<PathDetails> getPaths() {
			return paths;
		}
		public void setPaths(List<PathDetails> paths) {
			this.paths = paths;
		}
		public List<EndpointDetails> getEndpoints() {
			return endpoints;
		}
		public void setEndpoints(List<EndpointDetails> endpoints) {
			this.endpoints = endpoints;
		}
		public String getState() {
			return state;
		}
		public void setState(String state) {
			this.state = state;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public String getCreated_on() {
			return created_on;
		}
		public void setCreated_on(String created_on) {
			this.created_on = created_on;
		}
		public String getLast_modified_on() {
			return last_modified_on;
		}
		public void setLast_modified_on(String last_modified_on) {
			this.last_modified_on = last_modified_on;
		}
		@Override
		public String toString() {
			return "CircuitDetails [last_modified_by=" + last_modified_by + " created_by=" + created_by + " state=" + state + 
					" description=" + description + " created_on=" + created_on + " last_modified_on=" + last_modified_on + 
					" paths=" + paths + " endpoints=" + endpoints + "]";
		}
	}
	
	List<CircuitDetail> results;
	
	public List<CircuitDetail> getResults() {
		return results;
	}

	public void setResults(List<CircuitDetail> results) {
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
