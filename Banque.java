package entites;

import java.util.ArrayList;

import paperasse.CompteBancaire;
/**
 * Classe representant les banques
 * 
 * @author Remi Massart
 */
public class Banque extends Societe {

	private ArrayList<PossesseurDeCompte> clients;
	
	public Banque() {
		super();
	}
	
	public Banque(String nom, CompteBancaire compte, PossesseurDeCompte proprietaire, ArrayList<PossesseurDeCompte> possessions, Pays pays) {
		super(nom, compte, proprietaire, possessions, pays);
		clients = new ArrayList<PossesseurDeCompte>();
	}
	
	/**
	 * Ajoute un client a la banque en creant son compte chez celle-ci
	 * 
	 * @param client Le client a ajouter
	 * @param idCompte Le numero du compte a creer 
	 */
	public void ajouterClient(PossesseurDeCompte client, int idCompte) {
		// TODO gérer les sommes
		client.setCompte(new CompteBancaire(idCompte, 1000, this, client));
		clients.add(client);
	}

	public ArrayList<PossesseurDeCompte> getClients() {
		return clients;
	}

	public void setClients(ArrayList<PossesseurDeCompte> clients) {
		this.clients = clients;
	}
	
	@Override
	public String getNom() {
		return "Banque "+nom;
	}

}
