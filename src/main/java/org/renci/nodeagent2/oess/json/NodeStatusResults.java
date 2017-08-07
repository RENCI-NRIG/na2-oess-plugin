package org.renci.nodeagent2.oess.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * {"default_forward":"1","loopback_address":null,"short_name":null,"tx_delay_ms":"0","operational_state_mpls":"unknown","model":null,
 * "start_epoch":"1501677678","sw_version":null,"admin_state":"available","default_drop":"1","vlan_tag_range":"1-4096","node_id":"1",
 * "latitude":"0","tcp_port":"830","dpid":"3","pending_diff":"0","longitude":"0","in_maint":"no","name":"unnamed-3",
 * "mpls":null,"network_id":"1","openflow":"1","end_epoch":"-1","max_static_mac_flows":"0","mgmt_addr":null,"max_flows":"4000",
 * "send_barrier_bulk":"0","vendor":null,"operational_state":"up"}
 * @author ibaldin
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class NodeStatusResults extends Results {
	public static class NodeStatus {
		private String name;
		private String operational_state;
		private String loopback_address;
		private String vlan_tag_range;
		private int node_id;
		private int openflow;
		private int mpls;
		private String latitude;
		private String longitude;
		private String in_maint;
		private String model;

		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getOperational_state() {
			return operational_state;
		}
		public void setOperational_state(String operational_state) {
			this.operational_state = operational_state;
		}
		public String getLoopback_address() {
			return loopback_address;
		}
		public void setLoopback_address(String loopback_address) {
			this.loopback_address = loopback_address;
		}
		public String getVlan_tag_range() {
			return vlan_tag_range;
		}
		public void setVlan_tag_range(String vlan_tag_range) {
			this.vlan_tag_range = vlan_tag_range;
		}
		public int getNode_id() {
			return node_id;
		}
		public void setNode_id(int node_id) {
			this.node_id = node_id;
		}
		public int getOpenflow() {
			return openflow;
		}
		public void setOpenflow(int openflow) {
			this.openflow = openflow;
		}
		public int getMpls() {
			return mpls;
		}
		public void setMpls(int mpls) {
			this.mpls = mpls;
		}
		public String getLatitude() {
			return latitude;
		}
		public void setLatitude(String latitude) {
			this.latitude = latitude;
		}
		public String getLongitude() {
			return longitude;
		}
		public void setLongitude(String longitude) {
			this.longitude = longitude;
		}
		public String getIn_maint() {
			return in_maint;
		}
		public void setIn_maint(String in_maint) {
			this.in_maint = in_maint;
		}
		public String getModel() {
			return model;
		}
		public void setModel(String model) {
			this.model = model;
		}
		@Override
		public String toString() {
			return "NodeStatus [name=" + name + ", operational_state=" + operational_state + ", loopback_address="
					+ loopback_address + ", vlan_tag_range=" + vlan_tag_range + ", node_id=" + node_id + ", openflow="
					+ openflow + ", mpls=" + mpls + ", latitude=" + latitude + ", longitude=" + longitude + ", in_maint="
					+ in_maint + ", model=" + model + "]";
		}
	}
	private List<NodeStatus> results;
	
	public List<NodeStatus> getResults() {
		return results;
	}

	public void setResults(List<NodeStatus> results) {
		this.results = results;
	}

	@Override
	protected void resultsToString(StringBuilder sb) {
		_resultsToString(sb, results);
	}
}
