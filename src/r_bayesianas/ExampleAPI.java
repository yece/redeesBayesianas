/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package r_bayesianas;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import org.openmarkov.core.exception.InvalidStateException;
import org.openmarkov.core.inference.InferenceAlgorithm;
import org.openmarkov.core.model.graph.Node;
import org.openmarkov.core.model.network.EvidenceCase;
import org.openmarkov.core.model.network.Finding;
import org.openmarkov.core.model.network.ProbNet;
import org.openmarkov.core.model.network.ProbNode;
import org.openmarkov.core.model.network.Util;
import org.openmarkov.core.model.network.Variable;
import org.openmarkov.core.model.network.potential.TablePotential;
import org.openmarkov.inference.variableElimination.VariableElimination;
import org.openmarkov.io.probmodel.PGMXReader;


public class ExampleAPI {

    public static void main(String[] args) {
        new ExampleAPI();
    }

    // Constants
    final private String bayesNetworkName = "BN-two-diseases.pgmx";
    String res="";
    // Constructor
    public ArrayList<ProbNode> ExampleAPI() {
//        rs=rs;
        List<ProbNode> listPro =null;
        try {
//             Open the file containing the network
//                     InputStream file = new FileInputStream(new File("C:/Users/todohogar/"
//                    + "Documents/UNL/Marzo - Julio 2014/IA/X B/Redes bayesianas/Elvira/"
//                    + "elvira/D.pgmx"));
//            Open the file containing the network
            InputStream file = new FileInputStream(new File("C:/Users/ceci/Desktop/exam.pgmx"));
            // Load the Bayesian network
            PGMXReader pgmxReader = new PGMXReader();
            ProbNet probNet = pgmxReader.loadProbNet(file, bayesNetworkName).getProbNet();
            System.out.println(probNet.getNumNodes()+"---");
            // Create an evidence case
            // (An evidence case is composed of a set of findings)
            listPro = probNet.getProbNodes();
//            a= new ArrayList();
            for (int i = 0; i < listPro.size(); i++) {
                
                ProbNode probNode = listPro.get(i);
                System.out.println("----a " + probNode.getName());
                System.out.println("----b " + probNode.getProbNet());
                System.out.println("----c " + probNode.getRelevance());
                System.out.println("----d " + probNode.getNodeType().toString());
                
                System.out.println("1---- " + probNode.getUtilityFunction());
                System.out.println("11---- " + probNode.getNode().toString());
                System.out.println("12---- " + probNode.getUtilityParents());
                System.out.println("13---- " + probNode.getPolicyType().name());
                
            }
            
                        EvidenceCase evidence = new EvidenceCase();
//
//			// The first finding we introduce is the presence
//			// of the symptom 
			evidence.addFinding(probNet,"Aprobacion Modulo", "Aprobado");
//
//			// Create an instance of the inference algorithm
//			// In this example, we use the variable elimination algorithm
			InferenceAlgorithm variableElimination = new VariableElimination(probNet);
//
//			// Add the evidence to the algorithm
//			// The resolution of the network consists of finding the
//			// optimal policies. 
//			// In the case of a model that does not contain decision nodes
//			// (for example, a Bayesian network), there is no difference between
//			// pre-resolution and post-resolution evidence, but if the model
//			// contained decision nodes (for example, an influence diagram)
//			// evidence introduced before resolving the network is treated 
//			// differently from that introduce afterwards.
			variableElimination.setPreResolutionEvidence(evidence);
//
//			// We are interested in the posterior probabilities of the diseases
			Variable disease1 = probNet.getVariable("Aprobacion Modulo");
//			Variable disease2 = probNet.getVariable("Disease 2");
			ArrayList<Variable> variablesOfInterest = new ArrayList<Variable>();
			variablesOfInterest.add(disease1);
//			variablesOfInterest.add(disease2);
//
//			// Compute the posterior probabilities
			HashMap<Variable, TablePotential> posteriorProbabilities = variableElimination.getProbsAndUtilities();
//
//			// Print the posterior probabilities on the standard output
//			printResults(evidence, variablesOfInterest, posteriorProbabilities);
//
//			// Add a new finding and do inference again
//			// (We see that the presence of the sign confirms the presence
//			// of Disease 1 with high probability and explains away Disease 2)
			evidence.addFinding(probNet, "Aprobacion Modulo", "Aprobado");
			posteriorProbabilities = variableElimination.getProbsAndUtilities(variablesOfInterest);
			res=printResults(evidence, variablesOfInterest, posteriorProbabilities);
                        
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return (ArrayList<ProbNode>) listPro;
    }
    
    
    
    /**
     * Print the posterior probabilities of the variables of interest on the
     * standard output
     * @param evidence. <code>EvidenceCase</code> The set of observed findings
     * @param variablesOfInterest. <code>ArrayList</code> of
     * <code>Variable</code> The variables whoseposterior probability we are
     * interested in
     * @param posteriorProbabilities. <code>HashMap</code>. Each
     * <code>Variable</code> is mapped onto a <code>TablePotential</code>
     * containing its posterior probability
     */
    public String printResults(EvidenceCase evidence, ArrayList<Variable> variablesOfInterest,
            HashMap<Variable, TablePotential> posteriorProbabilities) {
        String rs="";
        // Print the findings
        System.out.println("Evidence:");
        for (Finding finding : evidence.getFindings()) {
            System.out.print("  " + finding.getVariable() + ":----------------- ");
            rs=rs+"Evidence: "+finding.getVariable()+" ["+finding.getState()+"]";
            System.out.println(finding.getState());
        }
        // Print the posterior probability of the state "present" of each variable of interest
        System.out.println("Posterior probabilities: ");
        for (Variable variable : variablesOfInterest) {
            double value;
            TablePotential posteriorProbabilitiesPotential = posteriorProbabilities.get(variable);
            System.out.print("  " + variable + ":");
            rs=rs+"         Posterior probabilities: "+variable;
            int stateIndex = -1;
            try {
                stateIndex = variable.getStateIndex("Aprobado");
                value = posteriorProbabilitiesPotential.values[stateIndex];
                rs=rs+" "+Util.roundedString(value, "0.008");
                System.out.println(Util.roundedString(value, "0.001"));
            } catch (InvalidStateException e) {
                System.err.println("State \"Aprobado\" not found for variable \""+ variable.getName() + "\".");
                e.printStackTrace();
            }
            System.out.println(""+rs);
        }
        System.out.println();
        return rs;
    }
    
    public void presentarTabla(JTable tabla,JTextArea a1,JLabel jl) throws Exception{
        ArrayList<ProbNode> rs2=this.ExampleAPI();
        String a ="";
        int numfilas=rs2.size();
        Object[] columns = new String [] {"Nombre Nodo","Tipo","Pertinencia","Max","Min","Función de Utilidad","x","y","H","P"};
        Object [][] datos = new String [numfilas][columns.length];
        for(int i = 0; i< rs2.size();i++){
                datos[i][0] = rs2.get(i).getName();
                datos[i][1] = String.valueOf(rs2.get(i).getNodeType());
                datos[i][2] = String.valueOf(rs2.get(i).getRelevance());
                datos[i][3] = String.valueOf(rs2.get(i).getApproximateMaximumUtilityFunction());
                datos[i][4] = String.valueOf(rs2.get(i).getApproximateMinimumUtilityFunction());
                datos[i][5] = String.valueOf(rs2.get(i).getUtilityFunction());
                datos[i][6] = String.valueOf(rs2.get(i).getNode().getCoordinateX());
                datos[i][7] = String.valueOf(rs2.get(i).getNode().getCoordinateY());
                datos[i][8] = String.valueOf(rs2.get(i).getNode().getChildren());
                datos[i][9] = String.valueOf(rs2.get(i).getNode().getParents());
                a=a+String.valueOf(rs2.get(i).getProbNet())+"\n-------------------------------------------------------------------\n";
    }   
        javax.swing.table.TableModel dataModel = new javax.swing.table.DefaultTableModel(datos,columns); 
        tabla.setModel(dataModel);
        a1.setText(a);
        jl.setText(res);
    }
}
