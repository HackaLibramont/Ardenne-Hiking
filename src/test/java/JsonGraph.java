import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import export.JsonFileWriter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.ardennes.pojo.graph.Edge;
import org.ardennes.pojo.graph.Graph;
import org.ardennes.pojo.graph.Node;

import java.io.IOException;
import java.util.*;

public class JsonGraph {

    public static void main(String [] args) throws Exception
    {
        JsonGraph jsonGraph = new JsonGraph();
        JsonFileWriter.writeJsonFile(jsonGraph.getGraph(),"target/graph.json");
    }

    private ClassLoader classLoader;
    private Model model;
    private String getRelationsQuery;
    private String getDetailQuery;
    private Set<String> loaded;

    public JsonGraph() throws Exception {
        this.model= ModelFactory.createDefaultModel();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.getRelationsQuery = readFile(classLoader, "sparql/allRelations.sparql");
        this.getDetailQuery = readFile(classLoader, "sparql/getDetail.sparql");
        this.loaded = new HashSet<>();
        model.read(classLoader.getResourceAsStream("anOntology.turtle"), null, "Turtle");
        System.out.println(model.size());
    }

    public Graph getGraph() throws Exception {
        Graph graph = new Graph();
        graph.setEdges(getEdges());
        graph.setNodes(getNodes());
        return graph;
    }

    private List<Edge> getEdges() {
        QueryExecution qe = QueryExecutionFactory.create(getRelationsQuery, model);
        ResultSet resultSet = qe.execSelect();
        HashSet<Edge> result = new HashSet<>();
        while(resultSet.hasNext())
        {
            QuerySolution sol = resultSet.nextSolution();
            String source =  sol.getResource("Source").toString();
            String target =  sol.getResource("Target").toString();
            loaded.add(source);
            loaded.add(target);
            Edge current = new Edge();
            current.setSource(source);
            current.setTarget(target);
            current.setId(stripLabel(source)+"_"+stripLabel(target));
            result.add(current);
        }
        qe.close();
        List<Edge> edges = new ArrayList<>();
        edges.addAll(result);
        return edges;
    }

    private List<Node> getNodes() {
        HashSet<Node> result = new HashSet<>();
        for (String currentURI:loaded){
            String queryString = getDetailQuery.replaceAll("REPLACEME",currentURI);
            QueryExecution qe = QueryExecutionFactory.create(queryString, model);
            ResultSet resultSet = qe.execSelect();
            while(resultSet.hasNext())
            {
                QuerySolution sol = resultSet.nextSolution();
                Node current = new Node();
                current.setLabel(getPreferedLabel(currentURI, asString(sol, "label1"), asString(sol, "label2")));
                current.setId(currentURI);
                current.setSize(current.getId().length());
                current.setX(new Random().nextInt(100));
                current.setY(new Random().nextInt(100));
                result.add(current);
            }
            qe.close();
        }
        List<Node> edges = new ArrayList<>();
        edges.addAll(result);
        return edges;
    }

    private static String asString(QuerySolution sol, String var) {
        Literal lit = sol.getLiteral(var);
        return lit==null?null:lit.toString();
    }

    private static String readFile(ClassLoader loader, String queryFile) throws IOException {
        return IOUtils.toString(loader.getResourceAsStream(queryFile), "UTF-8");
    }

    private static String getPreferedLabel(String URI, String label1, String label2){
        if (StringUtils.isNotBlank(label1)){
            return label1.replaceAll("@en-gb","");
        } else if (StringUtils.isNotBlank(label2)){
            return label2.replaceAll("@en-gb","");
        }
        return stripLabel(URI);
    }
    
    private static String stripLabel(String startURI) {
        return startURI.replace("ONTO","");
    }

}
