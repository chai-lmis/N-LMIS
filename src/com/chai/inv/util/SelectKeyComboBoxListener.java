package com.chai.inv.util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;

import com.chai.inv.model.LabelValueBean;

public class SelectKeyComboBoxListener implements EventHandler<KeyEvent> {

    private ComboBox<LabelValueBean> comboBox;
    private boolean moveCaretToPos = false;
	private int position;
	ObservableList<LabelValueBean> dataList = FXCollections.observableArrayList();
    public SelectKeyComboBoxListener(ComboBox<LabelValueBean> comboBox) {
        this.comboBox = comboBox;
        this.comboBox.setOnKeyReleased(this);
        dataList = comboBox.getItems();
        this.comboBox.setConverter(new StringConverter<LabelValueBean>(){
			@Override
			public String toString(LabelValueBean object) {
				if(object!=null){
					return object.getLabel();
				}else return null;							
			}
			@Override
			public LabelValueBean fromString(String string) {
				LabelValueBean bean = new LabelValueBean();
				if (string != null && !string.isEmpty()) {
					for(LabelValueBean p : comboBox.getItems()){
						if(p.getLabel().equals(string)){
							System.out.println("bean value = "+p.getValue());
							System.out.println("bean label = "+p);
							bean = p;
							break;
						}
					}
					return bean;
				}else {
					return null;
				}				
			}        	
        });
    }
    
    @Override
	public void handle(KeyEvent event) {
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
//		System.out.println("dataList.size() = "+dataList.size());
		for (int i = 0; i < dataList.size(); i++) {
			if (dataList.get(i).getLabel().toLowerCase()
					.contains(comboBox.getEditor().getText().toLowerCase())) {
				System.out.println("dataList.get("+i+") = "+dataList.get(i));
				System.out.println("dataList.get("+i+") = "+dataList.get(i).getLabel());
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
		}else if(t.length()==0){
			comboBox.setItems(dataList);
		}
	}
}