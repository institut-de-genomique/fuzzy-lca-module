package fr.cea.genoscope.sbwh6.lca.util;

public class Taxon {





	private final Integer taxonId;
	private Integer left;
	private Integer right;
	private Integer level;
	private Integer parentTaxId;

	/*
	 * constructors
	 */

	public Taxon(Integer taxonId) {
		super();
		this.taxonId = new Integer(taxonId);
	}


	public Taxon(Integer parentTaxId, Integer taxonId) {
		super();
		this.parentTaxId = parentTaxId;
		this.taxonId = taxonId;
	}

	public Taxon(Integer taxonId, Integer left, Integer right, Integer level,
			Integer parentTaxId) {
		super();
		this.taxonId = taxonId;
		this.left = left;
		this.right = right;
		this.level = level;
		this.parentTaxId = parentTaxId;
	}

	/*
	 * getter/setter
	 */

	public Integer getParentTaxId() {
		return parentTaxId;
	}


	public Integer getLeft() {
		return left;
	}


	public void setLeft(Integer left) {
		this.left = left;
	}


	public Integer getRight() {
		return right;
	}


	public void setRight(Integer right) {
		this.right = right;
	}


	public Integer getLevel() {
		return level;
	}


	public void setLevel(Integer level) {
		this.level = level;
	}


	public Integer getTaxonId() {
		return taxonId;
	}

	@Override
	public String toString() {
		return "Taxon [taxonId=" + taxonId + ", left=" + left + ", right="
				+ right + ", level=" + level + ", parentTaxId=" + parentTaxId
				+ "]";
	}
}
