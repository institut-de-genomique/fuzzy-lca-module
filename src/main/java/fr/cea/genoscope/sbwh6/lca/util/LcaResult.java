package fr.cea.genoscope.sbwh6.lca.util;

import java.util.Collection;

public class LcaResult {

	
	private final Collection<Taxon> partialLCAs;
	private final Taxon trueLCA;
	private final float ratio;
	private final Integer maxLevelDiff;
	private final String readid;
	private final String query;
	
	public LcaResult(String readId,Taxon lcaa, Collection<Taxon> taxonss, float rati, String query){
		readid=readId;
		trueLCA = lcaa;
		partialLCAs = taxonss;
		ratio=rati;
		this.query = query;
		
		//Retrieve new maximum level depth gain
		Integer tmpLevl = 0;
		for(Taxon t : partialLCAs){
			if(t.getLevel()>tmpLevl){
				tmpLevl = t.getLevel();
			}
		}
		if(lcaa!=null && lcaa.getLevel()!=null){
			maxLevelDiff=tmpLevl-lcaa.getLevel();
		}
		else{
			maxLevelDiff=0;
		}
	}

	public Collection<Taxon> getTaxons() {
		return partialLCAs;
	}

	public Taxon getLca() {
		return trueLCA;
	}

	public float getRatio() {
		return ratio;
	}

	public Integer getMaxLevelDiff() {
		return maxLevelDiff;
	}

	public String getReadid() {
		return readid;
	}
	
	public String toString(){
		Collection<Taxon> taxonLcas = this.getTaxons();
		Taxon lca = this.getLca();
		String result = new String();
		String split=new String();
		Taxon t = null;
		for(Taxon tt : taxonLcas){
			t = tt;
			result = result.concat(split+tt.getTaxonId()+"@"+tt.getLevel());
			split=",";
		}

		if(result.contains(",") || t.getTaxonId()!=lca.getTaxonId()){
			return new String("#"+this.getMaxLevelDiff()+"\t"+this.getReadid()+"\t"+lca.getTaxonId()+"@"+lca.getLevel()+"\t"+result+"\t"+query);
		}
		else{
			return new String("-0\t"+this.getReadid()+"\t"+lca.getTaxonId()+"@"+lca.getLevel()+"\t"+result+"\t"+query);
		}
	}

	public String getQuery() {
		return query;
	}
}
