package com.dariuszmech;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static com.badlogic.gdx.Input.Keys.*;

/**
 * glowna klasa gry
 */
public class EagleEyeCG extends ApplicationAdapter {
	public static final int WINDOW_WIDTH = 1280, WINDOW_HEIGHT = 720;
	/**
	 * punkt osi X na ekranie gdzie narysowano pole glowne
	 */
	private int xPG,
	/**
	 * punkt osi Y na ekranie gdzie narysowano pole glowne
	 */
 				yPG, runda;
	/**
	 * punkt w czasie uzywany do obliczen czasu do konca gry i rundy
	 */
	private int m0,
	/**
	 * punkt w czasie uzywany do obliczen czasu do konca gry i rundy
	 */
				m1,
	/**
	 * punkt w czasie uzywany do obliczen czasu do konca gry i rundy
	 */
				m2,
	/**
	 * punkt w czasie uzywany do obliczen czasu do konca gry i rundy
	 */
				m4, pkt, ukonczoneGry = 0;
	/**
	 * zaznaczenie poprawnego/niepoprawnego ksztaltu lub brak zaznaczenia
	 */
	private byte znak,
	/**
	 * keycode klawiszy z klawiatury
	 */
			q= 0;
	private byte czasRundy = 5, czasGry = 30;
	private float trafione, nieTrafione = 0;
	private boolean czyGraRuszyla, zmianaTrybuGry, historiaKlik, menuKlik, koniecGry = false, zmianaUkladu = true;
	private SpriteBatch batch;
	private Texture background, startowa, poleGlowne, menu, start, restart, dobrze, zle, historia,
	/**
	 * menu
	 */
 			logo,
	/**
	 * chwilowa textura przypisywana do wpisywania w listy
	 */
 			t,
	/**
	 * wwybrany try rundy
	 */
			wtr,
	/**
	 * wybrany tryb gry
	 */
			wtg;
	private Sound dzwiekPoczatkaRundy, dzwiekKoncaGry;
	/**
	 * poprawny ksztalt/figura
	 */
	private String poprawny;
	private BitmapFont font, font2, font3, font4;
	private static final DecimalFormat df = new DecimalFormat("0.00");
	SimpleDateFormat formatDaty = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	ArrayList<String>
			allFigures = new ArrayList<>(),
	/**
	 * 21 czarnych figur
	 */
			wylosowaneCzarne = new ArrayList<>(),
	/**
	 * 5 zlotych figur (4 inne niz czarne i 1 czarny)
	 */
			wylosowaneZlote = new ArrayList<>(),
	/**
	 * 5 zlotych (4 inne niz czarne i 1 czarny w losowej kolejnosci)
	 */
			wylosowaneZlote2 = new ArrayList<>(),
			historiaUkonczonychGier = new ArrayList<>();
	/**
	 * Wszystkie zlote figury
	 */
	ArrayList<Texture>
			zlote= new ArrayList<>(),
	/**
	 * Wszystkie czarne figury
	 */
			czarne= new ArrayList<>(),
	/**
	 * Numery pod zlotymi figurami w polu uzytkownika
	 */
			numery = new ArrayList<>();

	/**
	 * Metoda, ktora przechowuje dane obrazkow, czcionek i dzwiekow
	 */
	@Override
	public void create() {
		batch = new SpriteBatch();

		background = new Texture("background.jpg");
		poleGlowne = new Texture("poleGlowne.png");
		startowa = new Texture("startowa.png");
		menu = new Texture("menu2.png");
		restart = new Texture("restart.png");
		start = new Texture("start.png");
		dobrze = new Texture("dobrze.png");
		zle = new Texture("zle.png");
		logo = new Texture("logo.png");
		historia = new Texture("historia.png");

		dzwiekPoczatkaRundy = Gdx.audio.newSound(Gdx.files.internal("dzwiekPoczatkaRundy.mp3"));
		dzwiekKoncaGry = Gdx.audio.newSound(Gdx.files.internal("dzwiekKoncaGry.mp3"));

		xPG = WINDOW_WIDTH / 2 - poleGlowne.getWidth() / 2;
		yPG = WINDOW_HEIGHT / 2 + 25;

		font = new BitmapFont();
		font.setColor(0,1,0,1);
		font.getData().setScale(2,2);
		font2 = new BitmapFont();
		font2.setColor(1,1,1,1);
		font2.getData().setScale(2,2);
		font3 = new BitmapFont();
		font3.setColor(0,1,0,1);
		font4 = new BitmapFont();
		font4.setColor(1,1,0,1);
	}

