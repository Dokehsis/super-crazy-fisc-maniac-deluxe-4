package moteur;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import entites.Banque;
import entites.Contribuable;
import entites.Enqueteur;
import entites.Pays;
import entites.PossesseurDeCompte;
import entites.Societe;
import paperasse.Registre;
import paperasse.Requete;
import paperasse.Transaction;

public class GestionnaireDePartie {

	private Generateur dieu;
	private Donnees donnees;
	private int nbreJoueurs;
	private ArrayList<Enqueteur> joueurs;
	private Scanner clavier;
	private boolean vincentExiste;
	
	public GestionnaireDePartie() {
		clavier = new Scanner(System.in);
		joueurs = new ArrayList<Enqueteur>();
		dieu = new Generateur();
		vincentExiste = false;
	}
	
	/**
	 * Affiche le fichier lisez-moi
	 */
	public static void lisezMoi() {
		BufferedReader br = null;
		try {
			String ligne;
			br = new BufferedReader(new FileReader("lisez_moi.txt"));
			while ((ligne = br.readLine()) != null) {
				System.out.println(ligne);
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
	 * Lance la partie en creant les donnees de jeu et les joueurs
	 */
	public void lancerLaPartie() {
		dieu.genese();
		lisezMoi();
		if(Donnees.debug)
			affichageStyle("test font!");
		promptEntree(clavier);
		BufferedReader br = null;
		if(Donnees.histoire) {
			try {
				int character;
				br = new BufferedReader(new FileReader("introduction.txt"));
				while ((character = br.read()) != -1) {
					System.out.print((char)character);
					try {
						if(((char)character >= 'a' && (char)character <= 'z') || ((char)character >= 'A' && (char)character <= 'Z') || (char)character == ' ' || (char)character == '\'')
							Thread.sleep(50);
						else if((char)character == '\n')
							Thread.sleep(1000);
						else if((char)character == '.')
							Thread.sleep(500);
						else
							Thread.sleep(200);
					}
					catch(InterruptedException e) {
						Thread.currentThread().interrupt();
					}
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
		try {
			int character;
			br = new BufferedReader(new FileReader("titre.txt"));
			while ((character = br.read()) != -1) {
				System.out.print((char)character);
				try {
						Thread.sleep(3);
				}
				catch(InterruptedException e) {
					Thread.currentThread().interrupt();
				}
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
			Thread.sleep(1500);
		}
		catch(InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		donnees = dieu.getDonnees();
		System.out.println("Combien de joueurs participeront a cette partie?");
		nbreJoueurs = lireEntier(clavier);
		String nomPays;
		ArrayList<Pays> paysSelectionnes = new ArrayList<Pays>();
		Pays paysChoisi;
		for(int i=1; i<=nbreJoueurs; i++) {
			System.out.println("C'est au joueur "+i+" de creer son personnage.");
			promptEntree(clavier);
			System.out.println("Les pays existants sont les suivants:");
			for(Pays p : donnees.getMonde()) {
				System.out.println(p.getNom());
			}
			promptEntree(clavier);
			System.out.println("Joueur "+i+", quel pays voulez-vous representer?");
			nomPays = clavier.nextLine();
			while((paysChoisi = donnees.trouverPays(nomPays)) == null || paysSelectionnes.contains(paysChoisi)) {
				System.out.println("Ce pays n'est pas disponible. Choisissez-en un autre:");
				nomPays = clavier.nextLine();
			}
			paysSelectionnes.add(paysChoisi);
			paysChoisi.getEnqueteurNational().setPays(paysChoisi);
			paysChoisi.getEnqueteurNational().rendreJoueur(clavier, donnees.getNomsUtilises());
			joueurs.add(paysChoisi.getEnqueteurNational());
		}
		for(Pays p : donnees.getMonde()) {
			if(!paysSelectionnes.contains(p)) {
				p.getEnqueteurNational().setPays(p);
				p.getEnqueteurNational().rendreAuto();
				if(vincentExiste)
					p.getEnqueteurNational().setNom(dieu.genererNom());
				else {
					vincentExiste = true;
					p.getEnqueteurNational().setNom("Vincent");
					p.getEnqueteurNational().setCharisme(2);
					p.getEnqueteurNational().setReputation(-1);
					p.getEnqueteurNational().setDetermination(1);
				}
				joueurs.add(p.getEnqueteurNational());
			}
		}
		ArrayList<Banque> banques = donnees.getToutesLesBanques();
		for(PossesseurDeCompte client : joueurs) {
			banques.get((int)(Math.random()*banques.size())).ajouterClient(client, dieu.utiliserID());
			client.getCompte().setMontant(0);
		}
	}
	
	/**
	 * Schema repetitif definissant une partie 
	 */
	public void boucleDePartie() {
		donnees.miseAJourTransactions();
		donnees.getRegistre().rotationDesInfos();
		for(Enqueteur joueur : joueurs) {
			joueur.recompenser();
			recevoirReponses(joueur);
			if(joueur.nEstPasAuto())
				faireJouerEnqueteur(joueur);
			else
				decoder(joueur.jouerAuto(), joueur);
		}
		donnees.setTourDeJeu(donnees.getTourDeJeu()+1);
	}
	
	/**
	 * Met fin a la partie
	 */
	public void finDePartie() {
		System.out.println("La partie est terminee! Les Enqueteurs ont vaincu la terrible Fraude Fiscale!");
		System.out.println("Voici le tableau des scores final:");
		Registre.afficherScores(joueurs);
		joueurs.sort(new Enqueteur.ComparatorEnqueteur());
		System.out.println("Le gagnant est donc:");
		affichageStyle(joueurs.get(0).getNomCourt()+"!");
		clavier.close();
	}
	
	/**
	 * Verifie s'il reste des transactions frauduleuses, et doncsi la partie doit continuer
	 * @return Vrai s'il reste des transactions, faux sinon
	 */
	public boolean laPartieContinue() {
		return(donnees.getToutesLesTransactions().size() != 0);
	}
	
	/**
	 * Simple demande a l'utilisateur d'appuyer sur [entree]. Permet de faire des pauses dans les textes relativement longs.
	 */
	public static void promptEntree(Scanner s) {
		System.out.println("[Appuyez sur ENTREE]");
		if(!s.hasNextLine()) {
			s.reset();
		}
		s.nextLine();
	}
	
	public static int lireEntier(Scanner s) {
		boolean ok = false;
		boolean premierEssai = true;
		int aRetourner = 0;
		while(!ok) {
			ok = true;
			if(!premierEssai)
				System.out.println("Un entier est attendu. Votre reponse:");
			String ligne = s.nextLine();
			try {
				aRetourner = Integer.parseInt(ligne);
			}
			catch(NumberFormatException e) {
				ok = false;
				premierEssai = false;
			}
		}
		return aRetourner;
	}
	
	/**
	 * Donne au joueur les reponses aux requetes dont la date de reponse est le tour en cours.
	 * 
	 * @param joueur Le joueur
	 */
	public void recevoirReponses(Enqueteur joueur) {
		Requete requete;
		boolean premier = true;
		while(joueur.getRequetes().size() != 0 && (requete = joueur.getRequetes().first()).getDateReponse() == donnees.getTourDeJeu()) {
			if(premier) {
				System.out.println(joueur.getNomCourt()+", vous avez du courrier!");
				promptEntree(clavier);
				premier = false;
			}
			System.out.println(requete.getInformation());
			joueur.setInformations(joueur.getInformations()+requete.getInformation()+"\n");
			joueur.getRequetes().remove(requete);
		}
	}
	
	/**
	 * Traduit un code d'action provenant d'une IA en action a effectuer
	 * @param code Le code a decoder
	 */
	public void decoder(String code, Enqueteur ia) {
		if(!code.equals("")) {
			int profondeurMax = Integer.parseInt(code);
			ArrayList<Transaction> transactions = donnees.getToutesLesTransactions();
			Transaction aDenoncer;
			int infiniCheck = 0;
			while((aDenoncer = transactions.get((int)(Math.random()*transactions.size()))).getDegreDeDifficulte() > profondeurMax && infiniCheck++ < 100) {}
			if(infiniCheck < 99) {
				int prime = donnees.verifierDenonciation(aDenoncer.getCompteSuspect().getIdCompte(), aDenoncer.getFraudeur().getNom(), ia);
				ia.setPrimesEnAttente(ia.getPrimesEnAttente()+prime);
			}
		}
	}
	
	/**
	 * Fonction qui demande a un joueur de jouer, en lui affichant les instructions necessaires.
	 * 
	 * @param joueur Le joueur qui doit jouer
	 */
	public void faireJouerEnqueteur(Enqueteur joueur) {
		int pointsAction = Donnees.pointsAction+joueur.getDetermination();
		int action, champsDAction;
		System.out.println("C'est a "+joueur.getNomCourt()+" de jouer!");
		while(pointsAction != -1) {
			champsDAction = 5;
			promptEntree(clavier);
			System.out.println("Tour "+donnees.getTourDeJeu()+", il vous reste "+pointsAction+" points d'action. Voici vos options:");
			System.out.println("[ 0] Mettre fin a votre tour. Les points d'action non depenses sont perdus.");
			System.out.println("[ 1] Consulter les publications du registre central (0 point)");
			System.out.println("[ 2] Consulter la liste des informations que vous avez obtenues (0 point)");
			System.out.println("[ 3] Consulter la liste des banques et leurs pays (0 point)");
			System.out.println("[ 4] Consulter la liste des societes et leurs pays (0 point)");
			System.out.println("[ 5] Consulter la liste des residents et leurs pays (0 point)");
			if(pointsAction > 0) {
				champsDAction = 8;
				System.out.println("[ 6] Consulter la liste des comptes heberges par une banque (1 point)");
				System.out.println("[ 7] Denoncer un contribuable associe a un compte frauduleux (1 point)");
				System.out.println("[ 8] Demander a une banque qui est le proprietaire d'un compte qu'elle heberge (1 point)");
				if(pointsAction > 1) {
					champsDAction = 10;
					System.out.println("[ 9] Demander a un enqueteur a qui appartient une societe de son pays (2 points)");
					System.out.println("[10] Demander a un enqueteur ce que possede une entite de son pays (2 points)");
				}
			}
			System.out.println("Votre choix:");
			while((action = lireEntier(clavier)) < 0 || action > champsDAction) {
				System.out.println("Indice d'action invalide. Entrez-en un nouveau: (0 -> "+champsDAction+")");
			}
			String nomLu, reponse;
			Banque banque;
			int idCompte;
			Pays pays;
			PossesseurDeCompte enQuestion;
			Requete simple;
			ArrayList<Requete> multiple;
			Enqueteur aSolliciter;
			boolean estJoueur = false;
			switch(action) {
				case 0:
					pointsAction = -1;
					break;
				case 1:
					consulterRegistre();
					break;
				case 2:
					if(joueur.getInformations().equals(""))
						System.out.println("Vous ne detenez pas d'informations pour le moment.");
					else {
						System.out.println("Voici les informations que vous avez recoltees:");
						System.out.println(joueur.getInformations());
					}
					break;
				case 3:
					for(Banque b : donnees.getToutesLesBanques())
						System.out.println(b.getNom()+"  <\t>  "+b.getPays().getNom());
					break;
				case 4:
					for(Pays p : donnees.getMonde()) {
						System.out.println("\n"+p.getNom());
						for(Societe s : p.getSocietes()) {
							System.out.println("\t"+s.getNom());
						}
					}
					break;
				case 5:
					for(Pays p : donnees.getMonde()) {
						System.out.println("\n"+p.getNom());
						for(Contribuable c : p.getResidents()) {
							System.out.println("\t"+c.getNom());
						}
					}
					break;
				case 6:
					System.out.println("Entrez le nom de la banque a soliciter:");
					nomLu = clavier.nextLine();
					enQuestion = donnees.trouverEntite(nomLu);
					if(enQuestion != null && enQuestion.getClass() == Banque.class) {
						banque = (Banque)enQuestion;
						pointsAction--;
						String info = "La "+banque.getNom()+" heberge les comptes suivants:";
						String infoJoueur = "La "+banque.getNom()+" heberge les comptes frauduleux suivants:";
						for(PossesseurDeCompte pdc : banque.getClients()) {
							info += "\n\t"+pdc.getCompte().getIdCompte();
							for(Transaction t : donnees.getToutesLesTransactions()) {
								if(t.getCompteSuspect().getIdCompte() == pdc.getCompte().getIdCompte()) {
									infoJoueur += "\n\t"+pdc.getCompte().getIdCompte()+"\t*Frauduleux*";
									info += "\t*Frauduleux*";
								}
							}
						}
						System.out.println(info);
						joueur.setInformations(joueur.getInformations()+infoJoueur+"\n");
					}
					else
						System.out.println(nomLu+" n'est pas une banque repertoriee.");
					break;
				case 7:
					System.out.println("Entrez le nom du contribuable a denoncer:");
					nomLu = clavier.nextLine();
					System.out.println("Entrez le numero de compte frauduleux:");
					idCompte = lireEntier(clavier);
					if(donnees.trouverEntite(nomLu) != null) {
						pointsAction--;
						int prime = donnees.verifierDenonciation(idCompte, nomLu, joueur);
						if(prime != -1) {
							joueur.setPrimesEnAttente(joueur.getPrimesEnAttente()+prime);
						}
						System.out.println("Votre denonciation a ete soumise au registre central. Vous recevrez votre recompense autour suivant si elle etait justifiee.");
					}
					else
						System.out.println("Aucune entite ne se nomme "+nomLu+".");
					break;
				case 8:
					System.out.println("Entrez le nom de la banque a soliciter:");
					nomLu = clavier.nextLine();
					System.out.println("Entrez le numero de compte a examiner:");
					idCompte = lireEntier(clavier);
					enQuestion = donnees.trouverEntite(nomLu);
					if(enQuestion != null && enQuestion.getClass() == Banque.class) {
						pointsAction--;
						banque = (Banque)enQuestion;
						boolean res = joueur.quiEstDerriereCeCompte(banque, idCompte, donnees);
						if(res) {
							System.out.println("Votre requete a ete soumise. Vous recevrez la reponse d'ici 1 a 7 tours.");
						}
						else {
							System.out.println("Il semble que le compte "+idCompte+" ne soit pas heberge par la "+banque.getNom()+".");
							System.out.println("Votre point d'action a tout de meme ete depense ceci etant, en soi, une information de valeur non nulle.");
						}
					}
					else
						System.out.println(nomLu+" n'est pas une banque repertoriee.");
					break;
				case 9:
					System.out.println("Entrez le nom de la societe a examiner:");
					nomLu = clavier.nextLine();
					enQuestion = donnees.trouverEntite(nomLu);
					if(enQuestion != null && enQuestion.getProprietaire() != null) {
						pays = enQuestion.getPays();
						aSolliciter = pays.getEnqueteurNational();
						System.out.println("Cette societe se situe en "+pays.getNom()+", l'enqueteur "+aSolliciter.getNomCourt()+" va donc etre sollicite. Soumettre la requete? (oui/non)");
						while(!(reponse = clavier.nextLine()).equalsIgnoreCase("oui") && !reponse.equalsIgnoreCase("non")) {
							System.out.println("Merci de repondre par oui ou non. Votre reponse:");
						}
						if(reponse.equalsIgnoreCase("oui")) {
							pointsAction -= 2;
							simple = joueur.aQuiAppartient(enQuestion, donnees);
							System.out.println("Votre requete a ete soumise. Vous recevrez la reponse au tour "+simple.getDateReponse()+".");
							if(!aSolliciter.equals(joueur))
								estJoueur = solliciterEnqueteur(aSolliciter, simple);
						}
						else
							System.out.println("Requete annulee.");
					}
					else if(enQuestion == null)
						System.out.println("Il semblerait que la societe "+nomLu+" ne soit pas repertoriee.");
					else
						System.out.println(nomLu+" n'appartient a personne.");
					if(estJoueur) {
						System.out.println("Retour au tour de "+joueur.getNomCourt());
					}
					break;
				case 10:
					System.out.println("Entrez le nom du possesseur de compte a examiner:");
					nomLu = clavier.nextLine();
					enQuestion = donnees.trouverEntite(nomLu);
					if(enQuestion != null && enQuestion.getPossessions().size() != 0) {
						pays = enQuestion.getPays();
						aSolliciter = pays.getEnqueteurNational();
						System.out.println("Ce possesseur se situe en "+pays.getNom()+", l'enqueteur "+aSolliciter.getNomCourt()+" va donc etre sollicite. Soumettre la requete? (oui/non)");
						while(!(reponse = clavier.nextLine()).equalsIgnoreCase("oui") && !reponse.equalsIgnoreCase("non")) {
							System.out.println("Merci de repondre par oui ou non. Votre reponse:");
						}
						if(reponse.equalsIgnoreCase("oui")) {
							pointsAction -= 2;
							multiple = joueur.quePossede(enQuestion, donnees);
							int max = 0;
							for(Requete r : multiple) {
								max = Math.max(max, r.getDateReponse());
							}
							System.out.println("Votre requete a ete soumise. Vous recevrez plusieurs reponses jusqu'au tour "+max+".");
							if(!aSolliciter.equals(joueur))
								estJoueur = solliciterEnqueteur(aSolliciter, multiple.get((int)(Math.random()*multiple.size()))); // L'enqueteur sollicite ne peut agir que sur l'une des requetes, par souci d'equilibre
						}
						else
							System.out.println("Requete annulee.");
					}
					else if(enQuestion == null)
						System.out.println("Il semblerait que la societe "+nomLu+" ne soit pas repertoriee.");
					else
						System.out.println("La societe "+nomLu+" ne possede rien.");
					if(estJoueur) {
						System.out.println("Retour au tour de "+joueur.getNomCourt());
					}
					break;
			}
		}
	}
	
	/**
	 * Fraction de faireJouerEnqueteur(), permet de consulter les diverses informations du registre central.
	 */
	public void consulterRegistre() {
		int action = -1;
		while(action != 0) {
			if(action != -1)
				promptEntree(clavier);
			System.out.println("Voici vos options:");
			System.out.println("[ 0] Cesser de consulter le registre");
			System.out.println("[ 1] Consulter la liste des transactions frauduleuses");
			System.out.println("[ 2] Consulter la liste des denonciations de la veille");
			System.out.println("[ 3] Consulter le tableau des scores");
			System.out.println("Votre choix:");
			while((action = lireEntier(clavier)) < 0 || action > 3) {
				System.out.println("Indice d'action invalide. Entrez-en un nouveau: (0 -> 3)");
			}
			switch(action) {
				case 0:
					System.out.println("Vous allez etre redirige vers le menu precedent.");
					break;
				case 1:
					System.out.println("Compte frauduleux\tMontant\t\tCompte recepteur");
					for(Transaction t : donnees.getToutesLesTransactions()) {
						System.out.println(t.infosAccessibles());
					}
					break;
				case 2:
					if(donnees.getRegistre().getDenonciationsVeille().size() != 0) {
						System.out.println("Voici les denonciations effectuees au tour precedent:");
						donnees.getRegistre().afficherDenonciations();
					}
					else
						System.out.println("Aucune denonciation n'a ete enregistree au tour dernier.");
					break;
				case 3:
					System.out.println("Voici les scores de cette partie:");
					Registre.afficherScores(joueurs);
					break;
			}
		}
	}
	
	/**
	 * Sollicite l'enqueteur d'un certain pays, qui pourra a sa guise agir sur la requete qui lui est soumise
	 * 
	 * @param aSolliciter L'enqueteur pouvant agir sur la requete
	 * @param requete La requete sur laquelle agir
	 * @return vrai si l'enqueteur est un joueur, faux sinon
	 */
	public boolean solliciterEnqueteur(Enqueteur aSolliciter, Requete requete) {
		if(aSolliciter.nEstPasAuto()) {
			int action;
			System.out.println(aSolliciter.getNomCourt()+" peut intervenir!");
			promptEntree(clavier);
			System.out.println(requete.getObjet());
			System.out.println(aSolliciter.getNomCourt()+", voici vos options:");
			System.out.println("[1] Ne rien faire.");
			System.out.println("[2] Intercepter la requete. Sa reponse sera alors censuree, mais vous perdez immediatement 1 point de Reputation.");
			System.out.println("[3] Mentir en modifiant la reponse a cette requete ("+10*(1+3*aSolliciter.getCharisme())+"% de chance de reussite). Si votre mensonge echoue, vous perdez 2 points de Reputation.");
			System.out.println("Votre choix:");
			while((action = lireEntier(clavier)) < 1 || action > 3) {
				System.out.println("Indice d'action invalide. Entrez-en un nouveau: (1 -> 3)");
			}
			switch(action) {
				case 1:
					break;
				case 2:
					requete.setInformation("La reponse a cette requete a ete censuree par "+aSolliciter.getNomCourt()+".");
					aSolliciter.setReputation(aSolliciter.getReputation()-1);
					break;
				case 3:
					boolean reussite = (int)(Math.random()*10) < 1+3*aSolliciter.getCharisme();
					requete.corrompre(donnees.entiteAleatoire(), reussite);
					if(!reussite) {
						aSolliciter.setReputation(aSolliciter.getReputation()-2);
						System.out.println("Vous n'etes pas aussi convainquant que prevu...");
					}
					break;
			}
			return true;
		}
		else {
			/*
			 * Selon son Charisme, l'IA va decider d'essayer de mentir au joueur.
			 * Les IA les plus charismatiques, a parametres de base, ont en tout
			 * 49% de chances de reussir a mentir a un joueur (2 en Charisme).
			 * A charisme moyen, cette proba tombe a 16% (1 en Charisme).
			 * A charisme nul, elle tombe a 1% (0 en Charisme).
			 */
			if((int)(Math.random()*10) < 1+3*aSolliciter.getCharisme() || aSolliciter.getNomCourt().equals("Vincent")) {
				boolean reussite = (int)(Math.random()*10) < 1+3*aSolliciter.getCharisme();
				requete.corrompre(donnees.entiteAleatoire(), reussite);
				if(!reussite) {
					aSolliciter.setReputation(aSolliciter.getReputation()-2);
				}
			}
			return false;
		}
	}
	
	/**
	 * Affiche un texte de maniere fort stylee.
	 * @param aAfficher Le texte a afficher
	 */
	public void affichageStyle(String aAfficher) {
		String s = aAfficher.toLowerCase();
		char[] allChars = Donnees.alphabet.toCharArray();
		BufferedReader br = null;
		for(int i=0; i<6; i++) {
			try {
				String ligne;
				for(char c : s.toCharArray()) {
					br = new BufferedReader(new FileReader("font.txt"));
					int j, cpt = 0;
					for(j=0; allChars[j] != c; j++){}
					while(cpt < j) {
						ligne = br.readLine();
						if(ligne.substring(0, 1).equals("#")) {
							cpt++;
						}
					}
					for(int k=0; k<i; k++)
						br.readLine();
					System.out.print(ligne = br.readLine());
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
			System.out.println();
		}
	}

	public Generateur getDieu() {
		return dieu;
	}

	public void setDieu(Generateur dieu) {
		this.dieu = dieu;
	}

	public Donnees getDonnees() {
		return donnees;
	}

	public void setDonnees(Donnees donnees) {
		this.donnees = donnees;
	}

	public int getNbreJoueurs() {
		return nbreJoueurs;
	}

	public void setNbreJoueurs(int nbreJoueurs) {
		this.nbreJoueurs = nbreJoueurs;
	}

	public ArrayList<Enqueteur> getJoueurs() {
		return joueurs;
	}

	public void setJoueurs(ArrayList<Enqueteur> joueurs) {
		this.joueurs = joueurs;
	}

}
