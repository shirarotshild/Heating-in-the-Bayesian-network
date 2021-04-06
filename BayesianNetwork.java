import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
/*/
 * class that implements a Bayesian network
 */
public class BayesianNetwork {
	Hashtable<String, Node> hashTable;
	String []variables;
	int [] value;
	//////////constructor\\\\\\\\\\
	public BayesianNetwork() {
		hashTable =new Hashtable<String, Node>();
	}
	/*/
	 * copy constructor
	 */
	public BayesianNetwork( BayesianNetwork bn) {
		hashTable =new Hashtable<String, Node>();
		for (Entry<String, Node> e : bn.hashTable.entrySet()) {
			Node n =new Node(e.getValue());
			hashTable.put(e.getKey(), n);
		}
	    this.variables=bn.variables;
		this.value=bn.value;
	}
	//////////Methods\\\\\\\\\\
	public void addVariables(String variables) {
		this.variables=variables.split(",");
		this.value= new int [this.variables.length];
	}
	public String toString() {
		return hashTable.toString() +Arrays.toString(variables) +Arrays.toString(value)+"\n";
	}
}



