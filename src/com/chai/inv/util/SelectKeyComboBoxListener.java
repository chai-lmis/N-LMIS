package com.chai.inv.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.StringConverter;

import com.chai.inv.model.LabelValueBean;

public class SelectKeyComboBoxListener implements EventHandler<KeyEvent> {

	private ComboBox<LabelValueBean> comboBox;
	private boolean moveCaretToPos = false;
	private int position;
	ObservableList<LabelValueBean> dataList = FXCollections.observableArrayList();

	public SelectKeyComboBoxListener(ComboBox<LabelValueBean> comboBox){
		try{
			this.comboBox = comboBox;
			this.comboBox.setOnKeyReleased(this);
			dataList = comboBox.getItems();
			this.comboBox.setConverter(new StringConverter<LabelValueBean>() {
				@Override
				public String toString(LabelValueBean object) {
					String label = null;
					try{
						if (object != null) {
							label = object.getLabel();
						} else
							label = null;
					}catch(Exception e1){
						System.out.println("1---------------------"+e1.getMessage());
					}
					return label;
				}

				@Override
				public LabelValueBean fromString(String string) {
					LabelValueBean bean = new LabelValueBean();
					try{
						if (string != null && !string.isEmpty()) {
							for (LabelValueBean p : comboBox.getItems()) {
								System.out.println("p.getLabel() : --- "+p.getLabel());
								System.out.println("p.getValue() : --- "+p.getValue());
								if (p.getLabel().equals(string)) {
									System.out.println("bean value = " + p.getValue());
									System.out.println("bean label = " + p);
									bean = p;
									break;
								}
							}
						} else {
							bean = null;
						}
					}catch(Exception e2){
						System.out.println("2---------------------"+e2.getMessage());
					}
					return bean;
				}
			});
			
			comboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					try{
						comboBox.setItems(dataList);
					}catch(Exception e1){
						System.out.println("3---------------------"+e1.getMessage());
					}
				}
			});
		}catch(Exception e){
			System.out.println("4----------------------------------------------------------"+e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void handle(KeyEvent event) {
		try{
			if (event.getCode() == KeyCode.UP) {
				moveCaretToPos = true;
				comboBox.getEditor().positionCaret(comboBox.getEditor().getText().length());
				return;
			} else if (event.getCode() == KeyCode.DOWN) {
				if (!comboBox.isShowing()) {
					comboBox.show();
				}
				moveCaretToPos = true;
				comboBox.getEditor().positionCaret(comboBox.getEditor().getText().length());
				return;
			} else {
				position = comboBox.getEditor().getCaretPosition();
				int textLen = comboBox.getEditor().getText().length();
				if (position >= 0 && position <= textLen) {
					moveCaretToPos = false;
				}
			}
			if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT) {
				return;
			}
			ObservableList<LabelValueBean> list = FXCollections.observableArrayList();
			// System.out.println("dataList.size() = "+dataList.size());
			for (int i = 0; i < dataList.size(); i++) {
				if (dataList.get(i).getLabel().toLowerCase()
						.contains(comboBox.getEditor().getText().toLowerCase())) {
					System.out.println("dataList.get(" + i + ") = "+ dataList.get(i));
					System.out.println("dataList.get(" + i + ") = "+ dataList.get(i).getLabel());
					list.add(dataList.get(i));
				}
			}
			String t = comboBox.getEditor().getText();
			comboBox.setItems(list);
			comboBox.getEditor().setText(t);
			if (moveCaretToPos) {
				comboBox.getEditor().positionCaret(t.length());
			} else {
				comboBox.getEditor().positionCaret(position);
			}
			if (!list.isEmpty()) {
				comboBox.show();
			} else if (t.length() == 0) {
				comboBox.setItems(dataList);
			}
		}catch(Exception ex){
			System.out.println("5----------------------------------------------------------"+ex.getMessage());
			ex.printStackTrace();
		}
	}
}