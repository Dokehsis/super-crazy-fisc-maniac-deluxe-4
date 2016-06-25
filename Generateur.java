package moteur;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import entites.Banque;
import entites.Contribuable;
import entites.Enqueteur;
import entites.Pays;
import entites.PossesseurDeCompte;
import entites.Societe;
import paperasse.Transaction;

/**
 * Classe aux methodes generant le monde, les joueurs, les societes, etc
 * 
 * @author Remi Massart
 */
public class Generateur {
	
	private Donnees donnees;

	public Generateur() {
		donnees = new Donnees();
	}
	
	/**
	 * Genere l entierete du monde et des donnees necessaires au jeu
	 */
	public void genese() {
		System.out.print("[          ] 0%\r");
		genererMonde();
		System.out.print("[##        ] 25%\r");
		peuplerMonde();
		System.out.print("[#####     ] 50%\r");
		genererEconomie();
		System.out.print("[#######   ] 75%\r");
		genererToutesTransactions();
		System.out.print("[##########] 100%\r");
	}
	
	/**
	 * Genere les societes filles d'un proprietaire avec proba decroissante de creation au fil de la recursion
	 * 
	 * @param proprietaire Un possesseur de compte
	 * @param profondeur (>2) Plus cette valeur est grande, moins le proprietaire aura de possessions
	 */
	public void genererPossessions(PossesseurDeCompte proprietaire, int profondeur) {
		if(profondeur < 2)
			profondeur = 2;
		ArrayList<PossesseurDeCompte> possessions = new ArrayList<PossesseurDeCompte>();
		while(Math.random() < (float)1/((float) profondeur)) {
			Societe societe = genererSociete(proprietaire);
			genererPossessions(societe, profondeur+1);
			possessions.add(societe);
		}
		proprietaire.setPossessions(possessions);
	}
	
	/**
	 * Genere une societe, qui peut etre une banque, et tous ses attributs
	 *  
	 * @param proprietaire
	 * @return la nouvelle societe
	 */
	public Societe genererSociete(PossesseurDeCompte proprietaire) {
		Pays pays = donnees.getMonde().get((int)(Math.random()*donnees.getMonde().size()));
		Societe societe;
		if((int)(Math.random()*(1+pays.getNombreBanques())*5) != 0) {
			String nom = genererNom();
			societe = new Societe(nom, null, proprietaire, new ArrayList<PossesseurDeCompte>(), pays);
			donnees.getTousLesClients().add(societe);
		}
		else {
			String nom = genererNom();
			societe = new Banque(nom, null, proprietaire, new ArrayList<PossesseurDeCompte>(), pays);
			donnees.getToutesLesBanques().add((Banque)societe);
			pays.setNombreBanques(pays.getNombreBanques()+1);
		}
		donnees.getTousLesPossesseurs().add(societe);
		pays.ajouterSociete(societe);
		return societe;
	}
	
	/**
	 * Genere un contribuable dans un certain pays
	 * 
	 * @param pays Le pays de residence du contribuable
	 * @return Le contribuable
	 */
	public Contribuable genererContribuable(Pays pays) {
		String nom = genererNom();
		Contribuable contribuable = new Contribuable(nom, null, null, new ArrayList<PossesseurDeCompte>(), pays);
		donnees.getTousLesClients().add(contribuable);
		pays.ajouterResident(contribuable);
		return contribuable;
	}
	
	/**
	 * Peuple un pays en generant dix habitants, un enqueteur et leurs possessions
	 * 
	 * @param pays Le pays a peupler
	 */
	public void peuplerPays(Pays pays) {
		for(int i=0; i<10; i++) {
			Contribuable contribuable = genererContribuable(pays);
			genererPossessions(contribuable, 2);
			donnees.getTousLesPossesseurs().add(contribuable);
		}
		Enqueteur enqueteur = new Enqueteur();
		genererPossessions(enqueteur, 2);
		pays.setEnqueteurNational(enqueteur);
	}
	
	/**
	 * Peuple le monde en peulant chacun des pays (voir peupler pays)
	 */
	public void peuplerMonde() {
		for(Pays p : donnees.getMonde()) {
			peuplerPays(p);
		}
	}
	
