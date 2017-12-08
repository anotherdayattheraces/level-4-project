package entityRetrieval.core;

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
    
    public int compareTo(Pair other) {
        int compareVal=(Integer) other.getR();
        /* For Ascending order*/
        return (Integer) this.getR()-compareVal;

        /* For Descending order do like this */
        //return compareage-this.studentage;
    }

}
