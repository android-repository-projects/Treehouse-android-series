package com.andremlsantos.crystal.ball;

import java.util.Random;

public class CrystalBall {
	public String[] mAnswers = { "It is certain", "It is decidedly so",
			"All signs say YES", "The stars aro not aligned", "My reply is no",
			"It is doubtful", "Better not tell you now",
			"Concentrate and ask again", "Unable to answer now" };

	public String getAnAnswer() {
		String answer = "";

		Random ramdomGenerator = new Random();
		int randomNumber = ramdomGenerator.nextInt(mAnswers.length);

		answer = mAnswers[randomNumber];
		return answer;
	}
}
