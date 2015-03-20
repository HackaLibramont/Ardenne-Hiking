import export.CsvFileWriter;
import org.openthings.pojo.graph.Graph;

public class CsvGraph {

    public static void main(String [] args) throws Exception
    {
        // Quick export to gelphi
        Graph graph = new JsonGraph().getGraph();
        CsvFileWriter.writeCsvEdgesFile(graph.getEdges(),"target/edges.csv");
        CsvFileWriter.writeCsvNodesFile(graph.getNodes(), "target/nodes.csv");
    }
    
}
