import java.util.Comparator;
class CompareTopK implements Comparator<term2> {

	    @Override
	   public int compare(term2 obj1, term2 obj2) {
	    	if(obj1.postingSize > obj2.postingSize)
	    		return -1;
	    	else if(obj1.postingSize < obj2.postingSize)
	    		return 1;
	    	else
	    	return 0;
	    //return (o1.freq-o2.freq);
	    }
	}