	/**
	 * Metoda, w ktorej aktualizujemy stan gry
	 */
	@Override
	public void render() {
		ScreenUtils.clear(0.30f, 0.6f, 0.5f, 1);
		int currentMouseX = Gdx.input.getX();
		int currentMappedMouseY = WINDOW_HEIGHT - 1 - Gdx.input.getY();

		batch.begin();
		if(!czyGraRuszyla) {
			wyswietlInstrukcje(currentMouseX, currentMappedMouseY);
		}
		else{
			wyswietlStatyczneElementyGry();
			otworzZamknijMenu(currentMouseX, currentMappedMouseY);

			if(historiaKlik){
				wyswietlHistorie();
			}

			if(zmianaTrybuGry){
				zmienTrybGry(currentMouseX, currentMappedMouseY);
			}
			else if(!zmianaTrybuGry){
				if(koniecGry){
					wyswietlEkranKoncaGry(currentMouseX, currentMappedMouseY);
				}
				else if(!koniecGry){
					if(zmianaUkladu){
						zmienUklad();
					}
					zapelnijPoleGlowne();
					zapelnijPoleUzytkownika();
					sprawdzCzyWybranoPoprawnyKsztalt(currentMouseX, currentMappedMouseY);
					wyswietlParametryGry();
				}
			}
		}
		batch.end();
	}

	/**
	 * Metoda, ktora zwalnia zasoby gry
	 */
	@Override
	public void dispose() {
		batch.dispose();
		startowa.dispose();
		background.dispose();
		poleGlowne.dispose();
		dobrze.dispose();
		zle.dispose();
		logo.dispose();
		font.dispose();
		font2.dispose();
		font3.dispose();
		font4.dispose();
		restart.dispose();
		menu.dispose();
		wtr.dispose();
		wtg.dispose();
		historia.dispose();
		dzwiekPoczatkaRundy.dispose();
		dzwiekKoncaGry.dispose();
		for (int i = 0; i <= 20; i++) {
				czarne.get(i).dispose();
		}
		for (int i = 0; i <= 4; i++) {
			zlote.get(i).dispose();
			numery.get(i).dispose();
		}
	}

	/**
	 * Metoda losuje liczbe z ustalonego zakresu
	 * @param min Wartosc minimalna zakresu
	 * @param max Wartosc maksymalna zakresu
	 * @return Metoda zwraca losowa liczbe z ustalonego zakresu
	 */
	public int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	/**
	 * Metoda losuje 21 figur, ktore beda wyswietlane w polu glownym gry
	 */
	void losowanieTablicy() {
		int chwilowyInt;
		for (int i = 0; i < 21; i++) {
			chwilowyInt = getRandomNumber(0, allFigures.size());
			wylosowaneCzarne.add(allFigures.get(chwilowyInt));
			allFigures.remove(chwilowyInt);
		}
	}

