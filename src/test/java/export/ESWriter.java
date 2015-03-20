package export;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by cvasquez on 20.03.15.
 */
public class ESWriter {

    private static final Logger log = Logger.getLogger(ESWriter.class);

    protected Client client;

    public ESWriter() {
        log.info("Starting up Elastic Search connection");
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "sniper")
                .put("client.transport.sniff", true)
                .build();

        this.client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(
                        "localhost", 9300));
    }


    public String write(Object object,String type) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        return write(mapper.writeValueAsString(object), type);
    }

    private String write(final String type, final String json) throws Exception {
        final IndexResponse response = client
                .prepareIndex("ardennes", type).setSource(json).execute()
                .actionGet();
        return response.getId();
    }

}
