package com.app.dto;
public class RecouvrementDTO {

    private String locataireNom;
    private Long loyerMensuel;
    private Long montantPaye;
    private Long reste;
    private String statut; // PAYE, PARTIEL, IMPAYE, RETARD
    
 // ✅ CONSTRUCTEUR VIDE (IMPORTANT)
    public RecouvrementDTO() {
    }

    // ✅ CONSTRUCTEUR COMPLET
	public RecouvrementDTO(String locataireNom, Long loyerMensuel, Long montantPaye, Long reste, String statut) {
		super();
		this.locataireNom = locataireNom;
		this.loyerMensuel = loyerMensuel;
		this.montantPaye = montantPaye;
		this.reste = reste;
		this.statut = statut;
	}
	
	//getters et setters
	public String getLocataireNom() {
		return locataireNom;
	}
	public void setLocataireNom(String locataireNom) {
		this.locataireNom = locataireNom;
	}
	public Long getLoyerMensuel() {
		return loyerMensuel;
	}
	public void setLoyerMensuel(Long loyerMensuel) {
		this.loyerMensuel = loyerMensuel;
	}
	public Long getMontantPaye() {
		return montantPaye;
	}
	public void setMontantPaye(Long montantPaye) {
		this.montantPaye = montantPaye;
	}
	public Long getReste() {
		return reste;
	}
	public void setReste(Long reste) {
		this.reste = reste;
	}
	public String getStatut() {
		return statut;
	}
	public void setStatut(String statut) {
		this.statut = statut;
	}
    
    
}