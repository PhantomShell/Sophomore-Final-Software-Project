package com;

import java.util.ArrayList;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class AutoCompleteSearchHandler {
	
	final private TextField searchBar;
	final private ListView<String> searchResults;
	final private ListView<String> restrictionList;
	private ArrayList<ClassPeriod> classes;
	private ObservableList<String> choices;
	private ArrayList<ClassPeriod> restrictions;
	private ClassPeriodSearchComparator searchComparator;
	
	public AutoCompleteSearchHandler(TextField textField, ListView<String> listView1, ListView<String> listView2) {
		searchBar = textField;
		searchResults = listView1;
		restrictionList = listView2;
		searchBar.setStyle("-fx-font-size: 30");
		searchResults.setFocusTraversable(false);
		searchResults.setStyle("-fx-font-size: 30");
		searchBar.setOnKeyReleased(new EventHandler<KeyEvent>(){
			@Override
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.ENTER)
					handleSearch();
				else if (event.getCode() == KeyCode.TAB)
					setClosestChoice();
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
					int length = selected.get(0).length();
					searchBar.selectRange(length, length);
				}
				searchResults.getSelectionModel().clearSelection();
				updateChoices();
			}
		});
		restrictionList.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> listView) {
				return new RestrictionCell();
			}
		});
		restrictionList.setEditable(false);
		restrictionList.setFocusTraversable(false);
		updateChoices();
	}
	
	private class RestrictionCell extends ListCell<String> {
		
		RestrictionCell() {
			setOnDragDetected(event -> {
				if (getItem() == null)
					return;
				Dragboard dragboard = startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(getItem());
				dragboard.setContent(content);
				event.consume();
			});
			setOnDragOver(event -> {
				if (event.getDragboard().hasString())
					event.acceptTransferModes(TransferMode.ANY);
				event.consume();
			});
			setOnDragEntered(event -> {
				if (event.getDragboard().hasString())
					getListView().getSelectionModel().clearAndSelect(getIndex());
				event.consume();
			});
			setOnDragDropped(event -> {
				ListView<String> parent = getListView();
				ObservableList<String> items = parent.getItems();
				int sourceIndex = ((RestrictionCell) event.getGestureSource()).getIndex();
				int index = getIndex();
				if (index >= restrictions.size())
					index = restrictions.size() - 1;
				items.add(index, items.remove(sourceIndex));
				restrictions.add(index, restrictions.remove(sourceIndex));
				parent.setItems(items);
				parent.getSelectionModel().clearSelection();
				event.consume();
			});
		}
		
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            ListView<String> parent = getListView();
            int index = getIndex();
            HBox hBox = new HBox();
            if (item != null) {
            	VBox vBox = new VBox();
            	hBox.setAlignment(Pos.CENTER);
            	vBox.setAlignment(Pos.CENTER);
            	final Button upButton = new Button("▴");
            	upButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                    	try {
	                    	ClassPeriod temp = restrictions.get(index);
	                    	restrictions.set(index, restrictions.get(index - 1));
	                    	restrictions.set(index - 1, temp);
	                    	updateRestrictionList();
                    	}
                    	catch (IndexOutOfBoundsException e) {}
                    }
                });
            	final Button downButton = new Button("▾");
            	downButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                    	try {
	                    	ObservableList<String> items = parent.getItems();
	                    	String temp = items.get(index);
	                    	items.set(index, items.get(index + 1));
	                    	items.set(index + 1, temp);
	                    	parent.setItems(items);
                    	}
                    	catch (IndexOutOfBoundsException e) {}
                    }
                });
                final Button closeButton = new Button("X");
                closeButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                    	parent.getItems().remove(index);
                    	classes.add(restrictions.remove(index));
                    	updateChoices();
                    }
                });
                upButton.setStyle("-fx-font-size: 8.5");
                upButton.setFocusTraversable(false);
                downButton.setStyle("-fx-font-size: 8.5");
                downButton.setFocusTraversable(false);
                closeButton.setStyle("-fx-font-size: 17.5");
                closeButton.setFocusTraversable(false);
                Label label = new Label(item);
                label.setStyle("-fx-font-size: 30");
                label.setMaxWidth(Integer.MAX_VALUE);
                HBox.setHgrow(label, Priority.ALWAYS);
                vBox.getChildren().addAll(upButton, downButton);
                hBox.getChildren().addAll(label, vBox, closeButton);
            }
            setGraphic(hBox);
        }
    }
	
	private void setClosestChoice() {
		try {
			String teacher = choices.get(0);
			searchBar.setText(teacher);
			searchBar.positionCaret(teacher.length());
		}
		catch (IndexOutOfBoundsException e) {}
	}
	
	private void updateRestrictionList() {
		restrictionList.getItems().clear();
		ObservableList<String> temp = FXCollections.observableArrayList();
		for (ClassPeriod restriction : restrictions)
			temp.add(restriction.toString());
		restrictionList.setItems(temp);
	}
	
	private void updateListView() {
		choices = FXCollections.observableArrayList();
		for (ClassPeriod classPeriod : classes)
			choices.add(classPeriod.toString());
		searchResults.setItems(choices);
	}
	
	private void updateChoices() {
		searchComparator.setSearchTerm(searchBar.getText());
		Collections.sort(classes, searchComparator);
		updateListView();
	}
	
	public void setChoices(ArrayList<ClassPeriod> classes) {
		restrictions = new ArrayList<ClassPeriod>();
		restrictionList.getItems().clear();
		this.classes = new ArrayList<ClassPeriod>(classes);
		updateChoices();
	}
	
	private void handleSearch() {
		String teacher = searchBar.getText();
		if (!choices.contains(teacher)) {
			setClosestChoice();
		}
		else {
			searchBar.setText("");
			for (int i = 0; i < classes.size(); i++)
				if (classes.get(i).toString().equals(teacher)) {
					restrictions.add(classes.get(i));
					classes.remove(i);
					break;
				}
			updateChoices();
			updateRestrictionList();
		}
	}
	
	public ArrayList<ClassPeriod> getRestrictions() {
		return restrictions;
	}
	
}