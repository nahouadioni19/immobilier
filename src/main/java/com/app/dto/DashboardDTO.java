package com.app.dto;
public class DashboardDTO {

    private int totalAPayer;
    private int totalPaye;
    private int totalRetard;

    public DashboardDTO(int totalAPayer, int totalPaye, int totalRetard) {
        this.totalAPayer = totalAPayer;
        this.totalPaye = totalPaye;
        this.totalRetard = totalRetard;
    }

    // getters
    
	public int getTotalAPayer() {
		return totalAPayer;
	}

	public void setTotalAPayer(int totalAPayer) {
		this.totalAPayer = totalAPayer;
	}

	public int getTotalPaye() {
		return totalPaye;
	}

	public void setTotalPaye(int totalPaye) {
		this.totalPaye = totalPaye;
	}

	public int getTotalRetard() {
		return totalRetard;
	}

	public void setTotalRetard(int totalRetard) {
		this.totalRetard = totalRetard;
	}
   
}