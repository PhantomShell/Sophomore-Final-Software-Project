package com;

import java.util.ArrayList;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public class AutoCompleteSearchHandler {
	
	final private TextField searchBar;
	final private ListView<String> searchResults;
	private ArrayList<ClassPeriod> classes;
	private ObservableList<String> choices;
	private ClassPeriodSearchComparator searchComparator;
	
	public AutoCompleteSearchHandler(TextField textField, ListView<String> listView) {
		searchResults = listView;
		searchBar = textField;
		searchBar.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER)
					handleSearch();
				updateChoices();
			}
		});
		classes = new ArrayList<ClassPeriod>();
		choices = FXCollections.observableArrayList();
		searchComparator = new ClassPeriodSearchComparator();
		searchResults.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				ObservableList<String> selected = searchResults.getSelectionModel().getSelectedItems();
				searchBar.requestFocus();
				if (selected.size() > 0) {
					searchBar.setText(selected.get(0));
					searchBar.selectRange(0, 0);
					searchBar.positionCaret(selected.get(0).length());
					searchResults.getSelectionModel().clearSelection();
				}
			}
		});
		updateListView();
	}
	
	private void updateListView() {
		choices = FXCollections.observableArrayList();
		for (ClassPeriod classPeriod : classes)
			choices.add(classPeriod.toString());
		searchResults.setItems(choices);
	}
	
	public void setChoices(ArrayList<ClassPeriod> classes) {
		this.classes = new ArrayList<ClassPeriod>();
		for (ClassPeriod period : classes)
			this.classes.add(period);
		updateChoices();
	}
	
	private void updateChoices() {
		searchComparator.setSearchTerm(searchBar.getText());
		Collections.sort(classes, searchComparator);
		updateListView();
	}
	
	private void handleSearch() {
		String teacher = searchBar.getText();
		if (!choices.contains(teacher)) {
			teacher = choices.get(0);
			searchBar.setText(teacher);
			searchBar.positionCaret(teacher.length());
		}
		else {
			searchBar.setText("");
			System.out.println(teacher);
		}
	}
	
}