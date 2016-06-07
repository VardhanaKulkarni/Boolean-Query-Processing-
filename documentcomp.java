import java.util.Comparator;
class documentcomp implements Comparator<term> {

	    @Override
	   public int compare(term o1, term o2) {
	    	if(o1.docid > o2.docid)
	    		return 1;
	    	else if(o1.docid < o2.docid)
	    		return -1;
	    	else
	    	return 0;
	  }
}

