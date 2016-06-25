package moteur;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Properties;

import entites.Banque;
import entites.Contribuable;
import entites.Enqueteur;
import entites.Pays;
import entites.PossesseurDeCompte;
import paperasse.CompteBancaire;
import paperasse.Registre;
import paperasse.Transaction;

/**
 * Classe de stockage et de parcours des donnees du jeu
 * 
 * @author Remi Massart
 */
public class Donnees {

	private ArrayList<Integer> idUtilises;
	private ArrayList<Integer> idDispos;
	private ArrayList<String> nomsUtilises;
	private ArrayList<Pays> monde;
	private ArrayList<PossesseurDeCompte> tousLesClients;
	private ArrayList<Banque> toutesLesBanques;
	private ArrayList<PossesseurDeCompte> tousLesPossesseurs;
	private ArrayList<Transaction> toutesLesTransactions;
	private ArrayList<Transaction> transactionsResolues;
	private int[][] cooperationEntrePays;
	private Registre registre;
	
	private int tourDeJeu;
	
	public static final int nombreID = 10000;
	public static final float tauxDeFraude = 10.0f; // pourcentage de fraude de base. N'apparait pas dans les proprietes pour des raisons d'equilibre.
	public static boolean debug;
	public static boolean histoire;
	
	public static int pointsCompetences;
	public static int maxCompetence;
	public static int intelligenceIA;
	public static int pointsAction;
	
	private Hashtable<String, Integer> voyelles;
	private Hashtable<String, Integer> consonnes;
	
	public static final String alphabet = "abcdefghijklmnopqrstuvwxyz !";
	
