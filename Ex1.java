import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.ProtectionDomain;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Ex1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try {
			FileWriter myWriter = new FileWriter("output.txt");
		} catch (IOException e) {

			e.printStackTrace();
		}

		read("input.txt");
	}
	private static void read(String file_name) {
		String line;
		int counter=0;
		ArrayList<double []> listAns=new ArrayList<double []>();
		BayesianNetwork bn=new BayesianNetwork();
		String var="";
		try {
			File file=new File("input.txt");
			Scanner scanner=new Scanner(file);
			while (scanner.hasNextLine()) { 
				Node n;
				line=scanner.nextLine();
				if(line.contains("Variables:")) {
					line=line.substring(11);
					bn.addVariables(line);
					line=scanner.nextLine();
				}
				if(line.contains("Var")) {
					var =line.substring(4);
					line=scanner.nextLine();
					String values=line.substring(8);
					line=scanner.nextLine();
					String parents=line.substring(9);
					n=new Node(values,parents);
					bn.value[counter++]=n.values.length;
					bn.hashTable.put(var,n);
				}
				if(line.contains("CPT:")) {
					line=scanner.nextLine();
					int index=0;
					while(line.equals("") == false) {
						for(int i=0; i<line.length();i++) {
							if(line.charAt(i) == '=') {
								index=i;
								break;
							}
						}

						String parent="";
						if(index != 0) {
							parent=line.substring(0,index);
						}
						String me=line.substring(index+1);
						String [] me1=me.split(",");
						String key="";
						String tempKey="";
						String value="";
						double sumValue=0;
						for(int i=0; i<me1.length; i++) {
							if(me1[i].contains("=")) {
								me1[i]=me1[i].substring(1);
							}
							if(parent.equals("")) {
								key="P("+var+"="+me1[i]+")";
								i++;
								value=me1[i];
								sumValue+=Double.parseDouble(value);
								bn.hashTable.get(var).cpt.put(key, value);
							}
							else {
								String [] parent1=parent.split(",");
								key="P("+var+"="+me1[i]+"|";
								tempKey="";
								for(int j=0; j<parent1.length; j++) {
									key+=bn.hashTable.get(var).parents[j]+"="+parent1[j];
									tempKey+=bn.hashTable.get(var).parents[j]+"="+parent1[j];
									if(j!=parent1.length-1) {
										key+=",";
										tempKey+=",";
									}
								}
								key+=")";
								i++;
								value=me1[i];
								sumValue+=Double.parseDouble(value);
								bn.hashTable.get(var).cpt.put(key, value);
							}
						}
						for(int i=0; i<bn.hashTable.get(var).values.length; i++) {
							boolean flag=false;
							for(int j=0; j<me1.length; j++) {
								if( bn.hashTable.get(var).values[i].equals(me1[j])) {
									flag = true;
								}
								j++;
							}
							if(!flag) {
								if(parent=="") {
									key="P("+var+"="+bn.hashTable.get(var).values[i]+")";
									value=String.format("%.5f", 1-sumValue);
									bn.hashTable.get(var).cpt.put(key, value);

								}
								else {
									key="P("+var+"="+bn.hashTable.get(var).values[i]+"|"+tempKey+")";
									value=String.format("%.5f", 1-sumValue);
									bn.hashTable.get(var).cpt.put(key, value);
								}
							}
						}
						line=scanner.nextLine();

					}
				}
				if(line.contains("Queries")) {
					line=scanner.nextLine();
					while(!scanner.equals("")) {
						String algo=line.substring(line.length()-1);
						double flag=bn.hashTable.get(line.substring(2,line.indexOf("="))).containsKey(line.substring(0, line.length()-2));

						if(flag !=-1) {
							double []ans= {flag,0,0};
							listAns.add(ans);

						}
						else {
							line=line.substring(2, line.length()-3);
							if(algo.equals("1")) {
								Algo1 algo1= new Algo1(bn,line);
								double []ans=algo1.start();
								listAns.add(ans);
							}
							else if(algo.equals("2")) {
								Algo2 algo2= new Algo2(bn,line);
								double []ans=algo2.start();
								listAns.add(ans);
							}
							else {
								Algo3 algo3= new Algo3(bn,line);
								double []ans=algo3.start();
								listAns.add(ans);
							}
						}
						if(!scanner.hasNext())break;
						line=scanner.nextLine();
					}
				}
			}

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		try {
			PrintWriter writer = new PrintWriter("output.txt");
			for(int i=0; i<listAns.size(); i++) {
				double [] ans =listAns.get(i);
				writer.print(String.format("%.5f", ans[0])+",");
				writer.print((int)ans[1]+",");
				writer.print((int)ans[2]+"\n");		   
			}

			writer.close();

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

}




