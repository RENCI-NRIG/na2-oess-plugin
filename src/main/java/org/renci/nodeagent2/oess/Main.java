package org.renci.nodeagent2.oess;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.renci.nodeagent2.agentlib.Plugin;
import org.renci.nodeagent2.agentlib.PluginException;
import org.renci.nodeagent2.agentlib.PluginReturn;
import org.renci.nodeagent2.agentlib.Properties;
import org.renci.nodeagent2.agentlib.ReservationId;
import org.renci.nodeagent2.agentlib.Util;
import org.renci.nodeagent2.oess.json.CircuitDetailsResults;
import org.renci.nodeagent2.oess.json.ProvisionCircuitResults;


public class Main implements Plugin {

	public static class OESSEndpoint {
		private String node, intface;

		public String getNode() {
			return node;
		}

		public void setNode(String node) {
			this.node = node;
		}

		public String getIntface() {
			return intface;
		}

		public void setIntface(String intface) {
			this.intface = intface;
		}
	}
	
	private static final String TRUE_ANSWER = "true";
	private static final String YES_ANSWER = "yes";
	
	private static final String STATUS_PROP = "oess.status";
	private static final String DATE_PROP = "oess.date";
	private static final String USER_PROP = "oess.user";
	private static final String CIRCUIT_ID_PROP = "oess.circuit.id";
	private static final String TAGZ_PROP = "oess.tagZ";
	private static final String TAGA_PROP = "oess.tagA";
	private static final String EPZ_PROP = "oess.endpointZ";
	private static final String EPA_PROP = "oess.endpointA";
	private static final String BW_PROP = "oess.bw";
	private static final String PASSWORD_PROP = "oess.password";
	private static final String USERNAME_PROP = "oess.username";
	private static final String NETTYPE_PROP = "network.type";
	private static final String DISABLESSL_PROP = "disable.ssl.check";
	private static final String DISABLESNI_PROP = "disable.sni";
	private static final String DEBUGHTTP_PROP = "debug.http";
	private static final String WORKGROUP_ID_PROP = "workgroup.id";
	private static final String OESSURL_PROP = "oess.url";
	private static final String API_COMMENT_PROP = "oess.comment";
	
	private static Pattern urnPattern = Pattern.compile("urn:ogf:network:domain=([\\w.-]+):node=([\\w.-]+):port=([/\\w-]+([:.][\\d]+)?).*");
	//private static Pattern urnPattern = Pattern.compile("urn:ogf:network:domain=([\\w\\.\\-]+):node=([\\w\\.\\-]+):port=([/\\w.-]+[:\\d]+)+.*");

	Log log;
	private Driver oessDriver = null;
	NetworkType nt;
	String apiComment = "EXOGENI Circuit";
	
	// OESS Supports both types
	public enum NetworkType {mpls};

	public static OESSEndpoint parseUrn(String urn) {
		Matcher m = urnPattern.matcher(urn);
		
		if (m.matches()) {
			OESSEndpoint oep = new OESSEndpoint();
			oep.setNode(m.group(2));
			oep.setIntface(m.group(3));
			return oep;
		}
		return null;
	}
	
	// check if option is set to 'yes' or 'true' and return true, otherwise false
	private boolean yesOption(String optionVal) {
		if ((optionVal != null) && (YES_ANSWER.equalsIgnoreCase(optionVal) || TRUE_ANSWER.equalsIgnoreCase(optionVal))) 
			return true;
		return false;
	}
	
