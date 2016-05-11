import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import com.sun.pdfview.PDFFile;
import com.sun.pdfview.PDFPage;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Callback;

public class Controller {

	@FXML private Pagination pagination;
	@FXML private Label currentZoomLabel;
	@FXML private BorderPane borderPane;
	@FXML private ScrollPane scroller;
	
	private boolean fitOnLoad;

	private ObjectProperty<PDFFile> currentFile;
	private ObjectProperty<ImageView> currentImage;
	private DoubleProperty zoom;
	private PageDimensions currentPageDimensions;
	
	private ArrayList<ClassPeriod> classes;
	private ClassPeriodDistanceComparator distanceComparator;
	private SeatingHandler seatingHandler;
	
	private PDDocument doc;
	private File file;
	
	private ExecutorService imageLoadService;
	
	private static final double ZOOM_DELTA = 1.2;
	
	public void initialize() {
		classes = new ArrayList<ClassPeriod>();
		HashMap<Integer, Integer> roomDistances = new HashMap<Integer, Integer>();
		distanceComparator = new ClassPeriodDistanceComparator(roomDistances);
		int[][][] rowSizes =
			{
				{
					{7, 8, 8, 9, 10, 8, 11, 12, 12, 13, 14, 14, 14, 14},
					{9, 10, 10, 10, 11, 9, 12, 12, 12, 13, 14, 14, 14, 14},
					{7, 8, 8, 9, 10, 10, 11, 12, 12, 13, 14, 8, 8, 8}
				},
				{
					{9, 8, 13, 13, 12, 11, 11, 11},
					{15, 15, 15, 15, 10, 10, 5}
				}
			};
		seatingHandler = new SeatingHandler(rowSizes);
		
		createAndConfigureImageLoadService();

		currentFile = new SimpleObjectProperty<>();
		updateWindowTitleWhenFileChanges();
		
		currentImage = new SimpleObjectProperty<>();
		scroller.contentProperty().bind(currentImage);
		
		zoom = new SimpleDoubleProperty(1);
		zoom.addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				updateImage(pagination.getCurrentPageIndex());
			}
		});
		
		currentZoomLabel.textProperty().bind(Bindings.format("%.0f %%", zoom.multiply(100)));
		
		bindPaginationToCurrentFile();
		createPaginationPageFactory();
		bindZoomKeys();
		
		try {
			prepareTempFile();
		} catch (IOException e) {
			showErrorMessage("Could not load template", e);
			e.printStackTrace();
		}
		
		loadFile(file);
	}
	
	public void prepareTempFile() throws IOException {
		doc = PDDocument.load(new File("src/template.pdf"));
		for (File toDel : new File("temp").listFiles())
			System.out.println(toDel.delete());
		file = File.createTempFile("seating-chart", ".pdf", new File("temp"));
		doc.save(file);
		file.deleteOnExit();
	}
	
	public void addText(PDDocument doc, float x, float y, String text) throws IOException {
		PDFont font = PDType1Font.TIMES_ROMAN;
		float fontSize = 12;
		PDPage page = doc.getPages().get(0);
		PDPageContentStream contentStream = new PDPageContentStream(doc, page, AppendMode.APPEND, true, true);
		contentStream.beginText();
		contentStream.setFont(font, fontSize);
		contentStream.setNonStrokingColor(0, 0, 0);
		contentStream.setTextMatrix(Matrix.getTranslateInstance(x, y));
		contentStream.showText(text);
		contentStream.endText();
		contentStream.close();
	}
	
	public void generateSeatingChart() {
		seatingHandler.clear();
		seatingHandler.fill(classes, distanceComparator);
		//LinkedHashMap<String, Integer>[][][] seatingChart = seatingHandler.getSeatingChart();
		float x = 100;
		float y = 100;
		/*for (LinkedHashMap<String, Integer>[][] seatingRow : seatingChart) {
			for (LinkedHashMap<String, Integer>[] room : seatingRow) {
				for (LinkedHashMap<String, Integer> row : room) {
					String text = "";
					for (Map.Entry<String, Integer> entry : row.entrySet())
						text += entry.getKey() + " " + entry.getValue();
					addText(doc, x, y, text);
					y += 14;
				}
			}
		}*/
		try {
			doc.close();
			prepareTempFile();
			addText(doc, x, y, "hello");
			doc.save(file);
		}
		catch (IOException e) {
			showErrorMessage("Could not load/save temporary file", e);
			e.printStackTrace();
		}
		loadFile(file);		
	}

	private void createAndConfigureImageLoadService() {
		imageLoadService = Executors.newSingleThreadExecutor(new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r);
				thread.setDaemon(true);
				return thread;
			}
		});
	}

	private void updateWindowTitleWhenFileChanges() {
		currentFile.addListener(new ChangeListener<PDFFile>() {
			@Override
			public void changed(ObservableValue<? extends PDFFile> observable, PDFFile oldFile, PDFFile newFile) {
				try {
					String title = newFile == null ? "PDF Viewer" : newFile.getStringMetadata("Title");
					Window window = pagination.getScene().getWindow();
					if (window instanceof Stage) {
						((Stage)window).setTitle(title);
					}
				} catch (IOException e) {
					showErrorMessage("Could not read title from pdf file", e);
				}
			}
			
		});
	}
	
	private void bindPaginationToCurrentFile() {
		currentFile.addListener(new ChangeListener<PDFFile>() {
			@Override
			public void changed(ObservableValue<? extends PDFFile> observable, PDFFile oldFile, PDFFile newFile) {
				if (newFile != null) {
					pagination.setCurrentPageIndex(0);
				} 
			}
		});
		pagination.pageCountProperty().bind(new IntegerBinding() {
			{ super.bind(currentFile); }
			@Override
			protected int computeValue() {
				return currentFile.get() == null ? 0 : currentFile.get().getNumPages();
			}
		});
		pagination.disableProperty().bind(Bindings.isNull(currentFile));
	}
	
	private void bindZoomKeys() {
		borderPane.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) {
				if (event.getCode() == KeyCode.EQUALS) {
					zoomIn();
				}
				if (event.getCode() == KeyCode.MINUS) {
					zoomOut();
				}
			}
		});
	}
	
	private void createPaginationPageFactory() {
		pagination.setPageFactory(new Callback<Integer, Node>() {
			@Override
			public Node call(Integer pageNumber) {
				if (currentFile.get() == null)
					return null;
				else {
					if (pageNumber >= currentFile.get().getNumPages() || pageNumber < 0) {
						return null;
					} else {
						updateImage(pageNumber);
						return scroller;
					}
				}
			}
		});
	}
	
	@FXML private void loadFile(File file) {
		if (file != null) {
			final Task<PDFFile> loadFileTask = new Task<PDFFile>() {
				@Override
				protected PDFFile call() throws Exception {
					try ( 
							RandomAccessFile raf = new RandomAccessFile(file, "r");
							FileChannel channel = raf.getChannel() 
						) {
						ByteBuffer buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
						return new PDFFile(buffer);
					}
				}
			};
			loadFileTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					pagination.getScene().getRoot().setDisable(false);
					final PDFFile pdfFile = loadFileTask.getValue();
					currentFile.set(pdfFile);
					updateImage(pagination.getCurrentPageIndex());
				}
			});
			loadFileTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
				@Override
				public void handle(WorkerStateEvent event) {
					pagination.getScene().getRoot().setDisable(false);
					showErrorMessage("Could not load file "+file.getName(), loadFileTask.getException());
				}
			});
			fitOnLoad = true;
			imageLoadService.submit(loadFileTask);
		}
	}
	
	@FXML private void zoomIn() {
		zoom.set(zoom.get()*ZOOM_DELTA);
	}
	
	@FXML private void zoomOut() {
		zoom.set(zoom.get()/ZOOM_DELTA);
	}
	
	@FXML private void zoomFit() {
		double horizontalZoom = (scroller.getWidth() - 20) / currentPageDimensions.width;
		double verticalZoom = (scroller.getHeight() - 20) / currentPageDimensions.height;
		zoom.set(Math.min(horizontalZoom, verticalZoom));
	}
	
	private void updateImage(final int pageNumber) {
		final Task<ImageView> updateImageTask = new Task<ImageView>() {
			@Override
			protected ImageView call() throws Exception {
				PDFPage page = currentFile.get().getPage(pageNumber+1);
				Rectangle2D bbox = page.getBBox();
				final double actualPageWidth = bbox.getWidth();
				final double actualPageHeight = bbox.getHeight();

				currentPageDimensions = new PageDimensions(actualPageWidth, actualPageHeight);

				final int width = (int) (actualPageWidth * zoom.get());
				final int height = (int) (actualPageHeight * zoom.get());


				java.awt.Image awtImage = page.getImage(width, height, bbox, null, true, true); 

				BufferedImage buffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
				buffImage.createGraphics().drawImage(awtImage, 0, 0, null);

				Image image = SwingFXUtils.toFXImage(buffImage, null);

				ImageView imageView = new ImageView(image);
				imageView.setPreserveRatio(true);
				return imageView;
			}
		};

		updateImageTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				pagination.getScene().getRoot().setDisable(false);
				currentImage.set(updateImageTask.getValue());
				if (fitOnLoad) {
					zoomFit();
					fitOnLoad = false;
				}
			}
		});
		
		updateImageTask.setOnFailed(new EventHandler<WorkerStateEvent>() {
			@Override
			public void handle(WorkerStateEvent event) {
				pagination.getScene().getRoot().setDisable(false);
				updateImageTask.getException().printStackTrace();
			}
			
		});
		
		pagination.getScene().getRoot().setDisable(true);
		imageLoadService.submit(updateImageTask);
	}
	
	private void showErrorMessage(String message, Throwable exception) {
		final Stage dialogue = new Stage();
		dialogue.initOwner(pagination.getScene().getWindow());
		dialogue.initStyle(StageStyle.UNDECORATED);
		final VBox root = new VBox(10);
		root.setPadding(new Insets(10));
		StringWriter errorMessage = new StringWriter();
		exception.printStackTrace(new PrintWriter(errorMessage));
		final Label detailsLabel = new Label(errorMessage.toString());
		TitledPane details = new TitledPane();
		details.setText("Details:");
		Label briefMessageLabel = new Label(message);
		final HBox detailsLabelHolder =new HBox();
		
		Button closeButton = new Button("OK");
		closeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				dialogue.hide();
			}
		});
		HBox closeButtonHolder = new HBox();
		closeButtonHolder.getChildren().add(closeButton);
		closeButtonHolder.setAlignment(Pos.CENTER);
		closeButtonHolder.setPadding(new Insets(5));
		root.getChildren().addAll(briefMessageLabel, details, detailsLabelHolder, closeButtonHolder);
		details.setExpanded(false);
		details.setAnimated(false);

		details.expandedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue)
					detailsLabelHolder.getChildren().add(detailsLabel);
				else
					detailsLabelHolder.getChildren().remove(detailsLabel);
				dialogue.sizeToScene();
			}
			
		});
		final Scene scene = new Scene(root);

		dialogue.setScene(scene);
		dialogue.show();
	}
	
	
	/*
	 * Struct-like class intended to represent the physical dimensions of a page in pixels
	 * (as opposed to the dimensions of the (possibly zoomed) view.
	 * Used to compute zoom factors for zoomToFit and zoomToWidth.
	 * 
	 */
	
	private class PageDimensions {
		private double width;
		private double height;
		
		PageDimensions(double width, double height) {
			this.width = width;
			this.height = height;
		}
		
		@Override
		public String toString() {
			return String.format("[%.1f, %.1f]", width, height);
		}
	}

}