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
import pojo.Tree;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class JsonTree {

    public static void main(String [] args) throws Exception
    {
        JsonTree jsonTree = new JsonTree();
        JsonFileWriter.writeJsonFile(jsonTree.getTree("<REPLACEME>"), "target/taxonomy.json");
    }
    
    private ClassLoader classLoader;
    private Model model;
    private String getChildrenQuery;
    private Set<String> visited;
    
    public JsonTree() throws Exception {
        this.model= ModelFactory.createDefaultModel();
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.getChildrenQuery = readFile(classLoader, "sparql/getChildren.sparql");
        this.visited = new HashSet<>();
        model.read(classLoader.getResourceAsStream("openThingsOnto.turtle"), null, "Turtle");
        System.out.println(model.size());
    }

    public Tree getTree(String startURI) throws Exception {
        Tree head = new Tree();
        head.setURI(startURI);
        head.setName(stripLabel(startURI));
        head.setChildren(getChildrenNodes(startURI));
        return head;
    }

    private Set<Tree> getChildrenNodes(String startURI) {
        this.visited.add(startURI);
        String queryString = getChildrenQuery.replaceAll("<REPLACEME>",startURI);
        QueryExecution qe = QueryExecutionFactory.create(queryString, model);
        ResultSet resultSet = qe.execSelect();
        HashSet<Tree> result = new HashSet<>();
        while(resultSet.hasNext())
        {
            QuerySolution sol = resultSet.nextSolution();
            Tree current = new Tree();
            String currentURI =  sol.getResource("Target").toString();
            current.setURI(currentURI);
            current.setName(getPreferedLabel(currentURI, asString(sol, "label1"), asString(sol, "label2")));
            if (!this.visited.contains(currentURI)) {
                Set<Tree> children = getChildrenNodes(currentURI);
                if (children.isEmpty()){
                    current.setSize(1);
                } else {
                    current.setChildren(children);
                }
            }
            result.add(current);
        }
        qe.close();
        return result;
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
        return startURI.replace("<REPLACEME>","");
    }

}