	/**
	 * Metoda wypelnia pole glowne wczesniej wylosowanymi figurami
	 */
	void zapelnijPoleGlowne() {
		for (int i = 0; i <= 20; i++) {
			t = new Texture("figury/" + wylosowaneCzarne.get(i) + ".png");
			czarne.add(t);
			if (i < 7) {
				batch.draw(czarne.get(i), xPG + i * 100, yPG);
			} else if (i >= 7 && i < 14) {
				batch.draw(czarne.get(i), xPG + (i - 6) * 100 - 100, yPG + 100);
			} else if (i >= 14 && i <= 20) {
				batch.draw(czarne.get(i), xPG + (i - 13) * 100 - 100, yPG + 200);
			}
		}
	}

	/**
	 * Metoda losuje 4 elementy figury, ktore nie znajduja sie we wczesniej wylosowanych 21 oraz 1 element, ktory
	 * znajdowal sie wsrod tych 21, a nastepnie wstawia je w tablicy w losowej kolejnosci
	 */
	void losowanieTablicyZlotych(){
			int chwilowyInt;
		for (int i = 0; i < 4; i++) {
			chwilowyInt = getRandomNumber(0, allFigures.size());
			wylosowaneZlote.add(allFigures.get(chwilowyInt));
			allFigures.remove(chwilowyInt);
		}
		chwilowyInt = getRandomNumber(0, wylosowaneCzarne.size());
		poprawny = wylosowaneCzarne.get(chwilowyInt);
		wylosowaneZlote.add(poprawny);

		for (int i = 0; i <= 4; i++) {
			chwilowyInt = getRandomNumber(0, wylosowaneZlote.size());
			wylosowaneZlote2.add(wylosowaneZlote.get(chwilowyInt));
			wylosowaneZlote.remove(chwilowyInt);
		}
	}

	/**
	 * Metoda zapelnia pole uzytkownika 5 figurami z tablicy zlotych2
	 */
	void zapelnijPoleUzytkownika() {
		for (int i = 0; i <= 4; i++) {
			t = new Texture("figury/" + wylosowaneZlote2.get(i) + "x.png");
			zlote.add(t);
				batch.draw(zlote.get(i), xPG + i*125 + 50 , yPG - 225);
			t = new Texture("numery/" + (i+1) + ".png");
			numery.add(t);
				batch.draw(numery.get(i), xPG + i*125 + 50 , yPG - 350);
		}
	}

	/**
	 * Metoda usuwa wszystkie elementy kazdej arraylisty
	 */
	void wyczyscArrayListy(){
		int a = allFigures.size();
		int b = wylosowaneCzarne.size();
		int c = wylosowaneZlote2.size();
		int d = czarne.size();
		int e = zlote.size();

		for(int i=0; i < a; i++){
			allFigures.remove(a-1-i);
		}

		for(int i=0; i < b ;i++){
			wylosowaneCzarne.remove(b-1-i);
		}

		for(int i=0; i < c ;i++){
			wylosowaneZlote2.remove(c-1-i);
		}

		for(int i = 0; i < d; i++) {
			czarne.remove(d-1-i);
		}
		for(int i = 0; i < e; i++) {
			zlote.remove(e-1-i);
			numery.remove(e-1-i);
		}
	}

	/**
	 * Metoda wyswietla historie 10 ostatnich gier
	 * Gry zapisywane sa na przemian na zolto i zielono
	 */
	void wyswietlHistorie(){
		batch.draw(historia, 20, 125);
		for(int i=0; i<historiaUkonczonychGier.size(); i++){
			//System.out.println(i);
			if(i%2==0){
				font3.draw(batch, ""+historiaUkonczonychGier.get(i), 25, 530-i*40);
			}
			else if(i%2!=0){
				font4.draw(batch, ""+historiaUkonczonychGier.get(i), 25, 530-i*40);
			}
		}
	}

