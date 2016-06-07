import java.util.*;
class Compareterm implements Comparator<term> {

	    @Override
	   public int compare(term o1, term o2) {
	    	if(o1.freq > o2.freq)
	    		return -1;
	    	else if(o1.freq < o2.freq)
	    		return 1;
	    	else
	    	return 0;
	    //return (o1.freq-o2.freq);
	    }
	}

