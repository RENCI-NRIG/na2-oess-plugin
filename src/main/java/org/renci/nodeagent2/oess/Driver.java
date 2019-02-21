package org.renci.nodeagent2.oess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.logging.Log;
import org.apache.commons.validator.routines.UrlValidator;
import org.renci.nodeagent2.agentlib.Util;
import org.renci.nodeagent2.oess.Main.NetworkType;
import org.renci.nodeagent2.oess.json.CircuitDetailsResults;
import org.renci.nodeagent2.oess.json.NodeStatusResults;
import org.renci.nodeagent2.oess.json.ProvisionCircuitResults;
import org.renci.nodeagent2.oess.json.ShortestPathResults;
import org.renci.nodeagent2.oess.json.StatusResults;
import org.renci.nodeagent2.oess.json.WorkgroupResults;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class Driver {

    private static final String JSON_CIRCUIT_ID = "circuit_id";
    private static final String JSON_TYPE = "type";
    private static final String JSON_LINK = "link";
    private static final String JSON_BANDWIDTH = "bandwidth";
    private static final String JSON_DESCRIPTION = "description";
    private static final String JSON_TAG = "tag";
    private static final String JSON_INTERFACE = "interface";
    private static final String JSON_NODE = "node";
    private static final String JSON_REMOVE_TIME = "remove_time";
    private static final String JSON_PROVISION_TIME = "provision_time";
    private static final String JSON_WORKGROUP_ID = "workgroup_id";
    private static final String JSON_METHOD = "method";

    String username, password;
    String oessUrl, dataCgiUrl, provisionCgiUrl;
    boolean debugHttp = false;
    boolean disableSSLChecks = false;
    NetworkType netType = NetworkType.mpls;
    int wgid;
    RestTemplate restTemplate = null;
    Log log;
    private ReentrantLock fairLock = new ReentrantLock(true);

    /**
     * Host name verifier that does not perform any checks.
     * Good for using in tests on localhost, where the cert
     * may not match the hostname
     */
    private static class NullHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

    /**
     * Trust manager that does not perform nay checks.
     */
    private static class NullX509TrustManager implements X509TrustManager {
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * Print all message converters on a template. Each converter is associated with a list of media types
     * @param tmp
     */
    private void printConverters(RestTemplate tmp) {
        for(HttpMessageConverter<?> conv: tmp.getMessageConverters()) {
            log.debug(conv.getClass().getSimpleName() + " " + conv.getSupportedMediaTypes());
        }
    }

    /**
     * Optional all-trusting security manager
     */
    private void initAllTrustingSSL() throws OESSException {
        try{
            SSLContext sslc;

            sslc = SSLContext.getInstance("TLS");

            TrustManager[] trustManagerArray = { new NullX509TrustManager() };
            sslc.init(null, trustManagerArray, null);

            HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new NullHostnameVerifier());

        } catch(Exception e){
            throw new OESSException("Unable to initialize SSL with all-trusting policy: " + e);
        }
    }

    public static class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

        Log log;

        public LoggingRequestInterceptor() {
            try {
                log = Util.getLog(this.getClass().getName());
            } catch (Exception e) {
                ;
            }
        }
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

            ClientHttpResponse response = execution.execute(request, body);

            StringBuilder inputStringBuilder = new StringBuilder();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
            String line = bufferedReader.readLine();
            while (line != null) {
                inputStringBuilder.append(line);
                inputStringBuilder.append('\n');
                line = bufferedReader.readLine();
            }
            log.debug("============================response begin==========================================");
            log.debug("Status code  : {}" + response.getStatusCode());
            log.debug("Status text  : {}" + response.getStatusText());
            log.debug("Headers      : {}" + response.getHeaders());
            log.debug("Response body: {}" + inputStringBuilder.toString());
            log.debug("=======================response end=================================================");

            return response;
        }
    }

    /**
     * Generate a RestTemplate good for talking to OESS
     * (it properly interprets JSON even if content type is set to text/plain)
     * @return
     */
    private RestTemplate getTemplate() {

        RestTemplate restTemplate = null;

        if (debugHttp) {
            restTemplate = new RestTemplate(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
            restTemplate.getInterceptors().add(new LoggingRequestInterceptor());
            printConverters(restTemplate);
        }
        else
            restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor(username, password));

        /*
           HttpMessageConverter<?> jackson = null;
           for(HttpMessageConverter<?> c: restTemplate.getMessageConverters()) {
           if (c instanceof MappingJackson2HttpMessageConverter) {
           jackson = c;
           List<MediaType> mt = new ArrayList<>(jackson.getSupportedMediaTypes());
           mt.add(MediaType.TEXT_PLAIN);
           ((MappingJackson2HttpMessageConverter) jackson).setSupportedMediaTypes(mt);
           }
           }
           */

        // replace all converters with just one
        // Normally a template has this
        // ByteArrayHttpMessageConverter [application/octet-stream, */*]
        // StringHttpMessageConverter [text/plain, */*]
        // ResourceHttpMessageConverter [*/*]
        // SourceHttpMessageConverter [application/xml, text/xml, application/*+xml]
        // AllEncompassingFormHttpMessageConverter [application/x-www-form-urlencoded, multipart/form-data]
        // Jaxb2RootElementHttpMessageConverter [application/xml, text/xml, application/*+xml]
        // MappingJackson2HttpMessageConverter [application/json, application/*+json]

        // substitute for just one JSON converter with extended media type list
        MappingJackson2HttpMessageConverter jackson2 = new MappingJackson2HttpMessageConverter();
        List<MediaType> jmt = new ArrayList<>(jackson2.getSupportedMediaTypes());
        jmt.add(MediaType.TEXT_PLAIN);
        jackson2.setSupportedMediaTypes(jmt);
        restTemplate.setMessageConverters(Arrays.asList(jackson2));

        return restTemplate;
    }

    public Driver(String username, String password, int wgid, String url, NetworkType t, boolean disableSSLChecks, boolean disableSNI, boolean debug) throws OESSException {

        try {
            log = Util.getLog(this.getClass().getName());
        } catch (Exception e) {
            throw new OESSException(e);
        }

        this.username = username;
        this.password = password;
        this.netType = t;
        this.wgid = wgid;

        String[] schemes = {"http", "https"};
        UrlValidator val = new UrlValidator(schemes, UrlValidator.ALLOW_2_SLASHES | UrlValidator.ALLOW_LOCAL_URLS);
        if (!val.isValid(url)) {
            throw new OESSException("OESS URL is not a valid URL: " + url);
        }
        oessUrl = url;
        dataCgiUrl = oessUrl + "/data.cgi";
        provisionCgiUrl = oessUrl + "/provisioning.cgi";
        debugHttp = debug;

        log.info("Using OESS data.cgi "+ dataCgiUrl + " provisioning cgi " + provisionCgiUrl + " with HTTP debug " + (debugHttp ? "on" : " off"));

        if (disableSNI)
            System.setProperty("jsse.enableSNIExtension", "false");

        if (disableSSLChecks)
            initAllTrustingSSL();

        restTemplate = getTemplate();
    }

    /**
     * Get OESS workgroups
     * @return
     */
    public WorkgroupResults getWorkgroups() throws OESSException {
        log.info("Invoking getWorkgroups()");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dataCgiUrl)
            .queryParam(JSON_METHOD, "get_workgroups");

        WorkgroupResults res = restTemplate.getForObject(builder.build().encode().toUri(), WorkgroupResults.class);
        if (res.getError_text() != null)
            throw new OESSException("Unable to get OESS workgroups: " + res.getError_text());
        return res;
    }

    /**
     * Get node status records
     * @return
     */
    public NodeStatusResults getAllNodeStatus()  throws OESSException {
        log.info("Invoking getAllNodeStatus()");
        UriComponentsBuilder builder1 = UriComponentsBuilder.fromHttpUrl(dataCgiUrl)
            .queryParam(JSON_METHOD, "get_all_node_status");

        if (debugHttp) 
            log.debug(builder1.build().encode().toUri());

        NodeStatusResults res = restTemplate.getForObject(builder1.build().encode().toUri(), NodeStatusResults.class);
        if (res.getError_text() != null)
            throw new OESSException("Unable to get node status information: " + res.getError_text());
        return res;
    }

    /**
     * Get shortest path between src and dst
     * @param t
     * @param src
     * @param dst
     * @return
     * @throws OESSException
     */
    public ShortestPathResults getShortestPath(String src, String dst) throws OESSException {
        log.info("Invoking getShortestPath()");
        UriComponentsBuilder builder4 = UriComponentsBuilder.fromHttpUrl(dataCgiUrl)
            .queryParam(JSON_METHOD, "get_shortest_path")
            .queryParam(JSON_TYPE, netType.name())
            .queryParam(JSON_NODE, src)
            .queryParam(JSON_NODE, dst);
        if (debugHttp) 
            log.debug(builder4.build().encode().toUri());

        ShortestPathResults spres = restTemplate.getForObject(builder4.build().encode().toUri(), ShortestPathResults.class);
        if (spres.getError_text() != null)
            throw new OESSException("Unable to compute shortest path between: " + src + " and " + dst + " due to: " + 
                    spres.getError_text());
        return spres;
    }

    /**
     * Provision a ptop circuit using shortest path
     * @param desc
     * @param bw
     * @param src
     * @param srcInt
     * @param srcTag
     * @param dst
     * @param dstInt
     * @param dstTag
     * @param spres
     * @return
     * @throws OESSException
     */
    public ProvisionCircuitResults provisionCircuit(String desc, int bw, 
            String src, String srcInt, int srcTag, String dst, String dstInt, int dstTag,
            ShortestPathResults spres) throws OESSException {
        log.info("Invoking provisionCircuit() with shortets path for " + src + "/" + srcInt + "/" + srcTag + " and " + 
                dst + "/" + dstInt + "/" + dstTag);
        UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(provisionCgiUrl)
            .queryParam(JSON_METHOD, "provision_circuit")
            .queryParam(JSON_WORKGROUP_ID, wgid)
            .queryParam(JSON_PROVISION_TIME, "-1")
            .queryParam(JSON_REMOVE_TIME, "-1")
            .queryParam(JSON_DESCRIPTION, desc)
            .queryParam(JSON_BANDWIDTH, "" + bw)
            .queryParam(JSON_NODE, src)
            .queryParam(JSON_INTERFACE, srcInt)
            .queryParam(JSON_TAG, srcTag)
            .queryParam(JSON_NODE, dst)
            .queryParam(JSON_INTERFACE, dstInt)
            .queryParam(JSON_TAG, dstTag);

        for(ShortestPathResults.Link l: spres.getResults()) {
            builder2.queryParam(JSON_LINK, l.getLink());
        }

        if (debugHttp)
            log.debug(builder2.build().encode().toUri());

        fairLock.lock();
        ProvisionCircuitResults res2 = null;
        try {
            res2 = restTemplate.getForObject(builder2.build().encode().toUri(), ProvisionCircuitResults.class);
            if (res2.getError_text() != null)
                throw new OESSException("Unable to provision circuit between " + src + "/" + srcInt + "/" + srcTag + " and " +
                        dst + "/" + dstInt + "/" + dstTag + " due to " + res2.getError_text());
        }
        finally {
            fairLock.unlock();
        }
        return res2;
    }

    /**
     * Provision a ptop circuit with default path (MPLS only according to docs)
     * @param desc
     * @param bw
     * @param src
     * @param srcInt
     * @param srcTag
     * @param dst
     * @param dstInt
     * @param dstTag
     * @return
     * @throws OESSException
     */
    public ProvisionCircuitResults provisionCircuit(String desc, int bw, 
            String src, String srcInt, int srcTag, String dst, String dstInt, int dstTag) throws OESSException {
        log.info("Invoking provisionCircuit() with default path for " + src + "/" + srcInt + "/" + srcTag + " and " + 
                dst + "/" + dstInt + "/" + dstTag);
        UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(provisionCgiUrl)
            .queryParam(JSON_METHOD, "provision_circuit")
            .queryParam(JSON_WORKGROUP_ID, wgid)
            .queryParam(JSON_PROVISION_TIME, "-1")
            .queryParam(JSON_REMOVE_TIME, "-1")
            .queryParam(JSON_DESCRIPTION, desc)
            .queryParam(JSON_BANDWIDTH, "" + bw)
            .queryParam(JSON_NODE, src)
            .queryParam(JSON_INTERFACE, srcInt)
            .queryParam(JSON_TAG, srcTag)
            .queryParam(JSON_NODE, dst)
            .queryParam(JSON_INTERFACE, dstInt)
            .queryParam(JSON_TAG, dstTag);

        if (debugHttp)
            log.debug(builder2.build().encode().toUri());

        fairLock.lock();
        ProvisionCircuitResults res2 = null;
        try {
            res2 = restTemplate.getForObject(builder2.build().encode().toUri(), ProvisionCircuitResults.class);
            if (res2.getError_text() != null)
                throw new OESSException("Unable to provision circuit between " + src + "/" + srcInt + "/" + srcTag + " and " +
                        dst + "/" + dstInt + "/" + dstTag + " due to " + res2.getError_text());
        }
        finally {
            fairLock.unlock();
        }
        return res2;
    }

    public StatusResults removeCircuit(String circuitId) throws OESSException {
        log.info("Invoking removeCircuit() for " + circuitId);
        UriComponentsBuilder builder3 = UriComponentsBuilder.fromHttpUrl(provisionCgiUrl)
            .queryParam(JSON_METHOD, "remove_circuit")
            .queryParam(JSON_WORKGROUP_ID, wgid)
            .queryParam(JSON_REMOVE_TIME, "-1")
            .queryParam(JSON_CIRCUIT_ID, circuitId);

        if (debugHttp)
            log.debug(builder3.build().encode().toUri());

        StatusResults res3 = null;
        fairLock.lock();
        try {
            res3 = restTemplate.getForObject(builder3.build().encode().toUri(), StatusResults.class);
            if (res3.getError_text() != null)
                throw new OESSException("Unable to remove circuit " + circuitId + " due to " + res3.getError_text());
        }
        finally {
            fairLock.unlock();
        }

        return res3;
    }

    public CircuitDetailsResults circuitDetails(String circuitId) throws OESSException {
        log.info("Requesting circuit details for " + circuitId);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dataCgiUrl)
            .queryParam(JSON_METHOD, "get_circuit_details")
            .queryParam(JSON_CIRCUIT_ID, circuitId);

        if (debugHttp)
            log.debug(builder.build().encode().toUri());

        CircuitDetailsResults cdr = restTemplate.getForObject(builder.build().encode().toUri(), CircuitDetailsResults.class);
        if (cdr.getError_text() != null) 
            throw new OESSException("Unable to request circuit details for " + circuitId + " due to " + cdr.getError_text());

        return cdr;
    }

    public static void main(String[] argv) {
        try {

            Driver d = new Driver("oess", "oess", 1, "https://localhost:20443/oess", NetworkType.openflow, true, false, false);

            WorkgroupResults wres = d.getWorkgroups();
            System.out.println(wres);

            NodeStatusResults nres = d.getAllNodeStatus();
            System.out.println(nres);

            ShortestPathResults spres = d.getShortestPath("DD", "UNC");
            System.out.println(spres);

            ProvisionCircuitResults pcres = d.provisionCircuit("API Circuit", 100, "DD", "DD-eth3", 100, "UNC", "UNC-eth3", 100, spres);
            System.out.println(pcres);

            Thread.sleep(5000);

            CircuitDetailsResults cdr = d.circuitDetails(pcres.getResults().getCircuit_id());
            System.out.println(cdr);

            StatusResults sres = d.removeCircuit(pcres.getResults().getCircuit_id());
            System.out.println(sres);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
