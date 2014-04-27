package com.github.lpezet.java.patterns.worker.samples;

/* Domain classes */
class SearchParameters {
	private int mFrom;
	private int mTo;
	private String mFilters;
	public SearchParameters(int pForm, int pTo, String pFilters) {
		mFrom = pForm;
		mTo = pTo;
		mFilters = pFilters;
	}
	public String getFilters() {
		return mFilters;
	}
	public int getFrom() {
		return mFrom;
	}
	public int getTo() {
		return mTo;
	}
	
	@Override
	public String toString() {
		StringBuffer oBuf = new StringBuffer("[from:")
			.append(mFrom).append(", to:").append(mTo).append(", filters:").append(mFilters);
		return oBuf.toString();
	}
}