	/**
	 * Genere les comptes en banque de tous les possesseurs de compte et les associe a une banque
	 */
	public void genererEconomie() {
		ArrayList<Banque> banques = donnees.getToutesLesBanques();
		Banque hebergeur;
		for(PossesseurDeCompte client : donnees.getTousLesClients()) {
			banques.get((int)(Math.random()*banques.size())).ajouterClient(client, utiliserID());
		}
		for(Banque banque : banques) {
			do {
				hebergeur = banques.get((int)(Math.random()*banques.size()));
			}while(hebergeur.equals(banque));
			hebergeur.ajouterClient(banque, utiliserID());
		}
	}
	
	/**
	 * Genere une transaction (frauduleuse) entre l'un des comptes du fraudeur et un autre compte aleatoire
	 * 
	 * @param fraudeur
	 * @return
	 */
	public Transaction genererTransactionAleatoire(Contribuable fraudeur) {
		Contribuable destinataire;
		ArrayList<Contribuable> tirerParmi = donnees.getMonde().get((int)(Math.random()*donnees.getMonde().size())).getResidents();
		do {
			destinataire = tirerParmi.get((int)(Math.random()*tirerParmi.size()));
		}while(destinataire == fraudeur);
		return new Transaction(Donnees.compteAleatoire(fraudeur), Donnees.compteAleatoire(destinataire));
	}
	
	/**
	 * Genere toutes les transactions (frauduleuses) necessaires au jeu
	 */
	public void genererToutesTransactions() {
		for(Pays p : donnees.getMonde()) {
			for(Contribuable c : p.getResidents()) {
				if(Math.random() < (Donnees.tauxDeFraude+c.nombrePossessionsTotal()*10)/100.0f) {
					donnees.getToutesLesTransactions().add(genererTransactionAleatoire(c));
				}
			}
		}
	}
	
	/**
	 * Genere tous les pays a partir de pays.txt, ainsi que le tableau des indices de cooperation entre pays.
	 */
	public void genererMonde() {
		BufferedReader br = null;
		int idPays = 0;
		try {
			String ligne;
			br = new BufferedReader(new FileReader("pays.txt"));
			while ((ligne = br.readLine()) != null) {
				Pays pays = new Pays(ligne);
				pays.setIdTableCooperation(idPays);
				donnees.getMonde().add(pays);
				idPays++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		int tailleMonde = donnees.getMonde().size();
		int[][] cooperation = new int[tailleMonde][tailleMonde];
		for(int i=0; i<tailleMonde; i++) {
			for(int j=i; j<tailleMonde; j++) {
				int val = (int)(Math.random()*7);
				cooperation[i][j] = val;
				cooperation[j][i] = val;
			}
		}
		donnees.setCooperationEntrePays(cooperation);
	}
	
	/**
	 * Retourne un ID de compte et le rend inutilisable
	 * 
	 * @return ID de compte
	 */
	public int utiliserID() {
		int rangID = (int)(Math.random()*donnees.getIdDispos().size());
		int ID = donnees.getIdDispos().get(rangID);
		donnees.getIdUtilises().add(ID);
		donnees.getIdDispos().remove(rangID);
		return ID;
	}
	
	/**
	 * Genere un nom aleatoire lisible ou presque, base sur la frequence d'utilisation des lettres en francais
	 * 
	 * @return le nom en question
	 */
	public String genererNom() {
		String nom = "";
		boolean continuer = true;
		int suite = 0;
		boolean estVoyelle = (Math.random()>0.5);
		while(continuer) {
			nom += donnees.lettreDepuisProba((int)(Math.random()*100), estVoyelle);
			if(nom.length() == 1) {
				nom = nom.toUpperCase();
				estVoyelle = !estVoyelle;
			}
			else {
				suite ++;
				if(suite == 2) {
					estVoyelle = !estVoyelle;
					suite = 0;
				}
				else if(Math.random()>0.3)
				{
					estVoyelle = !estVoyelle;
					suite = 0;
				}
			}
			if(nom.length() > 5)
				continuer = (Math.random()>0.5);
			if(nom.length() > 10)
				continuer = false;
		}
		if(donnees.getNomsUtilises().contains(nom))
			nom = genererNom();
		else
			donnees.getNomsUtilises().add(nom);
		return nom;
	}

	public Donnees getDonnees() {
		return donnees;
	}

	public void setDonnees(Donnees donnees) {
		this.donnees = donnees;
	}
	
	
}