	public Donnees() {
		idUtilises = new ArrayList<Integer>();
		idDispos = new ArrayList<Integer>();
		nomsUtilises = new ArrayList<String>();
		nomsUtilises.add("Lemmy");
		nomsUtilises.add("Vincent");
		monde = new ArrayList<Pays>();
		tousLesClients = new ArrayList<PossesseurDeCompte>();
		toutesLesBanques = new ArrayList<Banque>();
		tousLesPossesseurs = new ArrayList<PossesseurDeCompte>();
		toutesLesTransactions = new ArrayList<Transaction>();
		transactionsResolues = new ArrayList<Transaction>();
		registre = new Registre();
		tourDeJeu = 0;
		for(int i=0; i<nombreID; i++)
			idDispos.add(i);
		voyelles = new Hashtable<String, Integer>();
		consonnes = new Hashtable<String, Integer>();
		setVoyellesEtConsonnes();
		try{
			InputStream stIn = new FileInputStream("SCFMD4.properties");
			Properties myProps = new Properties();
			myProps.load(stIn);
			for(String key : myProps.stringPropertyNames()) {
				if(key.equals("pointsAction")) {
					pointsAction = Integer.parseInt(myProps.getProperty(key));
				}
				if(key.equals("pointsCompetences")) {
					pointsCompetences = Integer.parseInt(myProps.getProperty(key));
				}
				if(key.equals("debug")) {
					debug = Boolean.parseBoolean(myProps.getProperty(key));
				}
				if(key.equals("intelligenceIA")) {
					intelligenceIA = Integer.parseInt(myProps.getProperty(key));
				}
				if(key.equals("maxCompetence")) {
					maxCompetence = Integer.parseInt(myProps.getProperty(key));
				}
				if(key.equals("histoire")) {
					histoire = Boolean.parseBoolean(myProps.getProperty(key));
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retourne le possesseur de compte portant un nom donne
	 * 
	 * @param nom Le nom de l'entite a retrouver
	 * @return L'entite portant ce nom
	 */
	public PossesseurDeCompte trouverEntite(String nom) {
		for(PossesseurDeCompte pdc : tousLesPossesseurs) {
			if(pdc.getNomCourt().equalsIgnoreCase(nom))
				return pdc;
		}
		return null;
	}
	
	/**
	 * Retourne un possesseur de compte aleatoire
	 * @return Ce possesseur de compte
	 */
	public PossesseurDeCompte entiteAleatoire() {
		return tousLesPossesseurs.get((int)(Math.random()*tousLesPossesseurs.size()));
	}
	
	/**
	 * Retourne le pays portant le nom donne
	 * 
	 * @param nom Le nom du pays a trouver
	 * @return Le pays en question
	 */
	public Pays trouverPays(String nom) {
		for(Pays p : monde) {
			if(p.getNom().equalsIgnoreCase(nom))
				return p;
		}
		return null;
	}
	
	/**
	 * Trouve l'ultime proprietaire (le "plus haut") d'une possession
	 * 
	 * @param possession La possession dont on doit trouver le proprietaire
	 * @return Le prorietaire en question
	 */
	public static PossesseurDeCompte trouverUltimeProprietaire(PossesseurDeCompte possession) {
		if(possession.getProprietaire() != null) {
			return trouverUltimeProprietaire(possession.getProprietaire());
		}
		return possession;
	}
	
	
	/**
	 * Verifie la veracite d'une denonciation, modifie la reputation de l'enqueteur en consequence, et retourne la prime ou -1 si la denonciation etait fausse
	 * 
	 * @param idCompte Compte frauduleux denonce
	 * @param nomProprietaire Nom du contribuable denonce
	 * @param denonciateur L'enqueteur a l'origine de cette denonciation
	 * @return La prime si la denonciation est justifiee, -1 sinon.
	 */
	public int verifierDenonciation(int idCompte, String nomProprietaire, Enqueteur denonciateur) {
		for(Transaction t : toutesLesTransactions) {
			if(t.getCompteSuspect().getIdCompte() == idCompte && (t.getFraudeur().getNomCourt().equalsIgnoreCase(nomProprietaire) || t.getFraudeur().getNom().equalsIgnoreCase(nomProprietaire))) {
				registre.ajouterDenonciation(denonciateur.getNomCourt() + " a denonce " + t.getFraudeur().getNom() + ", proprietaire du compte " + idCompte + ".");
				denonciateur.setReputation(denonciateur.getReputation()+t.getDegreDeDifficulte());
				transactionsResolues.add(t);
				return (int) Math.pow(t.getMontant(), 1+((float)t.getDegreDeDifficulte()/10.0f));
			}
		}
		registre.ajouterDenonciation(denonciateur.getNomCourt()+" a denonce a tort "+nomProprietaire+".");
		denonciateur.setReputation(denonciateur.getReputation()-1);
		return -1;
	}
	
	/**
	 * Elimine les transactions frauduleuses dont le commanditaire a ete denonce 
	 */
	public void miseAJourTransactions() {
		toutesLesTransactions.removeAll(transactionsResolues);
		transactionsResolues.removeAll(transactionsResolues);
	}
	
	/**
	 * Retourne le compte bancaire de l'une des possessions du proprietaire, voire du proprietaire lui-meme
	 * 
	 * @param proprietaire Le proprietaire en question
	 * @return L'un des comptes qui lui sont associes
	 */
	public static CompteBancaire compteAleatoire(PossesseurDeCompte proprietaire) {
		float proba;
		if(proprietaire.getClass() == Contribuable.class)
			proba = 90;
		else
			proba = 40+10*proprietaire.nombrePossessionsTotal();
		if(proprietaire.getPossessions().size() != 0 && Math.random() < proba/100.0f) {
			return compteAleatoire(proprietaire.getPossessions().get((int)(Math.random()*proprietaire.getPossessions().size())));
		}
		return proprietaire.getCompte();
	}
	
	/**
	 * Fonction recursive de calcul du degre de difficulte pour trouver un certain fraudeur.
	 * Ce dernier correspond a la taille du chemin entre le possesseur du compte suspect et le contribuable qui le possede.
	 * 
	 * @param proprietaire Le possesseur de compte en question
	 * @param profondeur Parametre incremente au fil de la recursion, et resultat renvoye en fin de recursion
	 * @return La taille de la chaine de societes entre fraudeur et compte frauduleux
	 */
	public static int calculerDegreDifficulte(PossesseurDeCompte proprietaire, int profondeur) {
		if(proprietaire.getProprietaire() != null)
			return calculerDegreDifficulte(proprietaire.getProprietaire(), profondeur+1);
		return profondeur;
	}
	
	/**
	 * Retourne le minimum d'un ArrayList d'entiers
	 * 
	 * @param a L'ArrayList d'entiers
	 * @return
	 */
	public static int getMinArray(ArrayList<Integer> a) {
		int min = Integer.MAX_VALUE;
		for(int i : a) {
			if(i<min)
				min = i;
		}
		return min;
	}
	
	/**
	 * Genere une chaine de caractere representant toutes les entites du monde
	 * 
	 * @return L arbre global des entites
	 */
	public String arbreComplet() {
		String res = "";
		for(Pays p : monde) {
			res += "\n" + p.arbreDesPossessions();
		}
		return res;
	}
	
	/**
	 * Charge les frequences d'utilisation des voyelles et des consonnes (voir genererNom dans la classe Generateur)
	 */
	public void setVoyellesEtConsonnes() {
		BufferedReader br = null;
		try {
			String ligne;
			br = new BufferedReader(new FileReader("freqVoyelles"));
			while ((ligne = br.readLine()) != null) {
				voyelles.put(ligne.substring(0, 1), Integer.parseInt(ligne.substring(1, ligne.length())));
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
		try {
			String ligne;
			br = new BufferedReader(new FileReader("freqConsonnes"));
			while ((ligne = br.readLine()) != null) {
				consonnes.put(ligne.substring(0, 1), Integer.parseInt(ligne.substring(1, ligne.length())));
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
	}
	
	/**
	 * Retourne la lettre correspondant a la probabilite d'apparition donnee
	 * 
	 * @param proba Le tirage aleatoire a faire correspondre
	 * @param estVoyelle Vrai si on veut une voyelle, faux si on veut une consonne
	 * @return La lettre correspondant a la proba
	 */
	public String lettreDepuisProba(int proba, boolean estVoyelle) {
		Hashtable<String, Integer> aLire;
		int i = 0;
		int sommeStats = 0;
		if(estVoyelle) {
			aLire = voyelles;
		}
		else {
			aLire = consonnes;
		}
		ArrayList<String> toutesLesLettres = new ArrayList<String>();
		toutesLesLettres.addAll(aLire.keySet());
		while(sommeStats < proba+1) {
			sommeStats += aLire.get(toutesLesLettres.get(i));
			i++;
		}
		return toutesLesLettres.get(i-1);
	}
	
	public ArrayList<Integer> getIdUtilises() {
		return idUtilises;
	}

	public void setIdUtilises(ArrayList<Integer> idUtilises) {
		this.idUtilises = idUtilises;
	}

	public ArrayList<Integer> getIdDispos() {
		return idDispos;
	}

	public void setIdDispos(ArrayList<Integer> idDispos) {
		this.idDispos = idDispos;
	}

	public ArrayList<String> getNomsUtilises() {
		return nomsUtilises;
	}

	public void setNomsUtilises(ArrayList<String> nomsUtilises) {
		this.nomsUtilises = nomsUtilises;
	}

	public ArrayList<Pays> getMonde() {
		return monde;
	}

	public void setMonde(ArrayList<Pays> monde) {
		this.monde = monde;
	}

	public ArrayList<PossesseurDeCompte> getTousLesClients() {
		return tousLesClients;
	}

	public void setTousLesClients(ArrayList<PossesseurDeCompte> tousLesClients) {
		this.tousLesClients = tousLesClients;
	}
	
	public ArrayList<Banque> getToutesLesBanques() {
		return toutesLesBanques;
	}

	public void setToutesLesBanques(ArrayList<Banque> toutesLesBanques) {
		this.toutesLesBanques = toutesLesBanques;
	}
	
	public int getTourDeJeu() {
		return tourDeJeu;
	}
	
	public void setTourDeJeu(int tourDeJeu) {
		this.tourDeJeu = tourDeJeu;
	}

	public ArrayList<Transaction> getToutesLesTransactions() {
		return toutesLesTransactions;
	}

	public void setToutesLesTransactions(ArrayList<Transaction> toutesLesTransactions) {
		this.toutesLesTransactions = toutesLesTransactions;
	}

	public int[][] getCooperationEntrePays() {
		return cooperationEntrePays;
	}

	public void setCooperationEntrePays(int[][] cooperationEntrePays) {
		this.cooperationEntrePays = new int[cooperationEntrePays.length][cooperationEntrePays.length];
		for(int i=0; i<cooperationEntrePays.length; i++) {
			for(int j=0; j<cooperationEntrePays.length; j++) {
				this.cooperationEntrePays[i][j] = cooperationEntrePays[i][j];
			}
		}
	}
	
	public int getCooperationCouple(Pays p1, Pays p2) {
		return (cooperationEntrePays[p1.getIdTableCooperation()][p2.getIdTableCooperation()]);
	}

	public ArrayList<PossesseurDeCompte> getTousLesPossesseurs() {
		return tousLesPossesseurs;
	}

	public void setTousLesPossesseurs(ArrayList<PossesseurDeCompte> tousLesPossesseurs) {
		this.tousLesPossesseurs = tousLesPossesseurs;
	}

	public Registre getRegistre() {
		return registre;
	}

	public void setRegistre(Registre registre) {
		this.registre = registre;
	}
	
	
	
}
