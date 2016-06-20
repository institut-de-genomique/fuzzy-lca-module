package fr.cea.genoscope.sbwh6.lca.util;

public class WeightedTaxon implements Comparable<WeightedTaxon>{
	
	private String taxId;
	private Double weight;
	
	public WeightedTaxon(String t, Double w)
	{
		setTaxId(t);
		setWeight(w);
		
	}
	
	public void setWeight(Double w)
	{
		this.weight = w;
	}
	
	public void setTaxId(String t)
	{
		this.taxId = t;
	}
	
	public Double getWeight()
	{
		return this.weight;
	}
	
	public String getTaxId()
	{
		return this.taxId;
	}
	
	public String toString()
	{
		String s = new String();
		s.concat(this.taxId).concat(":").concat(this.weight.toString());
		return s;
		
	}

	@Override
	public int compareTo(WeightedTaxon o) {
		// TODO Auto-generated method stub
		return this.weight.compareTo(o.getWeight());
		
	}
}