	/**
	 * Metoda rozwija menu, jesli jego obszar zostal klikniety przez uzytkownika,
	 * a gdy juz jest rozwiniete pozwala dokonac dalszych wyborow przez klikniecie odpowiednich obszarow w owym menu
	 * jak na przyklad zmiana trybu gry lub wyswietlenie/ukrycie historii gier
	 * @param currentMouseX Aktualna pozycja myszy na ekranie wzdluz osi X
	 * @param currentMappedMouseY Aktualna pozycja myszy na ekranie wzdluz osi Y
	 */
	void otworzZamknijMenu(int currentMouseX, int currentMappedMouseY){
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			if (currentMouseX >= WINDOW_WIDTH-220 &&
					currentMouseX < WINDOW_WIDTH &&
					currentMappedMouseY >= 0 &&
					currentMappedMouseY < 100) {
				if(menuKlik){
					menuKlik=false;
				}
				else if(!menuKlik){
					menuKlik = true;
				}
			}
		}

		if(menuKlik){
			batch.draw(menu, WINDOW_WIDTH-220, 100);
			wtr = new Texture(""+czasRundy+"s.png");
			wtg = new Texture(""+czasGry+"s.png");
			if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
				if (currentMouseX >= WINDOW_WIDTH-220+5 &&
						currentMouseX < WINDOW_WIDTH-220+70 &&
						currentMappedMouseY >= 100+180 &&
						currentMappedMouseY < 100+230) {
					//System.out.println("2 sekundy rundy");
					czasRundy=2;
					zmianaTrybuGry = true;
				}
				else if (currentMouseX >= WINDOW_WIDTH-220+70 &&
						currentMouseX < WINDOW_WIDTH-220+150 &&
						currentMappedMouseY >= 100+180 &&
						currentMappedMouseY < 100+230) {
					//System.out.println("5 sekund rundy");
					czasRundy=5;
					zmianaTrybuGry = true;
				}
				else if (currentMouseX >= WINDOW_WIDTH-220+150 &&
						currentMouseX < WINDOW_WIDTH-220+215 &&
						currentMappedMouseY >= 100+180 &&
						currentMappedMouseY < 100+230) {
					//System.out.println("10 sekund rundy");
					czasRundy=10;
					zmianaTrybuGry = true;
				}
				else if (currentMouseX >= WINDOW_WIDTH-220+5 &&
						currentMouseX < WINDOW_WIDTH-220+70 &&
						currentMappedMouseY >= 100+100 &&
						currentMappedMouseY < 100+150) {
					//System.out.println("10 sekund gry");
					czasGry=10;
					zmianaTrybuGry = true;
				}
				else if (currentMouseX >= WINDOW_WIDTH-220+70 &&
						currentMouseX < WINDOW_WIDTH-220+150 &&
						currentMappedMouseY >= 100+100 &&
						currentMappedMouseY < 100+150) {
					//System.out.println("30 sekund gry");
					czasGry=30;
					zmianaTrybuGry = true;
				}
				else if (currentMouseX >= WINDOW_WIDTH-220+150 &&
						currentMouseX < WINDOW_WIDTH-220+215 &&
						currentMappedMouseY >= 100+100 &&
						currentMappedMouseY < 100+150) {
					//System.out.println("60 sekund gry");
					czasGry=60;
					zmianaTrybuGry = true;
				}


			}

			batch.draw(wtr, WINDOW_WIDTH-220+26, 100+12);
			batch.draw(wtg, WINDOW_WIDTH-220+26+90, 100+12);