	@Override
	public void initialize(String config, Properties configProperties) throws PluginException {

		try {
			log = Util.getLog(this.getClass().getName());


			log.info("Initializing OESS plugin");

			if (!configProperties.containsKey(USERNAME_PROP) ||
					!configProperties.containsKey(PASSWORD_PROP) ||
					!configProperties.containsKey(NETTYPE_PROP) ||
					!configProperties.containsKey(OESSURL_PROP) ||
					!configProperties.containsKey(WORKGROUP_ID_PROP))
				throw new PluginException("Plugin configuration must specify the following properties: " + 
						USERNAME_PROP + ", " + PASSWORD_PROP + ", " + NETTYPE_PROP + ", " + OESSURL_PROP + ", " + WORKGROUP_ID_PROP);

			// disable SSL server cert and hostname verification
			boolean disableSSLChecks = yesOption(configProperties.get(DISABLESSL_PROP));
			if (disableSSLChecks) 
				log.warn("Disabling SSL certificate and host validation.");

			boolean debugHttp = yesOption(configProperties.get(DEBUGHTTP_PROP));
			if (debugHttp)
				log.info("Enabling HTTP debugging features.");
			
			nt = NetworkType.mpls;
			if (!NetworkType.mpls.name().equalsIgnoreCase(configProperties.get(NETTYPE_PROP))) 
					throw new PluginException("Plugin configuration must specify mpls network type only. Instead " + configProperties.get(NETTYPE_PROP));

			int wgid = Integer.parseInt(configProperties.get(WORKGROUP_ID_PROP));
			
			// client does not send name as part of handshake to tell server which certs to present
			// doesn't seem to work with OESS servers.
			boolean disableSNI = yesOption(configProperties.get(DISABLESNI_PROP));
			
			if (configProperties.get(API_COMMENT_PROP) != null)
				apiComment = configProperties.get(API_COMMENT_PROP);
			
			oessDriver = new Driver(configProperties.get(USERNAME_PROP), configProperties.get(PASSWORD_PROP), wgid,
					configProperties.get(OESSURL_PROP), nt, disableSSLChecks, disableSNI, debugHttp);
			
		} catch(Exception e) {
			throw new PluginException("OESS Plugin unable to get logger: " + e);
		}
	}

	@Override
	public PluginReturn join(Date until, Properties callerProps) throws PluginException {

		if (!callerProps.containsKey(EPA_PROP) ||
				!callerProps.containsKey(EPZ_PROP) ||
				!callerProps.containsKey(TAGA_PROP) ||
				!callerProps.containsKey(TAGZ_PROP))
			throw new PluginException("Insufficient configuration prameters for join operation: endpoint URN[AZ] and tag[AZ] must be specified");

		if ((callerProps.get(EPA_PROP).length() == 0) ||
				(callerProps.get(EPZ_PROP).length() == 0) ||
				(callerProps.get(TAGA_PROP).length() == 0) ||
				(callerProps.get(TAGZ_PROP).length() == 0))
			throw new PluginException("Insufficient configuration prameters for join operation: endpoint URN[AZ] and tag[AZ] must be non-zero length");

		long bw = 0;
		if (callerProps.containsKey(BW_PROP)) {
			bw = Long.parseLong(callerProps.get(BW_PROP));
		}
		int bwMbps = 0;
		int srcTag = 0;
		int dstTag = 0;
		OESSEndpoint src, dst;
		
		// parse urn in the form of urn:ogf:network:domain=al2s.net.internet2.edu:node=rtsw.clev.net.internet2.edu:port=et-3/3/0.0
		src = parseUrn(callerProps.get(EPA_PROP));
		dst = parseUrn(callerProps.get(EPZ_PROP));
		
		try {
			// ORCA operates on bps, OSCARS on Mbps
			bwMbps = (int)Math.round(Math.ceil(bw/1000000.0));
			
			srcTag = Integer.parseInt(callerProps.get(TAGA_PROP));
			dstTag = Integer.parseInt(callerProps.get(TAGZ_PROP));
		} catch (NumberFormatException nfe) {
			throw new PluginException("Invalid bandwidth or tag specification - expecting integer (Mbps for bw): " + callerProps.get(BW_PROP) + ", " + 
					callerProps.get(TAGA_PROP) + ", " + callerProps.get(TAGZ_PROP));
		}
		
		log.info("Creating OESS circuit from " + src.getNode() + "---" + src.getIntface() + "/" + srcTag + 
				" to " + dst.getNode() + "---" + dst.getIntface() + "/" + dstTag + " with bandwidth " + bwMbps + "Mbps");
		
		try {
			ProvisionCircuitResults pcr = null;
			pcr = oessDriver.provisionCircuit(apiComment, bwMbps, src.getNode(), src.getIntface(), srcTag, 
					dst.getNode(), dst.getIntface(), dstTag);
			
			log.info("Created OESS circuit " + pcr.getCircuit_id());

			Properties joinProps = new Properties();
			joinProps.put(CIRCUIT_ID_PROP, pcr.getCircuit_id());
			joinProps.putAll(callerProps);
			PluginReturn pRet = new PluginReturn(new ReservationId(pcr.getCircuit_id()), joinProps);

			return pRet;
		} catch (OESSException oe) {
			throw new PluginException("Error invoking OESS API: " + oe);
		}
	}

