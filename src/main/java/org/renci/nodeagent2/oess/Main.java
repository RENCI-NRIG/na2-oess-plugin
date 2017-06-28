package org.renci.nodeagent2.oess;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.renci.nodeagent2.agentlib.Plugin;
import org.renci.nodeagent2.agentlib.PluginException;
import org.renci.nodeagent2.agentlib.PluginReturn;
import org.renci.nodeagent2.agentlib.Properties;
import org.renci.nodeagent2.agentlib.ReservationId;
import org.renci.nodeagent2.agentlib.Util;


public class Main implements Plugin {

	Log log;
	
	@Override
	public void initialize(String config, Properties configProperties) throws PluginException {
		try {
			log = Util.getLog(this.getClass().getName());
		} catch (Exception e) {
			throw new PluginException("Unable to initialize oscars: " + e);
		}
	}

	@Override
	public PluginReturn join(Date until, Properties callerPropeties) throws PluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginReturn leave(ReservationId resId, Properties callerProperties, Properties schedProperties)
			throws PluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginReturn modify(ReservationId resId, Properties callerProperties, Properties schedProperties)
			throws PluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginReturn renew(ReservationId resId, Date until, Properties joinProperties, Properties schedProperties)
			throws PluginException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PluginReturn status(ReservationId resId, Properties schedProperties) throws PluginException {
		// TODO Auto-generated method stub
		return null;
	}
}
