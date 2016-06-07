
public class term{
		 int docid;  // document id 
		 int freq;  // frequency of the term in the document
	      public term(String doc, String frequency){
	    	this.docid = Integer.parseInt(doc);
	    	this.freq = Integer.parseInt(frequency);
	    }
	}
