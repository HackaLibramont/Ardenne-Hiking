package export;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.ardennes.Constants;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
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


    public DeleteIndexResponse deleteIndex(){
        final DeleteIndexRequest deleteIndexRequest=new DeleteIndexRequest(Constants.INDEX);
        return this.client.admin().indices().delete(deleteIndexRequest).actionGet();
    }

    public String write(String type,Object object,String id) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        IndexResponse response = client.prepareIndex(Constants.INDEX, type)
                .setSource(mapper.writeValueAsString(object))
                .setId(id)
                .execute()
                .actionGet();
        return response.getId();
    }


    public String write(String type,Object object) throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        IndexResponse response = client.prepareIndex(Constants.INDEX, type)
                .setSource(mapper.writeValueAsString(object))
                .execute()
                .actionGet();
        return response.getId();
    }

}
