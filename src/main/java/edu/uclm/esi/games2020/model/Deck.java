package edu.uclm.esi.games2020.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class Deck {
	private List<Card> cards;
	
	public Deck() {
		this.cards = new ArrayList<>();
		for (int i=0; i<4; i++) {
			Suit suit = i==0 ? Suit.OROS : i==1 ? Suit.COPAS : i==2 ? Suit.ESPADAS : Suit.BASTOS;
			for (int j=1; j<=7; j++) {
				Card card = new Card(j, suit);
				this.cards.add(card);
			}
			for (int j=10; j<=12; j++) {
				Card card = new Card(j, suit);
				this.cards.add(card);
			}
		}
	}

	public void suffle() {
		SecureRandom dado = new SecureRandom();
		for (int i=0; i<200; i++) {
			int a = dado.nextInt(40);
			int b = dado.nextInt(40);
			Card auxiliar = this.cards.get(a);
			this.cards.set(a, this.cards.get(b));
			this.cards.set(b, auxiliar);
		}
	}

	public Card getCard() {
		return this.cards.remove(0);
	}
}
