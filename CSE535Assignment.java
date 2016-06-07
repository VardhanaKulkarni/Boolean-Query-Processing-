import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class CSE535Assignment {
	
	public static void main(String[] args) throws IOException {
        try{		
		  FileReader input1 = new FileReader(args[0]); /* Reads the input index file */
		  BufferedReader bufRead = new BufferedReader(input1);
		  String line1 = null;
		 System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream(args[1])), true));
		  
	      /*create two Hash maps*/
		  HashMap<String, LinkedList<term>> Map1 =  new HashMap<String,LinkedList<term>>();  
		  HashMap<String, LinkedList<term>> Map2 = new HashMap<String,LinkedList<term>>();
		  
		  /* linked list to store the terms with its posting size */
   		  LinkedList<term2> List_of_terms = new LinkedList<term2>();
	
          while((line1 = bufRead.readLine())!= null)  /* parse the file line by line */
          {   
        	  String[] array3 = line1.split("\\\\");
  
        	  /*SizeOfList stores the posting list size*/ 
   		      String[] Size = array3[1].split("c");
   		      int sizeOfList = Integer.parseInt(Size[1]);
   		      
   		      /*Name of the term whose posting list is stored */
   		      String termName = array3[0];
   		    
   		 
   		      String[] index1 = array3[2].split("[m,\\[\\]\\s]+");
   		  
   		      /* Linked list to store the terms in the increasing order of document id.*/
   		      LinkedList<term> List_docs = new LinkedList<term>(); 
   		  
   		      /*Linked list to store the terms in the decreasing order of frequency*/
   		      LinkedList<term> List_freq = new LinkedList<term>();
   		 
   		      /* Adding the term name and Posting list size to the linked list */
   		      List_of_terms.add(new term2(termName,sizeOfList));  
   		      
   		      for(int i = 1; i<index1.length;i++){
   		    	  String[] index2 = index1[i].split("/");
   		    	  String document_id = index2[0];
   		    	  String termFrequency = index2[1];
   		    	  List_docs.add(new term(document_id,termFrequency));
   		    	  List_freq.add(new term(document_id,termFrequency));   
   		      }  		      
   		      Collections.sort(List_freq, new Compareterm());
   		  
   		      Map1.put(termName, List_docs);
   		      Map2.put(termName,List_freq);
          } 
          bufRead.close();
          
          /* Calling the function to find the top K terms */
          int K = Integer.parseInt(args[2]);
          getTopKTerms(List_of_terms, K);
         
          /*Reads the input query file*/
          FileReader input2 = new FileReader(args[3]); 
          BufferedReader bufRead2 = new BufferedReader(input2);
          String line2 = null;
          	
          /* parse the file line by line*/
          while((line2 = bufRead2.readLine())!= null) 
          {   
        	  String[] Query = line2.split(" ");
        	  for(int i = 0;i < Query.length;i++){
        		 /*print the posting list for all the query terms);*/
	        	 getPostings(Map1,Map2,Query[i]); 
        	  }
        	  termAtATimeQueryAnd(Query,Map2);
        	  termAtATimeQueryOr(Query,Map2);
        	  docAtATimeQueryAnd(Query,Map1);
        	  docAtATimeQueryOr(Query,Map1);
          }
          bufRead2.close();
          }catch(IOException e){
    	     e.printStackTrace();       
    	}
    } 	
	
	/*Function to get the top K terms. This function takes the linked list 
	//of all the terms as one of the parameters.The terms are arranged according
	//to the decreasing order of their posting list size. The  top K terms are 
	//retrieved by the top K terms in the sorted linked list of terms.*/
	
	public static void getTopKTerms(LinkedList<term2> ListOfterms, int K ){
		
		/*sort the terms in the decreasing order of their posting size*/
		Collections.sort(ListOfterms, new CompareTopK());
		
		//print the top K terms 
		System.out.print("FUNCTION: getTopK " + K + "\nResult: " );
		for(int i=0;i<ListOfterms.size();i++){
			System.out.print(ListOfterms.get(i).name); 
			if(i==K-1){
				System.out.print("\n");
				break;
			}
			else
				System.out.print(", ");	
		}
	}	
		
	/*Function to get the  both the posting lists of the term ordered by 
     *document id and frequency id */
	public static void getPostings(HashMap<String,LinkedList<term>> Map1, HashMap<String,LinkedList<term>> Map2, String SearchTerm){
			
		/*retrieve the posting list  ordered according to 
		 increasing document id for the query term*/
		LinkedList<term> list1 = Map1.get(SearchTerm); 
			
		/*retrieve the posting list  ordered according to 
		*decreasing freq for the query term*/
		LinkedList<term> list2 = Map2.get(SearchTerm);
			
	   // Check if the query term is a valid key in the hashmap
		if (Map1.containsKey(SearchTerm)) {
			System.out.print("\nFunction: getPostings" +" "+ SearchTerm);
			
			//Display the Posting list of query term in the 
			//increasing document Id
			System.out.print("\nOrdered by Doc: "); 
	        for(int i=0;i<list1.size();i++){
	             if(i==list1.size()-1)
	            	 System.out.print(list1.get(i).docid + " "); 
	             else
	       	          System.out.print(list1.get(i).docid + ", "); 
	        }
	        
	        //Display the Posting list of query term in the 
			//decreasing Frequency
	        System.out.print("\nOrdered by TF: "); 
	        for(int i=0;i<list2.size();i++)
	        	if(i==list2.size()-1)
	            	 System.out.print(list2.get(i).docid + " "); 
	             else
	       	          System.out.print(list2.get(i).docid + ", ");  
		}else 
		{
			//System.out.println();
			System.out.println("Term Not Found");
		}
	 }
	 
	/* Function implements the term at a  time " and "query processing. The input to the function is
	* input query string and hash map containing the words as the key and the corresponding postings list 
	* as the value. Firstly a check is made if the queries are valid and as this is "and" operation, if any
	* of the queries is invalid then return to the main function. If all the input queries are valid den 
	* further processing is carried out.The posting list of each query is considered  sequentially considering 
	* one query posting list at a time*/
	
	public static void termAtATimeQueryAnd(String[] inputQuery,HashMap<String,LinkedList<term>> Map21 ){
		/*resultList stores result of the and query operation */
		LinkedList<term> resultList1 = new LinkedList<term>();
	    LinkedList<term> interList = null;
	    
	    /*Variable used to store the number of Comparisons*/
		int numOfComp = 0;
		
		/*Variable used to store the comparison time */
		float compTime = 0;
		
		/*Variable used to store the current time */
		long startTime = System.currentTimeMillis();
		
		/*If any of the input query terms is invalid then it returns to the 
		 * main function.Here we consider each query term's posting list at a time
		 * The first list is directly added to the result list and den for the next 
		 * query terms, each of the document ids in the new posting list is considered 
		 * and only the ones that match are retained. This is carried out until all the 
		 * query terms are processed */
		for(int i=0; i < inputQuery.length;i++){
			if(Map21.containsKey(inputQuery[i])){
				if(i==0){
					for(int c=0 ;c < Map21.get(inputQuery[i]).size();c++ ){
						resultList1.add(Map21.get(inputQuery[i]).get(c));
					}	
				}
				else{
					 interList = Map21.get(inputQuery[i]);
					for(int j = 0;j < resultList1.size();j++){
						int matches=0;
						for(int k=0;k < interList.size();k++){
							numOfComp++;
							if(resultList1.get(j).docid == interList.get(k).docid){
								matches++;
							}
						}
						if(matches==0){
							resultList1.remove(j);	
						    j--;
						}
					}
				}
				
		    } else {
		    	System.out.println("\nFunction : termAtATimeQueryAnd ");
				System.out.println("Terms not found");
				return;
			}
	      }
		   Collections.sort(resultList1,new documentcomp());	
			long endTime = System.currentTimeMillis();
			compTime = (endTime-startTime)/1000f;
			showOutput("termAtATimeQueryAnd",inputQuery,resultList1,resultList1.size(),numOfComp,compTime);
	}
	
	
	/* Function implements the term at a time query processing. In this function, each posting list of 
	 * the query term is considered and if all the  query terms are invalid then return to the main function
	 * The posting lists of all the query terms are considered sequentially and compared to the result list 
	 * and added to the result list. The result list will contain the intersection of all the query posting lists*/
	public static void termAtATimeQueryOr(String[] inputQuery,HashMap<String,LinkedList<term>> Map22 ){
		LinkedList<term> resultList2= null;
	    //LinkedList<term> finalList = Map2.get(inputQuery[0]);
		LinkedList<term> interList0 = null;
		
		/*Variable to store the Number of comparisons */
		int numOfComp = 0;
		
		/*Variable to store the comparison time */
		float compTime = 0;
		
		/*Variable to store the count*/
		int count =0;
		
		/* Checking if all the query terms are invalid */
		for(int i = 0;i < inputQuery.length; i++){
			interList0 = Map22.get(inputQuery[i]);
			if(interList0==null){
				count++;
			}
		}
		if(count == inputQuery.length){
			System.out.println("Function : termAtATimeQueryOr ");
			System.out.println("Terms not Found");
			return;
		}
		long startTime = System.currentTimeMillis();
		/*This loop performs the union of all the posting lists for the input
		 *  query terms and the document ids that are already in the result list 
		 *  are not added again */
		for(int i=0; i < inputQuery.length;i++){
			if(Map22.containsKey(inputQuery[i])){
				if(resultList2 == null){
					 resultList2 = Map22.get(inputQuery[i]); 
				}
				else{
					LinkedList<term> interList1 = Map22.get(inputQuery[i]);
					for(int j = 0;j < interList1.size();j++){
						int matches=0;
						for(int k=0;k < resultList2.size();k++){
						numOfComp++;
							if(resultList2.get(k).docid == interList1.get(j).docid){
									matches++;
							}
						}
						if(matches==0){
						resultList2.add(interList1.get(j));	
						}
					}
				}	
		   }
		}
		
		int found = resultList2.size();
		if(found==0) {
			System.out.println("Function : termAtATimeQueryOr ");
			System.out.println("Terms not Found");
		} else {
			Collections.sort(resultList2, new documentcomp());
			long endTime = System.currentTimeMillis();
			compTime =(endTime-startTime)/1000f;
			showOutput("termAtATimeQueryOr",inputQuery,resultList2,resultList2.size(),numOfComp,compTime);
			System.out.println("\n"+resultList2.size());
		}
	}
		
	/*Function to implement document at a time "And" query processing. This function takes the query
	 * and  hash map containing the terms as the key values  as the parameters. This performs the 
	 * union of all the posting lists by considering the posting lists of all terms  simultaneously */
	public static void docAtATimeQueryAnd(String[] inputQuery,HashMap<String,LinkedList<term>> Map1 ){
			
		/*This linked list is used to store the result list 
		*containing the term whose document Ids that are 
		*present in the posting list of all the query terms*/
		LinkedList<term> resultList = new LinkedList<term>();
			
		/*This is a  linked list of all posting lists of the given query terms */
		LinkedList<LinkedList<term>> list = new LinkedList<LinkedList<term>>();
		LinkedList<term> interList = null;
			
		//This is the array used to store the pointers to
		//all the postings list of the query terms
		int[] pointTo = new int[inputQuery.length];		
		int numOfComp = 0;
		float compTime = 0;
			
		/*access the posting lists of all the query terms simultaneously
		*and store it in another list  and initialize the pointers to all
		*the links to point to the first term in its posting list*/
			
		for(int i = 0;i < inputQuery.length; i++){
			interList =  Map1.get(inputQuery[i]);
			if(interList==null){
				System.out.println("Function : docAtATimeQuery And");
				System.out.println("Terms not Found");
				return;
			}else{
				list.add(interList); 
			}
			pointTo[i] = 0;
		}
			
		/* Initailize the maximum document number to 0*/	
		int maxDoc = 0;	
		
		/* Get the current time to calculate the comparison time */
		long startTime = System.currentTimeMillis();
		
		/*This  loop executes until any one or all of the posting lists
		 * reach the end. The posting lists of all the query terms are parsed 
		 * in parallel. Initially the pointer is set to zero  and the maximum
		 *  doc id is calculated. Now the comparisons are mad 
		 * */
		while(true){
			for(int p = 0;p < list.size();p++) {
				if (pointTo[p] == list.get(p).size()) {
					if(resultList.size() > 0){
						Collections.sort(resultList, new documentcomp());
						long endTime = System.currentTimeMillis();
						compTime = (endTime-startTime)/1000f;
						showOutput("docAtATimeQueryAnd",inputQuery,
										resultList,resultList.size(),numOfComp,compTime);
					}else{
						System.out.println("Function : docAtATimeQueryAnd ");
						System.out.println("Terms not Found ");
					}
					return;
				}
				if(maxDoc < list.get(p).get(pointTo[p]).docid) {
						maxDoc = list.get(p).get(pointTo[p]).docid;
				}
			}
				
			int count = 0;
			for (int p = 0; p < list.size(); p++) {
				for(int l=pointTo[p]; l<list.get(p).size(); l++){
					numOfComp++;
					if(list.get(p).get(pointTo[p]).docid < maxDoc){
						pointTo[p]++;
					}else if(list.get(p).get(pointTo[p]).docid == maxDoc){
						count++;
						break;
					}else{
						break;
					}
				}
			}
			
			if(count == list.size()){
					resultList.add(list.get(0).get(pointTo[0]));
			}
		    
			for(int i = 0;i < inputQuery.length; i++){
				if(pointTo[i] < list.get(i).size()) {
					pointTo[i] ++;
				}
			}
		}
	}
		
		
	/*Function to implement document at a time "And" query processing. In document at a time query And operation
	 *the posting lists of the query terms are considered simutaneously.The posting lists of all the query terms are
	 *stored in a linked list. A pointer is initialized to point to the first term in each of the postings list. The 
	 * pointer is simultaneously updated for all the posting lists and the documents that are present in all the input 
	 *  query terms are added to the result list  */

	public static void docAtATimeQueryOr(String[] inputQuery,HashMap<String,LinkedList<term>> Map1 ){
					
		/*This linked list is used to store the result list */
		LinkedList<term> resultList = new LinkedList<term>();
					
		/*This is a  linked list of all posting lists of the given query terms */ 
		LinkedList<LinkedList<term>> list = new LinkedList<LinkedList<term>>();
		
		/*This is a linked list to store the posting list temporarily to check if its valid */
		LinkedList<term> interList = null;
		
					
		/*This is the array used to store the pointers to
		*all the postings list of the query terms*/
		int[] pointTo = new int[inputQuery.length];	
		int count =0;
					
		/* Access the posting lists of all the query terms simultaneously
		and if it is a valid term then store it in another list */ 
		
		for(int i = 0;i < inputQuery.length; i++){
			interList = Map1.get(inputQuery[i]);
			if(interList==null){
				count++;
			}
			else{
					list.add(interList); 
			} 
			pointTo[i] = 0;
		}
		
		/*Exits the function if all all the query terms are not found 
		*in the database*/
		
		if(count==inputQuery.length){
			System.out.println("Function : docAtATimeQueryOr ");
			System.out.println("Terms  d not found");
			return;
		}
		
		/* Variable used to store the maximum size of posting lists of the query terms */
		int maxDoc = 0;
		
		/* Variable used to store the number of comparisons */
		int numOfComp = 0;
		
		/*Variable used to store the comparison time */
		float compTime = 0;
		
		/* Calculates the maximum posting list size among the 
		 * posting lists of the input queries*/
		for(int i = 0;i < list.size();i++){
			if(i==0){
				maxDoc = list.get(i).size();
			}else{
				if(list.get(i).size() > maxDoc)
					maxDoc= (list.get(i).size());
			}	
		}	
		
		/*stores the start time of the comparison*/
		long startTime = System.currentTimeMillis();
		
		/* This loop is executed until all the query term posting lists reach the end 
		 * The result List gets updated here.If the result list is empty then the
		 * the first term is added directly to the list. Later the pointer is updated
		 * in parallel for all the postings list and the terms not present in the result
		 * are added to list*/ 
		for(int j=0;j<maxDoc; j++){
			for(int p = 0;p < list.size(); p++){
				if(resultList.isEmpty()){
					resultList.add(list.get(p).get(pointTo[p]));
					pointTo[p]++;
				}else{
					int numMatches = 0;
					if (pointTo[p] < list.get(p).size()) {
						for(int i = 0;i < resultList.size();i++){
							numOfComp++;
							if(list.get(p).get(pointTo[p]).docid == resultList.get(i).docid){
								numMatches++;
								break;
							}
						}
						if (0 == numMatches) {
							resultList.add(list.get(p).get(pointTo[p]));
						}
						pointTo[p]++;
					}
				}
			}		
		}
		
		/* Sorts the result list in the increasing order of the document Id*/
		Collections.sort(resultList, new documentcomp());
		
		/*Stores the end time of comparison*/
		long endTime = System.currentTimeMillis();
		
		/*Computes the time of comparison */
		compTime = (endTime - startTime)/1000f;
		
		/* print the result*/
		showOutput("docAtATimeQueryOr",inputQuery,resultList,resultList.size(),numOfComp,compTime);	
	}
	
	/* Function used to print the output to the file. This Function displays the 
	 * function name, the queries to the function, the result, time to compute the result
	 * and the number of comparison */
	public static void showOutput(String FuncName,String[] inputQuery, LinkedList<term> resultList,int documents,int comparisons,float seconds){
		System.out.print("\nFunction:" +" "+ FuncName + " " );
		for( int z=0; z<inputQuery.length; z++){
			if(z==(inputQuery.length-1))
				System.out.print(inputQuery[z]);
			else
				System.out.print(inputQuery[z]+ ", ");
		}
		System.out.println("\n" + documents + " documents are found");
		System.out.println(comparisons  + "  comparisons are made");
		System.out.println(seconds  + "  seconds are used");
		System.out.print("Result : ");
		for( int z=0;z<resultList.size(); z++)
			if(z==(resultList.size()-1))
				System.out.print(resultList.get(z).docid);
			else
				System.out.print(resultList.get(z).docid + ", ");
	}
		
}

		


	
