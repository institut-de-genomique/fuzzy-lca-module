package fr.cea.ig.sbwh6.lca.p;

public class Counter {
	
	private Long counter;
	private Long totalReadsAnalyzed;
	
	public Counter(){
		counter = new Long(0);
	}
	
	public void add(){
		synchronized(counter) {
			counter=counter+1;
        }
	}
	public void del(){
		synchronized(counter) {
			counter=counter-1;
        }
	}

	public Long getCounter() {
		return counter;
	}

	public Long getTotalReadsAnalyzed() {
		return totalReadsAnalyzed;
	}

	public void setTotalReadsAnalyzed(Long totalReadsAnalyzed) {
		this.totalReadsAnalyzed = totalReadsAnalyzed;
	}

}