			if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
				if (currentMouseX >= WINDOW_WIDTH-220 &&
						currentMouseX < WINDOW_WIDTH &&
						currentMappedMouseY >= 100+300 &&
						currentMappedMouseY < 100+350) {
					if(historiaKlik){
						historiaKlik=false;
					}
					else if(!historiaKlik){
						historiaKlik = true;
					}
				}
			}
		}
	}

	/**
	 * Metoda wyswietla ekran startowy gry (instrukcja oraz przycisk startu, po ktorego kliknieciu gra sie rozpoczyna)
	 * @param currentMouseX Aktualna pozycja myszy na ekranie wzdluz osi X
	 * @param currentMappedMouseY Aktualna pozycja myszy na ekranie wzdluz osi Y
	 */
	void wyswietlInstrukcje(int currentMouseX, int currentMappedMouseY){
		batch.draw(startowa, 0, 0);
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			if (currentMouseX >= 25 &&
					currentMouseX < 1255 &&
					currentMappedMouseY >= 20 &&
					currentMappedMouseY < 190) {
				czyGraRuszyla = true;
				m0 = (int) (new Date().getTime()/1000);
			}
		}
	}

	/**
	 * Metoda inforumje uzytkownika o zmianie trybu gry i czeka na nacisniecie przycisku start, aby rozpoczac
	 * rozgrywke ze zmienionymi ustawieniami
	 * @param currentMouseX Aktualna pozycja myszy na ekranie wzdluz osi X
	 * @param currentMappedMouseY Aktualna pozycja myszy na ekranie wzdluz osi Y
	 */
	void zmienTrybGry(int currentMouseX, int currentMappedMouseY){
		font.draw(batch, "            Zmieniono tryb gry"+System.lineSeparator()+"Rozpocznij z nowymi ustawieniami", WINDOW_WIDTH / 2-220, WINDOW_HEIGHT/2+270);
		batch.draw(start, WINDOW_WIDTH / 2-start.getWidth()/2 , WINDOW_HEIGHT/2+50);
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			if (currentMouseX >= WINDOW_WIDTH / 2-start.getWidth()/2 &&
					currentMouseX < WINDOW_WIDTH / 2-start.getWidth()/2+200 &&
					currentMappedMouseY >= WINDOW_HEIGHT/2+50 &&
					currentMappedMouseY < WINDOW_HEIGHT/2+50+120) {
				zmianaTrybuGry = false;
				koniecGry = false;
				zmianaUkladu=true;
				pkt=0;
				trafione = 0;
				nieTrafione = 0;
				runda=0;
				m0 = (int) (new Date().getTime()/1000);
				m1 = m0;
			}
		}
	}

	/**
	 * Metoda wyswietla ekran konca gry z podsumowaniem wynikow oraz oczekuje na uzytkownika z rozpoczeciem kolejnej
	 * gry do momentu klikniecia pola "ZAGRAJ JESZCZE RAZ" lub spacji
	 * @param currentMouseX Aktualna pozycja myszy na ekranie wzdluz osi X
	 * @param currentMappedMouseY Aktualna pozycja myszy na ekranie wzdluz osi Y
	 */
	void wyswietlEkranKoncaGry(int currentMouseX, int currentMappedMouseY){
		String odmianaPkt;
		if (pkt == 1) {
			odmianaPkt = "punkt";
		} else if (pkt == 0) {
			odmianaPkt = "punktow";
		} else if ((pkt - 2) % 10 == 0 || (pkt - 3) % 10 == 0 || (pkt - 4) % 10 == 0) {
			odmianaPkt = "punkty";
		} else {
			odmianaPkt = "punktow";
		}


		font.draw(batch, "  Koniec Gry" + System.lineSeparator() + "Twoj wynik to:", WINDOW_WIDTH / 2 - 80, WINDOW_HEIGHT / 2 + 290);
		font2.draw(batch, "  " + pkt + " " + odmianaPkt, WINDOW_WIDTH / 2 - 70, WINDOW_HEIGHT / 2 - 100 + 300);

		if (pkt >= czasGry / 10 * 4) {
			font.draw(batch, "Gratulacje!!! To bardzo dobry wynik", WINDOW_WIDTH / 2 - 200, WINDOW_HEIGHT / 2 + 140);
			if (czasRundy > 3) {
				font3.draw(batch, "Mozesz zwiekszyc poziom trudnosci skracjac czas rundy" + System.lineSeparator() + "   w menu, ktore znajduje sie w prawym dolnym rogu", WINDOW_WIDTH / 2 - 150, WINDOW_HEIGHT / 2 + 100);
			} else if (czasRundy < 3) {
				font3.draw(batch, "Osiagnieto najwyzszy poziom umiejetnosci" + System.lineSeparator() + "    Cwicz dalej, aby nie wyjsc z wprawy", WINDOW_WIDTH / 2 - 115, WINDOW_HEIGHT / 2 + 100);
			}
		} else if (pkt < czasGry / 10 * 4) {
			font.draw(batch, "Niestety, musisz jeszcze troche potrenowac", WINDOW_WIDTH / 2 - 270, WINDOW_HEIGHT / 2 + 140);
			if (czasRundy < 6) {
				font3.draw(batch, "Mozesz zmniejszyc poziom trudnosci wydluzajac czas rundy" + System.lineSeparator() + "    w menu, ktore znajduje sie w prawym dolnym rogu", WINDOW_WIDTH / 2 - 170, WINDOW_HEIGHT / 2 + 100);
			} else if (czasRundy > 6) {
				font3.draw(batch, "Twoje umiejetnosci sa na bardzo niskim poziomie" + System.lineSeparator() + "               Cwicz dalej, aby je polepszyc", WINDOW_WIDTH / 2 - 150, WINDOW_HEIGHT / 2 + 100);
			}
		}

		m4 = (int) (new Date().getTime() / 1000);
		if (m4 - m2 >= 1) {
			batch.draw(restart, xPG + 100, yPG - 300);
			if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
				if (currentMouseX >= xPG + 100 &&
						currentMouseX < xPG + 500 &&
						currentMappedMouseY >= yPG - 300 &&
						currentMappedMouseY < yPG - 300 + 200) {
					koniecGry = false;
					zmianaUkladu = true;
					pkt = 0;
					trafione = 0;
					nieTrafione = 0;
					runda = 0;
				}
				m0 = (int) (new Date().getTime() / 1000);
			}
			if (Gdx.input.isKeyJustPressed(SPACE)) { //spacja
				koniecGry = false;
				zmianaUkladu = true;
				pkt = 0;
				runda = 0;
				trafione = 0;
				nieTrafione = 0;
				m0 = (int) (new Date().getTime() / 1000);
			}
		}
	}

	/**
	 * Metoda zmienia uklad figur (losuje 21 czarnych, 5 zlotych oraz zwieksza liczbe rundy)
	 */
	void zmienUklad(){
		dzwiekPoczatkaRundy.play();
		wyczyscArrayListy();
		for (int i = 0; i <= 29; i++) {
			allFigures.add(String.valueOf(i));
		}
		losowanieTablicy();
		losowanieTablicyZlotych();
		int poprawnyPlus1 = wylosowaneZlote2.indexOf(poprawny)+1;
		System.out.println("poprawny jest: "+ poprawnyPlus1);
		//System.out.println(wylosowaneCzarne);
		m1 = (int) (new Date().getTime()/1000);
		znak = 0;
		zmianaUkladu = false;
		runda = runda + 1;
	}

	/**
	 * Metoda sprawdza czy uzytkownik wybral poprawny ksztalt za pomoca myszki lub klawiatury
	 * W przypadku powodzenia wyswietla zielone pole, dodaje punkt oraz nakazuje zmienic uklad figur
	 * W przypadku niepowodzenia wyswietla czerwone pole
	 * @param currentMouseX Aktualna pozycja myszy na ekranie wzdluz osi X
	 * @param currentMappedMouseY Aktualna pozycja myszy na ekranie wzdluz osi Y
	 */
	void sprawdzCzyWybranoPoprawnyKsztalt(int currentMouseX, int currentMappedMouseY){
		if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
			for(int i=0; i<5; i++){
				if (currentMouseX >= xPG + i*125 + 50 &&
						currentMouseX < xPG + i*125 + 50 + 100 &&
						currentMappedMouseY >= yPG - 225 &&
						currentMappedMouseY < yPG - 225 + 200) {
					if(i == wylosowaneZlote2.indexOf(poprawny)){
						//System.out.println("Trafiles na poprawny ksztalt");
						znak = 1;
						trafione=trafione+1;
					}
					else{
						//System.out.println("Zle");
						znak = 2;
						nieTrafione=nieTrafione+1;
					}
				}
			}
		}
		else{
			for(int i=0; i<5; i++){
				if(i ==0){q = 8;} //NUM_1
				else if (i == 1) {q = 9;} //NUM_2
				else if (i == 2) {q = 10;} //NUM_3
				else if (i == 3) {q = 11;} //NUM_4
				else if (i == 4) {q = 12;} //NUM_5

				if (Gdx.input.isKeyJustPressed(q)){
					if(i == wylosowaneZlote2.indexOf(poprawny)){
						//System.out.println("Trafiles na poprawny ksztalt");
						znak = 1;
						trafione=trafione+1;
					}
					else{
						//System.out.println("Zle");
						znak = 2;
						nieTrafione=nieTrafione+1;
					}
				}
			}
		}

		if(znak==1){
			batch.draw(dobrze, xPG, yPG-350);
			batch.draw(dobrze, xPG+650, yPG-350);
			zmianaUkladu = true;
			pkt = pkt + 1;
		}
		else if(znak==2){
			batch.draw(zle, xPG, yPG-350);
			batch.draw(zle, xPG+650, yPG-350);
		}
	}

	/**
	 * Metoda wyswietla parametry gry takie jak zdobyte punkty, czas, nr rundy oraz trafnosc
	 * Metoda podejmuje decyzje o zakonczeniu gry, gdy uplynal okreslony czas oraz dopisuje zakonczona gre do historii
	 */
	void wyswietlParametryGry(){
		m2 = (int) (new Date().getTime()/1000);
		int m3 = czasRundy - (m2-m1);
		if(m3<1){
			m3 = 1;
			zmianaUkladu = true;
		}
		m4 = czasGry-(m2-m0);
		if(m4<1){
			m4=1;
			koniecGry=true;
			dzwiekKoncaGry.play();
			ukonczoneGry=ukonczoneGry+1;
			Date data = new Date();
			if(trafione==0 && nieTrafione==0){
				nieTrafione=1;
			}
			poprawny="Tryb gry: "+czasRundy+"/"+czasGry+"  Wynik: "+pkt+"    " +
					df.format((trafione/(trafione+nieTrafione))*100)+"%"+System.lineSeparator()+
					"                       "+formatDaty.format(data);
			historiaUkonczonychGier.add(poprawny);
			if(historiaUkonczonychGier.size()>10){
				for(int i=0; i<10;i++){
					historiaUkonczonychGier.set(i,historiaUkonczonychGier.get(i+1));
				}
				historiaUkonczonychGier.remove(10);
			}
		}

		font.draw(batch, "Czas do konca"+System.lineSeparator()+"  gry:  /  rundy:", 50, 700);
		font2.draw(batch, ""+m4+"          "+m3, 80, 625);
		font.draw(batch, "Trafnosc:", 1080, 575);
		if(trafione!=0 || nieTrafione!=0){
			font2.draw(batch, ""+df.format((trafione/(trafione+nieTrafione))*100)+"%", 1090, 525);
		}
		font.draw(batch, "Runda nr:", 80, 100);
		font2.draw(batch, ""+runda, 130, 50);
		font.draw(batch, "Zdobyte punkty:", 1040, 700);
		font2.draw(batch, ""+pkt, 1130, 650);
	}

	/**
	 * Metoda wyswietla elementy statyczne gry takie jak tlo, pole glowne oraz logo/menu
	 */
	void wyswietlStatyczneElementyGry(){
		batch.draw(background, 0, 0);
		batch.draw(poleGlowne, xPG, yPG);
		batch.draw(logo, WINDOW_WIDTH-220, 0);
	}
}

