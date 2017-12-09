package entityRetrieval.core;

import java.util.Comparator;

public class Pair<L, R> {
	private L l;
    private R r;
    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }
    public L getL(){ return l; }
    public R getR(){ return r; }
    public void setL(L l){ this.l = l; }
    public void setR(R r){ this.r = r; }
    
    
    
    public static Comparator<Pair> EntityMentionsComparator = new Comparator<Pair>() {
    	public int compare(Pair p1, Pair p2) {
    		   int first = (Integer) p1.getR();
    		   int second = (Integer) p2.getR();
    		   //ascending order
    		   return first-second;
    }




    	
    };

    

}
