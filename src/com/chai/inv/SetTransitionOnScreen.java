package com.chai.inv;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SetTransitionOnScreen {
	/**
	 * 
	 * @param node
	 * @param transitionType
	 */
	public void setTransition(Object fxObject, String transitionType,
			String movePageDirection) {
		Node node = null;
		Stage stage = null;
		if (fxObject instanceof Stage) {
			stage = (Stage) fxObject;
		} else if (fxObject instanceof Node) {
			node = (Node) fxObject;
		}

		if (transitionType.equals("fadeTransition")) {
			System.out.println("fadeTransition");
			FadeTransition ft = new FadeTransition();
			ft.setNode(node);
			ft.setDuration(Duration.seconds(1));
			ft.setFromValue(0.2);
			ft.setInterpolator(Interpolator.EASE_IN);
			ft.setToValue(1);
			ft.play();
		} else if (transitionType.equals("scaleTransition")) {
			ScaleTransition st = new ScaleTransition(Duration.seconds(0.8),
					node);
			st.setFromX(0);
			st.setToX(1);
			st.setFromY(0);
			st.setToY(1);
			st.setInterpolator(Interpolator.LINEAR);
			st.play();

		} else if (transitionType.equals("translateTransitionForTable")) {

			TranslateTransition transTransition = new TranslateTransition();
			transTransition.setDuration(Duration.seconds(1));
			// transTransition.setDelay(Duration.millis(0.1));
			transTransition.setNode(node);
			transTransition.setFromY(500);
			transTransition.setToY(0);
			transTransition.setInterpolator(Interpolator.LINEAR);
			transTransition.setCycleCount(1);
			transTransition.play();

		} else if (transitionType.equals("parrallelFadeScale")) {
			FadeTransition ft = new FadeTransition(Duration.seconds(0.8), node);
			ft.setFromValue(0);
			ft.setToValue(1);
			ft.setInterpolator(Interpolator.LINEAR);
			ScaleTransition scale = new ScaleTransition(Duration.seconds(0.8),
					node);
			scale.setFromX(0);
			scale.setToX(1);
			scale.setFromY(0);
			scale.setToY(1);
			ParallelTransition pt = new ParallelTransition(ft, scale);
			pt.play();
		} else if (transitionType.equals("parrallelFadeTranslate")) {
			FadeTransition ft = new FadeTransition(Duration.seconds(1), node);
			ft.setFromValue(0);
			ft.setToValue(1);
			ft.setInterpolator(Interpolator.LINEAR);
			TranslateTransition tt = new TranslateTransition(
					Duration.seconds(0.8), node);
			if (movePageDirection.equals("farward")) {
				tt.setFromX(800);
				tt.setToX(1);
			} else if (movePageDirection.equals("backward")) {
				tt.setFromX(-800);
				tt.setToX(1);
			}

			ParallelTransition pt = new ParallelTransition(ft, tt);
			pt.play();
		}
	}
}