	@Override
	public PluginReturn leave(ReservationId resId, Properties callerProps, Properties schedProps)
			throws PluginException {
		try {
			log.info("Removing OESS circuit " + resId.getId());
			oessDriver.removeCircuit(resId.getId());
			PluginReturn pr = new PluginReturn(resId, schedProps);

			return pr;
		} catch (OESSException oe) {
			throw new PluginException("Unable to remove circuit " + resId + " due to OESS problem: " + oe);
		}
	}

	@Override
	public PluginReturn modify(ReservationId resId, Properties callerProperties, Properties schedProperties)
			throws PluginException {
		throw new PluginException("Modify functionality not implemented in OESS plugin");
	}

	@Override
	public PluginReturn renew(ReservationId resId, Date until, Properties joinProperties, Properties schedProps)
			throws PluginException {
		log.info("Renew for " + resId + " in OESS is a NOOP, proceeding");
		PluginReturn pr = new PluginReturn(resId, schedProps);
		return pr;
	}

	@Override
	public PluginReturn status(ReservationId resId, Properties schedProps) throws PluginException {
		try {
			log.info("Requesting OESS circuit detais for " + resId.getId());
			CircuitDetailsResults cdr = oessDriver.circuitDetails(resId.getId());
			
			if (cdr.getResults().size() > 0) {
				schedProps.put(STATUS_PROP, cdr.getResults().get(0).getState());
				schedProps.put(DATE_PROP, cdr.getResults().get(0).getCreated_on());
				schedProps.put(USER_PROP, cdr.getResults().get(0).getLast_modified_by().getUsername());
			} else {
				schedProps.put(STATUS_PROP, "unknown");
			}
			PluginReturn pr = new PluginReturn(resId, schedProps);

			return pr;
		} catch (OESSException oe) {
			throw new PluginException("Unable to request details for circuit " + resId + " due to OESS problem: " + oe);
		}
	}

	public static void main(String[] argv) {
		String urn = "urn:ogf:network:domain=al-2s.net.internet2.edu:node=sdn-sw.houh.net.internet2.edu:port=et-0/3/0.0";
		String urn1 = "urn:ogf:network:domain=al-2s.net.internet2.edu:node=sdn-sw.houh.net.internet2.edu:port=xe-0/1/10:10";
		
		Matcher m = urnPattern.matcher(urn);
		
		if (m.matches())  {
			System.out.println("COOL");
			System.out.println("domain= " + m.group(1));
			System.out.println("node= " + m.group(2));
			System.out.println("port= " + m.group(3));
		}
		else
			System.out.println("NOT COOL");
		
		Matcher m1 = urnPattern.matcher(urn1);
		
		if (m1.matches())  {
			System.out.println("COOL");
			System.out.println("domain= " + m1.group(1));
			System.out.println("node= " + m1.group(2));
			System.out.println("port= " + m1.group(3));
		}
		else
			System.out.println("NOT COOL");
		
		OESSEndpoint ep = parseUrn(urn);
		System.out.println(urn + ": " + ep.node + "@" + ep.intface);
		
		OESSEndpoint ep1 = parseUrn(urn1);
		System.out.println(urn + ": " + ep1.node + "@" + ep1.intface);
	}
}
